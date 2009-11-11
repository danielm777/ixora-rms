/*
 * Created on 18-Mar-2005
 */
package com.ixora.rms.providers.impl.sql;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.LinkedList;
import java.util.List;

import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.utils.Utils;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.providers.ProviderConfiguration;
import com.ixora.rms.providers.exception.InvalidProviderConfiguration;
import com.ixora.rms.providers.impl.AbstractProvider;
import com.ixora.rms.providers.utils.sql.CachedConnection;
import com.ixora.rms.providers.utils.sql.ConnectionCache;

/**
 * @author Daniel Moraru
 */
public class ProviderSql extends AbstractProvider {
	/** Logger */
	private static AppLogger logger = AppLoggerFactory.getLogger(ProviderSql.class);
	/**
	 * Connection manager.
	 */
	private static ConnectionCache fConnectionManager;

	static {
		try {
			fConnectionManager = new ConnectionCache();
		}catch(Exception e) {
			logger.error(e);
		}
	}
	/** Connection string */
	private String fConnectionString;
	/** SQL query */
	private String fSQLQuery;
	/** Username */
	private String fUsername;
	/** Password */
	private String fPassword;
	/** JDBC Driver */
	private Driver fDriver;
	/**
	 * Constructor.
	 */
	public ProviderSql() {
		super(false);
	}

	/**
	 * @see com.ixora.rms.providers.Provider#configure(com.ixora.rms.providers.ProviderConfiguration)
	 */
	public void configure(ProviderConfiguration conf)
			throws InvalidProviderConfiguration, RMSException, Throwable {
		super.configure(conf);
		String connString;
		String userName;
		String pass;
		// synchronize with collector
		synchronized(fCollectionLock) {
			Configuration custom = (Configuration)conf.getCustom();
			if(custom != null) {
				String driverClass = custom.getString(Configuration.DRIVER_CLASS).trim();
				try {
					Class.forName(driverClass);
				} catch(ClassNotFoundException e) {
					// TODO localize
					throw new RMSException("Driver class " + driverClass + " was not found. Check your classpath.");
				}
				fConnectionString = custom.getString(Configuration.CONNECTION_STRING).trim();
				fSQLQuery = custom.getString(Configuration.SQL_QUERY).trim();
				fUsername = custom.getString(Configuration.USERNAME).trim();
				fPassword = custom.getString(Configuration.PASSWORD).trim();
				fDriver = DriverManager.getDriver(fConnectionString);
			}
			connString = fConnectionString;
			userName = fUsername;
			pass = fPassword;
		}
		if(logger.isTraceEnabled()) {
			PrintWriter w = new PrintWriter(new OutputStreamWriter(System.out));
			DriverManager.setLogWriter(w);
		}

		// see if we can get a connection and convert SQLExceptions into RMS ones
		// to avoid being reported as system errors
		try {
			Connection conn = DriverManager.getConnection(connString, userName, pass);
			conn.close();
		} catch(SQLException e) {
			// TODO localize
			throw new RMSException("Failed to establish connection.", e);
		}
	}

	/**
	 * @see com.ixora.rms.providers.Provider#collectData()
	 */
	public void collectData() throws Throwable {
		String sql;
		Statement stat = null;
		ResultSet resultSet = null;
		CachedConnection connection = null;
		int columnCount;
		List<String[]> rows = new LinkedList<String[]>();

		try {
			// synch with configure()
			synchronized(fCollectionLock) {
				connection = fConnectionManager.getConnection(fDriver, fConnectionString, fUsername, fPassword);
				sql = fSQLQuery;
			}
			stat = connection.getSQLConnection().createStatement();
			resultSet = stat.executeQuery(sql);
			// make table
			ResultSetMetaData metaData = resultSet.getMetaData();
			columnCount = metaData.getColumnCount();
			while(resultSet.next()) {
				String[] row = new String[columnCount];
				for(int i = 1; i <= columnCount; i++) {
					Object obj = resultSet.getObject(i);
					row[i-1] = objectToString(obj, metaData.getColumnType(i));
					rows.add(row);
				}
			}
		} finally {
			// release db resources as quickly as possible
			if(resultSet != null) {
				try {
					resultSet.close();
				} catch(Exception e) {
					logger.error("Failed to close result set", e);
				}
			}
			if(stat != null) {
				try {
					stat.close();
				} catch(Exception e) {
					logger.error("Failed to close statement", e);
				}
			}
			if(connection != null) {
				try {
					connection.close();
				} catch(Exception e) {
					logger.error("Failed to close connection", e);
				}
			}
		}

		// send data
		String[][] data = rows.toArray(new String[rows.size()][columnCount]);
		fireData(data);
	}

	/**
	 * @see com.ixora.rms.providers.Provider#cleanup()
	 */
	public void cleanup() {
	}

	/**
	 * Returns a string representation of the returned column
	 */
	private String objectToString(Object obj, int columnType) {
        if (obj == null)
            return "";

        switch (columnType) {
            // Hex string out of binary buffer
            case Types.VARBINARY:{
                StringBuffer sb = new StringBuffer();
                try {
	                byte[] bytes = (byte[])obj;
	                for (byte b : bytes) {
	                    short ub = Utils.ubyte(b);
	                    if (ub >=0 && ub < 16) {// one digit: make it 0X
	                        sb.append("0"+Integer.toHexString(ub).toUpperCase());
	                    } else {// already two digits
	                        sb.append(Integer.toHexString(ub).toUpperCase());
	                    }
	                }
	                return sb.toString();
                } catch (ClassCastException e) {
                    return "";
                }
            }
            // Time types
            case Types.TIME:
            case Types.TIMESTAMP:{
                Timestamp ts = (Timestamp)obj;
                return String.valueOf(ts.getTime());
            }

        	// Double types
            case Types.BIGINT:
            case Types.BIT:
            case Types.BOOLEAN:
            case Types.CHAR:
            case Types.DATE:
            case Types.DECIMAL:
            case Types.DOUBLE:
            case Types.FLOAT:
            case Types.INTEGER:
            case Types.NUMERIC:
            case Types.REAL:
            case Types.SMALLINT:
            case Types.TINYINT:
            // String types
            case Types.LONGVARCHAR:
            case Types.VARCHAR:
            // Unsupported types
            case Types.BINARY:
            case Types.BLOB:
            case Types.CLOB:
            case Types.DATALINK:
            case Types.DISTINCT:
            case Types.JAVA_OBJECT:
            case Types.LONGVARBINARY:
            case Types.OTHER:
            case Types.REF:
            case Types.STRUCT:
            default: {
                return obj.toString();
            }
        }
	}
}
