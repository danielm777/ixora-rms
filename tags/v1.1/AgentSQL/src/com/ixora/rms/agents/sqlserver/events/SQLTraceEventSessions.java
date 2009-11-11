/*
 * Created on 21-Apr-2005
 */
package com.ixora.rms.agents.sqlserver.events;

import java.util.LinkedList;
import java.util.List;

import com.ixora.rms.EntityId;
import com.ixora.rms.agents.AgentExecutionContext;
import com.ixora.rms.agents.impl.Entity;
import com.ixora.rms.agents.sqlserver.Msg;
import com.ixora.rms.agents.sqlserver.SQLTraceChildEntity;
import com.ixora.rms.agents.sqlserver.SQLTraceCounter;
import com.ixora.rms.agents.sqlserver.SqlTrace;

/**
 * SQLTraceEventSessions
 */
public class SQLTraceEventSessions extends SQLTraceChildEntity {

    /**
     * SQLExistingSessionEntity
     * Used for creating child entities on the fly
     */
    private static class SQLExistingSessionEntity extends Entity {
        /**
         * Default constructor
         * @param idParent
         * @param name
         * @param c
         */
        protected SQLExistingSessionEntity(EntityId idParent, String name,
                AgentExecutionContext c, SqlTrace.SQLColumnData columnData) {
    		super(new EntityId(idParent, name), name, c);

    		// Extract interesting information from columnData
    	    String appName = columnData.getString(SQLTraceCounter.COL_APPLICATIONNAME);
    	    String ntUserName = columnData.getString(SQLTraceCounter.COL_NTUSERNAME);
    	    String loginUserName = columnData.getString(SQLTraceCounter.COL_LOGINNAME);
    	    int processID = (int)columnData.getDouble(SQLTraceCounter.COL_CLIENTPROCESSID);

    	    // Create each counter and add to entity
    	    SQLCounterString cntAppName = new SQLCounterString(Msg.SQLSERVERAGENT_COUNTER_TRACE_APPLICATIONNAME);
    	    cntAppName.set(appName);
    	    addCounter(cntAppName);

    	    SQLCounterString cntNTUserName = new SQLCounterString(Msg.SQLSERVERAGENT_COUNTER_TRACE_NTUSERNAME);
    	    cntNTUserName.set(ntUserName);
    	    addCounter(cntNTUserName);

    	    SQLCounterString cntLoginName = new SQLCounterString(Msg.SQLSERVERAGENT_COUNTER_TRACE_SQLSECURITYLOGINNAME);
    	    cntLoginName.set(loginUserName);
    	    addCounter(cntLoginName);

    	    SQLCounterAccum cntProcessID = new SQLCounterAccum(Msg.SQLSERVERAGENT_COUNTER_TRACE_CLIENTPROCESSID);
    	    cntProcessID.set(processID);
    	    addCounter(cntProcessID);
    	}

        /**
         * @see com.ixora.rms.agents.impl.Entity#retrieveCounterValues()
         */
        protected void retrieveCounterValues() throws Throwable {
            // nothing
        }
    }

    /**
     * Default constructor
     * @param c
     */
    public SQLTraceEventSessions(EntityId idParent, AgentExecutionContext c) {
        super(idParent, Msg.SQLSERVERAGENT_ENTITY_EVENT_SESSIONS, c);
    }

    /**
     * Each entity must return a list of column IDs (as defined by SQL
     * server) so that the root entity knowns what tracing to enable.
     * @see com.ixora.rms.agents.sqlserver.SQLTraceChildEntity#getTracedCounters()
     */
    public List<TracedEventColumn> getTracedEventsAndColumns() {
        List<TracedEventColumn> retList = new LinkedList<TracedEventColumn>();
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_EXISTINGCONNECTION, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_EXISTINGCONNECTION, SQLTraceCounter.COL_APPLICATIONNAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_EXISTINGCONNECTION, SQLTraceCounter.COL_NTUSERNAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_EXISTINGCONNECTION, SQLTraceCounter.COL_CLIENTPROCESSID));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_EXISTINGCONNECTION, SQLTraceCounter.COL_LOGINNAME));
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
        	case SQLTraceCounter.EV_EXISTINGCONNECTION:
        	    try {
	        	    int processID = (int)columnData.getDouble(SQLTraceCounter.COL_CLIENTPROCESSID);
	        	    String appName = columnData.getString(SQLTraceCounter.COL_APPLICATIONNAME);
	        	    Entity childEntity = new SQLExistingSessionEntity(
	        	            getId(), appName + " pid:" + processID, fContext, columnData);
	        	    addChildEntity(childEntity);
	        	    entitiesChanged = true;
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
