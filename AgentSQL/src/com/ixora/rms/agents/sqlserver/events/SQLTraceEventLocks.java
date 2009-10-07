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
 * SQLTraceEventLocks
 */
public class SQLTraceEventLocks extends SQLTraceChildEntity {
    private SQLCounterAccum	fcLocksAcquired;
    private SQLCounterAccum	fcLocksCanceled;
    private SQLCounterAccum	fcDeadlocks;
    private SQLCounterAccum	fcLocksEscalated;
    private SQLCounterAccum	fcLocksReleased;
    private SQLCounterAccum	fcLocksReleasedDuration;
    private SQLCounterAccum	fcLocksTimeout;

    /**
     * Default constructor
     * @param c
     */
    public SQLTraceEventLocks(EntityId idParent, AgentExecutionContext c) {
        super(idParent, Msg.SQLSERVERAGENT_ENTITY_EVENT_LOCKS, c);
        addCounter(fcLocksAcquired = new SQLCounterAccum(Msg.COUNTER_LOCKS_ACQUIRED));
        addCounter(fcLocksCanceled = new SQLCounterAccum(Msg.COUNTER_LOCKS_CANCELED));
        addCounter(fcDeadlocks = new SQLCounterAccum(Msg.COUNTER_LOCK_DEADLOCKS));
        addCounter(fcLocksEscalated = new SQLCounterAccum(Msg.COUNTER_LOCKS_ESCALATED));
        addCounter(fcLocksReleased = new SQLCounterAccum(Msg.COUNTER_LOCKS_RELEASED));
        addCounter(fcLocksReleasedDuration = new SQLCounterAccum(Msg.COUNTER_LOCKS_RELEASED_DURATION));
        addCounter(fcLocksTimeout = new SQLCounterAccum(Msg.COUNTER_LOCKS_TIMEOUT));
    }

    /**
     * Each entity must return a list of column IDs (as defined by SQL
     * server) so that the root entity knowns what tracing to enable.
     * @see com.ixora.rms.agents.sqlserver.SQLTraceChildEntity#getTracedCounters()
     */
    public List<TracedEventColumn> getTracedEventsAndColumns() {
        List<TracedEventColumn> retList = new LinkedList<TracedEventColumn>();
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOCK_ACQUIRED, SQLTraceCounter.COL_BINARYDATA));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOCK_ACQUIRED, SQLTraceCounter.COL_DURATION));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOCK_ACQUIRED, SQLTraceCounter.COL_ENDTIME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOCK_ACQUIRED, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOCK_ACQUIRED, SQLTraceCounter.COL_INDEXID));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOCK_ACQUIRED, SQLTraceCounter.COL_MODE));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOCK_ACQUIRED, SQLTraceCounter.COL_OBJECTID));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOCK_CANCEL, SQLTraceCounter.COL_BINARYDATA));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOCK_CANCEL, SQLTraceCounter.COL_DURATION));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOCK_CANCEL, SQLTraceCounter.COL_ENDTIME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOCK_CANCEL, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOCK_CANCEL, SQLTraceCounter.COL_INDEXID));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOCK_CANCEL, SQLTraceCounter.COL_MODE));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOCK_CANCEL, SQLTraceCounter.COL_OBJECTID));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOCK_DEADLOCK, SQLTraceCounter.COL_BINARYDATA));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOCK_DEADLOCK, SQLTraceCounter.COL_DURATION));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOCK_DEADLOCK, SQLTraceCounter.COL_ENDTIME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOCK_DEADLOCK, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOCK_DEADLOCK, SQLTraceCounter.COL_INDEXID));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOCK_DEADLOCK, SQLTraceCounter.COL_INTEGERDATA));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOCK_DEADLOCK, SQLTraceCounter.COL_MODE));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOCK_DEADLOCK, SQLTraceCounter.COL_OBJECTID));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOCK_DEADLOCK_CHAIN, SQLTraceCounter.COL_BINARYDATA));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOCK_DEADLOCK_CHAIN, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOCK_DEADLOCK_CHAIN, SQLTraceCounter.COL_INDEXID));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOCK_DEADLOCK_CHAIN, SQLTraceCounter.COL_INTEGERDATA));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOCK_DEADLOCK_CHAIN, SQLTraceCounter.COL_MODE));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOCK_DEADLOCK_CHAIN, SQLTraceCounter.COL_OBJECTID));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOCK_ESCALATION, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOCK_ESCALATION, SQLTraceCounter.COL_INDEXID));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOCK_ESCALATION, SQLTraceCounter.COL_MODE));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOCK_ESCALATION, SQLTraceCounter.COL_OBJECTID));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOCK_RELEASED, SQLTraceCounter.COL_BINARYDATA));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOCK_RELEASED, SQLTraceCounter.COL_DURATION));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOCK_RELEASED, SQLTraceCounter.COL_ENDTIME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOCK_RELEASED, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOCK_RELEASED, SQLTraceCounter.COL_INDEXID));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOCK_RELEASED, SQLTraceCounter.COL_OBJECTID));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOCK_TIMEOUT, SQLTraceCounter.COL_BINARYDATA));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOCK_TIMEOUT, SQLTraceCounter.COL_DURATION));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOCK_TIMEOUT, SQLTraceCounter.COL_ENDTIME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOCK_TIMEOUT, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOCK_TIMEOUT, SQLTraceCounter.COL_INDEXID));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOCK_TIMEOUT, SQLTraceCounter.COL_MODE));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOCK_TIMEOUT, SQLTraceCounter.COL_OBJECTID));
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
	    	case SQLTraceCounter.EV_LOCK_ACQUIRED:
	    	    fcLocksAcquired.add(1);
	    	    break;
	    	case SQLTraceCounter.EV_LOCK_CANCEL:
	    	    fcLocksCanceled.add(1);
	    	    break;
	    	case SQLTraceCounter.EV_LOCK_DEADLOCK:
	    	    fcDeadlocks.add(1);
	    	    break;
	    	case SQLTraceCounter.EV_LOCK_ESCALATION:
	    	    fcLocksEscalated.add(1);
	    	    break;
	    	case SQLTraceCounter.EV_LOCK_RELEASED:
	    	    fcLocksReleased.add(1);
	    	    fcLocksReleasedDuration.add(
	    	            columnData.getDouble(SQLTraceCounter.COL_DURATION));
	    	    break;
	    	case SQLTraceCounter.EV_LOCK_TIMEOUT:
	    	    fcLocksTimeout.add(1);
	    	    break;
        }
        return false; // no entities/counters added/removed
    }
}
