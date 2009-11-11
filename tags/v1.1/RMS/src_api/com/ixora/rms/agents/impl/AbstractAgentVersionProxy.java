/*
 * Created on 03-Mar-2004
 */
package com.ixora.rms.agents.impl;

import com.ixora.rms.EntityConfiguration;
import com.ixora.rms.EntityDescriptorTree;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.Agent;
import com.ixora.rms.agents.AgentConfiguration;
import com.ixora.rms.agents.AgentDescriptor;
import com.ixora.rms.agents.AgentId;
import com.ixora.rms.exception.InvalidConfiguration;
import com.ixora.rms.exception.InvalidEntity;

/**
 * This is a proxy that can be used to dispatch requests to various versions of the same agent.
 * @author Daniel Moraru
 */
public abstract class AbstractAgentVersionProxy implements Agent {
	/** Actual agent implementation for the current version */
	protected Agent fVersion;
	/** Listener */
	protected Listener fListener;
	/** Agent id */
	protected AgentId fAgentId;

	/**
	 * Constructor.
	 * @throws PmiException
	 */
	public AbstractAgentVersionProxy(AgentId agentId, Listener listener) {
		super();
        this.fAgentId = agentId;
        this.fListener = listener;
	}

	/**
	 * @see com.ixora.rms.agents.Agent#configure(com.ixora.rms.struct.AgentConfiguration)
	 */
	public EntityDescriptorTree configure(AgentConfiguration conf) throws InvalidConfiguration, Throwable {
        if(fVersion == null) {
        	fVersion = createAgent(conf);
        }
		return fVersion.configure(conf);
	}

	/**
	 * @param conf
	 */
	protected abstract Agent createAgent(AgentConfiguration conf) throws Throwable;

	/**
	 * @see com.ixora.rms.agents.Agent#setListener(com.ixora.rms.Agent.Listener)
	 */
	public void setListener(Listener l) {
		this.fListener = l;
	}

	/**
	 * @see com.ixora.rms.agents.Agent#configureEntity(com.ixora.rms.EntityId, com.ixora.rms.struct.EntityConfiguration)
	 */
	public EntityDescriptorTree configureEntity(EntityId entity, EntityConfiguration conf) throws InvalidEntity, InvalidConfiguration, Throwable {
		return fVersion.configureEntity(entity, conf);
	}

	/**
	 * @see com.ixora.rms.agents.Agent#getEntities(com.ixora.rms.EntityId, boolean, boolean)
	 */
	public EntityDescriptorTree getEntities(EntityId parent, boolean recursive, boolean refresh) throws InvalidEntity, Throwable {
		return fVersion.getEntities(parent, recursive, refresh);
	}

	/**
	 * @see com.ixora.rms.agents.Agent#collectData()
	 */
	public void collectData() throws Throwable {
		fVersion.collectData();
	}

	/**
	 * @see com.ixora.rms.agents.Agent#getConfiguration()
	 */
	public AgentConfiguration getConfiguration() {
		return fVersion.getConfiguration();
	}

	/**
	 * @see com.ixora.common.Startable#start()
	 */
	public void start() throws Throwable {
		fVersion.start();
	}
	/**
	 * @see com.ixora.common.Startable#stop()
	 */
	public void stop() throws Throwable {
		fVersion.stop();
	}

	/**
	 * @see com.ixora.rms.agents.Agent#setId(AgentId)
	 */
	public void setId(AgentId agentId) {
		this.fAgentId = agentId;
	}

	/**
	 * @see com.ixora.rms.agents.Agent#deactivate()
	 */
	public void deactivate() throws Throwable {
		if(fVersion != null) {
			fVersion.deactivate();
		}
	}

	/**
	 * @see com.ixora.rms.agents.Agent#requiresExternalCollector()
	 */
	public boolean requiresExternalCollector() {
		return fVersion.requiresExternalCollector();
	}

	/**
	 * @see com.ixora.rms.agents.Agent#getDescriptor()
	 */
	public AgentDescriptor getDescriptor() {
		return fVersion.getDescriptor();
	}


}
