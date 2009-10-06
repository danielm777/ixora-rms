/*
 * Created on 12-Dec-2003
 */
package com.ixora.rms.dataengine;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import com.ixora.rms.DataSink;
import com.ixora.common.ConfigurationMgr;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.rms.EntityDataBuffer;
import com.ixora.rms.agents.AgentDataBuffer;
import com.ixora.rms.dataengine.external.QueryListener;
import com.ixora.rms.reactions.ReactionDispatcher;
import com.ixora.rms.reactions.ReactionsComponent;
import com.ixora.rms.reactions.ReactionsComponentConfigurationConstants;
import com.ixora.rms.repository.QueryId;
import com.ixora.rms.services.DataEngineService;

/**
 * This class satisfies data queries.
 * @author Daniel Moraru
 * @author Cristian Costache
 */
/*
 * Modification history
 * --------------------------------------------------
 * 14 Nov 2004 - DM see DataEngineService
 * 20 Nov 004 - DM see DataEngineService
 */
public final class DataEngine implements DataEngineService, DataSink, Observer {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(DataEngine.class);
	/** Queries */
	private Map<QueryId, DataQueryExecutor> fQueries = new HashMap<QueryId, DataQueryExecutor>();
	/** Reaction dispatcher */
	private ReactionDispatcher fReactionDispatcher;
	/** True if analyzers are enabled */
	private boolean fAnalyzersEnabled;
	/** Reaction cool off period */
	private int fReactionCoolOff;
	/** Log replay mode */
	private boolean fLogReplayMode;

	/**
	 * Constructor for DataEngine.
	 * @param rd
	 */
	public DataEngine(ReactionDispatcher rd) {
		this.fReactionDispatcher = rd;
		fAnalyzersEnabled = ConfigurationMgr.get(ReactionsComponent.NAME).getBoolean(ReactionsComponentConfigurationConstants.REACTIONS_ENABLED);
		fReactionCoolOff = ConfigurationMgr.get(ReactionsComponent.NAME).getInt(ReactionsComponentConfigurationConstants.REACTIONS_COOL_OFF_PERIOD);
		ConfigurationMgr.get(ReactionsComponent.NAME).addObserver(this);
	}

	/**
	 * @see com.ixora.rms.DataSink#receiveDataBuffers(AgentDataBuffer[])
	 */
	public void receiveDataBuffers(AgentDataBuffer[] buffs) {
        AgentDataBuffer buff = null;
		try {
            synchronized(this.fQueries) {
    			// complete the buff with data from the cache
    			for(int i = 0; i < buffs.length; i++) {
    				buff = buffs[i];
    				if(!buff.isEmpty()) {
	    				EntityDataBuffer[] ebs = buff.getBuffers();
						// notify all queries of the new data
						FlatDataBuffer fdb = new FlatDataBuffer(buff);
						for(Iterator it = fQueries.values().iterator(); it.hasNext();) {
							DataQueryExecutor dqe = (DataQueryExecutor)it.next();
							// isolate errors per executor
							try {
								dqe.inspectDataBuffer(fdb);
							} catch(Exception e) {
								logger.error(e);
							}
						}
    				}
    			}
            }
		} catch(Exception e) {
			logger.error(e);
			if (logger.isTraceEnabled()) {
				logger.trace("DataBuffer:\n" + String.valueOf(buff));
			}
		}
	}

	/**
	 * @see com.ixora.common.Service#shutdown()
	 */
	public void shutdown() {
		; // nothing to do
	}

	/**
	 * Creates an object to execute the query of the cube. Also attaches
	 * the output of the query to the specified listener.
	 * @see com.ixora.rms.services.DataEngineService#addQuery(com.ixora.rms.repository.QueryId, com.ixora.rms.dataengine.hints.Cube, com.ixora.rms.dataengine.DataQueryExecutor.Listener)
	 */
	public void addQuery(QueryId qid, Cube cube, QueryListener listener) {
		synchronized(this.fQueries) {
			DataQueryExecutor queryExecutor = new DataQueryExecutor(cube, fReactionDispatcher,
					fLogReplayMode ? false : fAnalyzersEnabled, fReactionCoolOff);
			queryExecutor.addListener(listener);
			this.fQueries.put(qid, queryExecutor);
		}
	}

	/**
	 * Removes the query with the given Id.
	 * @see com.ixora.rms.services.DataEngineService#removeQuery(com.ixora.rms.repository.QueryId)
	 */
	public void removeQuery(QueryId qid) {
		DataQueryExecutor queryExecutor = null;
		synchronized(this.fQueries) {
			queryExecutor = this.fQueries.remove(qid);
		}
		if(queryExecutor != null) {
			queryExecutor.expired();
		}
	}

	/**
	 * @see com.ixora.rms.services.DataEngineService#addQueryListener(com.ixora.rms.repository.QueryId, com.ixora.rms.dataengine.DataQueryExecutor.Listener)
	 */
	public void addQueryListener(QueryId qid, QueryListener listener) {
		synchronized(this.fQueries) {
			DataQueryExecutor queryExecutor = this.fQueries.get(qid);
			if(queryExecutor != null) {
				queryExecutor.addListener(listener);
			}
		}
	}

	/**
	 * @see com.ixora.rms.services.DataEngineService#removeQueryListener(com.ixora.rms.repository.QueryId, com.ixora.rms.dataengine.DataQueryExecutor.Listener)
	 */
	public void removeQueryListener(QueryId qid, QueryListener listener) {
		synchronized(this.fQueries) {
			DataQueryExecutor queryExecutor = this.fQueries.get(qid);
			if(queryExecutor != null) {
				int count = queryExecutor.removeListener(listener);
				// remove the query when there are no more listeners
				if(count == 0) {
					this.fQueries.remove(qid);
					if(logger.isTraceEnabled()) {
						logger.info("Removed query " + qid + " as the last listener has just been removed.");
					}
				}
			}
		}
	}

	/**
	 * @see com.ixora.rms.services.DataEngineService#isQueryRegistered(com.ixora.rms.repository.QueryId)
	 */
	public boolean isQueryRegistered(QueryId qid) {
		synchronized(this.fQueries) {
			return this.fQueries.get(qid) != null ? true : false;
		}
	}

	/**
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg) {
		if(o == ConfigurationMgr.get(ReactionsComponent.NAME)) {
			fAnalyzersEnabled = ConfigurationMgr.get(ReactionsComponent.NAME).getBoolean(ReactionsComponentConfigurationConstants.REACTIONS_ENABLED);
			fReactionCoolOff = ConfigurationMgr.get(ReactionsComponent.NAME).getInt(ReactionsComponentConfigurationConstants.REACTIONS_COOL_OFF_PERIOD);
			synchronized(this.fQueries) {
				for(DataQueryExecutor exec : fQueries.values()) {
					exec.setAnalyzersEnabled(fAnalyzersEnabled);
					exec.setReactionCoolOffTime(fReactionCoolOff);
				}
			}
		}
	}

	/**
	 * @see com.ixora.rms.services.DataEngineService#setLogReplayMode(boolean)
	 */
	public void setLogReplayMode(boolean lrm) {
		fLogReplayMode = lrm;
	}
}
