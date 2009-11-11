/*
 * Created on 13-Aug-2004
 */
package com.ixora.rms.client.model;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ixora.rms.ResourceId;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.rms.dataengine.definitions.QueryDef;

/**
 * @author Daniel Moraru
 */
final class QueryModelHelperImpl implements QueryModelHelper {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(QueryModelHelperImpl.class);

    /** Session model */
    private SessionModel model;

    /**
     * Constructor.
     * @param model
     */
    QueryModelHelperImpl(SessionModel model) {
        this.model = model;
    }

	/**
	 * Sets the flag for the given query.
	 * @param f QueryInfo.ENABLED or ACTIVATED
	 * @param context
	 * @param queryId
	 * @param e
	 * @param commit
     * @param updateCounters whether or not to update the state
     * of the required counters; depending on the context where this method is called
     * it might not be necessary to update the state of the counters
	 */
	public void setQueryFlag(
	        	int f,
				ResourceId context,
				String queryId,
				boolean v,
				boolean commit,
                boolean updateCounters) {
		ArtefactInfoContainerImpl qc = model.getArtefactContainerImplForResource(context, true);
		if(qc == null) {
			if(logger.isTraceEnabled()) {
				logger.error("Couldn't find query container for: " + context);
			}
			return;
		}
		QueryInfo qi = qc.getQueryInfo(queryId);
		if(qi == null) {
			if(logger.isTraceEnabled()) {
				logger.error("Couldn't find query for name: "
				        + queryId + " Context: " + context);
			}
			return;
		}
        if(updateCounters) {
    		setCountersFlagForQuery(
    		        f,
    				context,
    				qi,
    				v);
        }
		qc.setQueryFlag(f, queryId, v, commit);
		model.refreshNode(context);
	}

	/**
	 * Sets the queries associated with the given resource.
	 * @param id a valid, non regex resource id
	 * @param q
	 */
	public void setQueries(ResourceId id, QueryDef[] q) {
		ArtefactInfoContainerImpl qc = model.getArtefactContainerImplForResource(id, true);
		if(qc == null) {
			if(logger.isTraceEnabled()) {
				logger.error("Couldn't find query container for: " + id);
			}
			return;
		}
		qc.setQueries(q, model.isLogReplayMode());
		model.refreshNode(id);
	}

	/**
	 * Pre enables all the counters that are part of the
	 * given cube.
	 * @param flag
	 * @param context
	 * @param ci
	 * @param value
	 */
	private void setCountersFlagForQuery(
	        int flag,
			ResourceId context,
			QueryInfo ci,
			boolean value) {
		Map<ResourceId, List<ResourceInfo>> cpe = model.getCounterHelper().getCounterInfoPerEntity(context, ci.getQuery());
		for(Iterator<Map.Entry<ResourceId, List<ResourceInfo>>> iter = cpe.entrySet().iterator(); iter.hasNext();) {
			Map.Entry<ResourceId, List<ResourceInfo>> me = iter.next();
			ResourceId eid = (ResourceId)me.getKey();
			EntityNode en = model.findEntityNode(
					eid.getHostId().toString(),
					eid.getAgentId(),
					eid.getEntityId(), false);
			if(en == null) {
				continue;
			}
			List<ResourceInfo> counters = me.getValue();
			ResourceInfo ri;
			CounterInfo ciinf;
			for(Iterator<ResourceInfo> iterator = counters.iterator(); iterator.hasNext();) {
				ri = iterator.next();
				ciinf = ri.getCounterInfo();
				if(ciinf != null) {
				    switch(flag) {
					    case QueryInfo.ENABLED:
					    	// if enabled is false then change the flag only for
					    	// counters which are not committed (this is to avoid a query
					    	// disabling the commited counters of another one)
					    	// @see QueryRealizerLiveSession for an excuse
					    	if(value || !ciinf.isCommitted()) {
								model.getCounterHelper().setCounterFlag(en, ciinf.getId(),
								        CounterInfo.ENABLED, value, false);
					    	}
					        break;
					    case QueryInfo.ACTIVATED:
					        model.getCounterHelper().setCounterFlag(en, ciinf.getId(),
					                CounterInfo.ACTIVATED, value, false);
					    break;
				    }
				}
			}
			model.nodeChanged(en);
		}
	}

	/**
	 * Rolls back all counters that are part of the
	 * given queries.
	 * @param context
	 * @param qis
	 */
	private void rollbackCountersForQueries(
			ResourceId context,
			Collection<QueryInfoImpl> qis) {
		if(qis == null) {
			return;
		}
		for(QueryInfoImpl qi : qis) {
			rollbackCountersForQuery(context, qi);
		}
	}

	/**
	 * Rolls back all counters that are part of the
	 * given queries.
	 * @param context
	 * @param ci collection of QueryInfo
	 */
	private void rollbackCountersForQuery(
			ResourceId context,
			QueryInfoImpl qi) {
		Map<ResourceId, List<ResourceInfo>> cpe = model.getCounterHelper().getCounterInfoPerEntity(context, qi.getQuery());
		for(Iterator<Map.Entry<ResourceId, List<ResourceInfo>>> iter = cpe.entrySet().iterator(); iter.hasNext();) {
			Map.Entry<ResourceId, List<ResourceInfo>> me = iter.next();
			ResourceId eid = (ResourceId)me.getKey();
			EntityNode en = model.findEntityNode(
					eid.getHostId().toString(),
					eid.getAgentId(),
					eid.getEntityId(), false);
			if(en == null) {
				if(logger.isTraceEnabled()) {
					logger.error("Couldn't find entity node for : " + eid);
				}
				continue;
			}
			List<ResourceInfo> counters = me.getValue();
			ResourceInfo ri;
			CounterInfo ciinf;
			for(Iterator<ResourceInfo> iterator = counters.iterator(); iterator.hasNext();) {
				ri = iterator.next();
				ciinf = ri.getCounterInfo();
				if(ciinf != null) {
				    model.getCounterHelper().rollbackCounter(en, ciinf.getId(), false);
				} else {
					if(logger.isTraceEnabled()) {
						logger.error("Couldn't find counter info for : " + ri);
					}
				}
			}
			qi.rollback();
			model.nodeChanged(en);
		}
	}

	/**
	 * Returns true if all counters required by the given
	 * query are present, are enabled and committed.<br>
	 * Used by the log replay view to filter out queries whose
	 * counters are not available.
	 * @return
	 */
	public boolean isQueryReady(ResourceId context, QueryDef query) {
		Map<ResourceId, List<ResourceInfo>> cpe = model.getCounterHelper().getCounterInfoPerEntity(context, query);
		if(cpe.size() == 0) {
		    return false;
		}
		for(Iterator<Map.Entry<ResourceId, List<ResourceInfo>>> iter = cpe.entrySet().iterator(); iter.hasNext();) {
			Map.Entry<ResourceId, List<ResourceInfo>> me = iter.next();
			ResourceId eid = me.getKey();
			EntityNode en = model.findEntityNode(
					eid.getHostId().toString(),
					eid.getAgentId(),
					eid.getEntityId(), false);
			if(en == null) {
				if(logger.isTraceEnabled()) {
					logger.error("Couldn't find entity node for : " + eid);
				}
				return false;
			}
			List<ResourceInfo> counters = me.getValue();
			CounterInfo ciinf;
			for(Iterator<ResourceInfo> iterator = counters.iterator(); iterator.hasNext();) {
				ResourceInfo ri = iterator.next();
				ciinf = ri.getCounterInfo();
				if(ciinf != null) {
				    if(!ciinf.isEnabled() || !ciinf.isCommitted()) {
				        return false;
				    }
				} else {
					if(logger.isTraceEnabled()) {
						logger.error("Couldn't find counter info for : " + ri);
					}
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Rolls back the queries belonging to the given
	 * resource.
	 * @param id
	 */
	public void rollbackQueries(ResourceId id) {
		ArtefactInfoContainerImpl qc = model.getArtefactContainerImplForResource(id, true);
		if(qc == null) {
			if(logger.isTraceEnabled()) {
				logger.error("Couldn't find query container for: " + id);
			}
			return;
		}
		rollbackCountersForQueries(id, qc.getQueryInfoImpl());
		model.refreshNode(id);
	}

	/**
	 * Rolls back the query belonging to the given
	 * resource.
	 * @param id
	 * @param queryId
	 */
	public void rollbackQuery(ResourceId id, String queryId) {
		ArtefactInfoContainerImpl qc = model.getArtefactContainerImplForResource(id, true);
		if(qc == null) {
			if(logger.isTraceEnabled()) {
				logger.error("Couldn't find query container for: " + id);
			}
			return;
		}
		QueryInfoImpl qi = qc.getQueryInfoImpl(queryId);
		if(qi == null) {
			if(logger.isTraceEnabled()) {
				logger.error("Couldn't find query info for: " + queryId);
			}
			return;
		}
		rollbackCountersForQuery(id, qi);
		model.refreshNode(id);
	}

    /**
     * @see com.ixora.rms.client.model.QueryModelHelper#commitQuery(com.ixora.rms.ResourceId, java.lang.String)
     */
    public void commitQuery(ResourceId id, String queryName) {
		ArtefactInfoContainerImpl qc = model.getArtefactContainerImplForResource(id, true);
		if(qc == null) {
			if(logger.isTraceEnabled()) {
				logger.error("Couldn't find query container for: " + id);
			}
			return;
		}
		QueryInfoImpl qi = qc.getQueryInfoImpl(queryName);
		if(qi == null) {
			if(logger.isTraceEnabled()) {
				logger.error("Couldn't find query info for: " + queryName);
			}
			return;
		}
		qi.commit();
	}

    /**
     * @see com.ixora.rms.client.model.QueryModelHelper#addQuery(com.ixora.rms.ResourceId)
     */
    public void addQuery(ResourceId id, QueryDef query) {
        ArtefactInfoContainerImpl qc = model.getArtefactContainerImplForResource(id, true);
		if(qc == null) {
			if(logger.isTraceEnabled()) {
				logger.error("Couldn't find query container for: " + id);
			}
			return;
		}
		qc.addQuery(query);
		model.refreshNode(id);
    }

    /**
     * @see com.ixora.rms.client.model.QueryModelHelper#removeQuery(com.ixora.rms.ResourceId, java.lang.String)
     */
    public void removeQuery(ResourceId id, String queryName) {
		ArtefactInfoContainerImpl qc = model.getArtefactContainerImplForResource(id, true);
		if(qc == null) {
			if(logger.isTraceEnabled()) {
				logger.error("Couldn't find query container for: " + id);
			}
			return;
		}
		qc.removeQuery(queryName);
		model.refreshNode(id);
    }

    /**
     * @see com.ixora.rms.client.model.QueryModelHelper#getAllQueriesToRealize()
     */
    @SuppressWarnings("unchecked")
	public Map<ResourceId, Collection<QueryInfo>> getAllQueriesToRealize() {
        Map<ResourceId, Collection<QueryInfo>> ret = new HashMap<ResourceId, Collection<QueryInfo>>();
        SessionNode sn = model.getSessionNode();
        Enumeration<ResourceNode> e = sn.breadthFirstEnumeration();
        ResourceNode rn;
        while(e.hasMoreElements()) {
            rn = (ResourceNode)e.nextElement();
	        // get the realizable queries
	        Collection<QueryInfo> col = sn.getArtefactInfoContainer().getQueriesToRealize();
	        if(col != null) {
	            ResourceId rid = rn.getResourceId();
	            ret.put(rid, col);
	        }
        }
        return ret;
    }

    /**
     * @see com.ixora.rms.client.model.QueryModelHelper#getAllQueriesToUnRealize()
     */
    @SuppressWarnings("unchecked")
	public Map<ResourceId, Collection<QueryInfo>> getAllQueriesToUnRealize() {
        Map<ResourceId, Collection<QueryInfo>> ret = new HashMap<ResourceId, Collection<QueryInfo>>();
        SessionNode sn = model.getSessionNode();
        Enumeration<ResourceNode> e = sn.breadthFirstEnumeration();
        ResourceNode rn;
        while(e.hasMoreElements()) {
            rn = e.nextElement();
	        // get the unrealizable queries
	        Collection<QueryInfo> col = sn.getArtefactInfoContainer().getQueriesToUnRealize();
	        if(col != null) {
	            ResourceId rid = rn.getResourceId();
	            ret.put(rid, col);
	        }
        }
        return ret;
    }
}
