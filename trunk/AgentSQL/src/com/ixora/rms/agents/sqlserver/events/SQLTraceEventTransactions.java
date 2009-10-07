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
 * SQLTraceEventTransactions
 */
public class SQLTraceEventTransactions extends SQLTraceChildEntity {
    private SQLCounterAccum	fcBeginTransaction;
    private SQLCounterAccum	fcCommitTransaction;
    private SQLCounterAccum	fcTransactionDuration;
    private SQLCounterAccum	fcRollbackTransaction;
	private SQLCounterAccum	fcSavepoints;

    /**
     * Default constructor
     * @param c
     */
    public SQLTraceEventTransactions(EntityId idParent, AgentExecutionContext c) {
        super(idParent, Msg.SQLSERVERAGENT_ENTITY_EVENT_TRANSACTIONS, c);
        addCounter(fcBeginTransaction = new SQLCounterAccum(Msg.COUNTER_TXN_BEGIN));
        addCounter(fcCommitTransaction = new SQLCounterAccum(Msg.COUNTER_TXN_COMMIT));
        addCounter(fcTransactionDuration = new SQLCounterAccum(Msg.COUNTER_TXN_DURATION));
        addCounter(fcRollbackTransaction = new SQLCounterAccum(Msg.COUNTER_TXN_ROLLBACK));
        addCounter(fcSavepoints = new SQLCounterAccum(Msg.COUNTER_TXN_SAVEPOINTS));
    }

    /**
     * Each entity must return a list of column IDs (as defined by SQL
     * server) so that the root entity knowns what tracing to enable.
     * @see com.ixora.rms.agents.sqlserver.SQLTraceChildEntity#getTracedCounters()
     */
    public List<TracedEventColumn> getTracedEventsAndColumns() {
        List<TracedEventColumn> retList = new LinkedList<TracedEventColumn>();
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_DTCTRANSACTION, SQLTraceCounter.COL_BINARYDATA));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_DTCTRANSACTION, SQLTraceCounter.COL_CPU));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_DTCTRANSACTION, SQLTraceCounter.COL_DURATION));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_DTCTRANSACTION, SQLTraceCounter.COL_ENDTIME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_DTCTRANSACTION, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_DTCTRANSACTION, SQLTraceCounter.COL_EVENTSUBCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_DTCTRANSACTION, SQLTraceCounter.COL_INTEGERDATA));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_DTCTRANSACTION, SQLTraceCounter.COL_READS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_DTCTRANSACTION, SQLTraceCounter.COL_WRITES));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SQLTRANSACTION, SQLTraceCounter.COL_DURATION));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SQLTRANSACTION, SQLTraceCounter.COL_ENDTIME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SQLTRANSACTION, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SQLTRANSACTION, SQLTraceCounter.COL_EVENTSUBCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SQLTRANSACTION, SQLTraceCounter.COL_OBJECTNAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SQLTRANSACTION, SQLTraceCounter.COL_TEXTDATA));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_SQLTRANSACTION, SQLTraceCounter.COL_TRANSACTIONID));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_TRANSACTIONLOG, SQLTraceCounter.COL_BINARYDATA));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_TRANSACTIONLOG, SQLTraceCounter.COL_CPU));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_TRANSACTIONLOG, SQLTraceCounter.COL_ENDTIME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_TRANSACTIONLOG, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_TRANSACTIONLOG, SQLTraceCounter.COL_EVENTSUBCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_TRANSACTIONLOG, SQLTraceCounter.COL_INDEXID));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_TRANSACTIONLOG, SQLTraceCounter.COL_INTEGERDATA));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_TRANSACTIONLOG, SQLTraceCounter.COL_OBJECTID));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_TRANSACTIONLOG, SQLTraceCounter.COL_READS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_TRANSACTIONLOG, SQLTraceCounter.COL_TRANSACTIONID));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_TRANSACTIONLOG, SQLTraceCounter.COL_WRITES));
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
    		case SQLTraceCounter.EV_SQLTRANSACTION:
    		    switch (columnData.getInteger(SQLTraceCounter.COL_EVENTSUBCLASS)) {
    		    	case 0:
    		    	    fcBeginTransaction.add(1);
    		    	    break;
    		    	case 1:
    		    	    fcCommitTransaction.add(1);
    		    	    fcTransactionDuration.add(
    		    	            columnData.getDouble(SQLTraceCounter.COL_DURATION));
    		    	    break;
    		    	case 2:
    		    	    fcRollbackTransaction.add(1);
    		    	    fcTransactionDuration.add(
    		    	            columnData.getDouble(SQLTraceCounter.COL_DURATION));
    		    	    break;
    		    	case 3:
    		    	    fcSavepoints.add(1);
    		    	    break;
    		    }
    		    break;
        }
        return false; // no entities/counters added/removed
    }
}
