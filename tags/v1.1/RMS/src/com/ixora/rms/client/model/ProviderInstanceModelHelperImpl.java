/*
 * Created on 01-Jan-2005
 */
package com.ixora.rms.client.model;

import java.util.Collection;

import com.ixora.rms.ResourceId;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.rms.repository.ProviderInstance;

/**
 * @author Daniel Moraru
 */
final class ProviderInstanceModelHelperImpl implements
		ProviderInstanceModelHelper {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(ProviderInstanceModelHelperImpl.class);

    /** Session model */
    private SessionModel model;

	/**
	 * Constructor.
	 * @param model
	 */
	public ProviderInstanceModelHelperImpl(SessionModel model) {
		super();
		this.model = model;
	}

	/**
	 * @see com.ixora.rms.client.model.ProviderInstanceModelHelper#setProviderInstanceData(com.ixora.rms.internal.ResourceId, com.ixora.rms.repository.ProviderInstance[])
	 */
	public void setProviderInstanceData(ResourceId rid, Collection<ProviderInstance> data) {
		ArtefactInfoContainerImpl ac = model.getArtefactContainerImplForResource(rid, true);
		if(ac == null) {
			logger.error("Couldn't find agent for: " + rid);
			return;
		}
		((AgentInfoImpl)ac).setProviderInstanceData(data);
		model.refreshNode(rid);
	}

	/**
	 * @see com.ixora.rms.client.model.ProviderInstanceModelHelper#addProviderInstance(com.ixora.rms.internal.ResourceId, com.ixora.rms.repository.ProviderInstance)
	 */
	public void addProviderInstance(ResourceId rid, ProviderInstance pi) {
		ArtefactInfoContainerImpl ac = model.getArtefactContainerImplForResource(rid, true);
		if(ac == null) {
			logger.error("Couldn't find agent for: " + rid);
			return;
		}
		((AgentInfoImpl)ac).addProviderInstanceData(pi);
		model.refreshNode(rid);
	}

	/**
	 * @see com.ixora.rms.client.model.ProviderInstanceModelHelper#removeProviderInstance(com.ixora.rms.internal.ResourceId, String)
	 */
	public void removeProviderInstance(ResourceId rid, String instanceName) {
		ArtefactInfoContainerImpl ac = model.getArtefactContainerImplForResource(rid, true);
		if(ac == null) {
			logger.error("Couldn't find agent for: " + rid);
			return;
		}
		((AgentInfoImpl)ac).removeProviderInstanceData(instanceName);
		model.refreshNode(rid);
	}
}
