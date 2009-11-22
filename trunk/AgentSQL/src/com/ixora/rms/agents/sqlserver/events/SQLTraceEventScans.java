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
 * SQLTraceEventScans
 */
public class SQLTraceEventScans extends SQLTraceChildEntity {
	private static final long serialVersionUID = 1473428188681861178L;
	private SQLCounterAccum	fcScans;
    private SQLCounterAccum	fcScansDuration;
    private SQLCounterAccum	fcScansReads;

    /**
     * Default constructor
     * @param c
     */
    public SQLTraceEventScans(EntityId idParent, AgentExecutionContext c) {
        super(idParent, Msg.SQLSERVERAGENT_ENTITY_EVENT_SCANS, c);
        addCounter(fcScans = new SQLCounterAccum(Msg.COUNTER_SCANS_COUNT));
        addCounter(fcScansDuration = new SQLCounterAccum(Msg.COUNTER_SCANS_DURATION));
        addCounter(fcScansReads = new SQLCounterAccum(Msg.COUNTER_SCANS_READS));
    }

    /**
     * Each entity must return a list of column IDs (as defined by SQL
     * server) so that the root entity knowns what tracing to enable.
     * @see com.ixora.rms.agents.sqlserver.SQLTraceChildEntity#getTracedCounters()
     */
    public List<TracedEventColumn> getTracedEventsAndColumns() {
        List<TracedEventColumn> retList = new LinkedList<TracedEventColumn>();
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SCAN_STARTED, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SCAN_STARTED, SQLTraceCounter.COL_INDEXID));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SCAN_STARTED, SQLTraceCounter.COL_MODE));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SCAN_STARTED, SQLTraceCounter.COL_OBJECTID));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SCAN_STARTED, SQLTraceCounter.COL_TRANSACTIONID));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SCAN_STOPPED, SQLTraceCounter.COL_DURATION));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SCAN_STOPPED, SQLTraceCounter.COL_ENDTIME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SCAN_STOPPED, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SCAN_STOPPED, SQLTraceCounter.COL_INDEXID));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SCAN_STOPPED, SQLTraceCounter.COL_MODE));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SCAN_STOPPED, SQLTraceCounter.COL_OBJECTID));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SCAN_STOPPED, SQLTraceCounter.COL_READS));
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
	    	case SQLTraceCounter.EV_SCAN_STOPPED:
	    	    fcScans.add(1);
	    	    fcScansDuration.add(
	    	            columnData.getDouble(SQLTraceCounter.COL_DURATION));
	    	    fcScansReads.add(
	    	            columnData.getDouble(SQLTraceCounter.COL_READS));
	    	    break;
        }
        return false; // no entities/counters added/removed
    }
}
