/*
 * Created on 27-Apr-2004
 */
package com.ixora.rms.services;
import java.util.Map;

import com.ixora.rms.ResourceId;
import com.ixora.common.Service;
import com.ixora.rms.EntityId;
import com.ixora.rms.repository.DataViewMap;
import com.ixora.rms.repository.exception.FailedToSaveRepository;

/**
 * Repository for data views.
 * @author Daniel Moraru
 */
public interface DataViewRepositoryService extends Service {
	/**
	 * @param agentId
	 * @param entityId
	 * @return predefined views for the given entity
	 */
	public DataViewMap getEntityDataViews(String agentId,
			EntityId entityId);

	/**
	 * @param agentId
	 * @return all predefined views defined at entity level for the given agent
	 */
	public Map<EntityId, DataViewMap> getEntityDataViews(String agentId);

	/**
	 * Sets entity views.
	 * @param agentId
	 * @param entityId
	 * @param views
	 */
	public void setEntityDataViews(String agentId,
			EntityId entityId, DataViewMap views);

	/**
	 * @param agentId
	 * @return predefined views for the given agent
	 */
	public DataViewMap getAgentDataViews(
			String agentId);

	/**
	 * Sets agent queries.
	 * @param agentId
	 * @param views
	 */
	public void setAgentDataViews(String agentId,
	        DataViewMap views);

	/**
	 * @return predefined global views
	 */
	public DataViewMap getGlobalDataViews();

	/**
	 * Sets global views.
	 * @param views
	 */
	public void setGlobalDataViews(DataViewMap queries);

	/**
	 * @param host
	 * @return user defined views for the given host
	 */
	public DataViewMap getHostDataViews(
			String host);

	/**
	 * Sets user defined host views.
	 * @param host
	 * @param views
	 */
	public void setHostDataViews(String host,
	        DataViewMap views);

	/**
	 * @param context
	 * @return
	 */
	public DataViewMap getDataViewMap(ResourceId context);

   /**
     * @param context
     */
    public void setDataViewMap(ResourceId context, DataViewMap map);

   	/**
	 * Saves changes made to the repository.
	 * @throws FailedToSaveRepository
	 */
	public void save() throws FailedToSaveRepository;
}