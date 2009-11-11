/*
 * Created on 13-Aug-2004
 */
package com.ixora.rms.client.model;

import java.util.List;
import java.util.Map;

import com.ixora.rms.ResourceId;
import com.ixora.rms.CounterId;
import com.ixora.rms.dataengine.definitions.QueryDef;

/**
 * @author Daniel Moraru
 */
public interface CounterModelHelper {
    /**
     * Rolls back the counters belonging to the given entity.
     * @param en
     */
    void rollbackCounters(EntityNode en);

    /**
     * Rolls back the given counter.
     * @param en
     * @param cid
     * @param triggerUpdateEvent
     */
    void rollbackCounter(EntityNode en, CounterId cid, boolean triggerUpdateEvent);

    /**
     * Sets the given flag for the given counter.
     * @param en
     * @param cid
     * @param flag CounterInfo.ENABLED or PLOTTED
     * @param value
     * @param triggerUpdateEvent
     */
    void setCounterFlag(EntityNode en, CounterId cid, int flag, boolean value, boolean triggerUpdateEvent);

    /**
     * Sets the given flag for the given counter.
     * @param rid
     * @param flag CounterInfo.ENABLED or PLOTTED
     * @param value
     * @param triggerUpdateEvent
     */
    void setCounterFlag(ResourceId cid, int flag, boolean value, boolean triggerUpdateEvent);

    /**
     * Rolls back the given counter.
     * @param counterId
     * @param triggerUpdateEvent
     */
    void rollbackCounter(ResourceId counterId, boolean triggerUpdateEvent);

    /**
     * Returns a map (key: resourceId, value: list of ResourceInfo) with counter info
     * per entity map for all counters used by the given query.
     * @param rid a regex or non-regex resource id
     * @return a map of resource info per entity id
     */
    Map<ResourceId, List<ResourceInfo>> getCounterInfoPerEntity(ResourceId context, QueryDef query);
    /**
     * @param counter
     * @return
     */
    ResourceInfo getCounterInfo(ResourceId counter);
}