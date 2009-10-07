/*
 * Created on 21-Apr-2005
 */
package com.ixora.rms.agents.sqlserver;

import java.util.List;

import com.ixora.rms.EntityId;
import com.ixora.rms.agents.AgentExecutionContext;
import com.ixora.rms.agents.impl.Entity;
import com.ixora.rms.agents.sqlserver.events.TracedEventColumn;

/**
 * SQLTraceChildEntity
 */
public abstract class SQLTraceChildEntity extends Entity {

    /**
     * Calls default constructor
     * @param id
     * @param description
     * @param c
     */
    protected SQLTraceChildEntity(EntityId idParent, String name, AgentExecutionContext c) {
		super(new EntityId(idParent, name), name + ".desc", c);
	}

    /**
     * Each entity must return a list of column IDs (as defined by SQL
     * server) so that the root entity knowns what tracing to enable.
     * @see com.ixora.rms.agents.sqlserver.SQLTraceChildEntity#getTracedCounters()
     */
    public abstract List<TracedEventColumn> getTracedEventsAndColumns();

    /**
     * @see com.ixora.rms.agents.impl.Entity#retrieveCounterValues()
     */
    protected void retrieveCounterValues() throws Throwable {
        // nothing
    }

    /**
     * Called by the main trace entity when an event is received. This
     * entity is supposed to inspect the data here and update its counters
     * @param eventId
     * @param columnData
     */
    public abstract boolean eventReceived(int eventId, SqlTrace.SQLColumnData columnData);
}
