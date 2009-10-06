/*
 * Created on 21-Jul-2004
 */
package com.ixora.rms.client.model;

import java.util.Collection;

import com.ixora.common.MessageRepository;
import com.ixora.common.collections.CaseInsensitiveLinkedMap;
import com.ixora.rms.agents.AgentDescriptor;
import com.ixora.rms.agents.AgentState;
import com.ixora.rms.client.AgentInstanceData;
import com.ixora.rms.providers.ProviderState;
import com.ixora.rms.repository.AgentInstallationData;
import com.ixora.rms.repository.ProviderInstance;


/** Holds agent data */
final class AgentInfoImpl extends ArtefactInfoContainerImpl
		implements AgentInfo {
	/** Translated agent name */
	private String translatedName;
	/** Translated agent description */
	private String translatedDescription;
	/** Identifier string */
	private String identifier;
	/** Agent installation details */
	private AgentInstallationData installationDtls;
	/** Agent state */
	private AgentState state;
	/** Agent deployment details */
	private AgentInstanceData deploymentDtls;
	/** Exception that caused the transition into the ERROR state */
	private Throwable errorStateException;
	/**
	 * Provider instances.
	 * Key: String
	 * Value: ProviderInstanceInfo
	 */
	private CaseInsensitiveLinkedMap providerInstances;

	/**
	 * Constructor.
	 * @param idtls
	 * @param ddtls
	 * @param state
	 */
	AgentInfoImpl(
			AgentInstallationData idtls,
			AgentInstanceData ddtls,
			AgentState state,
			SessionModel model) {
	    super(model);
		this.installationDtls = idtls;
		this.deploymentDtls = ddtls;
		this.state = state;
		messageRepository = idtls.getMessageCatalog();
		translatedName = MessageRepository.get(messageRepository, idtls.getAgentName());
		translatedDescription = MessageRepository.get(messageRepository, idtls.getAgentDescription());
		this.identifier = ddtls.getAgentId().toString();
		int idx = this.deploymentDtls.getAgentId().getInstallationIdx();
		if(idx > 0) {
			this.translatedName += "(" + idx + ")";
		}
	}

	/**
	 * @return
	 */
	public AgentInstallationData getInstallationDtls() {
		return installationDtls;
	}

	/**
	 * @return
	 */
	public AgentState getAgentState() {
		return state;
	}
	/**
	 * @return
	 */
	public Throwable getErrorStateException() {
		return this.errorStateException;
	}
	/**
	 * @return the deployment details
	 */
	public AgentInstanceData getDeploymentDtls() {
		return this.deploymentDtls;
	}
	/**
	 * @return the description
	 */
	public String getTranslatedDescription() {
		return translatedDescription;
	}
	/**
	 * @return the messageRepository
	 */
	public String getMessageRepository() {
		return messageRepository;
	}
	/**
	 * @return the name
	 */
	public String getTranslatedName() {
		return translatedName;
	}

	/**
	 * @see com.ixora.rms.client.model.AgentInfo#getProviderInstances()
	 */
	public Collection<ProviderInstanceInfo> getProviderInstances() {
		if(this.providerInstances == null) {
			return null;
		}
		return this.providerInstances.values();
	}

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        String text;
        if(model.getShowIdentifiers()) {
            text = this.identifier;
        } else {
            text = this.translatedName;
        }
        return text;
    }

// package
	/**
	 * @param state
	 * @param e
	 */
	void setAgentState(AgentState state, Throwable e) {
		this.state = state;
		this.errorStateException = e;
	}

	/**
	 * @param state
	 * @param e
	 */
	void setNonFatalError(Throwable e) {
		this.errorStateException = e;
	}

	/**
	 * @param pis
	 */
	void setProviderInstanceData(Collection<ProviderInstance> pis) {
		if(pis == null) {
			return;
		}
		if(this.providerInstances == null) {
			this.providerInstances = new CaseInsensitiveLinkedMap();
		}
		for(ProviderInstance pi : pis) {
			this.providerInstances.put(pi.getInstanceName()
					, new ProviderInstanceInfoImpl(pi));
		}
	}

	/**
	 * @param pi
	 */
	void addProviderInstanceData(ProviderInstance pi) {
		if(this.providerInstances == null) {
			this.providerInstances = new CaseInsensitiveLinkedMap();
		}
		ProviderInstanceInfoImpl pimpl = (ProviderInstanceInfoImpl)this.providerInstances.get(pi.getInstanceName());
		if(pimpl == null) {
			this.providerInstances.put(pi.getInstanceName(), new ProviderInstanceInfoImpl(pi));
		} else {
			pimpl.update(pi);
		}
	}

	/**
	 * @param pi
	 */
	void removeProviderInstanceData(String instanceName) {
		if(this.providerInstances == null) {
			return;
		}
		this.providerInstances.remove(instanceName);
	}

	/**
	 * @param providerInstanceName
	 * @param state2
	 * @param e
	 */
	void setProviderState(String providerInstanceName, ProviderState state, Throwable e) {
		if(this.providerInstances == null) {
			return;
		}
		ProviderInstanceInfoImpl pi = (ProviderInstanceInfoImpl)this.providerInstances.get(providerInstanceName);
		if(pi != null) {
			pi.setProviderState(state, e);
		}
	}

	/**
	 * @param conf
	 */
	void setAgentDescriptor(AgentDescriptor desc) {
		// just the config can change
		this.deploymentDtls.setConfiguration(desc.getAgentConfiguration());
	}

    /**
     * @param ad
     */
    void update(AgentInstanceData ad) {
        // just the config can change
        this.deploymentDtls.setConfiguration(ad.getConfiguration());
    }

	/**
	 * @see com.ixora.rms.client.model.AgentInfo#safeToRefreshRecursivelly()
	 */
	public boolean safeToRefreshRecursivelly() {
		return this.deploymentDtls.getDescriptor().safeToRefreshRecursivelly();
	}
}