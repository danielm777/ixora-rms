/*
 * Created on 09-Jun-2004
 */
package com.ixora.rms;

import java.util.HashMap;
import java.util.Map;

import com.ixora.rms.EntityId;
import com.ixora.rms.RecordDefinition;
import com.ixora.rms.agents.AgentDescriptor;
import com.ixora.rms.agents.AgentId;

/**
 * Cache for all monitored entities record definitions.
 * @author Daniel Moraru
 */
public final class RecordDefinitionCache {
	/**
	 * Cache map.
	 */
	private Map<ResourceId, RecordDefinition> fCacheRecordDefinition;
    /**
     * Cache map for agent descriptor.
     */
    private Map<ResourceId, AgentDescriptor> fCacheAgentDescriptors;

	/**
	 * Constructor.
	 */
	public RecordDefinitionCache() {
		super();
		fCacheRecordDefinition = new HashMap<ResourceId, RecordDefinition>();
        fCacheAgentDescriptors = new HashMap<ResourceId, AgentDescriptor>();
	}

	/**
	 * Returns the cached record definition for the given
	 * entity (null if not found).
	 * @param host
	 * @param agent
	 * @param entity
	 * @return
	 */
	public synchronized RecordDefinition getRecordDefinition(HostId host,
			AgentId agent, EntityId entity) {
		return fCacheRecordDefinition.get(new ResourceId(host, agent, entity, null));
	}

	/**
	 * Puts a record definition into the cache.
	 * @param host
	 * @param agent
	 * @param entity
	 * @param rd
	 */
	public synchronized void putRecordDefinition(HostId host, AgentId agent, EntityId entity,
			RecordDefinition rd) {
		fCacheRecordDefinition.put(new ResourceId(host, agent, entity, null), rd);
	}

    /**
     * Puts an agent descriptor into the cache.
     * @param host
     * @param agent
     * @param desc
     */
    public synchronized void putAgentDescriptor(HostId host, AgentId agent, AgentDescriptor desc) {
        fCacheAgentDescriptors.put(new ResourceId(host, agent, null, null), desc);
    }

    /**
     * @param host
     * @param agent
     * @return
     */
    public synchronized AgentDescriptor getAgentDescriptor(HostId host, AgentId agent) {
        return fCacheAgentDescriptors.get(new ResourceId(host, agent, null, null));
    }
}
