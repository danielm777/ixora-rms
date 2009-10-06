/*
 * Created on 14-Sep-2004
 */
package com.ixora.rms.client;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import com.ixora.rms.ResourceId;
import com.ixora.rms.client.model.ResourceInfo;
import com.ixora.rms.dataengine.definitions.QueryDef;
import com.ixora.rms.exception.InvalidAgentState;
import com.ixora.rms.exception.InvalidConfiguration;
import com.ixora.rms.exception.InvalidEntity;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.repository.QueryId;

/**
 * This class manages a set of queries making sure that
 * the monitoring session will be modified to satisfy all registered items.
 * @author Daniel Moraru
 */
public interface QueryRealizer {
	/**
	 * Callback.
	 */
	public interface Callback {
		/**
		 * Invoked when the monitoring level must be increased in order
		 * to satisfy a query.
		 * @param counters the list of counters requiring an increase in the momitoring level
		 * @return
		 */
		boolean acceptIncreaseInMonitoringLevel(List<ResourceInfo> counters);
	}

    /**
     * Enables all counters that are needed by the given
     * query and registers it so that changes in the session model
     * will be monitored and the session reconfigured to satisfy the registered
     * query.
     * @param context the context of the given query
     * @param query
     * @param callback
     * @throws InvalidAgentState
     * @throws InvalidConfiguration
     * @throws InvalidEntity
     * @throws RemoteException
     * @throws RMSException
     */
    void realizeQuery(ResourceId context, QueryDef query, Callback callback) throws InvalidEntity,
            InvalidConfiguration, InvalidAgentState, RMSException,
            RemoteException;

    /**
     * Tries to disable all counters that were used by the given
     * query and unregisters it from this query realizer.
     * @param queryId
     * @param force if true the query will be unrealized and
     * unregistered with the query manager even if the reference count is not zero
     * @throws InvalidAgentState
     * @throws InvalidConfiguration
     * @throws InvalidEntity
     * @throws RemoteException
     * @throws RMSException
     */
    void unrealizeQuery(QueryId queryId, boolean force) throws InvalidEntity,
            InvalidConfiguration, InvalidAgentState, RMSException,
            RemoteException;

    /**
     * @return the registered queries
     */
    Map<QueryId, QueryDef> getRegisteredQueries();

    /**
     * @param queryId
     * @return true if the query with the given identifier has already
     * been registered with the query realizer
     */
    boolean isQueryRegistered(QueryId queryId);
}