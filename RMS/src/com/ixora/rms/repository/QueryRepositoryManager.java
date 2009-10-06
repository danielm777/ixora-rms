/*
 * Created on 25-Apr-2004
 */
package com.ixora.rms.repository;

import com.ixora.common.xml.XMLExternalizable;
import com.ixora.rms.EntityId;
import com.ixora.rms.repository.exception.FailedToSaveRepository;
import com.ixora.rms.services.QueryRepositoryService;

/**
 * Manages data queries at various levels: system, host, agent and
 * entity.
 * @author Daniel Moraru
 */
public final class QueryRepositoryManager
		implements QueryRepositoryService {
	/** Name of the file containing entity queries */
	private static final String FILE_ENTITY = "entity.qr";
	/** Name of the file containing agent queries */
	private static final String FILE_AGENT = "agent.qr";
	/** Name of the file containing global queries */
	private static final String FILE_GLOBAL = "global.qr";
	/** Extension name of the files containing queries */
	private static final String FILE_EXT = "qr";

	/** Respository */
	private TreeArtefactRepository repository;

	/**
	 * Constructor.
	 */
	public QueryRepositoryManager() {
		super();
		this.repository = new TreeArtefactRepository(
		       FILE_GLOBAL,
		       FILE_AGENT,
		       FILE_ENTITY,
		       FILE_EXT,
		       new TreeArtefactFactory() {
                public XMLExternalizable createArtefact() {
                    return new QueryMap();
                }
		       });
	}

	/**
	 * @see com.ixora.rms.services.QueryRepositoryService#getEntityQueries(java.lang.String, com.ixora.rms.EntityId)
	 */
	public synchronized QueryMap getEntityQueries(String agentId,
	        EntityId entityId) {
	    return (QueryMap)this.repository.getEntityResources(agentId, entityId);
	}

	/**
	 * @see com.ixora.rms.services.QueryRepositoryService#getAgentQueries(java.lang.String)
	 */
	public synchronized QueryMap getAgentQueries(String agentId) {
	    return (QueryMap)this.repository.getAgentResources(agentId);
	}

	/**
	 * @see com.ixora.rms.services.QueryRepositoryService#getGlobalQueries()
	 */
	public synchronized QueryMap getGlobalQueries() {
	    return (QueryMap)this.repository.getGlobalResources();
	}

	/**
	 * @see com.ixora.rms.services.QueryRepositoryService#getHostQueries(java.lang.String)
	 */
	public synchronized QueryMap getHostQueries(String host) {
	    return (QueryMap)this.repository.getHostResources(host);
	}

	/**
	 * @see com.ixora.rms.services.QueryRepositoryService#setEntityQueries(java.lang.String, com.ixora.rms.EntityId, com.ixora.rms.control.struct.DataQueryMap)
	 */
	public synchronized void setEntityQueries(String agentId,
	        EntityId entityId, QueryMap queries) {
	    this.repository.setEntityResources(agentId, entityId, queries);
	}

	/**
	 * @see com.ixora.rms.services.QueryRepositoryService#setAgentQueries(java.lang.String, com.ixora.rms.control.struct.DataQueryMap)
	 */
	public synchronized void setAgentQueries(String agentId,
	        QueryMap queries) {
	    this.repository.setAgentResources(agentId, queries);
	}

	/**
	 * @see com.ixora.common.Service#shutdown()
	 */
	public void shutdown() {
		; // nothing
	}

	/**
	 * @see com.ixora.rms.services.QueryRepositoryService#setGlobalQueries(com.ixora.rms.control.struct.DataQueryMap)
	 */
	public synchronized void setGlobalQueries(QueryMap queries) {
	    this.repository.setGlobalResources(queries);
	}

	/**
	 * @see com.ixora.rms.services.QueryRepositoryService#setHostQueries(java.lang.String, com.ixora.rms.control.struct.DataQueryMap)
	 */
	public synchronized void setHostQueries(String host, QueryMap queries) {
	    this.repository.setHostResources(host, queries);
	}

	/**
	 * @see com.ixora.rms.services.QueryRepositoryService#save()
	 */
	public synchronized void save() throws FailedToSaveRepository {
	    this.repository.save();
	}
}
