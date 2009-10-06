/**
 * 12-Jul-2005
 */
package com.ixora.rms.agents;

import com.ixora.rms.HostDataBuffer;
import com.ixora.rms.EntityDataBuffer;

/**
 * @author Daniel Moraru
 */
//NOTE: this class needs setters and getters for all members
//as it will be persisted and restored for data logging...
public interface AgentDataBuffer extends HostDataBuffer {
	/**
	 * @see com.ixora.rms.HostDataBufferImpl#isValid()
	 */
	boolean isValid();

	/**
	 * @return true if there is no data in this buffer
	 */
	boolean isEmpty();

	/**
	 * @return
	 */
	EntityDataBuffer[] getBuffers();

	/**
	 * @return
	 */
	AgentId getAgent();

	/**
	 * @return the descriptor of the agent that built this data buffer; it is not null
	 * only when changes take place in the agent.
	 */
	AgentDescriptor getAgentDescriptor();

	/**
	 * @param agentDescriptor
	 */
	void setAgentDescriptor(AgentDescriptor agentDescriptor);

	/**
	 * @param buff
	 */
	void setBuffers(EntityDataBuffer[] buff);

	/**
	 * @param host
	 */
	void setAgent(AgentId agent);

}