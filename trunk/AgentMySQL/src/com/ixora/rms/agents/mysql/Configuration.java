/*
 * Created on 26-Dec-2005
 */
package com.ixora.rms.agents.mysql;

import com.ixora.common.utils.Utils;
import com.ixora.rms.agents.AgentCustomConfiguration;

/**
 * Configuration
 */
public class Configuration extends AgentCustomConfiguration {
	private static final long serialVersionUID = -1139748984286868407L;
	public static final String USERNAME = Msg.USERNAME;
	public static final String PASSWORD = Msg.PASSWORD;
	public static final String PORT = Msg.PORT;
	public static final String JDBC_DRIVER_JAR_NAME = Msg.JDBC_DRIVER_JAR_NAME;
	public static final String DATABASE = Msg.DATABASE;
	public static final String JDBC_DRIVER_CLASS_NAME = Msg.JDBC_DRIVER_CLASS_NAME;

	/**
	 * Empty constructor. Required to allow the associated panel
	 * to create an empty configuration instance for editing.
	 */
	public Configuration(){
		super();
		setProperty(USERNAME, TYPE_STRING, true, false);
		setString(USERNAME, "root");

		setProperty(PASSWORD, TYPE_SECURE_STRING, true, false);
		setString(PASSWORD, "password");

		setProperty(PORT, TYPE_INTEGER, true);
		setInt(PORT, 3306);

		setProperty(JDBC_DRIVER_JAR_NAME, TYPE_STRING, true, true);

		setProperty(DATABASE, TYPE_STRING, true, true);
		setString(DATABASE, "mysql");

		setProperty(JDBC_DRIVER_CLASS_NAME, TYPE_STRING, true, true);
		setString(JDBC_DRIVER_CLASS_NAME, "com.mysql.jdbc.Driver");
	}

	/**
	 * @see com.ixora.common.plugin.PluginDescriptor#getClasspath()
	 */
	public String getClasspath() {
		String jdbcDriverPath = getString(JDBC_DRIVER_JAR_NAME);
		if(jdbcDriverPath.startsWith("\\") || jdbcDriverPath.startsWith("/")) {
			jdbcDriverPath = jdbcDriverPath.substring(1);
		}
		return Utils.getPath("/" + jdbcDriverPath);
	}

	public String getConnectionString(String host) {
		return "jdbc:mysql://" + host + ":" + getInt(PORT) + "/" + getString(DATABASE);
	}
}
