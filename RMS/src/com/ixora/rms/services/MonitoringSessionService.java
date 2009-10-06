/*
 * Created on 19-Nov-2003
 */
package com.ixora.rms.services;

import java.rmi.RemoteException;

import com.ixora.common.Service;
import com.ixora.rms.EntityConfiguration;
import com.ixora.rms.EntityDescriptorTree;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.AgentActivationData;
import com.ixora.rms.agents.AgentActivationTuple;
import com.ixora.rms.agents.AgentConfiguration;
import com.ixora.rms.agents.AgentConfigurationTuple;
import com.ixora.rms.agents.AgentId;
import com.ixora.rms.agents.AgentState;
import com.ixora.rms.exception.AgentIsNotInstalled;
import com.ixora.rms.exception.InvalidAgentState;
import com.ixora.rms.exception.InvalidConfiguration;
import com.ixora.rms.exception.InvalidEntity;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.exception.UnreachableHostManager;
import com.ixora.rms.providers.ProviderState;

/**
 * MonitoringManagerService
 * @author Daniel Moraru
 */
public interface MonitoringSessionService extends Service {
	/**
	 * Listener.
	 */
	public interface Listener {
		/**
		 * Invoked when the state of a monitoring agent changed.
		 * @param host the host where the monitoring agent resides
		 * @param agentId the agent whose state has changed
		 * @param state the new state
		 * @param e the exception that caused the error state
		 */
		void agentStateChanged(String host,
						AgentId agentId, AgentState state, Throwable e);

		/**
		 * Invoked when the monitored entities for an agent changed.
		 * @param host the host where the monitoring agent resides
		 * @param agentId the agent whose entities have changed
		 * @param entities the new children entities
		 */
		void entitiesChanged(String host,
						AgentId agentId,
						EntityDescriptorTree entities);
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

		/**
		 * Invoked when the state of a provider belonging to a monitoring agent changed.
		 * @param host the host where the monitoring agent resides
		 * @param agentId the agent who owns the provider whose state has changed
		 * @param state the new state
		 * @param e the exception that caused the error state
		 */
		void providerStateChanged(String host,
						AgentId agentId, String providerInstanceName,
						ProviderState state, Throwable e);
	}

	/**
	 * Activates a monitoring agent on a remote host.
	 * @param activationData
	 * @throws UnreachableHostManager
	 * @throws AgentIsNotInstalled
	 * @throws InvalidAgentState
	 * @throws InvalidConfiguration
	 * @throws RMSException
	 * @throws RemoteException
	 * @return the initial available monitored entities and the agent id
	 */
	AgentActivationTuple activateAgent(
			AgentActivationData activationData)
				throws UnreachableHostManager,
				AgentIsNotInstalled,
				InvalidAgentState,
				InvalidConfiguration,
				RMSException,
				RemoteException;

	/**
	 * Deactivates a monitoring agent on a remote host.
	 * @param host
	 * @param agentId
	 * @throws RMSException
	 * @throws RemoteException
	 */
	void deactivateAgent(String host, AgentId agentId)
				throws RMSException,
				RemoteException;

	/**
	 * Updates the configuration of the monitoring agent with the given name.
	 * @param host
	 * @param agentId
	 * @param conf
	 * @throws InvalidConfiguration
	 * @throws InvalidAgentState
	 * @throws RMSException
	 */
	AgentConfigurationTuple configureAgent(String host, AgentId agentId,
				AgentConfiguration conf)
		throws
			InvalidConfiguration,
			InvalidAgentState,
			RMSException,
			RemoteException;

	/**
	 * Updates the configuration of the monitoring agent with the given name.
	 * @param host
	 * @param agentId
	 * @param entity
	 * @param conf
	 * @throws InvalidEntity
	 * @throws InvalidConfiguration
	 * @throws InvalidAgentState
	 * @throws RMSException
	 * @throws RemoteException
	 */
	EntityDescriptorTree configureEntity(String host, AgentId agentId,
				EntityId entity, EntityConfiguration conf)
		throws
			InvalidEntity,
			InvalidConfiguration,
			InvalidAgentState,
			RMSException,
			RemoteException;

	/**
	 * Starts a monitoring agent.
	 * @param host
	 * @param agentId
	 * @throws InvalidAgentState
	 * @throws RMSException
	 * @throws RemoteException
	 * @throws StartableError
	 */
	void startAgent(String host, AgentId agentId)
			throws InvalidAgentState,
			RMSException,
			RemoteException;

	/**
	 * Stops a monitoring agent.
	 * @param agentId
	 * @param host
	 * @throws InvalidAgentState
	 * @throws RMSException
	 * @throws RemoteException
	 */
	void stopAgent(String host, AgentId agentId)
			throws InvalidAgentState,
			RMSException,
			RemoteException;

	/**
	 * Starts all monitoring agents.
	 * @throws InvalidAgentState
	 * @throws RMSException
	 * @throws RemoteException
	 */
	void startAllAgents()
		throws InvalidAgentState,
		RMSException,
		RemoteException;

	/**
	 * Stops all monitoring agents.
	 */
	void stopAllAgents();

	/**
	 * Deactivates all monitoring agents.
	 */
	void deactivateAllAgents();

	/**
	 * @param agentId
	 * @param parent the parent entity
	 * @param recursive
	 * @param refresh
	 * @return the monitored children entities for the parent
	 * entity as well as the descriptor of the parent entity (at index 0)
	 * @throws InvalidAgentState
	 * @throws RMSException
	 */
	EntityDescriptorTree getAgentEntities(String host, AgentId agentId, EntityId parent, boolean recursive, boolean refresh)
		throws
			InvalidAgentState,
			RMSException,
			RemoteException;

	/**
	 * @return the state of the given monitoring agent on the given host.
	 * @param host
	 * @param agentId
	 * @throws RMSException
	 * @throws RemoteException
	 */
	AgentState getAgentState(String host, AgentId agentId) throws RMSException, RemoteException;

	/**
	 * Adds a listener.
	 * @param listener
	 */
	void addListener(Listener listener);
	/**
	 * Removes a listener.
	 * @param listener
	 */
	void removeListener(Listener listener);
}
