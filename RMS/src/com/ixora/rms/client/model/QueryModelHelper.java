/*
 * Created on 13-Aug-2004
 */
package com.ixora.rms.client.model;

import java.util.Collection;
import java.util.Map;

import com.ixora.rms.ResourceId;
import com.ixora.rms.dataengine.definitions.QueryDef;

/**
 * @author Daniel Moraru
 */
public interface QueryModelHelper {
    /**
     * Sets the flag for the given query.
     * @param flag
     * @param id
     * @param queryName
     * @param value
     * @param commit
     * @param updateCounters whether or not to update the state
     * of the required counters; depending on the context where this method is called
     * it might not be necessary to update the state of the counters
     */
    void setQueryFlag(int flag, ResourceId id, String queryName,
            boolean value, boolean commit, boolean updateCounters);

    /**
     * Commits the query with the given name
     * @param id
     * @param queryName
     */
    void commitQuery(ResourceId id, String queryName);

    /**
     * Sets the queries associated with the given resource.
     * @param id a valid, non regex resource id
     * @param q
     */
    void setQueries(ResourceId id, QueryDef[] q);

    /**
     * Rolls back the queries belonging to the given
     * resource.
     * @param id
     */
    void rollbackQueries(ResourceId id);

    /**
     * Rolls back the query belonging to the given
     * resource.
     * @param id
     * @param queryName
     */
    void rollbackQuery(ResourceId id, String queryName);

    /**
     * Adds the given query to the given context.
     * @param id
     * @param query
     */
    void addQuery(ResourceId id, QueryDef query);

    /**
     * Removes the query with the given name from the given context.
     * @param id
     * @param query
     */
    void removeQuery(ResourceId id, String queryName);

    /**
     * Key: ResourceId context
     * Value: Collection of QueryInfo
     * @return all queries that are realizable
     */
    Map<ResourceId, Collection<QueryInfo>> getAllQueriesToRealize();

    /**
     * Key: ResourceId context
     * Value: Collection of QueryInfo
     * @return all queries that are unrealizable
     */
    Map<ResourceId, Collection<QueryInfo>> getAllQueriesToUnRealize();

    /**
	 * Returns true if all counters required by the given
	 * query are present, are enabled and committed.<br>
	 * Used by the log replay view to filter out queries whose
	 * counters are not available.
	 * @return
	 */
	public boolean isQueryReady(ResourceId context, QueryDef query);
}