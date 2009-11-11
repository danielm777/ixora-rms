/*
 * Created on 27-Apr-2004
 */
package com.ixora.rms.services;
import com.ixora.common.Service;
import com.ixora.rms.EntityId;
import com.ixora.rms.repository.QueryMap;
import com.ixora.rms.repository.exception.FailedToSaveRepository;

/**
 * Repository for data queries.
 * @author Daniel Moraru
 */
public interface QueryRepositoryService extends Service {
	/**
	 * @param agentId
	 * @param entityId
	 * @return predefined queries for the given entity
	 */
	public QueryMap getEntityQueries(String agentId,
			EntityId entityId);

	/**
	 * Sets entity queries.
	 * @param agentId
	 * @param entityId
	 * @param queries
	 */
	public void setEntityQueries(String agentId,
			EntityId entityId, QueryMap queries);

	/**
	 * @param agentId
	 * @return predefined queries for the given agent
	 */
	public QueryMap getAgentQueries(
			String agentId);

	/**
	 * Sets agent queries.
	 * @param agentId
	 * @param queries
	 */
	public void setAgentQueries(String agentId,
			QueryMap queries);

	/**
	 * @return predefined global queries
	 */
	public QueryMap getGlobalQueries();

	/**
	 * Sets global queries.
	 * @param queries
	 */
	public void setGlobalQueries(QueryMap queries);

	/**
	 * @param host
	 * @return user defined queries for the given host
	 */
	public QueryMap getHostQueries(
			String host);

	/**
	 * Sets user defined host queries.
	 * @param host
	 * @param queries
	 */
	public void setHostQueries(String host,
			QueryMap queries);

	/**
	 * Saves changes made to the repository.
	 * @throws FailedToSaveRepository
	 */
	public void save() throws FailedToSaveRepository;
}