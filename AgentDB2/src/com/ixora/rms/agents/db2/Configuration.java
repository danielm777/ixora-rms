/*
 * Created on 27-Oct-2004
 */
package com.ixora.rms.agents.db2;

import com.ixora.rms.agents.utils.AuthenticationConfiguration;

/**
 * Configuration
 */

public class Configuration extends AuthenticationConfiguration {
	private static final long serialVersionUID = 1L;
	// value keys
	public static final String INSTANCE = Msg.DB2_INSTANCE;
	public static final String DATABASE = Msg.DB2_DATABASE;
	public static final String UOW_SW = Msg.DB2_UOW_SW;
	public static final String STATEMENT_SW = Msg.DB2_STATEMENT_SW;
	public static final String TABLE_SW = Msg.DB2_TABLE_SW;
	public static final String BUFFER_POOL_SW = Msg.DB2_BUFFER_POOL_SW;
	public static final String LOCK_SW = Msg.DB2_LOCK_SW;
	public static final String SORT_SW = Msg.DB2_SORT_SW;
	public static final String TIMESTAMP_SW = Msg.DB2_TIMESTAMP_SW;
    public static final String AGG_ALL_NODES = Msg.AGG_ALL_NODES;

	/**
	 * Empty constructor. Required to allow the associated panel
	 * to create an empty configuration instance for editing.
	 */
	public Configuration() {
		super();
		setString(USERNAME, "db2admin");
		setString(PASSWORD, "db2admin");

		setProperty(INSTANCE, TYPE_STRING, true);
		setString(INSTANCE, "db2");

		setProperty(DATABASE, TYPE_STRING, true, false);
		setString(DATABASE, "toolsdb");

		setProperty(UOW_SW, TYPE_BOOLEAN, true, false);
		setBoolean(UOW_SW, true);

		setProperty(STATEMENT_SW, TYPE_BOOLEAN, true, false);
		setBoolean(STATEMENT_SW, true);

		setProperty(TABLE_SW, TYPE_BOOLEAN, true, false);
		setBoolean(TABLE_SW, true);

		setProperty(BUFFER_POOL_SW, TYPE_BOOLEAN, true, false);
		setBoolean(BUFFER_POOL_SW, true);

		setProperty(LOCK_SW, TYPE_BOOLEAN, true, false);
		setBoolean(LOCK_SW, true);

		setProperty(SORT_SW, TYPE_BOOLEAN, true, false);
		setBoolean(SORT_SW, true);

		setProperty(TIMESTAMP_SW, TYPE_BOOLEAN, true, false);
		setBoolean(TIMESTAMP_SW, true);

		setProperty(AGG_ALL_NODES, TYPE_BOOLEAN, true);
		setBoolean(AGG_ALL_NODES, false);

	}

}
