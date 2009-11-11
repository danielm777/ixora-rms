/*
 * Created on 25-Apr-2004
 */
package com.ixora.rms.repository;

import java.util.Collection;

import com.ixora.common.xml.XMLExternalizable;
import com.ixora.rms.repository.exception.FailedToSaveRepository;
import com.ixora.rms.services.ProviderInstanceRepositoryService;

/**
 * Manages data queries at various levels: system, host, agent and
 * entity.
 * @author Daniel Moraru
 */
public final class ProviderInstanceRepositoryManager
		implements ProviderInstanceRepositoryService {
	/** Extension name of the files containing provider instances */
	public static final String FILE_EXT = "pi";
	/** Name of the file containing agent provider instances */
	private static final String FILE_AGENT = "agent.pi";

	/** Respository */
	private TreeArtefactRepository repository;

	/**
	 * Constructor.
	 */
	public ProviderInstanceRepositoryManager() {
		super();
		createRepository();
	}

	/**
	 * @see com.ixora.rms.services.ProviderInstanceRepositoryService#save()
	 */
	public synchronized void save() throws FailedToSaveRepository {
	    this.repository.save();
	}

    /**
     * Reloads the repository discarding any unsaved data.
     */
    public synchronized void reload() {
    	createRepository();
    }

    /**
     * Creates the underlying repository.
     */
    private void createRepository() {
		this.repository = new TreeArtefactRepository(
			       "",
			       FILE_AGENT,
			       "",
			       FILE_EXT,
			       new TreeArtefactFactory() {
	                public XMLExternalizable createArtefact() {
	                    return new ProviderInstanceMap();
	                }
			       });
    }

	/**
	 * @see com.ixora.rms.services.ProviderInstanceRepositoryService#getAgentProviderInstances(java.lang.String)
	 */
	public ProviderInstanceMap getAgentProviderInstances(String agentId) {
		return (ProviderInstanceMap)this.repository.getAgentResources(agentId);
	}

	/**
	 * @see com.ixora.rms.services.ProviderInstanceRepositoryService#getOptionalAgentProviderInstances(java.lang.String)
	 */
	public ProviderInstanceMap getOptionalAgentProviderInstances(String agentId) {
		ProviderInstanceMap ret = new ProviderInstanceMap();
		ProviderInstanceMap all = (ProviderInstanceMap)this.repository.getAgentResources(agentId);
		if(all == null) {
			return null;
		}
		Collection<ProviderInstance> pids = all.getAll();
        if(pids != null) {
    		for(ProviderInstance pid : pids) {
    			if(pid.isOptional()) {
    				ret.add(pid);
    			}
    		}
        }
		return ret;
	}

	/**
	 * @see com.ixora.rms.services.ProviderInstanceRepositoryService#setAgentProviderInstances(java.lang.String, com.ixora.rms.repository.ProviderInstanceMap)
	 */
	public void setAgentProviderInstances(String agentId, ProviderInstanceMap providerInstance) {
		this.repository.setAgentResources(agentId, providerInstance);
	}

	/**
	 * @see com.ixora.common.Service#shutdown()
	 */
	public void shutdown() {
		; // nothing
	}
}

