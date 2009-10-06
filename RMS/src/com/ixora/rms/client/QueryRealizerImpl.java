/*
 * Created on 12-Dec-2003
 */
package com.ixora.rms.client;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ixora.rms.ResourceId;
import com.ixora.common.Progress;
import com.ixora.common.ProgressProvider;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.utils.Utils;
import com.ixora.rms.CounterId;
import com.ixora.rms.EntityConfiguration;
import com.ixora.rms.EntityDescriptorTree;
import com.ixora.rms.MonitoringLevel;
import com.ixora.rms.client.locator.SessionArtefactInfoLocatorImpl;
import com.ixora.rms.client.model.CounterInfo;
import com.ixora.rms.client.model.EntityInfo;
import com.ixora.rms.client.model.QueryInfo;
import com.ixora.rms.client.model.ResourceInfo;
import com.ixora.rms.client.model.SessionModel;
import com.ixora.rms.dataengine.Cube;
import com.ixora.rms.dataengine.definitions.QueryDef;
import com.ixora.rms.dataengine.external.QueryListener;
import com.ixora.rms.exception.InvalidAgentState;
import com.ixora.rms.exception.InvalidConfiguration;
import com.ixora.rms.exception.InvalidEntity;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.reactions.ReactionId;
import com.ixora.rms.repository.QueryId;
import com.ixora.rms.services.DataEngineService;
import com.ixora.rms.services.DataViewRepositoryService;
import com.ixora.rms.services.MonitoringSessionService;

/**
 * This class realizes queries by enabling the necessary counters and registering them
 * with the data engine; it monitors changes in the session
 * model and updates counters that are required by the registered queries.
 * Note: As this class operates on the model it must remain
 * thread unaware and the caller is responsible for providing
 * the correct execution thread; the class is not designed for
 * multithreaded access.
 * @author Daniel Moraru
 */
public final class QueryRealizerImpl implements QueryRealizer {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(QueryRealizerImpl.class);

	/** Session model */
	private SessionModel fSessionModel;
	/**
	 * Session service.
	 * Used to enable/disable counters.
	 */
	private MonitoringSessionService fSessionService;
	/** Progress provider */
	private Progress fProgress;
	/**
	 * Map with all registered queries. The query realizer
	 * listens to entity changes in the model and makes changes
	 * to the configuration of the newly added entities that
	 * must contribute to any of the registered queries.
	 */
	private Map<QueryId, QueryData> fQueries;
	/** Event handler */
	private EventHandler fEventHandler;
	/** True when in process of updating the session model */
	private boolean fUpdatingModel;
	/** Artefact info locator */
    private SessionArtefactInfoLocatorImpl fArtefactInfoLocator;
    /** Reference to the data engine */
    private DataEngineService fDataEngine;

	/**
	 * Query data.
	 */
	private static final class QueryData {
		public QueryData(ResourceId context, QueryDef q) {
			this.context = context;
			this.query = q;
			count = 1;
		}
		/** Query context */
		ResourceId context;
		/** Query def */
		QueryDef query;
		/** Number of times register was called */
		int count;
	}

	/**
	 * Event handler.
	 */
	private final class EventHandler implements SessionModel.RoughListener, QueryListener {
		/**
		 * @see com.ixora.rms.client.model.SessionModel.RoughListener#modelChanged(com.ixora.rms.internal.ResourceId[], int, int)
		 */
		public void modelChanged(ResourceId[] ridChanged, int change, int changeType) {
			handleSessionModelChanged(ridChanged, change, changeType);
		}
		/**
		 * @see com.ixora.rms.dataengine.external.QueryListener#expired()
		 */
		public void expired() {
			; // not interested
		}
		/**
		 * @see com.ixora.rms.dataengine.external.QueryListener#reactionsArmed(com.ixora.rms.reactions.ReactionId[])
		 */
		public void reactionsArmed(ReactionId[] rids) {
			; // not interested
		}
		/**
		 * @see com.ixora.rms.dataengine.external.QueryListener#reactionsDisarmed(com.ixora.rms.reactions.ReactionId[])
		 */
		public void reactionsDisarmed(ReactionId[] rids) {
			; // not interested
		}
		/**
		 * @see com.ixora.rms.dataengine.external.QueryListener#reactionsFired(com.ixora.rms.reactions.ReactionId[])
		 */
		public void reactionsFired(ReactionId[] rids) {
			; // not interested
		}
		/**
		 * @see com.ixora.rms.dataengine.external.QueryListener#dataAvailable(com.ixora.rms.dataengine.external.QueryData)
		 */
		public void dataAvailable(com.ixora.rms.dataengine.external.QueryData data) {
			; // not interested
		}
	}

	/**
	 * Constructor.
	 * @param sessionData
	 * @param monitoringSession
	 * @param dataEngine
	 * @param dataViewRepository
	 */
	public QueryRealizerImpl(
			SessionModel sessionData,
			MonitoringSessionService monitoringSession,
			DataEngineService dataEngine,
			DataViewRepositoryService dataViewRepository) {
		if(sessionData == null) {
			throw new IllegalArgumentException("null session model");
		}
		if(monitoringSession == null) {
			throw new IllegalArgumentException("null monitoring session service");
		}
		if(dataEngine == null) {
			throw new IllegalArgumentException("null data engine service");
		}

		this.fEventHandler = new EventHandler();
		this.fSessionModel = sessionData;
		this.fSessionModel.addListener(fEventHandler);
		this.fDataEngine = dataEngine;
		this.fSessionService = monitoringSession;
		this.fProgress = new Progress();
		this.fQueries = new HashMap<QueryId, QueryData>();
		this.fArtefactInfoLocator = new SessionArtefactInfoLocatorImpl(sessionData, dataViewRepository);
	}

	/**
	 * Enables all counters that are needed by the given
	 * query.
	 * @param context the context of the given query
	 * @param cube query
	 * @param callback
	 * @throws InvalidAgentState
	 * @throws InvalidConfiguration
	 * @throws InvalidEntity
	 * @throws RemoteException
	 * @throws RMSException
	 */
	public void realizeQuery(ResourceId context, QueryDef query, Callback callback)
			throws
				InvalidEntity,
				InvalidConfiguration,
				InvalidAgentState,
				RMSException,
				RemoteException {
		// realize query with reference counting
		realizeQueryRefCount(context, query, true, callback);
	}

	/**
	 * Enables all counters that are needed by the given
	 * query. Each call to realize MUST be followed by a call
	 * to unrealize as reference counting is used to keep track
	 * of queries.
	 * @param context the context of the given query
	 * @param cube query
	 * @param count whether or not to use reference counting
	 * @param callback
	 * @throws InvalidAgentState
	 * @throws InvalidConfiguration
	 * @throws InvalidEntity
	 * @throws RemoteException
	 * @throws RMSException
	 */
	private void realizeQueryRefCount(ResourceId context, QueryDef query, boolean count, Callback callback)
			throws
				InvalidEntity,
				InvalidConfiguration,
				InvalidAgentState,
				RMSException,
				RemoteException {
		// if this is an external call (i.e. count is true) then
		// do not re-realize an already registered query, just increase
		// the reference count
		QueryId qid = new QueryId(context, query.getIdentifier());

		// cache here counters that have to be enabled
		// per entity (host/agentID/entityID)
		Map<ResourceId, List<ResourceInfo>> countersPerEntity = fSessionModel.
				getCounterHelper().getCounterInfoPerEntity(context, query);

		// read the map and configure each entity
		fProgress.setMax(countersPerEntity.size());
		ResourceId erid;
		EntityConfiguration conf;
		CounterInfo ci;
		EntityInfo ei = null;
		boolean error;
		boolean alreadyInvokedCallback = false;
		// for each entity...
		for(Map.Entry<ResourceId, List<ResourceInfo>> me : countersPerEntity.entrySet()) {
            // continue if this entity fails to be reconfigured
            try {
    			error = false;
    			erid = me.getKey();
    			List<ResourceInfo> erds = me.getValue();
    			Set<CounterId> cids = new HashSet<CounterId>();
    			List<ResourceInfo> countersThatNeedUpdateToMonitoringLevel = new LinkedList<ResourceInfo>();

    			MonitoringLevel currentMonitoringLevelForEntity = null;
    			int i = 0;
                // for each entity... compile the list of counters
                // that need to be enabled
    			ResourceInfo ri;
    			for(Iterator<ResourceInfo> iterator = erds.iterator();
    					iterator.hasNext(); ++i) {
    				ri = iterator.next();
    				if(i == 0) {
    					// get the entity id from the resurce info
    					// for the first counter
    					ei = ri.getEntityInfo();
    					if(ei == null) {
    						// that shouldn't happen
    						logger.error("Entity info not found for: " + ri);
    						error = true;
    						break;
    					}
    					currentMonitoringLevelForEntity = ei.getConfiguration().getMonitoringLevel();
    					this.fProgress.setTask(ei.getTranslatedPath());
    				}
    				ci = ri.getCounterInfo();
    				if(ci != null) {
                        if(!ci.isCommitted() || !ci.getFlag(CounterInfo.ENABLED)) {
                            // no need to check monitoring level as this will be
                            // updated by the agent if needed
        					cids.add(ci.getId());
        					// but check it just so we can pass it to the callback for
        					// confirmation
        					if(currentMonitoringLevelForEntity != null) {
        						if(ci.getLevel().compareTo(currentMonitoringLevelForEntity) > 0) {
        							countersThatNeedUpdateToMonitoringLevel.add(ri);
        						}
        					}
                        }
    				} else {
    					// that shouldn't happen
    					logger.error("Counter info not found for: " + ri);
    				}
    			}

    			// invoke callback if needed
    			if(callback != null) {
	    			if(!Utils.isEmptyCollection(countersThatNeedUpdateToMonitoringLevel)) {
	    				if(!alreadyInvokedCallback) {
	    					alreadyInvokedCallback = true;
	    					if(!callback.acceptIncreaseInMonitoringLevel(countersThatNeedUpdateToMonitoringLevel)) {
	    						return;
	    					}
	    				}
	    			}
    			}

                // if list is valid, reconfigure entity
    			if(!error && cids.size() > 0) {
        			// add the counters that are already monitored
        			Set<CounterId> alreadyMonitored =
        				ei.getConfiguration().getMonitoredCountersIds();
        			if(alreadyMonitored != null) {
        				cids.addAll(alreadyMonitored);
        			}
    				conf = new EntityConfiguration(cids);
    				EntityDescriptorTree descriptors = fSessionService.configureEntity(
    						erid.getHostId().toString(),
    						erid.getAgentId(),
    						erid.getEntityId(), conf);
    				// update model with the new entity
    				// configuration
    				try {
    					fUpdatingModel = true;
    					fSessionModel.updateEntities(
    						erid.getHostId().toString(),
    						erid.getAgentId(),  descriptors);
    				} finally {
    					fUpdatingModel = false;
    				}
                }
            } catch(InvalidEntity e) {
                // this can happen under normal circumstances for
                // volatile entities
                if(logger.isTraceEnabled()) {
                    logger.error(e);
                }
            } catch(Exception e) {
                logger.error(e);
            }
			this.fProgress.setDelta(1);
		}
		this.fProgress.done();

        QueryData qd = this.fQueries.get(qid);
        if(qd != null) {
            if(count) {
                qd.count++;
            }
        } else {
            fQueries.put(qid, new QueryData(context, query));

    		// create query and register it with the data engine if not already registered
    		if(!fDataEngine.isQueryRegistered(qid)) {
    			Cube cube = new Cube(fArtefactInfoLocator, query, context);
    			fDataEngine.addQuery(qid, cube, fEventHandler);
    		}
        }

		// commit query status to enabled
		fSessionModel.getQueryHelper().setQueryFlag(
		        QueryInfo.ENABLED,
		        context, query.getIdentifier(),
                true, true,
                false); // no need to update state of counters as we've just
                        // updated the required entities
	}

	/**
	 * Tries to disable all counters that were used by the given
	 * query and unregisters it from this query realizer.
	 * @param qid
	 * @param force
	 * @throws InvalidConfiguration
	 * @throws InvalidEntity
	 * @throws RemoteException
	 * @throws RMSException
	 */
	public void unrealizeQuery(QueryId qid, boolean force)
			throws
				InvalidEntity,
				InvalidConfiguration,
				InvalidAgentState,
				RMSException,
				RemoteException {
		// do not disable counters used by this query
		// this is a workaround for the lack of reference counting for
		// enabling/disabling counters
/*
		// get here counters per entity (host/agentID/entityID)
		Map<ResourceId, List<ResourceInfo>> countersPerEntity = sessionModel.
				getCounterHelper().getCounterInfoPerEntity(context, cube);

		// read the map and configure each entity
		progress.setMax(countersPerEntity.size());
		ResourceId erid;
		EntityConfiguration conf;
		CounterInfo ci;
		EntityInfo ei = null;
		MonitoringLevel entityLevel = null;
		boolean error;
		for(Map.Entry<ResourceId, List<ResourceInfo>> me : countersPerEntity.entrySet()) {
			error = false;
			erid = me.getKey();
			List<ResourceInfo> erds = me.getValue();
			Set<CounterId> cids = new HashSet<CounterId>();
			int i = 0;
			ResourceInfo ri;
			for(Iterator<ResourceInfo> iterator = erds.iterator();
					iterator.hasNext(); ++i) {
				ri = iterator.next();
				if(i == 0) {
					// get the entity id from the resurce info
					// for the first counter
					ei = ri.getEntityInfo();
					if(ei == null) {
						// that shouldn't happen
						logger.error("Entity info not found for: " + ri);
						error = true;
						break;
					}
					this.progress.setTask(ei.getTranslatedPath());
				}
				ci = ri.getCounterInfo();
				if(ci != null) {
					if(ci.isDisposable()) {
						cids.add(ci.getId());
					}
				} else {
					// that shouldn't happen
					logger.error("Counter info not found for: " + ri);
				}
			}

			if(error) {
			    break;
			}

			// check the counters that are already monitored
			Set<CounterId> alreadyMonitored =
				ei.getConfiguration().getMonitoredCountersIds();
			// if this is null then no need to do anything
			if(alreadyMonitored != null) {
				if(cids.size() > 0) {
					Set<CounterId> newCids = new HashSet<CounterId>(alreadyMonitored);
					newCids.removeAll(cids);
					conf = new EntityConfiguration(newCids);
					if(entityLevel != null) {
						conf.setMonitoringLevel(entityLevel);
					}
					sessionService.configureEntity(
							erid.getHostID().toString(),
							erid.getAgentID(),
							erid.getEntityID(), conf);
					// update model with the new entity
					// configuration
					try {
						updatingModel = true;
						sessionModel.applyEntityConfiguration(erid, conf);
					} finally {
						updatingModel = false;
					}
				}
			}
			this.progress.setDelta(1);
		}
		this.progress.done();
*/
		// remove query if ref count is 0
		QueryData qd = this.fQueries.get(qid);
		if(qd != null) {
			qd.count--;
			if(qd.count == 0 || force) {
				fQueries.remove(qid);

				// deregister query listener with the data engine
				fDataEngine.removeQueryListener(qid, fEventHandler);

				fSessionModel.getQueryHelper().setQueryFlag(
				        QueryInfo.ENABLED,
				        qid.getContext(), qid.getName(), false, true, true);
			}
		}
	}

	/**
	 * @return the progress provider
	 */
	public ProgressProvider getProgressProvider() {
		return this.fProgress;
	}

	/**
	 * Checks if the newly added nodes are agents or entities
	 * and if so it checks if they must be configured to satisfy
	 * any registered query.
	 * @param ridChanged
     * @param e
     */
    private void handleSessionModelChanged(ResourceId[] ridChanged, int change, int changeType) {
        try {
        	if(fUpdatingModel) {
        		return;
        	}
        	StringBuffer msgRids = new StringBuffer();
        	for (int i = 0; i < ridChanged.length; i++) {
        		msgRids.append(ridChanged[i]);
        		if(i != ridChanged.length - 1) {
        			msgRids.append(", ");
        		}
			}
        	String reasonMsg = null;
        	boolean interested = false;
        	if(change == SessionModel.RoughListener.CHANGE_AGENTS
        			&& changeType == SessionModel.RoughListener.CHANGE_TYPE_ADDED) {
        		interested = true;
        		reasonMsg = "Agent added: " + msgRids;
        	}
        	if(!interested) {
	        	if(change == SessionModel.RoughListener.CHANGE_ENTITIES) {
	        		if(changeType == SessionModel.RoughListener.CHANGE_TYPE_ADDED) {
		        		interested = true;
		        		reasonMsg = "Entities added: " + msgRids;
	        		}
	        	}
        	}
        	if(interested) {
	            // just re-realize all registered queries
	            for(Iterator<Map.Entry<QueryId, QueryData>> iter = fQueries.entrySet().iterator(); iter.hasNext();) {
	            	Map.Entry<QueryId, QueryData> me = iter.next();
	                QueryId qid = me.getKey();
	                QueryDef query = me.getValue().query;
	                boolean reRealizeQuery = false;
	                for(ResourceId rid : ridChanged) {
						if(query.isQueryInterestedInResource(qid.getContext(), rid)) {
							reRealizeQuery = true;
							break;
						}
					}
	                if(reRealizeQuery) {
		                try {
		                    if(logger.isTraceEnabled()) {
		                        logger.info("Automatically re-realizing query " + qid + " as a result of a change in the session model."
		                        		+ (reasonMsg != null ? "Reason: " + reasonMsg : ""));
		                    }
		                    // realize query without reference counting
		                    realizeQueryRefCount(qid.getContext(), query, false, null);
			            } catch(Exception e) {
			                logger.error(e);
			            }
	                }
	            }
        	}
        	// if an agent is removed, remove also all queries whose
        	// context is a descendent of the agent's resource id
        	if(change == SessionModel.RoughListener.CHANGE_AGENTS
        			&& changeType == SessionModel.RoughListener.CHANGE_TYPE_REMOVED) {
        		for(ResourceId ridAgent : ridChanged) {
        			// go through the list of registered queries
        			// and remove (from realizer AND data engine)
        			for(Iterator<QueryData> itr = fQueries.values().iterator(); itr.hasNext(); ) {
        				QueryData queryData = itr.next();
        				ResourceId queryContext = queryData.context;
        				if(queryContext != null && queryContext.contains(ridAgent)) {
        					// remove it
        					itr.remove();
        					// deregister with the data engine
        					fDataEngine.removeQuery(
        						new QueryId(queryData.context, queryData.query.getIdentifier()));
        				}
        			}
        		}
        	}

        } catch(Exception e) {
            logger.error(e);
        }
    }

    /**
     * @return the registered queries
     */
    public Map<QueryId, QueryDef> getRegisteredQueries() {
    	Map<QueryId, QueryDef> ret = new HashMap<QueryId, QueryDef>(this.fQueries.size());
        for(Map.Entry<QueryId, QueryData> me : fQueries.entrySet()) {
        	ret.put(me.getKey(), me.getValue().query);
        }
        return ret;
    }

	/**
	 * @see com.ixora.rms.client.QueryRealizer#isQueryRegistered(com.ixora.rms.repository.QueryId)
	 */
	public boolean isQueryRegistered(QueryId queryId) {
		return fQueries.keySet().contains(queryId);
	}
}
