/*
 * Created on 19-Mar-2005
 */
package com.ixora.rms.providers.utils.sql;

import java.sql.Connection;
import java.sql.SQLException;

import com.ixora.common.pooling.AbstractPoolableObject;

/**
 * A cached database connection.
 * @author Daniel Moraru
 */
public final class CachedConnection extends AbstractPoolableObject {
	/** Connection */
	private Connection fConnection;

	/**
	 * Constructor.
	 * @param conn
	 */
	CachedConnection(Connection conn) {
		super();
		fConnection = conn;
	}

	/**
	 * @return
	 */
	public Connection getSQLConnection() {
		return fConnection;
	}

	/**
	 * Invoke close on this object rather then the sql connection when the connection
	 * is no longer needed.
	 */
	public void close() {
		release();
	}

	/**
	 * @see com.ixora.common.pooling.PoolableObject#evicted()
	 */
	public void evicted() {
		try {
			fConnection.close();
		}catch(SQLException e) {
		}
	}
}
