/**
 * 19-Mar-2006
 */
package com.ixora.rms.remote.agents;

import com.ixora.common.collections.CircullarLinkedList;
import com.ixora.rms.EntityDescriptorTree;
import com.ixora.rms.agents.AgentDataBuffer;
import com.ixora.rms.agents.AgentId;
import com.ixora.rms.agents.AgentPollBuffer;
import com.ixora.rms.agents.AgentState;
import com.ixora.rms.agents.HostAgentManager;

/**
 * Holds data between polling cycles.
 * @author Daniel Moraru
 */
public class AgentPollBufferManager implements HostAgentManager.Listener {
	private CircullarLinkedList<AgentDataBuffer[]> fDataBuffers;
	private CircullarLinkedList<AgentPollBuffer.NonFatalError> fNonFatalErrors;
	private CircullarLinkedList<AgentPollBuffer.StateChange> fStateChanges;
	private CircullarLinkedList<AgentPollBuffer.EntitiesChange> fEntitiesChanges;

	/**
	 *
	 */
	public AgentPollBufferManager() {
		super();
		fDataBuffers = new CircullarLinkedList<AgentDataBuffer[]>(100);
		fNonFatalErrors = new CircullarLinkedList<AgentPollBuffer.NonFatalError>(50);
		fStateChanges = new CircullarLinkedList<AgentPollBuffer.StateChange>(50);
		fEntitiesChanges = new CircullarLinkedList<AgentPollBuffer.EntitiesChange>(50);
	}

	/**
	 * @see com.ixora.rms.agents.HostAgentManager.Listener#agentStateChanged(java.lang.String, com.ixora.rms.agents.AgentId, com.ixora.rms.agents.AgentState, java.lang.Throwable)
	 */
	public synchronized void agentStateChanged(String host, AgentId agentId, AgentState state, Throwable e) {
		fStateChanges.add(new AgentPollBuffer.StateChange(host, agentId, state, e));

	}

	/**
	 * @see com.ixora.rms.agents.HostAgentManager.Listener#entitiesChanged(java.lang.String, com.ixora.rms.agents.AgentId, com.ixora.rms.EntityDescriptorTree)
	 */
	public synchronized void entitiesChanged(String host, AgentId agentId, EntityDescriptorTree entities) {
		fEntitiesChanges.add(new AgentPollBuffer.EntitiesChange(host, agentId, entities));
	}

	/**
	 * @see com.ixora.rms.agents.HostAgentManager.Listener#receiveDataBuffers(com.ixora.rms.agents.AgentDataBuffer[])
	 */
	public synchronized void receiveDataBuffers(AgentDataBuffer[] buff) {
		fDataBuffers.add(buff);
	}

	/**
	 * @see com.ixora.rms.agents.HostAgentManager.Listener#agentNonFatalError(java.lang.String, com.ixora.rms.agents.AgentId, java.lang.Throwable)
	 */
	public synchronized void agentNonFatalError(String host, AgentId agentId, Throwable t) {
		fNonFatalErrors.add(new AgentPollBuffer.NonFatalError(host, agentId, t));
	}

	/**
	 * @return
	 */
	public synchronized AgentPollBuffer getAgentPullBuffer() {
		AgentPollBuffer buff = new AgentPollBuffer(
				fDataBuffers.size() == 0 ?
						null : fDataBuffers.toArray(new AgentDataBuffer[fDataBuffers.size()][]),
				fNonFatalErrors.size() == 0 ?
						null : fNonFatalErrors.toArray(new AgentPollBuffer.NonFatalError[fNonFatalErrors.size()]),
				fStateChanges.size() == 0 ?
						null : fStateChanges.toArray(new AgentPollBuffer.StateChange[fStateChanges.size()]),
				fEntitiesChanges.size() == 0 ?
						null : fEntitiesChanges.toArray(new AgentPollBuffer.EntitiesChange[fEntitiesChanges.size()])
		);
		fDataBuffers.clear();
		fNonFatalErrors.clear();
		fStateChanges.clear();
		fEntitiesChanges.clear();
		return buff;
	}
}
