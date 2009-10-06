/*
 * Created on 28-Dec-2004
 */
package com.ixora.rms.agents;

import com.ixora.rms.EntityConfiguration;
import com.ixora.rms.EntityDescriptorTree;
import com.ixora.rms.EntityId;
import com.ixora.rms.exception.AgentIsNotInstalled;
import com.ixora.rms.exception.InvalidAgentState;
import com.ixora.rms.exception.InvalidConfiguration;
import com.ixora.rms.exception.InvalidEntity;
import com.ixora.rms.exception.RMSException;

/**
 * @author Daniel Moraru
 */
public interface HostAgentManager {
	/**
	 * HostAgentManager listener.
	 */
	public interface Listener {
		/**
		 * Invoked when the state of a monitoring agent changed.
		 * @param host the monitored host
		 * @param agentId the id of the agent whose state has changed
		 * @param state the new state
		 * @param e might be non null only if the state is
		 * <code>StartableState.ERROR</code>
		 */
		void agentStateChanged(String host,
						AgentId agentId,
						AgentState state,
						Throwable e);
		/**
		 * Invoked when the monitored entities changed.
		 * @param host the host where the monitoring agent resides
		 * @param agentId the id of the agent whose entities have changed
		 * @param entities the new children entities
		 */
		void entitiesChanged(String host,
						AgentId agentId,
						EntityDescriptorTree entities);
		/**
		 * Invoked when new data buffers are available.
		 * @param buff
		 */
		void receiveDataBuffers(AgentDataBuffer[] buff);
		/**
		 * Invoked when a non fatal exception was signaled by
		 * the given agent.
		 * @param host
		 * @param agentId
		 * @param t
		 */
		void agentNonFatalError(
						String host,
						AgentId agentId,
						Throwable t);
	}

	/**
	 * Activates the given agent.
	 * @param agentInstallationId
	 * @param conf
	 * @return the available entities
	 * @throws AgentIsNotInstalled
	 * @throws InvalidAgentState
	 * @throws InvalidConfiguration
	 * @throws RMSException
	 */
	AgentActivationTuple activateAgent(
			String agentInstallationId,
			AgentConfiguration conf) throws AgentIsNotInstalled,
			InvalidAgentState, InvalidConfiguration, RMSException;

	/**
	 * Deactivates the given agent.
	 * @param agentId
	 * @throws InvalidAgentState
	 * @throws RMSException
	 */
	void deactivateAgent(AgentId agentId) throws InvalidAgentState, RMSException;

	/**
	 * Deactivates all agents.
	 */
	void deactivateAllAgents();

	/**
	 * Starts all agents.
	 * @throws StartableError
	 */
	void startAllAgents();

	/**
	 * Stops all agents.
	 */
	void stopAllAgents();

	/**
	 * Configures all agents.
	 * @throws InvalidConfiguration
	 * @throws RMSException
	 */
	AgentConfigurationTuple[] configureAllAgents(AgentConfiguration conf)
			throws InvalidConfiguration, RMSException;

	/**
	 * Starts the given agent.
	 * @param agentId
	 */
	void startAgent(AgentId agentId) throws InvalidAgentState, RMSException;

	/**
	 * Stops the given agent.
	 * @param agentId
	 */
	void stopAgent(AgentId agentId) throws InvalidAgentState, RMSException;

	/**
	 * Configures the given agent.
	 * @param agent
	 * @param conf
	 * @return
	 * @throws InvalidAgentState
	 * @throws InvalidConfiguration
	 * @throws RMSException
	 */
	AgentConfigurationTuple configureAgent(AgentId agent, AgentConfiguration conf)
			throws InvalidConfiguration, InvalidAgentState, RMSException;

	/**
	 * Configures the given entity.
	 * @param agent
	 * @param conf
	 * @return the subtree started at <code>entity</code> that was affected by the change in configuration
	 * @throws InvalidAgentState
	 * @throws InvalidConfiguration
	 * @throws InvalidEntity
	 */
	EntityDescriptorTree configureEntity(AgentId agent, EntityId entity, EntityConfiguration conf)
			throws InvalidConfiguration, InvalidAgentState, InvalidEntity,
			RMSException;

	/**
	 * Returns the children entities of the given entity on the
	 * given agent.
	 * @param agent
	 * @param parent
	 * @param recursive
	 * @param refresh
	 * @return the available entities for the given agent and parent entity; the tree starts
	 * with the entity descriptor for parent
	 * @throws InvalidAgentState
	 * @throws RMSException
	 */
	EntityDescriptorTree getEntities(AgentId agent, EntityId parent, boolean recursive, boolean refresh)
			throws InvalidAgentState, RMSException;

	/**
	 * @return the state of the given agent, null if the
	 * agent is not found
	 * @param agentId
	 */
	AgentState getAgentState(AgentId agentId);
}