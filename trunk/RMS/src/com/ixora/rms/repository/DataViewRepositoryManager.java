/*
 * Created on 12-Oct-2004
 */
package com.ixora.rms.repository;

import java.util.HashMap;
import java.util.Map;

import com.ixora.rms.ResourceId;
import com.ixora.common.xml.XMLExternalizable;
import com.ixora.rms.EntityId;
import com.ixora.rms.repository.exception.FailedToSaveRepository;
import com.ixora.rms.services.DataViewRepositoryService;

/**
 * @author Daniel Moraru
 */
public final class DataViewRepositoryManager implements DataViewRepositoryService {
	/** Extension name of the files containing views */
	public static final String FILE_EXT = "dv";
	/** Name of the file containing entity views */
	private static final String FILE_ENTITY = "entity.dv";
	/** Name of the file containing agent views */
	private static final String FILE_AGENT = "agent.dv";
	/** Name of the file containing global views */
	private static final String FILE_GLOBAL = "global.dv";

	/** Respository */
	private TreeArtefactRepository repository;

    /**
     * Constructor.
     */
    public DataViewRepositoryManager() {
        super();
        createRepository();
    }

    /**
     * @see com.ixora.rms.services.DataViewRepositoryService#getEntityDataViews(java.lang.String, com.ixora.rms.EntityId)
     */
    public synchronized DataViewMap getEntityDataViews(String agentId, EntityId entityId) {
        return (DataViewMap)this.repository.getEntityResources(agentId, entityId);
    }

    /**
     * @see com.ixora.rms.services.DataViewRepositoryService#setEntityDataViews(java.lang.String, com.ixora.rms.EntityId, com.ixora.rms.repository.DataViewMap)
     */
    public synchronized void setEntityDataViews(String agentId, EntityId entityId,
            DataViewMap views) {
        this.repository.setEntityResources(agentId, entityId, views);
    }

    /**
     * @see com.ixora.rms.services.DataViewRepositoryService#getAgentDataViews(java.lang.String)
     */
    public synchronized DataViewMap getAgentDataViews(String agentId) {
        return (DataViewMap)this.repository.getAgentResources(agentId);
    }

    /**
     * @see com.ixora.rms.services.DataViewRepositoryService#setAgentDataViews(java.lang.String, com.ixora.rms.repository.DataViewMap)
     */
    public synchronized void setAgentDataViews(String agentId, DataViewMap views) {
        this.repository.setAgentResources(agentId, views);
    }

    /**
     * @see com.ixora.rms.services.DataViewRepositoryService#getGlobalDataViews()
     */
    public synchronized DataViewMap getGlobalDataViews() {
        return (DataViewMap)this.repository.getGlobalResources();
    }

    /**
     * @see com.ixora.rms.services.DataViewRepositoryService#setGlobalDataViews(com.ixora.rms.repository.DataViewMap)
     */
    public synchronized void setGlobalDataViews(DataViewMap views) {
        this.repository.setGlobalResources(views);
    }

    /**
     * @see com.ixora.rms.services.DataViewRepositoryService#getHostDataViews(java.lang.String)
     */
    public synchronized DataViewMap getHostDataViews(String host) {
        return (DataViewMap)this.repository.getHostResources(host);
    }

    /**
     * @see com.ixora.rms.services.DataViewRepositoryService#setHostDataViews(java.lang.String, com.ixora.rms.repository.DataViewMap)
     */
    public synchronized void setHostDataViews(String host, DataViewMap views) {
        this.repository.setHostResources(host, views);
    }

    /**
     * @see com.ixora.rms.services.DataViewRepositoryService#save()
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
     * @see com.ixora.common.Service#shutdown()
     */
    public void shutdown() {
    }

	/**
	 * @see com.ixora.rms.services.DataViewRepositoryService#getEntityDataViews(java.lang.String)
	 */
	public Map<EntityId, DataViewMap> getEntityDataViews(String agentId) {
		Map<EntityId, XMLExternalizable> exts = this.repository.getEntityResources(agentId);
		if(exts != null) {
			Map<EntityId, DataViewMap> ret = new HashMap<EntityId, DataViewMap>(exts.size());
			for(Map.Entry<EntityId, XMLExternalizable> ext : exts.entrySet()) {
				ret.put(ext.getKey(), (DataViewMap)ext.getValue());
			}
			return ret;
		}
		return null;
	}

    /**
     * Creates the underlying repository.
     */
    private void createRepository() {
		this.repository = new TreeArtefactRepository(
			       FILE_GLOBAL,
			       FILE_AGENT,
			       FILE_ENTITY,
			       FILE_EXT,
			       new TreeArtefactFactory() {
	                public XMLExternalizable createArtefact() {
	                    return new DataViewMap();
	                }
			       });
    }

	/**
	 * @see com.ixora.rms.services.DataViewRepositoryService#getDataViewMap(com.ixora.rms.ResourceId)
	 */
	public DataViewMap getDataViewMap(ResourceId context) {
        if(context == null) {
            return getGlobalDataViews();
        }
        int rep = context.getRepresentation();
        switch(rep) {
            case ResourceId.HOST:
                return getHostDataViews(
                    context.getHostId().toString());
            case ResourceId.AGENT:
                return getAgentDataViews(
                    context.getAgentId().getInstallationId().toString());
            case ResourceId.ENTITY:
                return getEntityDataViews(
                    context.getAgentId().getInstallationId().toString(),
                    context.getEntityId());
        }
        return null;
	}

	/**
	 * @see com.ixora.rms.services.DataViewRepositoryService#saveDataViewMap(com.ixora.rms.ResourceId, com.ixora.rms.repository.DataViewMap)
	 */
	public void setDataViewMap(ResourceId context, DataViewMap map) {
        if(context == null) {
            setGlobalDataViews(map);
        } else {
            int rep = context.getRepresentation();
            switch(rep) {
                case ResourceId.HOST:
                    setHostDataViews(
                            context.getHostId().toString(), map);
                break;
                case ResourceId.AGENT:
                    setAgentDataViews(
                            context.getAgentId().getInstallationId(), map);
                break;
                case ResourceId.ENTITY:
                    setEntityDataViews(
                            context.getAgentId().getInstallationId(),
                            context.getEntityId(),
                            map);
                break;
            }
        }
	}
}
