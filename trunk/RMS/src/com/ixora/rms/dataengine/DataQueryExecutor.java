/*
 * Created on Aug 18, 2004
 */
package com.ixora.rms.dataengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ixora.rms.ResourceId;
import com.ixora.common.exception.AppRuntimeException;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.utils.Utils;
import com.ixora.rms.CounterType;
import com.ixora.rms.data.CounterValue;
import com.ixora.rms.dataengine.definitions.ParamDef;
import com.ixora.rms.dataengine.definitions.ScriptFunctionDef;
import com.ixora.rms.dataengine.external.QueryData;
import com.ixora.rms.dataengine.external.QueryListener;
import com.ixora.rms.dataengine.external.QuerySeries;
import com.ixora.rms.dataengine.functions.Function;
import com.ixora.rms.dataengine.functions.Identity;
import com.ixora.rms.dataengine.functions.Janino;
import com.ixora.rms.dataengine.reactions.Reaction;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.reactions.ReactionDispatcher;
import com.ixora.rms.reactions.ReactionId;

/**
 * DataQueryExecutor
 * @author Cristian Costache
 * @author Daniel Moraru
 */
/*
 * Modification history
 * --------------------------------------------------
 * 14 Nov 2004 - DM added addListener() and removeListener() methods to allow
 * one query to have multiple listeners.
 * 20 Nov 2004 - DM added expired() as part of the work for fixing resource leaks
 */
public class DataQueryExecutor {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(DataQueryExecutor.class);
	/**
	 * The time in milliseconds after each the
	 * analyzer and function regex instances clean up mechanism kicks in.
	 */
	private static final int REGEX_INSTANCE_CLEANUP_INTERVAL = 60000;
	/** Listeners */
	private List<QueryListener> fListeners = new LinkedList<QueryListener>();
	/** Cube's query */
	private Cube fQuery;
	/** Functions */
	private List<Function> fFunctions = new LinkedList<Function>();
	/** Each regexp match will have a different function instance */
	private Map<FunctionInstanceId, FunctionInstanceData> fMapFunctionInstances
		= new HashMap<FunctionInstanceId, FunctionInstanceData>();
	/** Analyzers prototypes */
	private List<Analyzer> fAnalyzers = new LinkedList<Analyzer>();
	/**
	 * Each regex match will have a different analyzer instance.
	 * These analyzers instances needs to be cleaned up for obvious reasons so
	 * for every
	 */
	private Map<AnalyzerInstanceId, AnalyzerInstanceData> fMapAnalyzerInstances
		= new HashMap<AnalyzerInstanceId, AnalyzerInstanceData>();
	/** Buffer with data collected until now */
	private FlatDataBuffer fBufferCache;
	/** Reaction dispatcher */
	private ReactionDispatcher fReactionDispatcher;
	/** True if analyzers are enabled */
	private boolean fAnalyzersEnabled;
	/** Reaction cool off period */
	private int fReactionCoolOffTime;
	/** Event handler */
	private EventHandler fEventHandler;
	/** @see REGEX_INSTANCE_CLEANUP_INTERVAL */
	private long fLastCleanup;

	/**
	 * Event handler.
	 */
	private final class EventHandler implements Reaction.Listener {
		/**
		 * @see com.ixora.rms.dataengine.reactions.Reaction.Listener#reactionsArmed(com.ixora.rms.reactions.ReactionId[])
		 */
		public void reactionsArmed(ReactionId[] rids) {
			handleReactionsArmed(rids);
		}
		/**
		 * @see com.ixora.rms.dataengine.reactions.Reaction.Listener#reactionsDisarmed(com.ixora.rms.reactions.ReactionId[])
		 */
		public void reactionsDisarmed(ReactionId[] rids) {
			handleReactionsDisarmed(rids);
		}
		/**
		 * @see com.ixora.rms.dataengine.reactions.Reaction.Listener#reactionsFired(com.ixora.rms.reactions.ReactionId[])
		 */
		public void reactionsFired(ReactionId[] rids) {
			handleReactionsFired(rids);
		}
	}

	private static class AnalyzerInstanceData {
		Analyzer analyzer;
		boolean used;

		AnalyzerInstanceData(Analyzer analyzer) {
			this.analyzer = analyzer;
		}
	}

	private static class FunctionInstanceData {
		Function function;
		boolean used;

		FunctionInstanceData(Function fun) {
			this.function = fun;
		}
	}

	/**
     * FunctionInstanceId
     * A function instance is identified by the ID of the original function, plus the
     * resource ID of the match.
     */
    private class FunctionInstanceId {
        private String		id;
        private ResourceId	resourceId;

        /**
         * @param id
         * @param resourceId
         */
        public FunctionInstanceId(String id, ResourceId resourceId) {
            this.id = id;
            this.resourceId = resourceId;
        }

        /**
         * @see java.lang.Object#hashCode()
         */
        public int hashCode() {
            return id.hashCode() ^ resourceId.hashCode();
        }

        /**
         * @see java.lang.Object#equals(java.lang.Object)
         */
        public boolean equals(Object obj) {
            if (!(obj instanceof FunctionInstanceId)) {
                return false;
            }

            FunctionInstanceId that = (FunctionInstanceId)obj;
            if (!Utils.equals(this.id, that.id) || !Utils.equals(this.resourceId, that.resourceId)) {
                return false;
            }
            return true;
        }
    }

    /**
     * AbalyzerInstanceId
     * An analyzer instance is identified by the ID of the original analyzer plus the
     * resource ID of the match.
     */
    private class AnalyzerInstanceId {
        private String id;
        private ResourceId resourceId;

        /**
         * @param id
         * @param resourceId
         */
        public AnalyzerInstanceId(String id, ResourceId resourceId) {
            this.id = id;
            this.resourceId = resourceId;
        }

        /**
         * @see java.lang.Object#hashCode()
         */
        public int hashCode() {
            return id.hashCode() ^ resourceId.hashCode();
        }

        /**
         * @see java.lang.Object#equals(java.lang.Object)
         */
        public boolean equals(Object obj) {
            if (!(obj instanceof AnalyzerInstanceId)) {
                return false;
            }
            AnalyzerInstanceId that = (AnalyzerInstanceId)obj;
            if (!Utils.equals(this.id, that.id) || !Utils.equals(this.resourceId, that.resourceId)) {
                return false;
            }
            return true;
        }
    }

	/**
	 * Constructor. This executor only operates with functions, so it will
	 * create IDENTITY functions for all resources of this query.
	 * @param qs
	 * @param dispatcher
	 * @param analyzersEnabled
	 * @param reactionCoolOffTime
	 */
	public DataQueryExecutor(Cube qs, ReactionDispatcher dispatcher, boolean analyzersEnabled, int reactionCoolOffTime) {
		this.fEventHandler = new EventHandler();
		this.fQuery = qs;
		this.fReactionDispatcher = dispatcher;
		this.fAnalyzersEnabled = analyzersEnabled;
		this.fReactionCoolOffTime = reactionCoolOffTime;
		for (QueryResult queryResult : qs.getQueryResults()) {
		    if (queryResult instanceof Function) {
		        Function f = (Function)queryResult;
		        fFunctions.add(f);
		    } else {
		        Resource r = (Resource)queryResult;
                if (Utils.isEmptyString(r.getStyle().getCode())) {
                    fFunctions.add(new Identity(r));
                } else {
                    ParamDef param = new ParamDef(r.getStyle().getID());
                    List<ParamDef> listParams = new ArrayList<ParamDef>(1);
                    listParams.add(param);
                    ScriptFunctionDef sfd = new ScriptFunctionDef(listParams, r.getStyle().getCode());
                    Janino jan = new Janino(sfd, qs.getQuerySource(), null, r.getStyle()); // null context = already realized
                    fFunctions.add(jan);
                }
		    }
		}
		List<Reaction> reactions = qs.getReactions();
		if(!Utils.isEmptyCollection(reactions)) {
			for(Reaction reaction : reactions) {
				fAnalyzers.add(reaction);
			}
		}
	}

	/**
	 * @param rids
	 */
	private void handleReactionsFired(ReactionId[] rids) {
		try {
			synchronized(this.fListeners) {
				for(QueryListener listener : fListeners) {
					listener.reactionsFired(rids);
				}
			}
		} catch(Exception e) {
			logger.error(e);
		}
	}

	/**
	 * @param rids
	 * @param revs
	 */
	private void handleReactionsDisarmed(ReactionId[] rids) {
		try {
			synchronized(this.fListeners) {
				for(QueryListener listener : fListeners) {
					listener.reactionsDisarmed(rids);
				}
			}
		} catch(Exception e) {
			logger.error(e);
		}
	}

	/**
	 * @param rids
	 */
	private void handleReactionsArmed(ReactionId[] rids) {
		try {
			synchronized(this.fListeners) {
				for(QueryListener listener : fListeners) {
					listener.reactionsArmed(rids);
				}
			}
		} catch(Exception e) {
			logger.error(e);
		}
	}

	/**
	 * Adds a listener.
	 */
	public void addListener(QueryListener l) {
		synchronized(this.fListeners) {
			if(!this.fListeners.contains(l)) {
				this.fListeners.add(l);
			}
		}
	}

	/**
	 * Removes a listener.
	 * @return the number of remaining listeners
	 */
	public int removeListener(QueryListener l) {
		synchronized(this.fListeners) {
			this.fListeners.remove(l);
			return this.fListeners.size();
		}
	}

	/**
	 * A query receives data from DataEngine through this method,
	 * performs its processing and forwards the results through the
	 * listener.
	 *
	 * @param buff buffer to inspect and extract data from
	 */
	public void inspectDataBuffer(FlatDataBuffer buff) throws RMSException {
		// Get resourceIDs which are the source of this query
		List<Resource> sources = fQuery.getQuerySource();
		List<ResourceId> rids = new LinkedList<ResourceId>();
		for(Iterator<Resource> it = sources.iterator(); it.hasNext();) {
			Resource res = it.next();
			rids.add(res.getResourceID());
		}

		// Get a subset of the data buffer, with only the interesting counters
		fBufferCache = buff.filter(rids);

		// If the fdbCache object is not null, then it contains all resources
		// requested by the current query. Queries spanning multiple DataBuffers
		// are not yet supported.
		boolean isComplete = (fBufferCache != null);
		if(isComplete) {
			calculateAndSubmit();
		}
	}

	/**
	 * @param rid
	 * @return an instance of a Function to be executed for this
	 * regexp match, represented by the matched ResourceId.
	 */
	private Function getFunctionInstance(ResourceId rid, Function originalFunction) {
	    // Don't clone stateless functions
        if (!originalFunction.getKeepsState()) {
            return originalFunction;
        }
        // Use an ID made from function's ID and matched regexp id
        FunctionInstanceId functionID = new FunctionInstanceId(originalFunction.getStyle().getID(), rid);
	    FunctionInstanceData retF = fMapFunctionInstances.get(functionID);
	    if (retF == null) {
			try {
			    retF = new FunctionInstanceData((Function)Utils.deepClone(originalFunction));
			} catch(Exception e) {
                throw new AppRuntimeException("Could not create a function clone", e);
			}
	        fMapFunctionInstances.put(functionID, retF);
	    }
	    retF.used = true;
	    return retF.function;
	}

	/**
	 * @param params
	 * @return an instance of a Function to be executed for this
	 * regexp match, represented by the matched ResourceId.
	 */
	private Analyzer getAnalyzerInstance(ResourceData[] params, Analyzer originalAnalyzer) {
		ResourceId rid = params[0].getResourceId();
        // use an ID made from analyzer's ID and matched regexp id
        AnalyzerInstanceId analyzerID = new AnalyzerInstanceId(originalAnalyzer.getID(), rid);
	    AnalyzerInstanceData ret = fMapAnalyzerInstances.get(analyzerID);
	    if (ret == null) {
			try {
			    ret = new AnalyzerInstanceData((Analyzer)Utils.deepClone(originalAnalyzer));
			} catch(Exception e) {
                throw new AppRuntimeException("Could not create an analyzer clone", e);
			}
		    List<ResourceId> matchedRids = new ArrayList<ResourceId>(params.length);
		    for(ResourceData rd : params) {
		    	matchedRids.add(rd.getResourceId());
		    }
		    if(ret.analyzer instanceof Reaction) {
		    	((Reaction)ret.analyzer).initializeInstance(matchedRids, fEventHandler, fReactionDispatcher,
		    			fQuery.getSessionArtefactInfoLocator(), fReactionCoolOffTime);
		    } else { // generic analyzer
		    	ret.analyzer.initializeInstance(matchedRids);
		    }
	        fMapAnalyzerInstances.put(analyzerID, ret);
	    }
	    ret.used = true;
	    return ret.analyzer;
	}


    /**
     * @param rid
     * @return
     */
    private String getDefaultName(ResourceId rid) {
        String retVal = "";
        if (rid == null) {
            return retVal;
        }
        switch (rid.getRepresentation()) {
            case ResourceId.COUNTER:
                retVal = "$host/$agent/$entity/$counter";
                break;
            case ResourceId.ENTITY:
                retVal = "$host/$agent/$entity";
                break;
            case ResourceId.AGENT:
                retVal = "$host/$agent";
                break;
            case ResourceId.HOST:
                retVal = "$host";
                break;
        }
        return retVal;
    }

	/**
	 * When all required data has been extracted from DataEngine, this method
	 * is called to compute the query and submit results to clients.
	 */
	private void calculateAndSubmit() throws RMSException {
	    // Initialize data to be submitted to clients
		int cntSamplesCount = fBufferCache.getCounterSamplesCount();
		QueryData[]	data = new QueryData[cntSamplesCount];
		for(int j = 0; j < cntSamplesCount; j++) {
			data[j] = new QueryData();
		}

		// For each function, extract data and calculate result
		int functionIndex = -1;
		for(Function function : fFunctions) {
			functionIndex++;
			List<ResourceId> functionParams = function.getParameters();
			int cntCounters = functionParams.size();
			// Extract series for each (potential) regexp resourceId
			FlatDataBuffer[] args = new FlatDataBuffer[cntCounters];
			int idx = 0;
			for(ResourceId rID : functionParams) {
				FlatDataBuffer fdbTemp = fBufferCache.getMatchingResourceIds(rID);
				// Should not happen: filter should have returned a null FlatDataBuffer
				if(fdbTemp == null || fdbTemp.size() == 0) {
				    throw new IllegalArgumentException("calculateAndSubmit called with incomplete data.");
				}
				args[idx++] = fdbTemp;
			}

			// Go through each series, note that not all lists have same
			// number of elements.
			boolean seriesEnd = true;
			int idxSeries = 0;
			ResourceData[] params = new ResourceData[cntCounters];
			do {
				seriesEnd = true;
				for(int k = 0; k < cntCounters; k++) {
					// Get the current series, or the last one
					ResourceData rdata;
					FlatDataBuffer fdb = args[k];
					int fdbSize = fdb.size();
					if(fdbSize <= idxSeries) {
						rdata = fdb.get(fdbSize - 1);
					} else {
						rdata = fdb.get(idxSeries);
						seriesEnd = false;
					}
					params[k] = rdata;
				}
				// Now we can finally calculate the function
				if(!seriesEnd) {
				    // Each match of a regular expression will have its own
				    // function that calculates it.
				    Function functionInstance = function;
				    if(cntCounters > 0) {
				        functionInstance = getFunctionInstance(params[0].getResourceId(), function);
				    }
					// Send data for each timestamp independently
					for(int j = 0; j < cntSamplesCount; j++) {
						// Prepare a QueryResult with same ID and name as the series
						QueryResult qr = new QueryResult();
						qr.getStyle().merge(function.getStyle());
						String name = qr.getStyle().getName();
						String instanceName = qr.getStyle().getIName();
						String id = qr.getStyle().getID();

						// Remember regexp match of every counter. For functions
						// it will be the match of the first counter.
						ResourceId matchedResourceId = null;
                        String[] captures = null;
						if(cntCounters > 0) {
							ResourceData rdata = params[0];
							matchedResourceId = rdata.getResourceId();
                            captures = rdata.getCaptures();
						}

						// Prepare parameters: a column in the samples
						CounterValue[] vals = new CounterValue[cntCounters];
						CounterType[] types = new CounterType[cntCounters];
						for(int i = 0; i < cntCounters; i++) {
							ResourceData rdata = params[i];
							vals[i] = rdata.getCounterSamples()[j];
							types[i] = rdata.getCounterType();
							// Give default values for id, name and instanceName
							if (id == null) {
								if (function instanceof Identity) {// resources
									id = rdata.getResourceId().toString();
								} else { // functions
									id = "r" + functionIndex;
								}
							}
							if(name == null) {
								if (function instanceof Identity) { // resources
                                    name = getDefaultName(rdata.getResourceId());
								} else { // functions
									name = "r" + functionIndex;
								}
							}
							if (instanceName == null) {
								if (function instanceof Identity) { // resources
                                    instanceName = getDefaultName(rdata.getResourceId());
								} else { // functions
									instanceName = "$0/" + name;
								}
							}
						}
						// Execute the function and obtain the result. Result
						// may be null, which means the function is a filter
						CounterValue result = functionInstance.execute(vals, types);
						if (result != null) {
							// Don't allow name or id to be null
							if (id == null) {
								id = "r" + functionIndex;
							}
							if (name == null) {
								name = id;
							}
							// If there were no regular expressions then the only
							// series will have function's name
							if (instanceName == null) {
								instanceName = name;
							}

							qr.getStyle().setName(name);
							qr.getStyle().setIName(instanceName);
							qr.getStyle().setID(id);

							// Get or create the current series for this Data object
							QuerySeries	seriesData = data[j].getSeries(idxSeries);
							if (seriesData == null) {
								seriesData = new QuerySeries();
								data[j].addSeries(seriesData);
							}

							// Place it in the resulted list
							seriesData.add(new QueryResultData(qr,
									functionInstance.getReturnedType(), result,
                                    matchedResourceId, captures));
						} else { // function rejected this data
							// Don't create a new series if one didn't exist,
							// but add a null value to it otherwise
							QuerySeries	seriesData = data[j].getSeries(idxSeries);
							if (seriesData != null) {
								seriesData.add(null);
							}
						}
					}
				}
				idxSeries++;
			} while(!seriesEnd);
		}

		// pass data to analyzers now
		if(fAnalyzersEnabled) {
			// TODO reuse FlatDataBuffers found for functions
			// for each analyzer, find data and pass it to be analyzed
			int analyzerIndex = -1;
			for(Analyzer analyzer : fAnalyzers) {
				analyzerIndex++;
				// isolate individual analyzers, if one fails there is no reason to impact the others
				try {
					List<ResourceId> analyzerRes = analyzer.getAnalyzedResources();
					int cntCounters = analyzerRes.size();
					// extract series for each (potential) regexp resourceId
					FlatDataBuffer[] resourceBuffers = new FlatDataBuffer[cntCounters];
					int idx = 0;
					for(ResourceId rID : analyzerRes) {
						FlatDataBuffer fdbTemp = fBufferCache.getMatchingResourceIds(rID);
						// it happens if the current buffer doesn;t hold data for all resources
						if(fdbTemp == null || fdbTemp.size() == 0) {
						    throw new IllegalArgumentException("calculateAndSubmit called with incomplete data.");
						}
						resourceBuffers[idx++] = fdbTemp;
					}

					// Go through each series, note that not all lists have same
					// number of elements.
					boolean seriesEnd = true;
					int idxSeries = 0;
					ResourceData[] params = new ResourceData[cntCounters];
					do {
						seriesEnd = true;
						for(int k = 0; k < cntCounters; k++) {
							// Get the current series, or the last one
							ResourceData rdata;
							FlatDataBuffer fdb = resourceBuffers[k];
							int fdbSize = fdb.size();
							if(fdbSize <= idxSeries) {
								rdata = fdb.get(fdbSize - 1);
							} else {
								rdata = fdb.get(idxSeries);
								seriesEnd = false;
							}
							params[k] = rdata;
						}
						// now we can pass values to analyser
						if(!seriesEnd) {
							// send samples independently
							for(int j = 0; j < cntSamplesCount; j++) {
								// prepare parameters: a column in the history
								List<CounterValue> vals = new ArrayList<CounterValue>(cntCounters);
								for(int i = 0; i < cntCounters; i++) {
									ResourceData rdata = params[i];
									vals.add(rdata.getCounterSamples()[j]);
								}
								Analyzer analyzerInstance = getAnalyzerInstance(params, analyzer);
								analyzerInstance.analyze(vals);
							}
						}
						idxSeries++;
					} while(!seriesEnd);
				} catch(Throwable e) {
					// don't show stacktrace as this should happen only for
					// user code and they will only be interested in the error message
					logger.error("Analyzer failed with the folowing error: " + e.getMessage());
				}
			}
		}

		// Clean out null values entered by filters and make sure all series
		// have the same number of QueryResultData
		for (QueryData queryData : data) {
			int idxSeries = 0;
			for (Iterator<QuerySeries> it = queryData.iterator(); it.hasNext();) {
				QuerySeries querySeries = it.next();
				if (querySeries.contains(null)) {
					// Remove the whole series if at least one value is null (filtered out)
					it.remove();
				} else {
					// Copy empty values from the previous series
					if (idxSeries > 0) {
						QuerySeries prevQuerySeries = queryData.get(idxSeries-1);
						// Only do this if the new series has less elements
						if (querySeries.size() != prevQuerySeries.size()) {
							int idxFunction = 0;
							for (Function function : fFunctions) {
								// If function does not have values in this series,
								// copy from prev series
								String functionID = function.getStyle().getID();
								QueryResultData qrd = null;
								if (idxFunction < querySeries.size()) {
									qrd = querySeries.get(idxFunction);
								}
								if (qrd == null || qrd.getQueryResult().getStyle().getID() != functionID) {
									QueryResultData qrdPrev = prevQuerySeries.get(idxFunction);
									querySeries.add(idxFunction, qrdPrev);
								}

								idxFunction++;
							}
						}
					}
					idxSeries++;
				}
			}
		}

		// Send calculated data to clients
		fireDataAvailable(data);

		// cleanup unused regex instances if necesary
		long now = System.currentTimeMillis();
		if(fLastCleanup == 0) {
			fLastCleanup = now;
		} else if(now - fLastCleanup >= REGEX_INSTANCE_CLEANUP_INTERVAL) {
			fLastCleanup = now;
			// clean up
			for(Iterator<Map.Entry<AnalyzerInstanceId, AnalyzerInstanceData>> iter
					= fMapAnalyzerInstances.entrySet().iterator(); iter.hasNext();) {
				Map.Entry<AnalyzerInstanceId, AnalyzerInstanceData> element = iter.next();
				if(!element.getValue().used) {
					iter.remove();
				} else {
					// prepare for next cycle
					element.getValue().used = false;
				}
			}
			for(Iterator<Map.Entry<FunctionInstanceId, FunctionInstanceData>> iter
					= fMapFunctionInstances.entrySet().iterator(); iter.hasNext();) {
				Map.Entry<FunctionInstanceId, FunctionInstanceData> element = iter.next();
				if(!element.getValue().used) {
					iter.remove();
				} else {
					// prepare for next cycle
					element.getValue().used = false;
				}
			}
		}
	}

	/**
	 * Fires data available event.
	 * @param d
	 */
	private void fireDataAvailable(QueryData[] d) {
		// Maybe the listener could accept the full array, rather than each element.
		synchronized(this.fListeners) {
			for(int i = 0; i < d.length; i++) {
				for(Iterator<QueryListener> iter = this.fListeners.iterator(); iter.hasNext();) {
					// don't penalize all listeners when one throws an exception
					try {
						// TODO: debug, remove
						//System.out.println(d[i].toString());
						iter.next().dataAvailable(d[i]);
					} catch(Exception e) {
						logger.error(e);
					}
				}
			}
		}
	}

	/**
	 * Invoked when the query associated to this executor has been unregistered from
	 * the data engine.
	 */
	public void expired() {
		synchronized(this.fListeners) {
			for(Iterator<QueryListener> iter = this.fListeners.iterator(); iter.hasNext();) {
				try {
					iter.next().expired();
				} catch(Exception e) {
					logger.error(e);
				}
			}
		}
	}

	/**
	 * Disables or enables reactions.
	 * @param enabled
	 */
	public void setAnalyzersEnabled(boolean enabled) {
		fAnalyzersEnabled = enabled;
	}

	/**
	 * Sets the reaction cool off period.
	 * @param sec
	 */
	public void setReactionCoolOffTime(int sec) {
		fReactionCoolOffTime = sec;
	}
}
