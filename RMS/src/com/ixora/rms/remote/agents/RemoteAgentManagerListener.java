package com.ixora.rms.remote.agents;

import java.rmi.RemoteException;

import com.ixora.remote.RemoteManagedListener;
import com.ixora.rms.EntityDescriptorTree;
import com.ixora.rms.agents.AgentDataBuffer;
import com.ixora.rms.agents.AgentId;
import com.ixora.rms.agents.AgentState;

/**
 * Remote agent manager listener.
 * @author Daniel Moraru
 */
public interface RemoteAgentManagerListener extends RemoteManagedListener {
	/**
	 * Invoked when the state of a monitoring agent changed.
	 * @param host the host where the monitoring agent resides
	 * @param agentId the name of the agent whose state has changed
	 * @param state the new state
	 * @param e the exception that caused the error state
	 * @throws RemoteException
	 */
	void monitoringAgentStateChanged(
			String host,
			AgentId agentId,
			AgentState state,
			Throwable e) throws RemoteException;

	/**
	 * Invoked when the monitored entities changed.
	 * @param host the host where the monitoring agent resides
	 * @param agentId the name of the agent whose entities have changed
	 * @param entities the new children entities
	 * @throws RemoteException
	 */
	void monitoredEntitiesChanged(String host,
					AgentId agentId,
					EntityDescriptorTree entities) throws RemoteException;

	/**
	 * Invoked when new data buffer is available.
	 * @param buffs data buffers
	 * @throws RemoteException
	 */
	void receiveDataBuffers(AgentDataBuffer[] buffs) throws RemoteException;

	/**
	 * Invoked when the given agent signals a non fatal error.
	 * @param host
	 * @param agentId
	 * @param t
	 * @throws RemoteException
	 */
	void monitoringAgentNonFatalError(
					String host,
					AgentId agentId,
					Throwable t) throws RemoteException;
}
