/*
 * Created on 18-Apr-2005
 */
package com.ixora.rms.agents.sqlserver;

import com.ixora.rms.CounterType;
import com.ixora.rms.agents.impl.Counter;

/**
 * SQLTraceCounter
 */
public class SQLTraceCounter extends Counter {
	private static final long serialVersionUID = -3703649008801608432L;
	public static final int COL_TEXTDATA = 1;
    public static final int COL_BINARYDATA = 2;
    public static final int COL_DATABASEID = 3;
    public static final int COL_TRANSACTIONID = 4;
	public static final int COL_RESERVED = 5;
    public static final int COL_NTUSERNAME = 6;
    public static final int COL_NTDOMAINNAME = 7;
    public static final int COL_CLIENTHOSTNAME = 8;
    public static final int COL_CLIENTPROCESSID = 9;
    public static final int COL_APPLICATIONNAME = 10;
    public static final int COL_LOGINNAME = 11;
    public static final int COL_SPID_SERVER = 12;
    public static final int COL_DURATION = 13;
    public static final int COL_STARTTIME = 14;
    public static final int COL_ENDTIME = 15;
    public static final int COL_READS = 16;
    public static final int COL_WRITES = 17;
    public static final int COL_CPU = 18;
    public static final int COL_PERMISSIONS = 19;
    public static final int COL_SEVERITY = 20;
    public static final int COL_EVENTSUBCLASS = 21;
    public static final int COL_OBJECTID = 22;
    public static final int COL_SUCCESS = 23;
    public static final int COL_INDEXID = 24;
    public static final int COL_INTEGERDATA = 25;
    public static final int COL_SERVERNAME = 26;
    public static final int COL_EVENTCLASS = 27;
    public static final int COL_OBJECTTYPE = 28;
    public static final int COL_NESTLEVEL = 29;
	public static final int COL_STATE = 30;
	public static final int COL_ERROR = 31;
	public static final int COL_MODE = 32;
	public static final int COL_HANDLE = 33;
	public static final int COL_OBJECTNAME = 34;
	public static final int COL_DATABASENAME = 35;
	public static final int COL_FILENAME = 36;
	public static final int COL_OBJECTOWNER = 37;
	public static final int COL_TARGETROLENAME = 38;
	public static final int COL_TARGETUSERNAME = 39;
	public static final int COL_DATABASEUSERNAME = 40;
	public static final int COL_LOGINSID = 41;
	public static final int COL_TARGETLOGINNAME = 42;
	public static final int COL_TARGETLOGINSID = 43;
	public static final int COL_COLUMNPERMISSIONSSET = 44;

	public static final int EV_TRACESTART = 0;
	public static final int EV_TRACEPAUSE = 1;
	public static final int EV_TRACERESTART = 2;
	public static final int EV_TRACEAUTOPAUSE = 3;
	public static final int EV_TRACEAUTORESTART = 4;
	public static final int EV_TRACESTOP = 5;
	public static final int EV_EVENTREQUIRED = 6;
	public static final int EV_FILTERCHANGED = 7;
	//public static final int EV_RESERVED = 8;
	//public static final int EV_RESERVED = 9;
	public static final int EV_RPC_COMPLETED = 10;
	public static final int EV_RPC_STARTING = 11;
	public static final int EV_SQL_BATCHCOMPLETED = 12;
	public static final int EV_SQL_BATCHSTARTING = 13;
	public static final int EV_CONNECT = 14;
	public static final int EV_DISCONNECT = 15;
	public static final int EV_ATTENTION = 16;
	public static final int EV_EXISTINGCONNECTION = 17;
	public static final int EV_SERVICECONTROL = 18;
	public static final int EV_DTCTRANSACTION = 19;
	public static final int EV_LOGINFAILED = 20;
	public static final int EV_EVENTLOG = 21;
	public static final int EV_ERRORLOG = 22;
	public static final int EV_LOCK_RELEASED = 23;
	public static final int EV_LOCK_ACQUIRED = 24;
	public static final int EV_LOCK_DEADLOCK = 25;
	public static final int EV_LOCK_CANCEL = 26;
	public static final int EV_LOCK_TIMEOUT = 27;
	public static final int EV_INSERT = 28;
	public static final int EV_UPDATE = 29;
	public static final int EV_DELETE = 30;
	public static final int EV_SELECT = 31;
	public static final int EV_CONNECTIONBEINGKILLED = 32;
	public static final int EV_EXCEPTION = 33;
	public static final int EV_SP_CACHEMISS = 34;
	public static final int EV_SP_CACHEINSERT = 35;
	public static final int EV_SP_CACHEREMOVE = 36;
	public static final int EV_SP_RECOMPILE = 37;
	public static final int EV_SP_CACHEHIT = 38;
	public static final int EV_SP_EXECCONTEXTHIT = 39;
	public static final int EV_SQL_STMTSTARTING = 40;
	public static final int EV_SQL_STMTCOMPLETED = 41;
	public static final int EV_SP_STARTING = 42;
	public static final int EV_SP_COMPLETED = 43;
	public static final int EV_SP_STMTSTARTING = 44;
	public static final int EV_SP_STMTCOMPLETED = 45;
	public static final int EV_OBJECT_CREATED = 46;
	public static final int EV_OBJECT_DELETED = 47;
	public static final int EV_OBJECT_OPENED = 48;
	public static final int EV_OBJECT_CLOSED = 49;
	public static final int EV_SQLTRANSACTION = 50;
	public static final int EV_SCAN_STARTED = 51;
	public static final int EV_SCAN_STOPPED = 52;
	public static final int EV_CURSOROPEN = 53;
	public static final int EV_TRANSACTIONLOG = 54;
	public static final int EV_HASHWARNING = 55;
	//public static final int EV_RESERVED = 56;
	//public static final int EV_RESERVED = 57;
	public static final int EV_AUTO_UPDATESTATS = 58;
	public static final int EV_LOCK_DEADLOCK_CHAIN = 59;
	public static final int EV_LOCK_ESCALATION = 60;
	public static final int EV_OLEDB_ERRORS = 61;
	public static final int EV_REPLAY_ERROR = 62;
	public static final int EV_REPLAY_ERROR_INTERNAL = 63;
	public static final int EV_REPLAY_SET_RESULT = 64;
	public static final int EV_REPLAY_ROW_RESULT = 65;
	//public static final int EV_RESERVED = 66;
	public static final int EV_EXECUTION_WARNINGS = 67;
	public static final int EV_EXECUTION_PLAN = 68;
	public static final int EV_SORT_WARNINGS = 69;
	public static final int EV_CURSORPREPARE = 70;
	public static final int EV_PREPARE_SQL = 71;
	public static final int EV_EXEC_PREPARED_SQL = 72;
	public static final int EV_UNPREPARE_SQL = 73;
	public static final int EV_CURSOREXECUTE = 74;
	public static final int EV_CURSORRECOMPILE = 75;
	public static final int EV_CURSORIMPLICITCONVERSION = 76;
	public static final int EV_CURSORUNPREPARE = 77;
	public static final int EV_CURSORCLOSE = 78;
	public static final int EV_MISSING_COLUMN_STATISTICS = 79;
	public static final int EV_MISSING_JOIN_PREDICATE = 80;
	public static final int EV_SERVER_MEMORY_CHANGE = 81;
	public static final int EV_USERCONFIGURABLE_1 = 82;
	public static final int EV_USERCONFIGURABLE_2 = 83;
	public static final int EV_USERCONFIGURABLE_3 = 84;
	public static final int EV_USERCONFIGURABLE_4 = 85;
	public static final int EV_USERCONFIGURABLE_5 = 86;
	public static final int EV_USERCONFIGURABLE_6 = 87;
	public static final int EV_USERCONFIGURABLE_7 = 88;
	public static final int EV_USERCONFIGURABLE_8 = 89;
	public static final int EV_USERCONFIGURABLE_9 = 90;
	public static final int EV_USERCONFIGURABLE_10 = 91;
	public static final int EV_DATA_FILE_AUTO_GROW = 92;
	public static final int EV_LOG_FILE_AUTO_GROW = 93;
	public static final int EV_DATA_FILE_AUTO_SHRINK = 94;
	public static final int EV_LOG_FILE_AUTO_SHRINK = 95;
	public static final int EV_SHOW_PLAN_TEXT = 96;
	public static final int EV_SHOW_PLAN_ALL = 97;
	public static final int EV_SHOW_PLAN_STATISTICS = 98;
	//public static final int EV_RESERVED = 99;
	public static final int EV_RPC_OUTPUT_PARAMETER = 100;
	//public static final int EV_RESERVED = 101;
	public static final int EV_AUDIT_STATEMENT_GDR = 102;
	public static final int EV_AUDIT_OBJECT_GDR = 103;
	public static final int EV_AUDIT_ADD_DROP_LOGIN = 104;
	public static final int EV_AUDIT_LOGIN_GDR = 105;
	public static final int EV_AUDIT_LOGIN_CHANGE_PROPERTY = 106;
	public static final int EV_AUDIT_LOGIN_CHANGE_PASSWORD = 107;
	public static final int EV_AUDIT_ADD_LOGIN_TO_SERVER_ROLE = 108;
	public static final int EV_AUDIT_ADD_DB_USER = 109;
	public static final int EV_AUDIT_ADD_MEMBER_TO_DB = 110;
	public static final int EV_AUDIT_ADD_DROP_ROLE = 111;
	public static final int EV_APP_ROLE_PASS_CHANGE = 112;
	public static final int EV_AUDIT_STATEMENT_PERMISSION = 113;
	public static final int EV_AUDIT_OBJECT_PERMISSION = 114;
	public static final int EV_AUDIT_BACKUP_RESTORE = 115;
	public static final int EV_AUDIT_DBCC = 116;
	public static final int EV_AUDIT_CHANGE_AUDIT = 117;
	public static final int EV_AUDIT_OBJECT_DERIVED_PERMISSION = 118;


    protected Integer	sqlColumnId;

	public SQLTraceCounter(int sqlColumnId, String name) {
		super(name, name + ".desc");

		this.sqlColumnId = new Integer(sqlColumnId);

		switch (sqlColumnId) {
			// String columns (UNICODE)
			case 1: // TextData
			case 6: // NTUserName
			case 7: // NTDomainName
			case 8: // ClientHostName
			case 10: // ApplicationName
			case 11: // SQLSecurityLoginName
			case 26: // ServerName
			case 34: // ObjectName
			case 35: // DatabaseName
			case 36: // Filename
			case 38: // TargetRoleName
			case 39: // TargetUserName
			case 40: // DatabaseUserName
			case 42: // TargetLoginName
				fType = CounterType.STRING;
				fDiscreet = true;
				break;

			// Integer discreet columns (4 bytes)
			case 3: // DatabaseID
			case 4: // TransactionID
			case 9: // ClientProcessID
			case 12: // SPID Server
			case 20: // Severity
			case 21: // EventSubClass
			case 22: // ObjectID
			case 23: // Success
			case 24: // IndexID
			case 27: // EventClass
			case 28: // ObjectType
			case 29: // NestLevel
			case 30: // State
			case 31: // Error
			case 32: // Mode
			case 33: // Handle
			case 37: // ObjectOwner
				fType = CounterType.LONG;
				fDiscreet = true;
				break;

			// Integer continuous columns (4 bytes)
			case 25: // IntegerData
			case 18: // CPU
				fType = CounterType.LONG;
				fDiscreet = false;
				break;

			// Long columns (8 bytes)
			case 13: // Duration
			case 16: // Reads
			case 17: // Writes
				fType = CounterType.LONG;
				fDiscreet = false;
				break;

			// Time columns (16 bytes)
			case 14: // StartTime
			case 15: // EndTime
				fType = CounterType.LONG;
				fDiscreet = false;
				break;

			// Unknown columns
			default:
			// 2 - BinaryData
			// 5 - Reserved
			// 19 - Permissions
			// 41 - LoginSID
			// 43 - TargetLoginSID
			// 44 - ColumnPermissionsSet
				fType = CounterType.LONG;
				fDiscreet = true;
				break;
		}
	}
}
