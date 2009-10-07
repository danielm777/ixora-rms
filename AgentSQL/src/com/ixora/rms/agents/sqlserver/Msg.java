/*
 * Created on 27-Dec-2003
 */
package com.ixora.rms.agents.sqlserver;

/**
 * @author Cristian Costache
 */
public interface Msg
{
    public static final String SQLSERVERAGENT_CONFIGURATION_SQL_INSTANCE =
        "config.sql_instance";
	public static final String SQLSERVERAGENT_CONFIGURATION_EVENT_CURSORS =
	    "config.cursors";
	public static final String SQLSERVERAGENT_CONFIGURATION_EVENT_DATABASE =
	    "config.database";
	public static final String SQLSERVERAGENT_CONFIGURATION_EVENT_ERRORS =
	    "config.errors";
	public static final String SQLSERVERAGENT_CONFIGURATION_EVENT_LOCKS =
	    "config.locks";
	public static final String SQLSERVERAGENT_CONFIGURATION_EVENT_SCANS =
	    "config.scans";
	public static final String SQLSERVERAGENT_CONFIGURATION_EVENT_SECURITY =
	    "config.security";
	public static final String SQLSERVERAGENT_CONFIGURATION_EVENT_SESSIONS =
	    "config.sessions";
	public static final String SQLSERVERAGENT_CONFIGURATION_EVENT_STORED_PROCEDURES =
	    "config.storedprocedures";
	public static final String SQLSERVERAGENT_CONFIGURATION_EVENT_TRANSACTIONS =
	    "config.transactions";
	public static final String SQLSERVERAGENT_CONFIGURATION_EVENT_TSQL =
	    "config.tsql";

// entities
	public static final String SQLSERVERAGENT_ENTITY_EVENT_CURSORS =
		"ev_cursors";
	public static final String SQLSERVERAGENT_ENTITY_EVENT_DATABASE =
		"ev_database";
	public static final String SQLSERVERAGENT_ENTITY_EVENT_ERRORS =
		"ev_errors";
	public static final String SQLSERVERAGENT_ENTITY_EVENT_LOCKS =
		"ev_locks";
	public static final String SQLSERVERAGENT_ENTITY_EVENT_SCANS =
		"ev_scans";
	public static final String SQLSERVERAGENT_ENTITY_EVENT_SECURITY =
		"ev_security";
	public static final String SQLSERVERAGENT_ENTITY_EVENT_SESSIONS =
		"ev_sessions";
	public static final String SQLSERVERAGENT_ENTITY_EVENT_STORED_PROCEDURES =
		"ev_sps";
	public static final String SQLSERVERAGENT_ENTITY_EVENT_TRANSACTIONS =
		"ev_transactions";
	public static final String SQLSERVERAGENT_ENTITY_EVENT_TSQL =
		"ev_tsql";

// agent entries
	public static final String SQLSERVERAGENT_ENTITY_TRACE =
		"trace";

	public static final String SQLSERVERAGENT_COUNTER_TRACE_TEXTDATA =
		"text_data";
	public static final String SQLSERVERAGENT_COUNTER_TRACE_BINARYDATA =
		"binary_data";
	public static final String SQLSERVERAGENT_COUNTER_TRACE_DATABASEID =
		"database_id";
	public static final String SQLSERVERAGENT_COUNTER_TRACE_TRANSACTIONID =
		"transaction_id";

	// public static final String SQLSERVERAGENT_COUNTER_TRACE_RESERVED =

	public static final String SQLSERVERAGENT_COUNTER_TRACE_NTUSERNAME =
		"nt_user_name";
	public static final String SQLSERVERAGENT_COUNTER_TRACE_NTDOMAINNAME =
		"nt_domain_name";
	public static final String SQLSERVERAGENT_COUNTER_TRACE_CLIENTHOSTNAME =
		"client_host_name";
	public static final String SQLSERVERAGENT_COUNTER_TRACE_CLIENTPROCESSID =
		"client_process_id";
	public static final String SQLSERVERAGENT_COUNTER_TRACE_APPLICATIONNAME =
		"application_name";
	public static final String SQLSERVERAGENT_COUNTER_TRACE_SQLSECURITYLOGINNAME =
		"sql_security_login_name";
	public static final String SQLSERVERAGENT_COUNTER_TRACE_SPID_SERVER =
		"spid_server";
	public static final String SQLSERVERAGENT_COUNTER_TRACE_DURATION =
		"duration";
	public static final String SQLSERVERAGENT_COUNTER_TRACE_STARTTIME =
		"start_time";
	public static final String SQLSERVERAGENT_COUNTER_TRACE_ENDTIME =
		"end_time";
	public static final String SQLSERVERAGENT_COUNTER_TRACE_READS =
		"reads";
	public static final String SQLSERVERAGENT_COUNTER_TRACE_WRITES =
		"writes";
	public static final String SQLSERVERAGENT_COUNTER_TRACE_CPU =
		"cpu";
	public static final String SQLSERVERAGENT_COUNTER_TRACE_PERMISSIONS =
		"permissions";
	public static final String SQLSERVERAGENT_COUNTER_TRACE_SEVERITY =
		"severity";
	public static final String SQLSERVERAGENT_COUNTER_TRACE_EVENTSUBCLASS =
		"event_subclass";
	public static final String SQLSERVERAGENT_COUNTER_TRACE_OBJECTID =
		"object_id";
	public static final String SQLSERVERAGENT_COUNTER_TRACE_SUCCESS =
		"success";
	public static final String SQLSERVERAGENT_COUNTER_TRACE_INDEXID =
		"index_id";
	public static final String SQLSERVERAGENT_COUNTER_TRACE_INTEGERDATA =
		"integer_data";
	public static final String SQLSERVERAGENT_COUNTER_TRACE_SERVERNAME =
		"server_name";
	public static final String SQLSERVERAGENT_COUNTER_TRACE_EVENTCLASS =
		"event_class";
	public static final String SQLSERVERAGENT_COUNTER_TRACE_OBJECTTYPE =
		"object_type";
	public static final String SQLSERVERAGENT_COUNTER_TRACE_NESTLEVEL =
		"nest_level";
	public static final String SQLSERVERAGENT_COUNTER_TRACE_STATE =
		"state";
	public static final String SQLSERVERAGENT_COUNTER_TRACE_ERROR =
		"error";
	public static final String SQLSERVERAGENT_COUNTER_TRACE_MODE =
		"mode";
	public static final String SQLSERVERAGENT_COUNTER_TRACE_HANDLE =
		"handle";
	public static final String SQLSERVERAGENT_COUNTER_TRACE_OBJECTNAME =
		"object_name";
	public static final String SQLSERVERAGENT_COUNTER_TRACE_DATABASENAME =
		"database_name";
	public static final String SQLSERVERAGENT_COUNTER_TRACE_FILENAME =
		"file_name";
	public static final String SQLSERVERAGENT_COUNTER_TRACE_OBJECTOWNER =
		"object_owner";
	public static final String SQLSERVERAGENT_COUNTER_TRACE_TARGETROLENAME =
		"target_role_name";
	public static final String SQLSERVERAGENT_COUNTER_TRACE_TARGETUSERNAME =
		"target_user_name";
	public static final String SQLSERVERAGENT_COUNTER_TRACE_DATABASEUSERNAME =
		"database_user_name";
	public static final String SQLSERVERAGENT_COUNTER_TRACE_LOGINSID =
		"login_sid";
	public static final String SQLSERVERAGENT_COUNTER_TRACE_TARGETLOGINNAME =
		"target_login_name";
	public static final String SQLSERVERAGENT_COUNTER_TRACE_TARGETLOGINSID =
		"target_login_sid";
	public static final String SQLSERVERAGENT_COUNTER_TRACE_COLUMNPERMISSIONSSET =
		"column_permissions_set";

	// Cursors counters
	public static final String COUNTER_CURSORS_CLOSED = "c_closed";
	public static final String COUNTER_CURSORS_EXECUTED = "c_executed";
	public static final String COUNTER_CURSORS_IMPLICIT_CONVERTED = "c_implicit_converted";
	public static final String COUNTER_CURSORS_OPENED = "c_opened";
	public static final String COUNTER_CURSORS_PREPARED = "c_prepared";
	public static final String COUNTER_CURSORS_RECOMPILED = "c_recompiled";
	public static final String COUNTER_CURSORS_UNPREPARED = "c_unprepared";

	// Database counters
	public static final String COUNTER_DB_DATA_GROWS = "db_data_grows";
	public static final String COUNTER_DB_DATA_GROWS_DURATION = "db_data_grows_duration";
	public static final String COUNTER_DB_DATA_GROWS_SIZE = "db_data_grows_size";
	public static final String COUNTER_DB_DATA_SHRINKS = "db_data_shrinks";
	public static final String COUNTER_DB_DATA_SHRINKS_DURATION = "db_data_shrinks_duration";
	public static final String COUNTER_DB_DATA_SHRINKS_SIZE = "db_data_shrinks_size";
	public static final String COUNTER_DB_LOG_GROWS = "db_log_grows";
	public static final String COUNTER_DB_LOG_GROWS_DURATION = "db_log_grows_duration";
	public static final String COUNTER_DB_LOG_SIZE = "db_log_grows_size";
	public static final String COUNTER_DB_LOG_SHRINKS = "db_log_shrinks";
	public static final String COUNTER_DB_LOG_SHRINKS_DURATION = "db_log_shrinks_duration";
	public static final String COUNTER_DB_LOG_SHRINKS_SIZE = "db_log_shrinks_size";

	// Errors counters
	public static final String COUNTER_ERRORS_ATTENTION = "err_attention";
	public static final String COUNTER_ERRORS_ERRORLOG = "err_errorlog";
	public static final String COUNTER_ERRORS_EVENTLOG = "err_eventlog";
	public static final String COUNTER_ERRORS_EXCEPTIONS = "err_exceptions";
	public static final String COUNTER_ERRORS_QUERY_WAIT = "err_query_wait";
	public static final String COUNTER_ERRORS_QUERY_TIMEOUT = "err_query_timeout";
	public static final String COUNTER_ERRORS_HASH_RECURSION = "err_hash_recursion";
	public static final String COUNTER_ERRORS_HASH_BAIL = "err_hash_bail";
	public static final String COUNTER_ERRORS_MISSING_STATISTICS = "err_miss_stats";
	public static final String COUNTER_ERRORS_MISSING_JOIN_PREDICATE = "err_miss_join";
	public static final String COUNTER_ERRORS_OLEDB = "err_oledb";
	public static final String COUNTER_ERRORS_SORT_SINGLE = "err_sort_single";
	public static final String COUNTER_ERRORS_SORT_MULTI = "err_sort_multi";

	// Locks counters
	public static final String COUNTER_LOCKS_ACQUIRED = "locks_acquired";
	public static final String COUNTER_LOCKS_CANCELED = "locks_canceled";
	public static final String COUNTER_LOCK_DEADLOCKS = "locks_deadlocks";
	public static final String COUNTER_LOCKS_ESCALATED = "locks_escalated";
	public static final String COUNTER_LOCKS_RELEASED = "locks_released";
	public static final String COUNTER_LOCKS_RELEASED_DURATION = "locks_released_duration";
	public static final String COUNTER_LOCKS_TIMEOUT = "locks_timeout";

	// Scans counters
	public static final String COUNTER_SCANS_COUNT = "scans_count";
	public static final String COUNTER_SCANS_DURATION = "scans_duration";
	public static final String COUNTER_SCANS_READS = "scans_reads";

	// Transaction counters
	public static final String COUNTER_TXN_BEGIN = "txn_begin";
	public static final String COUNTER_TXN_COMMIT = "txn_commit";
	public static final String COUNTER_TXN_DURATION = "txn_duration";
	public static final String COUNTER_TXN_ROLLBACK = "txn_rollback";
	public static final String COUNTER_TXN_SAVEPOINTS = "txn_savepoints";
}
