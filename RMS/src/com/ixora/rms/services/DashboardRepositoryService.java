/*
 * Created on 07-Aug-2004
 */
package com.ixora.rms.services;

import java.util.Map;

import com.ixora.rms.ResourceId;
import com.ixora.rms.EntityId;
import com.ixora.rms.repository.DashboardMap;
import com.ixora.rms.repository.exception.FailedToSaveRepository;

/**
 * Repository for query groups.
 * @author Daniel Moraru
 */
public interface DashboardRepositoryService {
	/**
	 * @param agentId
	 * @param entityId
	 * @return predefined dashboards for the given entity
	 */
	public DashboardMap getEntityDashboards(
	        String agentId,
			EntityId entityId);

	/**
	 * @param agentId
	 * @return all predefined dashboards defined at entity level for the given agent
	 */
	public Map<EntityId, DashboardMap> getEntityDashboards(
	        String agentId);

	/**
	 * Sets entity queries.
	 * @param agentId
	 * @param entityId
	 * @param groups
	 */
	public void setEntityDashboards(String agentId,
			EntityId entityId, DashboardMap groups);

	/**
	 * @param agentId
	 * @return predefined queries for the given agent
	 */
	public DashboardMap getAgentDashboards(
			String agentId);

	/**
	 * Sets agent queries.
	 * @param agentId
	 * @param groups
	 */
	public void setAgentDashboards(String agentId,
			DashboardMap groups);

	/**
	 * @return predefined global queries
	 */
	public DashboardMap getGlobalDashboards();

	/**
	 * Sets global queries.
	 * @param dtls
	 */
	public void setGlobalDashboards(DashboardMap groups);

	/**
	 * @param host
	 * @return user defined queries for the given host
	 */
	public DashboardMap getHostDashboards(
			String host);

	/**
	 * Sets user defined host queries.
	 * @param host
	 * @param groups
	 */
	public void setHostDashboards(String host,
			DashboardMap groups);

    /**
     * @param context
     * @return the dashboard map for the given context
     */
    public DashboardMap getDashboardMap(ResourceId context);

    /**
     * @param context
     * @param map
     */
    public void setDashboardMap(ResourceId context, DashboardMap map);

	/**
	 * Saves changes made to the repository.
	 * @throws FailedToSaveRepository
	 */
	public void save() throws FailedToSaveRepository;
}
