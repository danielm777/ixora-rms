/*
 * Created on 21-Apr-2005
 */
package com.ixora.rms.agents.sqlserver.events;

import java.util.LinkedList;
import java.util.List;

import com.ixora.rms.EntityId;
import com.ixora.rms.agents.AgentExecutionContext;
import com.ixora.rms.agents.sqlserver.Msg;
import com.ixora.rms.agents.sqlserver.SQLTraceChildEntity;
import com.ixora.rms.agents.sqlserver.SQLTraceCounter;
import com.ixora.rms.agents.sqlserver.SqlTrace;

/**
 * SQLTraceEventSecurity
 */
public class SQLTraceEventSecurity extends SQLTraceChildEntity {
	private static final long serialVersionUID = 5727988151718544900L;

	/**
     * Default constructor
     * @param c
     */
    public SQLTraceEventSecurity(EntityId idParent, AgentExecutionContext c) {
        super(idParent, Msg.SQLSERVERAGENT_ENTITY_EVENT_SECURITY, c);
    }

    /**
     * Each entity must return a list of column IDs (as defined by SQL
     * server) so that the root entity knowns what tracing to enable.
     * @see com.ixora.rms.agents.sqlserver.SQLTraceChildEntity#getTracedCounters()
     */
    public List<TracedEventColumn> getTracedEventsAndColumns() {
        List<TracedEventColumn> retList = new LinkedList<TracedEventColumn>();
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_ADD_DB_USER, SQLTraceCounter.COL_DATABASENAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_ADD_DB_USER, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_ADD_DB_USER, SQLTraceCounter.COL_EVENTSUBCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_ADD_DB_USER, SQLTraceCounter.COL_TARGETROLENAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_ADD_DB_USER, SQLTraceCounter.COL_DATABASEUSERNAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_ADD_DB_USER, SQLTraceCounter.COL_SUCCESS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_ADD_DB_USER, SQLTraceCounter.COL_TARGETLOGINNAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_ADD_DB_USER, SQLTraceCounter.COL_TARGETLOGINSID));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_ADD_DB_USER, SQLTraceCounter.COL_TARGETUSERNAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_ADD_LOGIN_TO_SERVER_ROLE, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_ADD_LOGIN_TO_SERVER_ROLE, SQLTraceCounter.COL_EVENTSUBCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_ADD_LOGIN_TO_SERVER_ROLE, SQLTraceCounter.COL_TARGETROLENAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_ADD_LOGIN_TO_SERVER_ROLE, SQLTraceCounter.COL_SUCCESS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_ADD_LOGIN_TO_SERVER_ROLE, SQLTraceCounter.COL_TARGETLOGINNAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_ADD_LOGIN_TO_SERVER_ROLE, SQLTraceCounter.COL_TARGETLOGINSID));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_ADD_MEMBER_TO_DB, SQLTraceCounter.COL_DATABASENAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_ADD_MEMBER_TO_DB, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_ADD_MEMBER_TO_DB, SQLTraceCounter.COL_EVENTSUBCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_ADD_MEMBER_TO_DB, SQLTraceCounter.COL_DATABASEUSERNAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_ADD_MEMBER_TO_DB, SQLTraceCounter.COL_SUCCESS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_ADD_MEMBER_TO_DB, SQLTraceCounter.COL_TARGETLOGINNAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_ADD_MEMBER_TO_DB, SQLTraceCounter.COL_TARGETLOGINSID));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_ADD_MEMBER_TO_DB, SQLTraceCounter.COL_TARGETUSERNAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_ADD_DROP_ROLE, SQLTraceCounter.COL_DATABASENAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_ADD_DROP_ROLE, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_ADD_DROP_ROLE, SQLTraceCounter.COL_EVENTSUBCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_ADD_DROP_ROLE, SQLTraceCounter.COL_TARGETROLENAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_ADD_DROP_ROLE, SQLTraceCounter.COL_DATABASEUSERNAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_ADD_DROP_ROLE, SQLTraceCounter.COL_SUCCESS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_ADD_DROP_LOGIN, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_ADD_DROP_LOGIN, SQLTraceCounter.COL_EVENTSUBCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_ADD_DROP_LOGIN, SQLTraceCounter.COL_SUCCESS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_ADD_DROP_LOGIN, SQLTraceCounter.COL_TARGETLOGINNAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_ADD_DROP_LOGIN, SQLTraceCounter.COL_TARGETLOGINSID));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_LOGIN_CHANGE_PASSWORD, SQLTraceCounter.COL_DATABASENAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_LOGIN_CHANGE_PASSWORD, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_LOGIN_CHANGE_PASSWORD, SQLTraceCounter.COL_EVENTSUBCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_LOGIN_CHANGE_PASSWORD, SQLTraceCounter.COL_TARGETROLENAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_LOGIN_CHANGE_PASSWORD, SQLTraceCounter.COL_DATABASEUSERNAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_LOGIN_CHANGE_PASSWORD, SQLTraceCounter.COL_SUCCESS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_BACKUP_RESTORE, SQLTraceCounter.COL_DATABASENAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_BACKUP_RESTORE, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_BACKUP_RESTORE, SQLTraceCounter.COL_EVENTSUBCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_BACKUP_RESTORE, SQLTraceCounter.COL_DATABASEUSERNAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_BACKUP_RESTORE, SQLTraceCounter.COL_SUCCESS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_BACKUP_RESTORE, SQLTraceCounter.COL_TEXTDATA));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_CHANGE_AUDIT, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_CHANGE_AUDIT, SQLTraceCounter.COL_EVENTSUBCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_CHANGE_AUDIT, SQLTraceCounter.COL_SUCCESS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_DBCC, SQLTraceCounter.COL_DATABASENAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_DBCC, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_DBCC, SQLTraceCounter.COL_EVENTSUBCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_DBCC, SQLTraceCounter.COL_DATABASEUSERNAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_DBCC, SQLTraceCounter.COL_SUCCESS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_DBCC, SQLTraceCounter.COL_TEXTDATA));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_CONNECT, SQLTraceCounter.COL_BINARYDATA));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_CONNECT, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_CONNECT, SQLTraceCounter.COL_SUCCESS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_CONNECT, SQLTraceCounter.COL_TEXTDATA));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_LOGIN_CHANGE_PASSWORD, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_LOGIN_CHANGE_PASSWORD, SQLTraceCounter.COL_EVENTSUBCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_LOGIN_CHANGE_PASSWORD, SQLTraceCounter.COL_SUCCESS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_LOGIN_CHANGE_PASSWORD, SQLTraceCounter.COL_TARGETLOGINNAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_LOGIN_CHANGE_PASSWORD, SQLTraceCounter.COL_TARGETLOGINSID));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_LOGIN_CHANGE_PROPERTY, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_LOGIN_CHANGE_PROPERTY, SQLTraceCounter.COL_EVENTSUBCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_LOGIN_CHANGE_PROPERTY, SQLTraceCounter.COL_SUCCESS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_LOGIN_CHANGE_PROPERTY, SQLTraceCounter.COL_TARGETLOGINNAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_LOGIN_CHANGE_PROPERTY, SQLTraceCounter.COL_TARGETLOGINSID));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOGINFAILED, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOGINFAILED, SQLTraceCounter.COL_SUCCESS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_LOGIN_GDR, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_LOGIN_GDR, SQLTraceCounter.COL_EVENTSUBCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_LOGIN_GDR, SQLTraceCounter.COL_SUCCESS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_LOGIN_GDR, SQLTraceCounter.COL_TARGETLOGINNAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_LOGIN_GDR, SQLTraceCounter.COL_TARGETLOGINSID));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_DISCONNECT, SQLTraceCounter.COL_CPU));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_DISCONNECT, SQLTraceCounter.COL_DURATION));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_DISCONNECT, SQLTraceCounter.COL_ENDTIME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_DISCONNECT, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_DISCONNECT, SQLTraceCounter.COL_READS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_DISCONNECT, SQLTraceCounter.COL_SUCCESS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_DISCONNECT, SQLTraceCounter.COL_WRITES));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_OBJECT_DERIVED_PERMISSION, SQLTraceCounter.COL_DATABASENAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_OBJECT_DERIVED_PERMISSION, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_OBJECT_DERIVED_PERMISSION, SQLTraceCounter.COL_EVENTSUBCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_OBJECT_DERIVED_PERMISSION, SQLTraceCounter.COL_OBJECTNAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_OBJECT_DERIVED_PERMISSION, SQLTraceCounter.COL_OBJECTTYPE));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_OBJECT_DERIVED_PERMISSION, SQLTraceCounter.COL_OBJECTOWNER));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_OBJECT_DERIVED_PERMISSION, SQLTraceCounter.COL_DATABASEUSERNAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_OBJECT_DERIVED_PERMISSION, SQLTraceCounter.COL_SUCCESS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_OBJECT_DERIVED_PERMISSION, SQLTraceCounter.COL_TEXTDATA));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_OBJECT_GDR, SQLTraceCounter.COL_COLUMNPERMISSIONSSET));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_OBJECT_GDR, SQLTraceCounter.COL_DATABASENAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_OBJECT_GDR, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_OBJECT_GDR, SQLTraceCounter.COL_EVENTSUBCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_OBJECT_GDR, SQLTraceCounter.COL_OBJECTNAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_OBJECT_GDR, SQLTraceCounter.COL_OBJECTOWNER));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_OBJECT_GDR, SQLTraceCounter.COL_PERMISSIONS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_OBJECT_GDR, SQLTraceCounter.COL_DATABASEUSERNAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_OBJECT_GDR, SQLTraceCounter.COL_SUCCESS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_OBJECT_GDR, SQLTraceCounter.COL_TEXTDATA));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_OBJECT_PERMISSION, SQLTraceCounter.COL_COLUMNPERMISSIONSSET));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_OBJECT_PERMISSION, SQLTraceCounter.COL_DATABASENAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_OBJECT_PERMISSION, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_OBJECT_PERMISSION, SQLTraceCounter.COL_EVENTSUBCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_OBJECT_PERMISSION, SQLTraceCounter.COL_OBJECTNAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_OBJECT_PERMISSION, SQLTraceCounter.COL_OBJECTOWNER));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_OBJECT_PERMISSION, SQLTraceCounter.COL_PERMISSIONS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_OBJECT_PERMISSION, SQLTraceCounter.COL_DATABASEUSERNAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_OBJECT_PERMISSION, SQLTraceCounter.COL_SUCCESS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_OBJECT_PERMISSION, SQLTraceCounter.COL_TEXTDATA));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_OBJECT_DERIVED_PERMISSION, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_OBJECT_DERIVED_PERMISSION, SQLTraceCounter.COL_EVENTSUBCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_OBJECT_DERIVED_PERMISSION, SQLTraceCounter.COL_LOGINNAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_OBJECT_DERIVED_PERMISSION, SQLTraceCounter.COL_LOGINSID));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_STATEMENT_GDR, SQLTraceCounter.COL_DATABASENAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_STATEMENT_GDR, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_STATEMENT_GDR, SQLTraceCounter.COL_EVENTSUBCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_STATEMENT_GDR, SQLTraceCounter.COL_PERMISSIONS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_STATEMENT_GDR, SQLTraceCounter.COL_DATABASEUSERNAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_STATEMENT_GDR, SQLTraceCounter.COL_SUCCESS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_STATEMENT_GDR, SQLTraceCounter.COL_TEXTDATA));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_STATEMENT_PERMISSION, SQLTraceCounter.COL_DATABASENAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_STATEMENT_PERMISSION, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_STATEMENT_PERMISSION, SQLTraceCounter.COL_EVENTSUBCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_STATEMENT_PERMISSION, SQLTraceCounter.COL_PERMISSIONS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_STATEMENT_PERMISSION, SQLTraceCounter.COL_DATABASEUSERNAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_STATEMENT_PERMISSION, SQLTraceCounter.COL_SUCCESS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_AUDIT_STATEMENT_PERMISSION, SQLTraceCounter.COL_TEXTDATA));
        return retList;
    }

    /**
     * Called by the main trace entity when an event is received. This
     * entity is supposed to inspect the data here and update its counters
     * @param eventId
     * @param columnData
     */
    public boolean eventReceived(int eventId, SqlTrace.SQLColumnData columnData) {
/*        switch (eventId) {
	    	case SQLTraceCounter.EV_AUDIT_ADD_DB_USER:
	    	    switch (columnData.getInteger(SQLTraceCounter.COL_EVENTSUBCLASS)) {
	    	    	case 1: fcAddDBUser.add(1); break;
	    	    	case 2: fcDropDBUser.add(1); break;
	    	    	case 3: fcGrantAccess.add(1); break;
	    	    	case 4: fcRevokeAccess.add(1); break;
	    	    }
	    	    break;
	    	case SQLTraceCounter.EV_AUDIT_ADD_LOGIN_TO_SERVER_ROLE:
	    	    switch (columnData.getInteger(SQLTraceCounter.COL_EVENTSUBCLASS)) {
	    	    	case 1: fcAddToSrvRole.add(1); break;
	    	    	case 2: fcDropFromSrvRole.add(1); break;
	    	    }
	    	    break;
	    	case SQLTraceCounter.EV_AUDIT_ADD_MEMBER_TO_DB:
	    	    switch (columnData.getInteger(SQLTraceCounter.COL_EVENTSUBCLASS)) {
	    	    	case 1: fcAddToDBRole.add(1); break;
	    	    	case 2: fcDropFromDBRole.add(1); break;
	    	    }
	    	    break;
	    	case SQLTraceCounter.EV_AUDIT_ADD_DROP_ROLE:
	    	    switch (columnData.getInteger(SQLTraceCounter.COL_EVENTSUBCLASS)) {
	    	    	case 1: fcAddDBRole.add(1); break;
	    	    	case 2: fcDropDBRole.add(1); break;
	    	    }
	    	    break;
	    	case SQLTraceCounter.EV_AUDIT_ADD_DROP_LOGIN:
	    	    switch (columnData.getInteger(SQLTraceCounter.COL_EVENTSUBCLASS)) {
	    	    	case 1: fcAddSrvLogin.add(1); break;
	    	    	case 2: fcDropSrvLogin.add(1); break;
	    	    }
	    	    break;
	    	case SQLTraceCounter.EV_AUDIT_:
	    	    switch (columnData.getInteger(SQLTraceCounter.COL_EVENTSUBCLASS)) {
	    	    	case 1: fcAddSrvLogin.add(1); break;
	    	    	case 2: fcDropSrvLogin.add(1); break;
	    	    }
	    	    break;
        }*/
        return false; // no entities/counters added/removed
    }
}
