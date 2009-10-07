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
 * SQLTraceEventErrors
 */
public class SQLTraceEventErrors extends SQLTraceChildEntity {

    private SQLCounterAccum	fcAttention;
    private SQLCounterAccum	fcErrorLog;
    private SQLCounterAccum	fcEventLog;
    private SQLCounterAccum	fcExceptions;
    private SQLCounterAccum	fcQueryWait;
    private SQLCounterAccum	fcQueryTimeout;
    private SQLCounterAccum	fcHashRecursion;
    private SQLCounterAccum	fcHashBail;
    private SQLCounterAccum	fcMissingStatistics;
    private SQLCounterAccum	fcMissingJoinPredicate;
    private SQLCounterAccum	fcOLEDBErrors;
    private SQLCounterAccum	fcSortSingle;
    private SQLCounterAccum	fcSortMulti;

    /**
     * Default constructor
     * @param c
     */
    public SQLTraceEventErrors(EntityId idParent, AgentExecutionContext c) {
        super(idParent, Msg.SQLSERVERAGENT_ENTITY_EVENT_ERRORS, c);
        addCounter(fcAttention = new SQLCounterAccum(Msg.COUNTER_ERRORS_ATTENTION));
        addCounter(fcErrorLog = new SQLCounterAccum(Msg.COUNTER_ERRORS_ERRORLOG));
        addCounter(fcEventLog = new SQLCounterAccum(Msg.COUNTER_ERRORS_EVENTLOG));
        addCounter(fcExceptions = new SQLCounterAccum(Msg.COUNTER_ERRORS_EXCEPTIONS));
        addCounter(fcQueryWait = new SQLCounterAccum(Msg.COUNTER_ERRORS_QUERY_WAIT));
        addCounter(fcQueryTimeout = new SQLCounterAccum(Msg.COUNTER_ERRORS_QUERY_TIMEOUT));
        addCounter(fcHashRecursion = new SQLCounterAccum(Msg.COUNTER_ERRORS_HASH_RECURSION));
        addCounter(fcHashBail = new SQLCounterAccum(Msg.COUNTER_ERRORS_HASH_BAIL));
        addCounter(fcMissingStatistics = new SQLCounterAccum(Msg.COUNTER_ERRORS_MISSING_STATISTICS));
        addCounter(fcMissingJoinPredicate = new SQLCounterAccum(Msg.COUNTER_ERRORS_MISSING_JOIN_PREDICATE));
        addCounter(fcOLEDBErrors = new SQLCounterAccum(Msg.COUNTER_ERRORS_OLEDB));
        addCounter(fcSortSingle = new SQLCounterAccum(Msg.COUNTER_ERRORS_SORT_SINGLE));
        addCounter(fcSortMulti = new SQLCounterAccum(Msg.COUNTER_ERRORS_SORT_MULTI));
    }

    /**
     * Each entity must return a list of column IDs (as defined by SQL
     * server) so that the root entity knowns what tracing to enable.
     * @see com.ixora.rms.agents.sqlserver.SQLTraceChildEntity#getTracedCounters()
     */
    public List<TracedEventColumn> getTracedEventsAndColumns() {
        List<TracedEventColumn> retList = new LinkedList<TracedEventColumn>();
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_ATTENTION, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_ERRORLOG, SQLTraceCounter.COL_ERROR));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_ERRORLOG, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_ERRORLOG, SQLTraceCounter.COL_SEVERITY));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_ERRORLOG, SQLTraceCounter.COL_TEXTDATA));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_EVENTLOG, SQLTraceCounter.COL_BINARYDATA));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_EVENTLOG, SQLTraceCounter.COL_ERROR));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_EVENTLOG, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_EVENTLOG, SQLTraceCounter.COL_SEVERITY));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_EVENTLOG, SQLTraceCounter.COL_TEXTDATA));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_EXCEPTION, SQLTraceCounter.COL_ERROR));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_EXCEPTION, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_EXCEPTION, SQLTraceCounter.COL_SEVERITY));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_EXCEPTION, SQLTraceCounter.COL_STATE));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_EXECUTION_WARNINGS, SQLTraceCounter.COL_ERROR));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_EXECUTION_WARNINGS, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_EXECUTION_WARNINGS, SQLTraceCounter.COL_EVENTSUBCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_HASHWARNING, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_HASHWARNING, SQLTraceCounter.COL_EVENTSUBCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_HASHWARNING, SQLTraceCounter.COL_INTEGERDATA));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_HASHWARNING, SQLTraceCounter.COL_OBJECTID));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_MISSING_COLUMN_STATISTICS, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_MISSING_COLUMN_STATISTICS, SQLTraceCounter.COL_TEXTDATA));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_MISSING_JOIN_PREDICATE, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_OLEDB_ERRORS, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_OLEDB_ERRORS, SQLTraceCounter.COL_TEXTDATA));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SORT_WARNINGS, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SORT_WARNINGS, SQLTraceCounter.COL_EVENTSUBCLASS));
        return retList;
    }

    /**
     * Called by the main trace entity when an event is received. This
     * entity is supposed to inspect the data here and update its counters
     * @param eventId
     * @param columnData
     */
    public boolean eventReceived(int eventId, SqlTrace.SQLColumnData columnData) {
        switch (eventId) {
    		case SQLTraceCounter.EV_ATTENTION:
    		    fcAttention.add(1);
    		    break;
    		case SQLTraceCounter.EV_ERRORLOG:
    		    fcErrorLog.add(1);
    		    break;
    		case SQLTraceCounter.EV_EVENTLOG:
    		    fcEventLog.add(1);
    		    break;
    		case SQLTraceCounter.EV_EXCEPTION:
    		    fcExceptions.add(1);
    		    break;
    		case SQLTraceCounter.EV_EXECUTION_WARNINGS:
    		    switch (columnData.getInteger(SQLTraceCounter.COL_EVENTSUBCLASS)) {
    		    	case 1: fcQueryWait.add(1); break;
    		    	case 2: fcQueryTimeout.add(1); break;
    		    }
    		    break;
    		case SQLTraceCounter.EV_HASHWARNING:
    		    switch (columnData.getInteger(SQLTraceCounter.COL_EVENTSUBCLASS)) {
			    	case 0: fcHashRecursion.add(1); break;
			    	case 1: fcHashBail.add(1); break;
			    }
			    break;
    		case SQLTraceCounter.EV_MISSING_COLUMN_STATISTICS:
    		    fcMissingStatistics.add(1);
    		    break;
    		case SQLTraceCounter.EV_MISSING_JOIN_PREDICATE:
    		    fcMissingJoinPredicate.add(1);
    		    break;
    		case SQLTraceCounter.EV_OLEDB_ERRORS:
    		    fcOLEDBErrors.add(1);
    		    break;
    		case SQLTraceCounter.EV_SORT_WARNINGS:
    		    switch (columnData.getInteger(SQLTraceCounter.COL_EVENTSUBCLASS)) {
			    	case 1: fcSortSingle.add(1); break;
			    	case 2: fcSortMulti.add(1); break;
			    }
    		    break;
        }
        return false; // no entities/counters added/removed
    }
}
