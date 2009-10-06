/*
 * Created on 05-Dec-2004
 */
package com.ixora.rms.agents.utils;

import com.ixora.common.utils.Utils;


/**
 * SQLAuthenticationConfiguration
 */
public abstract class SQLODBCConfiguration extends AuthenticationConfiguration {
    public static final String	JDBCSTRING = Msg.AGENT_CONFIGURATION_JDBCSTRING;
    public static final String	JDBCCLASS = Msg.AGENT_CONFIGURATION_JDBCCLASS;
    public static final String  ODBCDRIVER = Msg.AGENT_CONFIGURATION_ODBCDRIVER;

    /**
     * Regular constructor
     */
    public SQLODBCConfiguration() {
		super();
		setProperty(JDBCSTRING, TYPE_STRING, true, false); // not required
		setProperty(JDBCCLASS, TYPE_STRING, true, true); // mandatory

		// Use ODBC by default
		setString(JDBCCLASS, "sun.jdbc.odbc.JdbcOdbcDriver");

        // Make ODBC driver mandatory, but don't set a value
        setProperty(ODBCDRIVER, TYPE_STRING, true, true); // mandatory
	}

    /**
	 * Uses the configuration settings to build a JDBC connection string.
	 * Override for different types of database servers
	 * @param host
	 */
	public abstract String getConnectionString(String host) throws Throwable;

	/**
	 * Default implementation for getConnectionString: allows user
	 * to specify their own driver class and connection string, or
	 * just builds an ODBC connection string based on configuration
	 * details and a driver specified by the caller.
	 */
	protected String getDefaultConnectionString(String host, String driver) throws Throwable {

	    // Allow user to specify a different driver class
	    String driverClass = getString(JDBCCLASS);
	    Class.forName(driverClass);
	    //DriverManager.setLogStream(System.out); // TODO: debug only?

	    // Allow user to specify the whole jdbc string
	    String conn = getString(JDBCSTRING);

	    // Otherwise build a simple ODBC connection
	    if (Utils.isEmptyString(conn)) {
	        conn = "jdbc:odbc:;driver=" + driver;

		    if (!Utils.isEmptyString(host))
		        conn += ";server=" + host;

	        String user = getString(USERNAME);
		    if (!Utils.isEmptyString(user))
		        conn += ";user=" + user;

		    String password = getString(PASSWORD);
		    if (!Utils.isEmptyString(password))
		        conn += ";pwd=" + password;

		    //String database = getString(DATABASE);
		    //if (database != null)
		    //   conn += ";database=" + database;
	    }

	    return conn;
	}
}
