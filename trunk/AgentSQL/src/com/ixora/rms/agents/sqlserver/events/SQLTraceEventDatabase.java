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
 * SQLTraceEventDatabase
 */
public class SQLTraceEventDatabase extends SQLTraceChildEntity {
	private static final long serialVersionUID = 6195397918265338059L;
	private SQLCounterAccum	fcDataFileAutoGrows;
    private SQLCounterAccum	fcDataFileAutoGrowsDuration;
    private SQLCounterAccum	fcDataFileAutoGrowsSize;
    private SQLCounterAccum	fcDataFileAutoShrinks;
    private SQLCounterAccum	fcDataFileAutoShrinksDuration;
    private SQLCounterAccum	fcDataFileAutoShrinksSize;
    private SQLCounterAccum	fcLogFileAutoGrows;
    private SQLCounterAccum	fcLogFileAutoGrowsDuration;
    private SQLCounterAccum	fcLogFileAutoGrowsSize;
    private SQLCounterAccum	fcLogFileAutoShrinks;
    private SQLCounterAccum	fcLogFileAutoShrinksDuration;
    private SQLCounterAccum	fcLogFileAutoShrinksSize;

    /**
     * Default constructor
     * @param c
     */
    public SQLTraceEventDatabase(EntityId idParent, AgentExecutionContext c) {
        super(idParent, Msg.SQLSERVERAGENT_ENTITY_EVENT_DATABASE, c);
        addCounter(fcDataFileAutoGrows = new SQLCounterAccum(Msg.COUNTER_DB_DATA_GROWS));
        addCounter(fcDataFileAutoGrowsDuration = new SQLCounterAccum(Msg.COUNTER_DB_DATA_GROWS_DURATION));
        addCounter(fcDataFileAutoGrowsSize = new SQLCounterAccum(Msg.COUNTER_DB_DATA_GROWS_SIZE));
        addCounter(fcDataFileAutoShrinks = new SQLCounterAccum(Msg.COUNTER_DB_DATA_SHRINKS));
        addCounter(fcDataFileAutoShrinksDuration = new SQLCounterAccum(Msg.COUNTER_DB_DATA_SHRINKS_DURATION));
        addCounter(fcDataFileAutoShrinksSize = new SQLCounterAccum(Msg.COUNTER_DB_DATA_SHRINKS_SIZE));
        addCounter(fcLogFileAutoGrows = new SQLCounterAccum(Msg.COUNTER_DB_LOG_GROWS));
        addCounter(fcLogFileAutoGrowsDuration = new SQLCounterAccum(Msg.COUNTER_DB_LOG_GROWS_DURATION));
        addCounter(fcLogFileAutoGrowsSize = new SQLCounterAccum(Msg.COUNTER_DB_LOG_SIZE));
        addCounter(fcLogFileAutoShrinks = new SQLCounterAccum(Msg.COUNTER_DB_LOG_SHRINKS));
        addCounter(fcLogFileAutoShrinksDuration = new SQLCounterAccum(Msg.COUNTER_DB_LOG_SHRINKS_DURATION));
        addCounter(fcLogFileAutoShrinksSize = new SQLCounterAccum(Msg.COUNTER_DB_LOG_SHRINKS_SIZE));
    }

    /**
     * Each entity must return a list of column IDs (as defined by SQL
     * server) so that the root entity knowns what tracing to enable.
     * @see com.ixora.rms.agents.sqlserver.SQLTraceChildEntity#getTracedCounters()
     */
    public List<TracedEventColumn> getTracedEventsAndColumns() {
        List<TracedEventColumn> retList = new LinkedList<TracedEventColumn>();
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_DATA_FILE_AUTO_GROW, SQLTraceCounter.COL_DURATION));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_DATA_FILE_AUTO_GROW, SQLTraceCounter.COL_ENDTIME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_DATA_FILE_AUTO_GROW, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_DATA_FILE_AUTO_GROW, SQLTraceCounter.COL_FILENAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_DATA_FILE_AUTO_GROW, SQLTraceCounter.COL_INTEGERDATA));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_DATA_FILE_AUTO_SHRINK, SQLTraceCounter.COL_DURATION));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_DATA_FILE_AUTO_SHRINK, SQLTraceCounter.COL_ENDTIME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_DATA_FILE_AUTO_SHRINK, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_DATA_FILE_AUTO_SHRINK, SQLTraceCounter.COL_FILENAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_DATA_FILE_AUTO_SHRINK, SQLTraceCounter.COL_INTEGERDATA));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOG_FILE_AUTO_GROW, SQLTraceCounter.COL_DURATION));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOG_FILE_AUTO_GROW, SQLTraceCounter.COL_ENDTIME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOG_FILE_AUTO_GROW, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOG_FILE_AUTO_GROW, SQLTraceCounter.COL_FILENAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOG_FILE_AUTO_GROW, SQLTraceCounter.COL_INTEGERDATA));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOG_FILE_AUTO_SHRINK, SQLTraceCounter.COL_DURATION));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOG_FILE_AUTO_SHRINK, SQLTraceCounter.COL_ENDTIME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOG_FILE_AUTO_SHRINK, SQLTraceCounter.COL_EVENTCLASS));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOG_FILE_AUTO_SHRINK, SQLTraceCounter.COL_FILENAME));
        retList.add(new TracedEventColumn(SQLTraceCounter.EV_LOG_FILE_AUTO_SHRINK, SQLTraceCounter.COL_INTEGERDATA));
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
    		case SQLTraceCounter.EV_DATA_FILE_AUTO_GROW:
    		    fcDataFileAutoGrows.add(1);
    		    fcDataFileAutoGrowsDuration.add(
    		            columnData.getDouble(SQLTraceCounter.COL_DURATION));
    		    fcDataFileAutoGrowsSize.add(
    		            columnData.getDouble(SQLTraceCounter.COL_INTEGERDATA));
    		    break;
    		case SQLTraceCounter.EV_DATA_FILE_AUTO_SHRINK:
    		    fcDataFileAutoShrinks.add(1);
    		    fcDataFileAutoShrinksDuration.add(
    		            columnData.getDouble(SQLTraceCounter.COL_DURATION));
    		    fcDataFileAutoShrinksSize.add(
    		            columnData.getDouble(SQLTraceCounter.COL_INTEGERDATA));
    		    break;
    		case SQLTraceCounter.EV_LOG_FILE_AUTO_GROW:
    		    fcLogFileAutoGrows.add(1);
    		    fcLogFileAutoGrowsDuration.add(
    		            columnData.getDouble(SQLTraceCounter.COL_DURATION));
    		    fcLogFileAutoGrowsSize.add(
    		            columnData.getDouble(SQLTraceCounter.COL_INTEGERDATA));
    		    break;
    		case SQLTraceCounter.EV_LOG_FILE_AUTO_SHRINK:
    		    fcLogFileAutoShrinks.add(1);
    		    fcLogFileAutoShrinksDuration.add(
    		            columnData.getDouble(SQLTraceCounter.COL_DURATION));
    		    fcLogFileAutoShrinksSize.add(
    		            columnData.getDouble(SQLTraceCounter.COL_INTEGERDATA));
    		    break;
        }
        return false; // no entities/counters added/removed
    }

}
