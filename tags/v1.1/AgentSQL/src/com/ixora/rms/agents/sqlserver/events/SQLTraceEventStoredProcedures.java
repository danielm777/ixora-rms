/*
 * Created on 21-Apr-2005
 */
package com.ixora.rms.agents.sqlserver.events;

import java.util.LinkedList;
import java.util.List;

import com.ixora.common.utils.Utils;
import com.ixora.rms.CounterId;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.AgentExecutionContext;
import com.ixora.rms.agents.impl.Entity;
import com.ixora.rms.agents.sqlserver.Msg;
import com.ixora.rms.agents.sqlserver.SQLTraceChildEntity;
import com.ixora.rms.agents.sqlserver.SQLTraceCounter;
import com.ixora.rms.agents.sqlserver.SqlTrace;

/**
 * SQLTraceEventStoredProcedures
 */
public class SQLTraceEventStoredProcedures extends SQLTraceChildEntity {


    /**
     * SQLRPCEntity
     * Used for creating child entities on the fly
     */
    private static class SQLRPCEntity extends Entity {
        /**
         * Default constructor
         * @param idParent
         * @param name
         * @param c
         */
        protected SQLRPCEntity(EntityId idParent, String name,
                AgentExecutionContext c, SqlTrace.SQLColumnData columnData) {
    		super(new EntityId(idParent, name), name, c);

    		updateData(columnData);
        }

        /**
         * Creates or updates counters.
         * @param columnData
         */
        public boolean updateData(SqlTrace.SQLColumnData columnData) {
            boolean changedStructure = false;

    		// Extract interesting information from columnData
    	    String appName = columnData.getString(SQLTraceCounter.COL_APPLICATIONNAME);
    	    String ntUserName = columnData.getString(SQLTraceCounter.COL_NTUSERNAME);
    	    String loginUserName = columnData.getString(SQLTraceCounter.COL_LOGINNAME);
    	    int processID = (int)columnData.getDouble(SQLTraceCounter.COL_CLIENTPROCESSID);
    	    String textData = columnData.getString(SQLTraceCounter.COL_TEXTDATA);
    	    double cpuUsage = columnData.getDouble(SQLTraceCounter.COL_CPU);
    	    double diskReads = columnData.getDouble(SQLTraceCounter.COL_READS);
    	    double diskWrites = columnData.getDouble(SQLTraceCounter.COL_WRITES);
    	    double rpcDuration = columnData.getDouble(SQLTraceCounter.COL_DURATION);

    	    // Create each counter and add to entity
    	    CounterId childCounterId = new CounterId(Msg.SQLSERVERAGENT_COUNTER_TRACE_APPLICATIONNAME);
    	    SQLCounterString cntAppName = (SQLCounterString)fCounters.get(childCounterId);
    	    if (cntAppName == null) { // create new
    	        cntAppName = new SQLCounterString(Msg.SQLSERVERAGENT_COUNTER_TRACE_APPLICATIONNAME);
        	    addCounter(cntAppName);
        	    changedStructure = true;
    	    }
    	    cntAppName.set(appName);

    	    childCounterId = new CounterId(Msg.SQLSERVERAGENT_COUNTER_TRACE_NTUSERNAME);
    	    SQLCounterString cntNTUserName = (SQLCounterString)fCounters.get(childCounterId);
    	    if (cntNTUserName == null) { // create new
    	        cntNTUserName = new SQLCounterString(Msg.SQLSERVERAGENT_COUNTER_TRACE_NTUSERNAME);
        	    addCounter(cntNTUserName);
        	    changedStructure = true;
    	    }
    	    cntNTUserName.set(ntUserName);

    	    childCounterId = new CounterId(Msg.SQLSERVERAGENT_COUNTER_TRACE_SQLSECURITYLOGINNAME);
    	    SQLCounterString cntLoginName = (SQLCounterString)fCounters.get(childCounterId);
    	    if (cntLoginName == null) { // create new
    	        cntLoginName = new SQLCounterString(Msg.SQLSERVERAGENT_COUNTER_TRACE_SQLSECURITYLOGINNAME);
        	    addCounter(cntLoginName);
        	    changedStructure = true;
    	    }
    	    cntLoginName.set(loginUserName);

    	    childCounterId = new CounterId(Msg.SQLSERVERAGENT_COUNTER_TRACE_CLIENTPROCESSID);
    	    SQLCounterAccum cntProcessID = (SQLCounterAccum)fCounters.get(childCounterId);
    	    if (cntProcessID == null) { // create new
    	        cntProcessID = new SQLCounterAccum(Msg.SQLSERVERAGENT_COUNTER_TRACE_CLIENTPROCESSID);
        	    addCounter(cntProcessID);
        	    changedStructure = true;
    	    }
    	    cntProcessID.set(processID);

    	    childCounterId = new CounterId(Msg.SQLSERVERAGENT_COUNTER_TRACE_CPU);
    	    SQLCounterAccum cntCPU = (SQLCounterAccum)fCounters.get(childCounterId);
    	    if (cntCPU == null) { // create new
    	        cntCPU = new SQLCounterAccum(Msg.SQLSERVERAGENT_COUNTER_TRACE_CPU);
        	    addCounter(cntCPU);
        	    changedStructure = true;
    	    }
    	    cntCPU.add(cpuUsage);

    	    childCounterId = new CounterId(Msg.SQLSERVERAGENT_COUNTER_TRACE_READS);
    	    SQLCounterAccum cntReads = (SQLCounterAccum)fCounters.get(childCounterId);
    	    if (cntReads == null) { // create new
    	        cntReads = new SQLCounterAccum(Msg.SQLSERVERAGENT_COUNTER_TRACE_READS);
        	    addCounter(cntReads);
        	    changedStructure = true;
    	    }
    	    cntReads.add(diskReads);

    	    childCounterId = new CounterId(Msg.SQLSERVERAGENT_COUNTER_TRACE_WRITES);
    	    SQLCounterAccum cntWrites = (SQLCounterAccum)fCounters.get(childCounterId);
    	    if (cntWrites == null) { // create new
    	        cntWrites = new SQLCounterAccum(Msg.SQLSERVERAGENT_COUNTER_TRACE_WRITES);
        	    addCounter(cntWrites);
        	    changedStructure = true;
    	    }
    	    cntWrites.add(diskWrites);

    	    childCounterId = new CounterId(Msg.SQLSERVERAGENT_COUNTER_TRACE_DURATION);
    	    SQLCounterAccum cntDuration = (SQLCounterAccum)fCounters.get(childCounterId);
    	    if (cntDuration == null) { // create new
    	        cntDuration = new SQLCounterAccum(Msg.SQLSERVERAGENT_COUNTER_TRACE_DURATION);
        	    addCounter(cntDuration);
        	    changedStructure = true;
    	    }
    	    cntDuration.add(rpcDuration);

    	    return changedStructure;
        }

        /**
         * @see com.ixora.rms.agents.impl.Entity#retrieveCounterValues()
         */
        protected void retrieveCounterValues() throws Throwable {
            ; // nothing
        }
    }

    /**
     * Default constructor
     * @param c
     */
    public SQLTraceEventStoredProcedures(EntityId idParent, AgentExecutionContext c) {
        super(idParent, Msg.SQLSERVERAGENT_ENTITY_EVENT_STORED_PROCEDURES, c);
    }

    /**
     * Each entity must return a list of column IDs (as defined by SQL
     * server) so that the root entity knowns what tracing to enable.
     * @see com.ixora.rms.agents.sqlserver.SQLTraceChildEntity#getTracedCounters()
     */
    public List<TracedEventColumn> getTracedEventsAndColumns() {
        List<TracedEventColumn> retList = new LinkedList<TracedEventColumn>();
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_RPC_COMPLETED, SQLTraceCounter.COL_CPU));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_RPC_COMPLETED, SQLTraceCounter.COL_DURATION));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_RPC_COMPLETED, SQLTraceCounter.COL_ENDTIME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_RPC_COMPLETED, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_RPC_COMPLETED, SQLTraceCounter.COL_READS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_RPC_COMPLETED, SQLTraceCounter.COL_TEXTDATA));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_RPC_COMPLETED, SQLTraceCounter.COL_WRITES));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_RPC_STARTING, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_RPC_STARTING, SQLTraceCounter.COL_TEXTDATA));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_RPC_OUTPUT_PARAMETER, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_RPC_OUTPUT_PARAMETER, SQLTraceCounter.COL_OBJECTNAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_RPC_OUTPUT_PARAMETER, SQLTraceCounter.COL_TEXTDATA));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SP_CACHEHIT, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SP_CACHEHIT, SQLTraceCounter.COL_OBJECTID));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SP_CACHEHIT, SQLTraceCounter.COL_OBJECTNAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SP_CACHEHIT, SQLTraceCounter.COL_TEXTDATA));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SP_CACHEINSERT, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SP_CACHEINSERT, SQLTraceCounter.COL_OBJECTID));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SP_CACHEINSERT, SQLTraceCounter.COL_OBJECTNAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SP_CACHEINSERT, SQLTraceCounter.COL_TEXTDATA));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SP_CACHEMISS, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SP_CACHEMISS, SQLTraceCounter.COL_EVENTSUBCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SP_CACHEMISS, SQLTraceCounter.COL_OBJECTNAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SP_CACHEREMOVE, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SP_CACHEREMOVE, SQLTraceCounter.COL_OBJECTID));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SP_CACHEREMOVE, SQLTraceCounter.COL_OBJECTNAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SP_CACHEREMOVE, SQLTraceCounter.COL_TEXTDATA));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SP_COMPLETED, SQLTraceCounter.COL_DURATION));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SP_COMPLETED, SQLTraceCounter.COL_ENDTIME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SP_COMPLETED, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SP_COMPLETED, SQLTraceCounter.COL_NESTLEVEL));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SP_COMPLETED, SQLTraceCounter.COL_OBJECTID));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SP_COMPLETED, SQLTraceCounter.COL_OBJECTNAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SP_COMPLETED, SQLTraceCounter.COL_OBJECTTYPE));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SP_COMPLETED, SQLTraceCounter.COL_TEXTDATA));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SP_EXECCONTEXTHIT, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SP_EXECCONTEXTHIT, SQLTraceCounter.COL_OBJECTID));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SP_EXECCONTEXTHIT, SQLTraceCounter.COL_OBJECTNAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SP_EXECCONTEXTHIT, SQLTraceCounter.COL_TEXTDATA));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SP_RECOMPILE, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SP_RECOMPILE, SQLTraceCounter.COL_NESTLEVEL));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SP_RECOMPILE, SQLTraceCounter.COL_OBJECTID));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SP_RECOMPILE, SQLTraceCounter.COL_OBJECTNAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SP_RECOMPILE, SQLTraceCounter.COL_TEXTDATA));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SP_STARTING, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SP_STARTING, SQLTraceCounter.COL_NESTLEVEL));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SP_STARTING, SQLTraceCounter.COL_OBJECTID));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SP_STARTING, SQLTraceCounter.COL_OBJECTNAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SP_STARTING, SQLTraceCounter.COL_OBJECTTYPE));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SP_STARTING, SQLTraceCounter.COL_TEXTDATA));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SP_STMTCOMPLETED, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SP_STMTCOMPLETED, SQLTraceCounter.COL_EVENTSUBCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SP_STMTCOMPLETED, SQLTraceCounter.COL_INTEGERDATA));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SP_STMTCOMPLETED, SQLTraceCounter.COL_OBJECTID));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SP_STMTCOMPLETED, SQLTraceCounter.COL_TEXTDATA));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SP_STARTING, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SP_STARTING, SQLTraceCounter.COL_EVENTSUBCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SP_STARTING, SQLTraceCounter.COL_OBJECTID));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SP_STARTING, SQLTraceCounter.COL_TEXTDATA));
        return retList;
    }

    /**
     * Called by the main trace entity when an event is received. This
     * entity is supposed to inspect the data here and update its counters
     * @param eventId
     * @param columnData
     */
    public boolean eventReceived(int eventId, SqlTrace.SQLColumnData columnData) {
        boolean entitiesChanged = false;
        switch (eventId) {
        	case SQLTraceCounter.EV_RPC_COMPLETED:
        	    try {
	        	    String appName = columnData.getString(SQLTraceCounter.COL_APPLICATIONNAME);
	        	    String ntUserName = columnData.getString(SQLTraceCounter.COL_NTUSERNAME);
	        	    String loginUserName = columnData.getString(SQLTraceCounter.COL_LOGINNAME);
	        	    int processID = (int)columnData.getDouble(SQLTraceCounter.COL_CLIENTPROCESSID);
	        	    String textData = columnData.getString(SQLTraceCounter.COL_TEXTDATA);
	        	    double cpuUsage = columnData.getDouble(SQLTraceCounter.COL_CPU);
	        	    double diskReads = columnData.getDouble(SQLTraceCounter.COL_READS);
	        	    double diskWrites = columnData.getDouble(SQLTraceCounter.COL_WRITES);
	        	    double rpcDuration = columnData.getDouble(SQLTraceCounter.COL_DURATION);

	        	    // Filter out sp_trace_setevent calls and empty strings
	        	    if (!Utils.isEmptyString(textData) &&
	        	            textData.indexOf("sp_trace_setevent") == -1) {
		        	    // Create or update an entity for this SQL query
		        	    EntityId childEntityId = new EntityId(getId(), textData);
		        	    SQLRPCEntity childEntity = (SQLRPCEntity)fChildrenEntities.get(childEntityId);
		        	    if (childEntity == null) { // Create new
		        	        childEntity = new SQLRPCEntity(getId(), textData, fContext, columnData);
		        	        addChildEntity(childEntity);
		        	        entitiesChanged = true;
		        	    } else {
		        	        // Update existing
		        	        entitiesChanged = childEntity.updateData(columnData);
		        	    }
	        	    }
        	    } catch (Throwable e) {
        	        // no reason for an exception here, but
        	        // not a critical error anyway.
        	        fContext.error(e);
        	    }
        	    break;
        }
        return entitiesChanged;
    }
}
