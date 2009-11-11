/*
 * Created on May 14, 2005
 */
package com.ixora.rms.dataengine.definitions;

import java.util.LinkedList;
import java.util.List;

import com.ixora.rms.ResourceId;
import com.ixora.common.utils.Utils;
import com.ixora.rms.CounterDescriptor;

/**
 * A query definition derived from a set of counters.
 */
public final class MultipleCounterQueryDef extends QueryDef {
	private static final long serialVersionUID = 6501547945575141342L;
	private ResourceId fTimestamp;

    /**
     * Default constructor to support XML.
     */
    public MultipleCounterQueryDef() {
    	super();
    }

    /**
     * NOTE: The assumption here is that all counters belong to the same entity.
     * @param countersContext
     * @param counters
     * @param identifier
     */
    public MultipleCounterQueryDef(ResourceId countersContext, ResourceId[] counters, String identifier) {
        super();
		if(Utils.isEmptyArray(counters)) {
		    throw new IllegalArgumentException("The list of counters is empty");
        }
        ResourceId first = counters[0];

        // Add a timestamp resource and the counter resource to the query
		List<ResourceDef> listResources = new LinkedList<ResourceDef>();

        fTimestamp = new ResourceId(
                first.getHostId(),
                first.getAgentId(),
                first.getEntityId(),
                CounterDescriptor.TIMESTAMP_ID);
        listResources.add(new ResourceDef(fTimestamp));

        // need this to check that all counters belong to the same entity
        ResourceId context = new ResourceId(
                first.getHostId(),
                first.getAgentId(),
                first.getEntityId(),
                null);
        for(ResourceId counter : counters) {
            ResourceId ccontext = new ResourceId(
                    counter.getHostId(),
                    counter.getAgentId(),
                    counter.getEntityId(),
                    null);
            if(!context.equals(ccontext)) {
                throw new IllegalArgumentException("All counters must belong to the same entity");
            }
            ResourceDef resourceDef = new ResourceDef(counter);
            resourceDef.setIName("$counter");
			listResources.add(resourceDef);
		}
		// set up
        this.resources.addAll(listResources);
        this.id = identifier;
    }

    /**
     * @return the resourceid of the timestamp.
     */
    public ResourceId getTimestamp() {
        return fTimestamp;
    }
}
