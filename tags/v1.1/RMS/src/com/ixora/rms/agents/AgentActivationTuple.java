/*
 * Created on 10-Jan-2005
 */
package com.ixora.rms.agents;

import java.io.Serializable;

import com.ixora.rms.EntityDescriptorTree;

/**
 * Information returned at agent activation time.
 * @author Daniel Moraru
 */
public final class AgentActivationTuple implements Serializable {
	private static final long serialVersionUID = 1061781446326336875L;
	/** Entity descriptors */
	private EntityDescriptorTree entities;
	/** Agent descriptor */
	private AgentDescriptor agentDescriptor;

	/**
	 * Constructor.
	 * @param agentId
	 * @param agentDesc
	 * @param entities
	 */
	public AgentActivationTuple(AgentDescriptor agentDesc,
				EntityDescriptorTree entities) {
		super();
		this.agentDescriptor = agentDesc;
		this.entities = entities;
	}

	/**
	 * @return the agentId.
	 */
	public AgentId getAgentId() {
		return this.agentDescriptor.getAgentId();
	}

	/**
	 * @return the entities.
	 */
	public EntityDescriptorTree getEntities() {
		return entities;
	}

	/**
	 * @return
	 */
	public AgentDescriptor getDescriptor() {
		return this.agentDescriptor;
	}
}
