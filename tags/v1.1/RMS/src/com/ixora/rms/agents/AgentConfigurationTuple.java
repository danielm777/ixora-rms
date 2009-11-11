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
public final class AgentConfigurationTuple implements Serializable {
	private static final long serialVersionUID = -1484007915946373413L;
	/** Agent configuration */
	private AgentDescriptor fAgentDescriptor;
	/** Descriptor */
	private EntityDescriptorTree fEntities;

	/**
	 * Constructor.
	 * @param agentDescriptor
	 * @param entities
	 */
	public AgentConfigurationTuple(AgentDescriptor agentDescriptor,
			EntityDescriptorTree entities) {
		super();
		this.fAgentDescriptor = agentDescriptor;
		this.fEntities = entities;
	}

	/**
	 * @return the agent descriptor.
	 */
	public AgentDescriptor getAgentDescriptor() {
		return fAgentDescriptor;
	}

	/**
	 * @return the entities.
	 */
	public EntityDescriptorTree getEntities() {
		return fEntities;
	}
}
