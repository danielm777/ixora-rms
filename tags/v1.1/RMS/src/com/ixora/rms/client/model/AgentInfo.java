/*
 * Created on 21-Jul-2004
 */
package com.ixora.rms.client.model;
import java.util.Collection;

import com.ixora.rms.agents.AgentState;
import com.ixora.rms.client.AgentInstanceData;
import com.ixora.rms.repository.AgentInstallationData;

/**
 * @author Daniel Moraru
 */
public interface AgentInfo  extends ArtefactInfoContainer {
	/**
	 * @return
	 */
	AgentInstallationData getInstallationDtls();
	/**
	 * @return the state of the agent
	 */
	AgentState getAgentState();
	/**
	 * @return the translated name of the agent
	 */
	String getTranslatedName();
	/**
	 * @return
	 */
	Throwable getErrorStateException();
	/**
	 * @return the deployment details
	 */
	AgentInstanceData getDeploymentDtls();
	/**
	 * @return the translated description
	 */
	String getTranslatedDescription();
	/**
	 * @return the provider instances for this agent
	 */
	Collection<ProviderInstanceInfo> getProviderInstances();
	/**
	 * @return true if it's safe to refresh its entities
	 */
	boolean safeToRefreshRecursivelly();
}