/*
 * Created on 19-Mar-2005
 */
package com.ixora.rms.providers.utils.sql;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.pooling.InactivityControllerFactory;
import com.ixora.common.pooling.KeyedPoolableObjectFactoryFactory;
import com.ixora.common.pooling.KeyedPoolableObjectPoolDefault;
import com.ixora.common.pooling.PoolInfo;
import com.ixora.common.pooling.PoolableObject;
import com.ixora.common.pooling.PoolableObjectFactory;
import com.ixora.common.pooling.exception.FailedToCreateObject;
import com.ixora.common.utils.Utils;
import com.ixora.rms.providers.utils.sql.exception.FailedToCreateConnection;

/**
 * Database connection cache.
 * @author Daniel Moraru
 */
public final class ConnectionCache {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(ConnectionCache.class);
	/** Connection pool */
	private KeyedPoolableObjectPoolDefault fPool;

	private static class ConnectionKey {
		Driver fDriver;
		String fConnectionString;
		Properties fProps;

		public ConnectionKey(Driver driver, String connString, String username, String password) {
			fDriver = driver;
			fConnectionString = connString;
			fProps = new Properties();
			fProps.put("user", username);
			fProps.put("password", password);
		}

		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object obj) {
			if(obj == this) {
				return true;
			}
			if(!(obj instanceof ConnectionKey)) {
				return false;
			}
			ConnectionKey that = (ConnectionKey)obj;
			boolean ret = Utils.equals(fConnectionString, that.fConnectionString)
				&& Utils.equals(fProps, that.fProps);
			return ret;
		}
		/**
		 * @see java.lang.Object#hashCode()
		 */
		public int hashCode() {
			int hc = fConnectionString.hashCode();
			return hc ^= fProps.hashCode();
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return fConnectionString
				+ " [" + fProps.getProperty("user") + "]"
				+ " [" + fProps.getProperty("password") + "]" ;
		}
	}

	/** Connection factory */
	private static class ConnectionFactory implements PoolableObjectFactory {
		private ConnectionKey fConnKey;

		/**
		 * Constructor.
		 * @param the connection key
		 */
		public ConnectionFactory(ConnectionKey connKey) {
			super();
			fConnKey = connKey;
		}
		/**
		 * @see com.ixora.common.pooling.PoolableObjectFactory#createObject()
		 */
		public PoolableObject createObject() throws FailedToCreateObject {
			try {
				Connection conn = fConnKey.fDriver.connect(fConnKey.fConnectionString,
						(Properties)fConnKey.fProps.clone());
				conn.setReadOnly(true);
				return new CachedConnection(conn);
			}catch(SQLException e) {
				throw new FailedToCreateObject(e);
			}
		}
	}

	/**
	 * Constructor.
	 * @throws Exception
	 */
	public ConnectionCache() throws Exception {
		super();
		fPool = new KeyedPoolableObjectPoolDefault(
				new KeyedPoolableObjectFactoryFactory() {
					public PoolableObjectFactory getFactory(Object key) {
						return new ConnectionFactory((ConnectionKey)key);
					}
				},
				InactivityControllerFactory.createTimingOutController(60000, 60),
				null
		);
	}

	/**
	 * @param driver
	 * @param connString
	 * @return
	 * @throws FailedToCreateConnection
	 */
	public CachedConnection getConnection(Driver driver, String connString, String username, String password) throws FailedToCreateConnection {
		ConnectionKey key = new ConnectionKey(driver, connString, username, password);
		CachedConnection conn;
		try {
			conn = (CachedConnection)fPool.getObject(key);
		} catch(FailedToCreateObject e) {
			throw new FailedToCreateConnection(e.getLocalizedMessage());
		}
		if(logger.isTraceEnabled()) {
			PoolInfo info = fPool.getState(key);
			logger.trace("Statistics for connections cache for " + key + ": "
					+ String.valueOf(info));
		}
		return conn;
	}
}
