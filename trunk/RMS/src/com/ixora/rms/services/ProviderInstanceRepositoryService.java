/*
 * Created on 31-Dec-2004
 */
package com.ixora.rms.services;

import com.ixora.common.Service;
import com.ixora.rms.repository.ProviderInstanceMap;
import com.ixora.rms.repository.exception.FailedToSaveRepository;

/**
 * @author Daniel Moraru
 */
public interface ProviderInstanceRepositoryService extends Service {
	/**
	 * @param agentId
	 * @return provider instances for the given agent
	 */
	ProviderInstanceMap getAgentProviderInstances(
			String agentId);

	/**
	 * @param agentId
	 * @return optional provider instances for the given agent
	 */
	ProviderInstanceMap getOptionalAgentProviderInstances(
			String agentId);

	/**
	 * Sets agent provider instances.
	 * @param agentId
	 * @param queries
	 */
	void setAgentProviderInstances(String agentId,
			ProviderInstanceMap providerInstance);

	/**
	 * Saves the repository.
	 * @throws FailedToSaveRepository
	 */
	void save() throws FailedToSaveRepository;
}
