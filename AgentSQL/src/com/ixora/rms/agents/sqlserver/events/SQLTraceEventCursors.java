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
 * SQLTraceEventCursors
 */
public class SQLTraceEventCursors extends SQLTraceChildEntity {

    private SQLCounterAccum	fcClosed;
    private SQLCounterAccum	fcExecuted;
    private SQLCounterAccum	fcImplicitConverted;
    private SQLCounterAccum	fcOpened;
    private SQLCounterAccum	fcPrepared;
    private SQLCounterAccum	fcRecompiled;
    private SQLCounterAccum	fcUnprepared;

    /**
     * Default constructor
     * @param c
     */
    public SQLTraceEventCursors(EntityId idParent, AgentExecutionContext c) {
        super(idParent, Msg.SQLSERVERAGENT_ENTITY_EVENT_CURSORS, c);
        addCounter(fcClosed = new SQLCounterAccum(Msg.COUNTER_CURSORS_CLOSED));
        addCounter(fcExecuted = new SQLCounterAccum(Msg.COUNTER_CURSORS_EXECUTED));
        addCounter(fcImplicitConverted = new SQLCounterAccum(Msg.COUNTER_CURSORS_IMPLICIT_CONVERTED));
        addCounter(fcOpened = new SQLCounterAccum(Msg.COUNTER_CURSORS_OPENED));
        addCounter(fcPrepared = new SQLCounterAccum(Msg.COUNTER_CURSORS_PREPARED));
        addCounter(fcRecompiled = new SQLCounterAccum(Msg.COUNTER_CURSORS_RECOMPILED));
        addCounter(fcUnprepared = new SQLCounterAccum(Msg.COUNTER_CURSORS_UNPREPARED));
    }

    /**
     * Each entity must return a list of column IDs (as defined by SQL
     * server) so that the root entity knowns what tracing to enable.
     * @see com.ixora.rms.agents.sqlserver.SQLTraceChildEntity#getTracedCounters()
     */
    public List<TracedEventColumn> getTracedEventsAndColumns() {
        List<TracedEventColumn> retList = new LinkedList<TracedEventColumn>();
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_CURSORCLOSE, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_CURSORCLOSE, SQLTraceCounter.COL_HANDLE));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_CURSOREXECUTE, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_CURSOREXECUTE, SQLTraceCounter.COL_HANDLE));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_CURSOREXECUTE, SQLTraceCounter.COL_INTEGERDATA));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_CURSORIMPLICITCONVERSION, SQLTraceCounter.COL_BINARYDATA));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_CURSORIMPLICITCONVERSION, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_CURSORIMPLICITCONVERSION, SQLTraceCounter.COL_HANDLE));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_CURSORIMPLICITCONVERSION, SQLTraceCounter.COL_INTEGERDATA));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_CURSOROPEN, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_CURSOROPEN, SQLTraceCounter.COL_HANDLE));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_CURSOROPEN, SQLTraceCounter.COL_INTEGERDATA));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_CURSORPREPARE, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_CURSORPREPARE, SQLTraceCounter.COL_HANDLE));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_CURSORRECOMPILE, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_CURSORRECOMPILE, SQLTraceCounter.COL_HANDLE));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_CURSORUNPREPARE, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_CURSORUNPREPARE, SQLTraceCounter.COL_EVENTSUBCLASS));
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
        	case SQLTraceCounter.EV_CURSORCLOSE:
        	    fcClosed.add(1);
        	    break;
        	case SQLTraceCounter.EV_CURSOREXECUTE:
        	    fcExecuted.add(1);
        	    break;
        	case SQLTraceCounter.EV_CURSORIMPLICITCONVERSION:
        	    fcImplicitConverted.add(1);
        	    break;
        	case SQLTraceCounter.EV_CURSOROPEN:
        	    fcOpened.add(1);
        	    break;
        	case SQLTraceCounter.EV_CURSORPREPARE:
        	    fcPrepared.add(1);
        	    break;
        	case SQLTraceCounter.EV_CURSORRECOMPILE:
        	    fcRecompiled.add(1);
        	    break;
        	case SQLTraceCounter.EV_CURSORUNPREPARE:
        	    fcUnprepared.add(1);
        	    break;
        }
        return false; // no entities/counters added/removed
    }
}
