/**
 * 22-Jul-2006
 */
package com.ixora.rms.logging.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.ixora.common.ConfigurationMgr;

/**
 * @author Daniel Moraru
 */
public class DatabaseLoggerUtil {

	private DatabaseLoggerUtil() {
		super();
	}

	/**
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public static Connection createConnection() throws SQLException, ClassNotFoundException {
		String driverClass = ConfigurationMgr.getString(LogComponentDB.NAME,
				LogConfigurationDBConstants.DB_JDBC_DRIVER_CLASS);
		Class.forName(driverClass);
		String connectionString = ConfigurationMgr.getString(LogComponentDB.NAME,
				LogConfigurationDBConstants.DB_CONNECTION_STRING);
		String username = ConfigurationMgr.getString(LogComponentDB.NAME,
				LogConfigurationDBConstants.DB_USERNAME);
		String password = ConfigurationMgr.getString(LogComponentDB.NAME,
				LogConfigurationDBConstants.DB_PASSWORD);
		return DriverManager.getConnection(connectionString, username, password);
	}
}
