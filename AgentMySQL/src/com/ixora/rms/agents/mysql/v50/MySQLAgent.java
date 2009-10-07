/**
 * 26-Dec-2005
 */
package com.ixora.rms.agents.mysql.v50;

import com.ixora.rms.EntityId;
import com.ixora.rms.agents.AgentId;
import com.ixora.rms.agents.mysql.Msg;
import com.ixora.rms.agents.mysql.MySQLAgentForVersion;
import com.ixora.rms.agents.mysql.MySQLEntity;

/**
 * @author Daniel Moraru
 */
public class MySQLAgent extends MySQLAgentForVersion {

	/**
	 * @param agentId
	 * @param listener
	 * @throws Throwable
	 */
	public MySQLAgent(AgentId agentId, Listener listener) throws Throwable {
		super(agentId, listener);
		MySQLEntity general = new MySQLEntity(
				new EntityId(fRootEntity.getId(), Msg.GENERAL),
				fContext,
				new String[]{
					Msg.OPEN_FILES,
					Msg.OPEN_STREAMS,
					Msg.OPEN_TABLES,
					Msg.OPENED_TABLES,
					Msg.QUESTIONS,
					Msg.UPTIME
				});
		fRootEntity.addChildEntity(general);
		MySQLEntity generalThreads = new MySQLEntity(
				new EntityId(general.getId(), Msg.THREADS),
				fContext,
				new String[]{
					Msg.THREADS_CACHED,
					Msg.THREADS_CONNECTED,
					Msg.THREADS_CREATED,
					Msg.THREADS_RUNNING,
				});
		general.addChildEntity(generalThreads);
		MySQLEntity generalTemporary = new MySQLEntity(
				new EntityId(general.getId(), Msg.TEMPORARY),
				fContext,
				new String[]{
					Msg.CREATED_TMP_DISK_TABLES,
					Msg.CREATED_TMP_FILES,
					Msg.CREATED_TMP_TABLES,
				});
		general.addChildEntity(generalTemporary);


		MySQLEntity performance = new MySQLEntity(
				new EntityId(fRootEntity.getId(), Msg.PERFORMANCE),
				fContext,
				new String[]{
					Msg.SLOW_LAUNCH_THREADS,
					Msg.SLOW_QUERIES,
					Msg.LAST_QUERY_COST
				});
		fRootEntity.addChildEntity(performance);
		MySQLEntity performanceQueryCache = new MySQLEntity(
				new EntityId(performance.getId(), Msg.QUERY_CACHE),
				fContext,
				new String[]{
					Msg.QCACHE_FREE_BLOCKS,
					Msg.QCACHE_FREE_MEMORY,
					Msg.QCACHE_HITS,
					Msg.QCACHE_INSERTS,
					Msg.QCACHE_LOWMEM_PRUNES,
					Msg.QCACHE_NOT_CACHED,
					Msg.QCACHE_QUERIES_IN_CACHE,
					Msg.QCACHE_TOTAL_BLOCKS
				});
		performance.addChildEntity(performanceQueryCache);
		MySQLEntity performanceKeys = new MySQLEntity(
				new EntityId(performance.getId(), Msg.KEYS),
				fContext,
				new String[]{
					Msg.KEY_BLOCKS_NOT_FLUSHED,
					Msg.KEY_BLOCKS_USED,
					Msg.KEY_READ_REQUESTS,
					Msg.KEY_READS,
					Msg.KEY_WRITE_REQUESTS,
					Msg.KEY_WRITES,
					Msg.KEY_BLOCKS_UNUSED
				});
		performance.addChildEntity(performanceKeys);
		MySQLEntity performanceSort = new MySQLEntity(
				new EntityId(performance.getId(), Msg.SORT),
				fContext,
				new String[]{
					Msg.SORT_MERGE_PASSES,
					Msg.SORT_RANGE,
					Msg.SORT_ROWS,
					Msg.SORT_SCAN,
				});
		performance.addChildEntity(performanceSort);
		MySQLEntity performanceDelayed = new MySQLEntity(
				new EntityId(performance.getId(), Msg.DELAYED),
				fContext,
				new String[]{
					Msg.DELAYED_ERRORS,
					Msg.DELAYED_INSERT_THREADS,
					Msg.DELAYED_WRITES,
					Msg.NOT_FLUSHED_DELAYED_ROWS
				});
		performance.addChildEntity(performanceDelayed);
		MySQLEntity performanceSelects = new MySQLEntity(
				new EntityId(performance.getId(), Msg.SELECTS),
				fContext,
				new String[]{
					Msg.SELECT_FULL_JOIN,
					Msg.SELECT_FULL_RANGE_JOIN,
					Msg.SELECT_RANGE,
					Msg.SELECT_RANGE_CHECK,
					Msg.SELECT_SCAN
				});
		performance.addChildEntity(performanceSelects);
		MySQLEntity performanceLocks = new MySQLEntity(
				new EntityId(performance.getId(), Msg.LOCKS),
				fContext,
				new String[]{
					Msg.TABLE_LOCKS_IMMEDIATE,
					Msg.TABLE_LOCKS_WAITED,
				});
		performance.addChildEntity(performanceLocks);
		MySQLEntity performanceInnoDB = new MySQLEntity(
				new EntityId(performance.getId(), Msg.INNO_DB),
				fContext,
				new String[]{
					Msg.INNODB_BUFFER_POOL_PAGES_DATA,
					Msg.INNODB_BUFFER_POOL_PAGES_DIRTY,
					Msg.INNODB_BUFFER_POOL_PAGES_FLUSHED,
					Msg.INNODB_BUFFER_POOL_PAGES_FREE,
					Msg.INNODB_BUFFER_POOL_PAGES_LATCHED,
					Msg.INNODB_BUFFER_POOL_PAGES_MISC,
					Msg.INNODB_BUFFER_POOL_PAGES_TOTAL,
					Msg.INNODB_BUFFER_POOL_READ_AHEAD_RND,
					Msg.INNODB_BUFFER_POOL_READ_AHEAD_SEQ,
					Msg.INNODB_BUFFER_POOL_READ_REQUESTS,
					Msg.INNODB_BUFFER_POOL_READS,
					Msg.INNODB_BUFFER_POOL_WAIT_FREE,
					Msg.INNODB_DATA_FSYNCS,
					Msg.INNODB_DATA_PENDING_FSYNCS,
					Msg.INNODB_DATA_PENDING_READS,
					Msg.INNODB_DATA_PENDING_WRITES,
					Msg.INNODB_DATA_READ,
					Msg.INNODB_DATA_READS,
					Msg.INNODB_DATA_WRITES,
					Msg.INNODB_DATA_WRITTEN,
					Msg.INNODB_DBLWR_PAGES_WRITTEN,
					Msg.INNODB_DBLWR_WRITES,
					Msg.INNODB_LOG_WAITS,
					Msg.INNODB_OS_LOG_FSYNCS,
					Msg.INNODB_OS_LOG_PENDING_FSYNCS,
					Msg.INNODB_OS_LOG_PENDING_WRITES,
					Msg.INNODB_OS_LOG_WRITTEN,
					Msg.INNODB_PAGE_SIZE,
					Msg.INNODB_PAGE_SIZE,
					Msg.INNODB_PAGES_CREATED,
					Msg.INNODB_PAGES_READ,
					Msg.INNODB_PAGES_WRITTEN,
					Msg.INNODB_ROW_LOCK_CURRENT_WAITS,
					Msg.INNODB_ROW_LOCK_TIME,
					Msg.INNODB_ROW_LOCK_TIME_AVG,
					Msg.INNODB_ROW_LOCK_TIME_MAX,
					Msg.INNODB_ROW_LOCK_WAITS,
					Msg.INNODB_ROWS_DELETED,
					Msg.INNODB_ROWS_INSERTED,
					Msg.INNODB_ROWS_READ,
					Msg.INNODB_ROWS_UPDATED
				});
		performance.addChildEntity(performanceInnoDB);
		MySQLEntity performanceBinLogCache = new MySQLEntity(
				new EntityId(performance.getId(), Msg.BIN_LOG_CACHE),
				fContext,
				new String[]{
					Msg.BINLOG_CACHE_DISK_USE,
					Msg.BINLOG_CACHE_USE,
				});
		performance.addChildEntity(performanceBinLogCache);
		MySQLEntity performanceTcLog = new MySQLEntity(
				new EntityId(performance.getId(), Msg.TC_LOG),
				fContext,
				new String[]{
					Msg.TC_LOG_MAX_PAGES_USED,
					Msg.TC_LOG_PAGE_SIZE,
					Msg.TC_LOG_PAGE_WAITS
				});
		performance.addChildEntity(performanceTcLog);

		MySQLEntity networking = new MySQLEntity(
				new EntityId(fRootEntity.getId(), Msg.NETWORKING),
				fContext,
				new String[]{
					Msg.ABORTED_CLIENTS,
					Msg.ABORTED_CONNECTS,
					Msg.CONNECTIONS,
					Msg.MAX_USED_CONNECTIONS
				});
		fRootEntity.addChildEntity(networking);
		MySQLEntity networkingTraffic = new MySQLEntity(
				new EntityId(networking.getId(), Msg.TRAFFIC),
				fContext,
				new String[]{
					Msg.BYTES_RECEIVED,
					Msg.BYTES_SENT
				});
		networking.addChildEntity(networkingTraffic);
		MySQLEntity networkingReplication = new MySQLEntity(
				new EntityId(networking.getId(), Msg.REPLICATION),
				fContext,
				new String[]{
					Msg.RPL_STATUS,
					Msg.SLAVE_OPEN_TEMP_TABLES,
					Msg.SLAVE_RUNNING,
					Msg.SLAVE_RETRIED_TRANSACTIONS
				});
		networking.addChildEntity(networkingReplication);
		MySQLEntity commandsExec = new MySQLEntity(
				new EntityId(fRootEntity.getId(), Msg.COMMANDS_EXECUTED),
				fContext,
				new String[]{
					Msg.COM_ADMIN_COMMANDS,
					Msg.COM_CHANGE_DB,
					Msg.COM_DROP_USER,
					Msg.COM_FLUSH,
					Msg.COM_GRANT,
					Msg.COM_HA_CLOSE,
					Msg.COM_HA_OPEN,
					Msg.COM_HA_READ,
					Msg.COM_HELP,
					Msg.COM_KILL,
					Msg.COM_LOCK_TABLES,
					Msg.COM_PRELOAD_KEYS,
					Msg.COM_PURGE,
					Msg.COM_PURGE_BEFORE_DATE,
					Msg.COM_RESET,
					Msg.COM_REVOKE,
					Msg.COM_REVOKE_ALL,
					Msg.COM_SAVEPOINT,
					Msg.COM_SET_OPTION,
					Msg.COM_UNLOCK_TABLES,
					Msg.FLUSH_COMMANDS,
					Msg.COM_DEALLOC_SQL,
					Msg.COM_EXECUTE_SQL,
					Msg.COM_PREPARE_SQL,
					Msg.COM_STMT_CLOSE,
					Msg.COM_STMT_EXECUTE,
					Msg.COM_STMT_FETCH,
					Msg.COM_STMT_PREPARE,
					Msg.COM_STMT_RESET,
					Msg.COM_STMT_SEND_LONG_DATA
				});
		fRootEntity.addChildEntity(commandsExec);
		MySQLEntity commandsExecDDL = new MySQLEntity(
				new EntityId(commandsExec.getId(), Msg.DDL),
				fContext,
				new String[]{
					Msg.COM_ALTER_DB,
					Msg.COM_ALTER_TABLE,
					Msg.COM_ANALYZE,
					Msg.COM_BACKUP_TABLE,
					Msg.COM_CHECK,
					Msg.COM_CHECKSUM,
					Msg.COM_CREATE_DB,
					Msg.COM_CREATE_FUNCTION,
					Msg.COM_CREATE_INDEX,
					Msg.COM_CREATE_TABLE,
					Msg.COM_DROP_DB,
					Msg.COM_DROP_FUNCTION,
					Msg.COM_DROP_INDEX,
					Msg.COM_DROP_TABLE,
					Msg.COM_OPTIMIZE,
					Msg.COM_RENAME_TABLE,
					Msg.COM_REPAIR,
					Msg.COM_RESTORE_TABLE,
					Msg.COM_TRUNCATE,
				});
		commandsExec.addChildEntity(commandsExecDDL);
		MySQLEntity commandsExecDML = new MySQLEntity(
				new EntityId(commandsExec.getId(), Msg.DML),
				fContext,
				new String[]{
					Msg.COM_BEGIN,
					Msg.COM_COMMIT,
					Msg.COM_DELETE,
					Msg.COM_DELETE_MULTI,
					Msg.COM_DO,
					Msg.COM_INSERT,
					Msg.COM_INSERT_SELECT,
					Msg.COM_LOAD,
					Msg.COM_REPLACE,
					Msg.COM_REPLACE_SELECT,
					Msg.COM_ROLLBACK,
					Msg.COM_SELECT,
					Msg.COM_UPDATE,
					Msg.COM_UPDATE_MULTI,
					Msg.COM_XA_COMMIT,
					Msg.COM_XA_END,
					Msg.COM_XA_PREPARE,
					Msg.COM_XA_RECOVER,
					Msg.COM_XA_ROLLBACK,
					Msg.COM_XA_START
				});
		commandsExec.addChildEntity(commandsExecDML);
		MySQLEntity commandsExecShow = new MySQLEntity(
				new EntityId(commandsExec.getId(), Msg.SHOW),
				fContext,
				new String[]{
					Msg.COM_SHOW_BINLOG_EVENTS,
					Msg.COM_SHOW_BINLOGS,
					Msg.COM_SHOW_CHARSETS,
					Msg.COM_SHOW_COLLATIONS,
					Msg.COM_SHOW_COLUMN_TYPES,
					Msg.COM_SHOW_CREATE_DB,
					Msg.COM_SHOW_CREATE_TABLE,
					Msg.COM_SHOW_DATABASES,
					Msg.COM_SHOW_ERRORS,
					Msg.COM_SHOW_FIELDS,
					Msg.COM_SHOW_GRANTS,
					Msg.COM_SHOW_INNODB_STATUS,
					Msg.COM_SHOW_KEYS,
					Msg.COM_SHOW_LOGS,
					Msg.COM_SHOW_MASTER_STATUS,
					Msg.COM_SHOW_NEW_MASTER,
					Msg.COM_SHOW_OPEN_TABLES,
					Msg.COM_SHOW_PRIVILEGES,
					Msg.COM_SHOW_PROCESSLIST,
					Msg.COM_SHOW_SLAVE_HOSTS,
					Msg.COM_SHOW_SLAVE_STATUS,
					Msg.COM_SHOW_STATUS,
					Msg.COM_SHOW_STORAGE_ENGINES,
					Msg.COM_SHOW_TABLES,
					Msg.COM_SHOW_VARIABLES,
					Msg.COM_SHOW_WARNINGS,
					Msg.COM_SHOW_NDB_STATUS,
					Msg.COM_SHOW_TRIGGERS
				});
		commandsExec.addChildEntity(commandsExecShow);
		MySQLEntity commandsExecReplication = new MySQLEntity(
				new EntityId(commandsExec.getId(), Msg.REPLICATION),
				fContext,
				new String[]{
					Msg.COM_CHANGE_MASTER,
					Msg.COM_LOAD_MASTER_DATA,
					Msg.COM_LOAD_MASTER_TABLE,
					Msg.COM_SLAVE_START,
					Msg.COM_SLAVE_STOP,
				});
		commandsExec.addChildEntity(commandsExecReplication);
		MySQLEntity misc = new MySQLEntity(
				new EntityId(fRootEntity.getId(), Msg.MISCELLANEOUS),
				fContext,
				new String[]{});
		fRootEntity.addChildEntity(misc);
		MySQLEntity miscHandler = new MySQLEntity(
				new EntityId(misc.getId(), Msg.HANDLER),
				fContext,
				new String[]{
					Msg.HANDLER_COMMIT,
					Msg.HANDLER_DELETE,
					Msg.HANDLER_READ_FIRST,
					Msg.HANDLER_READ_KEY,
					Msg.HANDLER_READ_NEXT,
					Msg.HANDLER_READ_PREV,
					Msg.HANDLER_READ_RND,
					Msg.HANDLER_READ_RND_NEXT,
					Msg.HANDLER_ROLLBACK,
					Msg.HANDLER_UPDATE,
					Msg.HANDLER_WRITE,
					Msg.HANDLER_DISCOVER,
					Msg.HANDLER_PREPARE,
					Msg.HANDLER_SAVEPOINT,
					Msg.HANDLER_SAVEPOINT_ROLLBACK
				});
		misc.addChildEntity(miscHandler);
	}
}
