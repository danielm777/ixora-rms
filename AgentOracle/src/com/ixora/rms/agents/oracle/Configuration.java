/*
 * Created on 27-Oct-2004
 */
package com.ixora.rms.agents.oracle;

import com.ixora.rms.agents.utils.AuthenticationAndPortConfiguration;

/**
 * Configuration
 * @author Cristian Costache
 * @author Daniel Moraru
 */
public class Configuration extends AuthenticationAndPortConfiguration {
	public static final String SID = Msg.ORACLE_CONFIG_SID;
	public static final String JDBC_DRIVER_CLASS = Msg.ORACLE_CONFIG_JDBC_DRIVER_CLASS;
	public static final String CLASSPATH = Msg.ORACLE_CONFIG_CLASSPATH;

	/**
	 * Empty constructor. Required to allow the associated panel
	 * to create an empty configuration instance for editing.
	 */
	public Configuration() {
		super();
		setProperty(SID, TYPE_STRING, true, true);
		setProperty(JDBC_DRIVER_CLASS, TYPE_STRING, true, true);
		setProperty(CLASSPATH, TYPE_STRING, true, true);

		setString(USERNAME, "system");
		setString(PASSWORD, "password");
		setInt(PORT, 1521);
		setString(SID, "orcl");
		setString(JDBC_DRIVER_CLASS, "oracle.jdbc.driver.OracleDriver");
		setString(CLASSPATH, "/lib/ojdbc14.jar");
	}
}
