/*
 * Created on 25-Apr-2004
 */
package com.ixora.rms.repository;

import java.util.HashMap;
import java.util.Map;

import com.ixora.rms.ResourceId;
import com.ixora.common.xml.XMLExternalizable;
import com.ixora.rms.EntityId;
import com.ixora.rms.repository.exception.FailedToSaveRepository;
import com.ixora.rms.services.DashboardRepositoryService;

/**
 * Manages data query groups at various levels: system, host, agent and
 * entity.
 * @author Daniel Moraru
 */
public final class DashboardRepositoryManager
		implements DashboardRepositoryService {
	/** Extension name of the files containing query groups */
	public static final String FILE_EXT = "db";
	/** Name of the file containing entity query groups */
	private static final String FILE_ENTITY = "entity.db";
	/** Name of the file containing agent query groups */
	private static final String FILE_AGENT = "agent.db";
	/** Name of the file containing global query groups */
	private static final String FILE_GLOBAL = "global.db";

	/** Respository */
	private TreeArtefactRepository repository;

	/**
	 * Constructor.
	 */
	public DashboardRepositoryManager() {
		super();
		createRepository();
	}

    /**
     * @see com.ixora.rms.services.DashboardRepositoryService#getEntityDashboards(java.lang.String, com.ixora.rms.EntityId)
     */
    public synchronized DashboardMap getEntityDashboards(String agentId, EntityId entityId) {
        return (DashboardMap)this.repository.getEntityResources(agentId, entityId);
    }

    /**
     * @see com.ixora.rms.services.DashboardRepositoryService#setEntityDashboards(java.lang.String, com.ixora.rms.EntityId, com.ixora.rms.control.struct.QueryGroupMap)
     */
    public synchronized void setEntityDashboards(String agentId, EntityId entityId, DashboardMap dashboards) {
        this.repository.setEntityResources(agentId, entityId, dashboards);
    }

    /**
     * @see com.ixora.rms.services.DashboardRepositoryService#getAgentDashboards(java.lang.String)
     */
    public synchronized DashboardMap getAgentDashboards(String agentId) {
        return (DashboardMap)this.repository.getAgentResources(agentId);
    }

    /**
     * @see com.ixora.rms.services.DashboardRepositoryService#setAgentDashboards(java.lang.String, com.ixora.rms.control.struct.QueryGroupMap)
     */
    public synchronized void setAgentDashboards(String agentId, DashboardMap dashboards) {
      this.repository.setAgentResources(agentId, dashboards);
    }

    /**
     * @see com.ixora.rms.services.DashboardRepositoryService#getGlobalDashboards()
     */
    public synchronized DashboardMap getGlobalDashboards() {
        return (DashboardMap)this.repository.getGlobalResources();
    }

    /**
     * @see com.ixora.rms.services.DashboardRepositoryService#setGlobalDashboards(com.ixora.rms.control.struct.QueryGroupMap)
     */
    public synchronized void setGlobalDashboards(DashboardMap dashboards) {
        this.repository.setGlobalResources(dashboards);
    }

    /**
     * @see com.ixora.rms.services.DashboardRepositoryService#getHostDashboards(java.lang.String)
     */
    public synchronized DashboardMap getHostDashboards(String host) {
        return (DashboardMap)this.repository.getHostResources(host);
    }

    /**
     * @see com.ixora.rms.services.DashboardRepositoryService#setHostDashboards(java.lang.String, com.ixora.rms.control.struct.QueryGroupMap)
     */
    public void setHostDashboards(String host, DashboardMap dashboards) {
        this.repository.setHostResources(host, dashboards);
    }

    /**
     * @see com.ixora.rms.services.DashboardRepositoryService#save()
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
			       FILE_GLOBAL,
			       FILE_AGENT,
			       FILE_ENTITY,
			       FILE_EXT,
			       new TreeArtefactFactory() {
	                public XMLExternalizable createArtefact() {
	                    return new DashboardMap();
	                }
			       });
    }

	/**
	 * @see com.ixora.rms.services.DashboardRepositoryService#getEntityDashboards(java.lang.String)
	 */
	public Map<EntityId, DashboardMap> getEntityDashboards(String agentId) {
		Map<EntityId, XMLExternalizable> exts = this.repository.getEntityResources(agentId);
		if(exts != null) {
			Map<EntityId, DashboardMap> ret = new HashMap<EntityId, DashboardMap>(exts.size());
			for(Map.Entry<EntityId, XMLExternalizable> ext : exts.entrySet()) {
				ret.put(ext.getKey(), (DashboardMap)ext.getValue());
			}
			return ret;
		}
		return null;
	}

	/**
	 * @see com.ixora.rms.services.DashboardRepositoryService#getDashboardMap(com.ixora.rms.ResourceId)
	 */
	public DashboardMap getDashboardMap(ResourceId context) {
        if(context == null) {
            return getGlobalDashboards();
        }
        int rep = context.getRepresentation();
        switch(rep) {
            case ResourceId.HOST:
                return getHostDashboards(
                    context.getHostId().toString());
            case ResourceId.AGENT:
                return getAgentDashboards(
                    context.getAgentId().getInstallationId().toString());
            case ResourceId.ENTITY:
                return getEntityDashboards(
                    context.getAgentId().getInstallationId().toString(),
                    context.getEntityId());
        }
        return null;
	}

	/**
	 * @see com.ixora.rms.services.DashboardRepositoryService#setDashboardMap(com.ixora.rms.ResourceId, com.ixora.rms.repository.DashboardMap)
	 */
	public void setDashboardMap(ResourceId context, DashboardMap map) {
        if(context == null) {
            setGlobalDashboards(map);
        } else {
            int rep = context.getRepresentation();
            switch(rep) {
                case ResourceId.HOST:
                    setHostDashboards(
                        context.getHostId().toString(), map);
                break;
                case ResourceId.AGENT:
                    setAgentDashboards(
                        context.getAgentId().getInstallationId(), map);
                break;
                case ResourceId.ENTITY:
                    setEntityDashboards(
                        context.getAgentId().getInstallationId(),
                        context.getEntityId(),
                        map);
                break;
            }
        }
	}
}
