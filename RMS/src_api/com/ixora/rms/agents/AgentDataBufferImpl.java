/*
 * Created on 19-Nov-2003
 */
package com.ixora.rms.agents;

import com.ixora.rms.HostDataBufferImpl;
import com.ixora.rms.HostId;
import com.ixora.common.utils.Utils;
import com.ixora.rms.EntityDataBuffer;

/**
 * Monitored data buffer. Each agent sends such a buffer after
 * each sampling interval.
 * @author Daniel Moraru
 */
public final class AgentDataBufferImpl extends HostDataBufferImpl implements AgentDataBuffer {
    /**
     * The agent descriptor which is not null only for the first sample after a change
     * in the descriptor of the agent.
     */
    private AgentDescriptor agentDescriptor;
	/**
	 * Agent id.
	 */
	private AgentId agent;
	/**
	 * Buffers for each entity.
	 */
	private EntityDataBuffer[] entitiesData;

	/**
	 * Constructor.
	 */
	public AgentDataBufferImpl() {
		super();
	}

	/**
	 * @param host
	 * @param ad
	 * @param aid
	 * @param entities
	 */
	public AgentDataBufferImpl(HostId host, AgentId aid, AgentDescriptor ad, EntityDataBuffer[] entities) {
		super(host);
		this.agentDescriptor = ad;
		this.agent = aid;
		this.entitiesData = entities;
	}

    /**
	 * @see com.ixora.rms.agents.AgentDataBuffer#isValid()
	 */
    public boolean isValid() {
        return agent != null && super.isValid();
    }

    /**
	 * @see com.ixora.rms.agents.AgentDataBuffer#isEmpty()
	 */
    public boolean isEmpty() {
    	 return Utils.isEmptyArray(entitiesData);
    }

	/**
	 * @see com.ixora.rms.agents.AgentDataBuffer#getBuffers()
	 */
	public EntityDataBuffer[] getBuffers() {
		return entitiesData;
	}

	/**
	 * @see com.ixora.rms.agents.AgentDataBuffer#getAgent()
	 */
	public AgentId getAgent() {
		return agent;
	}

    /**
	 * @see com.ixora.rms.agents.AgentDataBuffer#getAgentDescriptor()
	 */
    public AgentDescriptor getAgentDescriptor() {
        return agentDescriptor;
    }

    /**
	 * @see com.ixora.rms.agents.AgentDataBuffer#setAgentDescriptor(com.ixora.rms.agents.AgentDescriptor)
	 */
    public void setAgentDescriptor(AgentDescriptor agentDescriptor) {
       this.agentDescriptor = agentDescriptor;
    }

	/**
	 * @see com.ixora.rms.agents.AgentDataBuffer#setBuffers(EntityDataBuffer[])
	 */
	public void setBuffers(EntityDataBuffer[] buff) {
		entitiesData = buff;
	}

	/**
	 * @see com.ixora.rms.agents.AgentDataBuffer#setAgent(com.ixora.rms.agents.AgentId)
	 */
	public void setAgent(AgentId agent) {
		this.agent = agent;
	}

	/**
	 * Debug only.
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if (entitiesData == null)
			return "";
		String retVal = new String();
		for (int i = 0; i < entitiesData.length; i++) {
			retVal += entitiesData[i].toString();
		}
		return retVal;
	}
}
