/*
 * Created on May 14, 2005
 */
package com.ixora.rms.dataengine.definitions;

import java.util.LinkedList;
import java.util.List;

import com.ixora.rms.ResourceId;
import com.ixora.common.utils.Utils;
import com.ixora.rms.CounterDescriptor;
import com.ixora.rms.dataengine.Style;

/**
 * A query definition derived from a single counter.
 */
public final class SingleCounterQueryDef extends QueryDef {
	private static final long serialVersionUID = -5323567933933559324L;

	/**
     * Default constructor to support XML.
     */
    public SingleCounterQueryDef() {
    	super();
    }

    /**
     * @param counter
     * @param queryId can be null and then the id of the query will be set
     * to the counter name
     * @param style
     */
    public SingleCounterQueryDef(ResourceId counter, String queryId, Style style) {
        super();
		// Add a timestamp resource and the counter resource to the query
		List<ResourceDef> listResources = new LinkedList<ResourceDef>();
		ResourceId ridTimestamp = new ResourceId(
				counter.getHostId(),
				counter.getAgentId(),
				counter.getEntityId(),
				CounterDescriptor.TIMESTAMP_ID);
		listResources.add(new ResourceDef(ridTimestamp));
        ResourceDef resourceDef;
        if(style == null) {
        	resourceDef = new ResourceDef(counter);
        	resourceDef.setIName("$counter");
        } else {
        	resourceDef = new ResourceDef(counter, style.getStyleDef());
            if(Utils.isEmptyString(style.getIName())) {
            	resourceDef.setIName(queryId);
            }
        }
		listResources.add(resourceDef);
		String counterName = counter.getCounterId().toString();
		// set up
		if(!Utils.isEmptyString(queryId)) {
			this.id = queryId;
		} else {
			this.id = counterName;
		}
        this.resources.addAll(listResources);
    }
}
