/*
 * Created on Jan 25, 2004
 */
package com.ixora.rms.agents.sqlserver;

import com.ixora.common.utils.Utils;
import com.ixora.rms.agents.utils.SQLODBCConfiguration;


/**
 * Configuration for the SQL Server Agent.
 * @author Crisian Costache
 * @author Daniel Moraru
 */
public final class Configuration extends SQLODBCConfiguration {
	private static final long serialVersionUID = 8655856082363916967L;

	public static final String SQL_INSTANCE = Msg.SQLSERVERAGENT_CONFIGURATION_SQL_INSTANCE;

    public static final String EVENT_CURSORS = Msg.SQLSERVERAGENT_CONFIGURATION_EVENT_CURSORS;
	public static final String EVENT_DATABASE = Msg.SQLSERVERAGENT_CONFIGURATION_EVENT_DATABASE;
	public static final String EVENT_ERRORS = Msg.SQLSERVERAGENT_CONFIGURATION_EVENT_ERRORS;
	public static final String EVENT_LOCKS = Msg.SQLSERVERAGENT_CONFIGURATION_EVENT_LOCKS;
	public static final String EVENT_SCANS = Msg.SQLSERVERAGENT_CONFIGURATION_EVENT_SCANS;
	public static final String EVENT_SECURITY = Msg.SQLSERVERAGENT_CONFIGURATION_EVENT_SECURITY;
	public static final String EVENT_SESSIONS = Msg.SQLSERVERAGENT_CONFIGURATION_EVENT_SESSIONS;
	public static final String EVENT_STORED_PROCEDURES = Msg.SQLSERVERAGENT_CONFIGURATION_EVENT_STORED_PROCEDURES;
	public static final String EVENT_TRANSACTIONS = Msg.SQLSERVERAGENT_CONFIGURATION_EVENT_TRANSACTIONS;
	public static final String EVENT_TSQL = Msg.SQLSERVERAGENT_CONFIGURATION_EVENT_TSQL;

	/**
	 * Empty constructor. Required to allow the associated panel
	 * to create an empty configuration instance for editing.
	 */
	public Configuration() {
		super();
		setString(USERNAME, "sa");

        setProperty(SQL_INSTANCE, TYPE_STRING, true, false);
        setString(SQL_INSTANCE, "");

        setString(ODBCDRIVER, "{SQL Server}");

		setProperty(EVENT_CURSORS, TYPE_BOOLEAN, true, false);
		setBoolean(EVENT_CURSORS, true);

		setProperty(EVENT_DATABASE, TYPE_BOOLEAN, true, false);
		setBoolean(EVENT_DATABASE, true);

		setProperty(EVENT_ERRORS, TYPE_BOOLEAN, true, false);
		setBoolean(EVENT_ERRORS, true);

		setProperty(EVENT_LOCKS, TYPE_BOOLEAN, true, false);
		setBoolean(EVENT_LOCKS, true);

		setProperty(EVENT_SCANS, TYPE_BOOLEAN, true, false);
		setBoolean(EVENT_SCANS, true);

		setProperty(EVENT_SECURITY, TYPE_BOOLEAN, true, false);
		setBoolean(EVENT_SECURITY, true);

		setProperty(EVENT_SESSIONS, TYPE_BOOLEAN, true, false);
		setBoolean(EVENT_SESSIONS, true);

		setProperty(EVENT_STORED_PROCEDURES, TYPE_BOOLEAN, true, false);
		setBoolean(EVENT_STORED_PROCEDURES, true);

		setProperty(EVENT_TRANSACTIONS, TYPE_BOOLEAN, true, false);
		setBoolean(EVENT_TRANSACTIONS, true);

		setProperty(EVENT_TSQL, TYPE_BOOLEAN, true, false);
		setBoolean(EVENT_TSQL, true);
	}

	/**
	 * Uses the configuration settings to build a JDBC
	 * connection string.
	 * @param host
	 */
	public String getConnectionString(String host) throws Throwable {
        // Instance name, if specified, is in the form server\instance.
        // If not specified then host name is used.
        String instanceName = getString(SQL_INSTANCE);
        if (!Utils.isEmptyString(instanceName)) {
            host = instanceName;
        }
        // Driver name, if not specified then {SQL Server} is used
        String driverName = getString(ODBCDRIVER);
        if (Utils.isEmptyString(driverName)) {
            driverName = "{SQL Server}";
        }

	    return getDefaultConnectionString(host, driverName);
	}
}