/*
 * Created on 16-Nov-2003
 */
package com.ixora.rms.remote.agents;

import java.rmi.RemoteException;

import com.ixora.remote.RemoteManaged;
import com.ixora.rms.EntityConfiguration;
import com.ixora.rms.EntityDescriptorTree;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.AgentActivationTuple;
import com.ixora.rms.agents.AgentConfiguration;
import com.ixora.rms.agents.AgentConfigurationTuple;
import com.ixora.rms.agents.AgentPollBuffer;
import com.ixora.rms.agents.AgentId;
import com.ixora.rms.agents.AgentState;
import com.ixora.rms.exception.AgentIsNotInstalled;
import com.ixora.rms.exception.InvalidAgentState;
import com.ixora.rms.exception.InvalidConfiguration;
import com.ixora.rms.exception.InvalidEntity;
import com.ixora.rms.exception.RMSException;

/**
 * Agent manager.
 */
public interface RemoteAgentManager extends RemoteManaged {
	/**
	 * Activates the monitoring agent with the given name and
	 * configuration.
	 * @param agentId agent id
	 * @throws RemoteException
	 * @throws AgentIsNotInstalled
	 * @throws InvalidConfiguration
	 * @throws InvalidAgentState
	 * @throws RMSException
	 * @return the available monitored entities and the agent id
	 */
	AgentActivationTuple activateAgent(
		String agentInstallationId,
		AgentConfiguration conf)
		throws AgentIsNotInstalled, InvalidConfiguration,
				InvalidAgentState,
				RMSException, RemoteException;

	/**
	 * Deactivates the given agent.
	 * @param agentId
	 * @throws RMSException
	 * @throws RemoteException
	 */
	void deactivateAgent(AgentId agentId)
		throws RMSException, RemoteException;

	/**
	 * Starts the monitoring agent with the given name.
	 * @param agentId
	 * @throws InvalidAgentState
	 * @throws RMSException
	 * @throws RemoteException
	 */
	void startAgent(AgentId agentId)
		throws InvalidAgentState,
			RMSException, RemoteException;

	/**
	 * Stops the monitoring agent with the given id.
	 * @param agentId
	 * @throws InvalidAgentState
	 * @throws RMSException
	 * @throws RemoteException
	 */
	void stopAgent(AgentId agentId)
		throws InvalidAgentState,
			RMSException, RemoteException;

	/**
	 * Starts all monitoring agents.
	 * @throws RMSException
	 * @throws RemoteException
	 */
	void startAllAgents() throws
			RMSException, RemoteException;

	/**
	 * Stops all monitoring agents.
	 * @throws RMSException
	 * @throws RemoteException
	 */
	void stopAllAgents() throws
			RMSException, RemoteException;

	/**
	 * Deactivates all monitoring agents.
	 * @throws RMSException
	 * @throws RemoteException
	 */
	void deactivateAllAgents() throws
			RMSException, RemoteException;

	/**
	 * Updates the configuration of the monitoring agent with the given name.
	 * @param agentId
	 * @param conf
	 * @throws InvalidConfiguration
	 * @throws RMSException
	 * @throws InvalidAgentState
	 * @throws RemoteException
	 */
	AgentConfigurationTuple configureAgent(AgentId agentId, AgentConfiguration conf)
		throws InvalidConfiguration, InvalidAgentState,
			RMSException, RemoteException;

	/**
	 * Updates the configuration of all the monitoring agents.
	 * @param conf
	 * @throws InvalidConfiguration
	 * @throws RMSException
	 * @throws RemoteException
	 */
	AgentConfigurationTuple[] configureAllAgents(AgentConfiguration conf)
		throws InvalidConfiguration, RMSException, RemoteException;

	/**
	 * Updates the configuration of the given monitored entity.
	 * @param agentId
	 * @param entity
	 * @param conf
	 * @throws InvalidConfiguration
	 * @throws InvalidEntity
	 * @throws RMSException
	 * @throws InvalidAgentState
	 * @throws RemoteException
	 */
	EntityDescriptorTree configureEntity(AgentId agentId, EntityId entity, EntityConfiguration conf)
		throws InvalidConfiguration, InvalidEntity, InvalidAgentState,
			RMSException, RemoteException;

	/**
	 * @param agentId
	 * @param parent the parent entity
	 * @return the monitored children entities for the parent
	 * entity
	 * @param recursive
	 * @param refresh
	 * @throws RMSException
	 * @throws RemoteException
	 */
	EntityDescriptorTree getEntities(AgentId agentId, EntityId parent, boolean recursive, boolean refresh)
		throws RMSException, RemoteException;

	/**
	 * @return the state of the given agent, null if the
	 * agent is not found
	 * @param agentId
	 */
	AgentState getAgentState(AgentId agentId) throws RMSException, RemoteException;

	/**
	 * This method accomodates clients that prefer polling for data as oposed to using
	 * the event based approach (the latter, while providing a better user experience, requires
	 * bidirectional communication between the client and this class which is not always available due
	 * to firewalls)
	 * @return
	 * @throws RMSException
	 * @throws RemoteException
	 */
	AgentPollBuffer getAgentPollBuffer() throws RMSException, RemoteException;
}
