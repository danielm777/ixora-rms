/*
 * Created on 13-Aug-2004
 */
package com.ixora.rms.client.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;

import com.ixora.rms.ResourceId;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.utils.Utils;
import com.ixora.rms.CounterDescriptor;
import com.ixora.rms.CounterId;
import com.ixora.rms.dataengine.definitions.QueryDef;
import com.ixora.rms.dataengine.definitions.ResourceDef;

/**
 * @author Daniel Moraru
 */
public class CounterModelHelperImpl implements CounterModelHelper {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(CounterModelHelperImpl.class);

    /** Session model */
    private SessionModel model;

    /**
     * Constructor.
     * @param model
     */
    CounterModelHelperImpl(SessionModel model) {
       this.model = model;
    }

    /**
	 * Rolls back the counters belonging to the given entity.
	 * @param en
	 */
	public void rollbackCounters(EntityNode en) {
		en.getEntityInfoImpl().rollbackAllCounters();
		model.nodeChanged(en);
	}

	/**
	 * Sets the given flag for the given counter.
	 * @param en
	 * @param cid
	 * @param flag
	 * @param p
	 * @param triggerUpdateEvent
	 */
	public void setCounterFlag(EntityNode en,
								CounterId cid,
								int flag,
								boolean value,
								boolean triggerUpdateEvent) {
		en.getEntityInfoImpl().setCounterFlag(cid, flag, value);
		if(triggerUpdateEvent) {
			model.nodeChanged(en);
		}
	}

	/**
	 * Returns a map (key: resourceId, value: list of ResourceInfo) with counter info
	 * per entity map for all counters used by the given query.
	 * @param context a non-regex resource id
	 * @return a map of resource info per entity id
	 */
	public Map<ResourceId, List<ResourceInfo>> getCounterInfoPerEntity(ResourceId context, QueryDef query) {
		Map<ResourceId, List<ResourceInfo>> countersPerEntity = new HashMap<ResourceId, List<ResourceInfo>>();
		ResourceId rid;
		ResourceInfo[] ris;
		for(Iterator iter =
			query.getResources().iterator(); iter.hasNext();) {
			rid = ((ResourceDef)iter.next()).getResourceId();
			if(context != null) {
				rid = rid.complete(context);
			}
			if(rid.getRepresentation() != ResourceId.COUNTER) {
			    continue;
			}

			// -------------------------------------------------
			// TODO URGENT!!!!! - #time# counter shouldn't be part
			// of a cube's definition
			// for now hack it here, don't search for info on the
			// timestamp counter
			//
			if(CounterDescriptor.TIMESTAMP_ID.equals(rid.getCounterId())) {
				continue;
			}
			// -------------------------------------------------

			ris = model.getResourceInfo(rid, true);
			if(Utils.isEmptyArray(ris)) {
				if(logger.isTraceEnabled()) {
					logger.error("Couldn't find info for resource: " + rid);
				}
				continue;
			}
			for(int i = 0; i < ris.length; i++) {
				ResourceInfo ri = ris[i];
				CounterInfo ci = ri.getCounterInfo();
				if(ci != null) {
					ResourceId erid = ri.getResourceId().getSubResourceId(ResourceId.ENTITY);
					List<ResourceInfo> list = countersPerEntity.get(erid);
					if(list == null) {
						list = new LinkedList<ResourceInfo>();
						countersPerEntity.put(erid, list);
					}
					list.add(ri);
				}
			}
		}
		return countersPerEntity;
	}

    /**
     * @see com.ixora.rms.client.model.CounterModelHelper#rollbackCounter(com.ixora.rms.client.model.EntityNode, com.ixora.rms.CounterId)
     */
    public void rollbackCounter(EntityNode en, CounterId cid, boolean triggerUpdateEvent) {
        en.getEntityInfoImpl().rollbackCounter(cid);
        if(triggerUpdateEvent) {
        	model.nodeChanged(en);
        }
    }

    /**
     * @see com.ixora.rms.client.model.CounterModelHelper#rollbackCounter(com.ixora.rms.ResourceId)
     */
    public void rollbackCounter(ResourceId cid, boolean triggerUpdateEvent) {
        // get Entity node first
        DefaultMutableTreeNode node = model.getNodeForResourceId(cid.getSubResourceId(ResourceId.ENTITY));
        if(!(node instanceof EntityNode)) {
            // should not happen
            logger.error("Node for resource id: " + cid + " is not an entity");
            return;
        }
        rollbackCounter((EntityNode)node, cid.getCounterId(), triggerUpdateEvent);
    }

    /**
     * @see com.ixora.rms.client.model.CounterModelHelper#setCounterFlag(com.ixora.rms.ResourceId, int, boolean)
     */
    public void setCounterFlag(ResourceId cid, int flag, boolean value, boolean triggerUpdateEvent) {
        // get Entity node first
        DefaultMutableTreeNode node = model.getNodeForResourceId(cid.getSubResourceId(ResourceId.ENTITY));
        if(!(node instanceof EntityNode)) {
            // should not happen
            logger.error("Node for resource id: " + cid + " is not an entity");
            return;
        }
        setCounterFlag((EntityNode)node, cid.getCounterId(), flag, value, triggerUpdateEvent);
    }

    /**
     * @see com.ixora.rms.client.model.CounterModelHelper#getCounterInfo(com.ixora.rms.ResourceId)
     */
    public ResourceInfo getCounterInfo(ResourceId cid) {
        ResourceInfo[]  ris = model.getResourceInfo(cid, true);
        if(Utils.isEmptyArray(ris)) {
            return null;
        }
        return ris[0];
    }
}