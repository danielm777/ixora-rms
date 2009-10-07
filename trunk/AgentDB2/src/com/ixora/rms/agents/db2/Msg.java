/*
 * Created on 17-Oct-2004
 */
package com.ixora.rms.agents.db2;

/**
 * Msg
 */

public class Msg {
	public static final String DB2_NAME = "agents.db2";

	public static final String DB2_INSTANCE = "config.instance";
	public static final String DB2_DATABASE = "config.database";

    public static final String DB2_UOW_SW = "config.uow.switch";
	public static final String DB2_STATEMENT_SW = "config.statement.switch";
	public static final String DB2_TABLE_SW = "config.table.switch";
	public static final String DB2_BUFFER_POOL_SW = "config.buffer_pool.switch";
	public static final String DB2_LOCK_SW = "config.lock.switch";
	public static final String DB2_SORT_SW = "config.sort.switch";
    public static final String AGG_ALL_NODES = "config.agg_all_nodes";
	public static final String DB2_TIMESTAMP_SW = "config.timestamp.switch";

	public static final String DB2_ERROR_INTERNAL = "db2.error.internal_agent_error";
	public static final String DB2_ERROR_NOT_SUPPORTED = "db2.error.not_supported";
	public static final String DB2_ERROR_NATIVE_ERROR = "db2.error.native";
}
