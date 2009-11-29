/*
 * Created on 07-Jun-2004
 */
package com.ixora.rms.logging;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import com.ixora.common.ComponentConfiguration;
import com.ixora.common.ConfigurationMgr;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.rms.DataSink;
import com.ixora.rms.ResourceId;
import com.ixora.rms.agents.AgentDataBuffer;
import com.ixora.rms.client.session.MonitoringSessionDescriptor;
import com.ixora.rms.logging.data.AggAgentDataBuffer;
import com.ixora.rms.logging.db.DataLogRepositoryDB;
import com.ixora.rms.logging.exception.DataLogException;
import com.ixora.rms.logging.exception.InvalidLogRepository;
import com.ixora.rms.logging.exception.NoLogWasLoaded;
import com.ixora.rms.logging.messages.Msg;
import com.ixora.rms.logging.xml.DataLogRepositoryXML;
import com.ixora.rms.services.DataLogReplayService;

/**
 * Replays logged data.
 * @author Daniel Moraru
 */
public final class DataLogReplay implements DataLogReplayService, Observer {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(DataLogReplay.class);
    /** Data burst threshold in time difference */
    private static final long BURST_THRESHOLD_MILLISECONDS = 800;
    /** Data burst size */
    private static final int BURST_SIZE = 10;

    /** Data sink to receive read data buffers */
	private DataSink fSink;
	/** Monitoring scheme */
	private MonitoringSessionDescriptor fScheme;
	/** Data log reader for log 1 */
	private DataLogReader fReader1;
	/** Data log reader for log 2 */
	private DataLogReader fReader2;
	/** Playing flag */
	private boolean fPlaying;
	/** Paused flag */
	private boolean fPaused;
    /**
     * Keeps a set of agent data buffers that will be sent
     * in one burst to the data sinks.
     */
	private List<AgentDataBuffer> fBurstBuffer;
	/**
	 * Speed value expressed as the pause
	 * between the data buffers, in milliseconds.
	 */
	private volatile int fSpeed;
	/** Listeners */
	private List<ReadListener> fReadListeners;
	/** Event handler */
	private EventHandler fEventHandler;
	/** Aggregation interval;if 0 no aggregation occurs */
	private int fAggPeriod;
    /** Aggregated data buffers */
	private Map<ResourceId, AggAgentDataBuffer> fAggBuffer;
	/** The time when the current aggregation period started */
	private long fAggregationStartTimestamp;
	/** True if it's time to stop aggregation and fire new data buffer event */
	private boolean fStopAggregation;
	/** Replay configuration */
	private DataLogCompareAndReplayConfiguration fReplayConfig;

	/**
	 * Event handler.
	 */
	private final class EventHandler implements DataLogReader.ReadCallback {
        /**
         * @see com.ixora.rms.logging.DataLogReader.ReadCallback#handleDataBuffer(com.ixora.rms.logging.DataLogReader, com.ixora.rms.agents.AgentDataBuffer)
         */
        public void handleDataBuffer(DataLogReader source, AgentDataBuffer db) {
            DataLogReplay.this.handleDataBuffer(getLogRepositoryFromLogReader(source), db);
        }
        /**
         * @see com.ixora.rms.logging.DataLogReader.ReadCallback#handleReadFatalError(com.ixora.rms.logging.DataLogReader, java.lang.Exception)
         */
        public void handleReadFatalError(DataLogReader source, Exception e) {
            DataLogReplay.this.fireFatalReadError(getLogRepositoryFromLogReader(source), e);
        }
		/**
		 * @see com.ixora.rms.logging.DataLogReader.ReadCallback#handleReadProgress(com.ixora.rms.logging.DataLogReader, long)
		 */
		public void handleReadProgress(DataLogReader source, long time) {
			DataLogReplay.this.fireReadProgress(getLogRepositoryFromLogReader(source), time);
		}
	}

	/**
	 * @param sink data sink to receive read data buffers.
	 */
	public DataLogReplay(DataSink sink) {
		super();
		if(sink == null) {
			throw new IllegalArgumentException("null data sink");
		}
		this.fEventHandler = new EventHandler();
		this.fSink = sink;
        this.fBurstBuffer = new LinkedList<AgentDataBuffer>();
        this.fAggBuffer = new HashMap<ResourceId, AggAgentDataBuffer>();
		this.fReadListeners = new LinkedList<ReadListener>();
		this.fSpeed = ConfigurationMgr.get(LogComponent.NAME).getInt(LogConfigurationConstants.LOG_REPLAY_SPEED);
		this.fAggPeriod = 1000 * ConfigurationMgr.get(LogComponent.NAME).getInt(LogConfigurationConstants.LOG_AGGREGATION_PERIOD);
		ConfigurationMgr.get(LogComponent.NAME).addObserver(this);
	}

	/**
	 * @param source
	 * @return
	 */
	private LogRepositoryInfo getLogRepositoryFromLogReader(DataLogReader source) {
		if(source == fReader1) {
			return fReplayConfig.getLogOne().getLogRepository();
		} else if(source == fReader2) {
			return fReplayConfig.getLogTwo().getLogRepository();
		}
		return null;
	}

	/**
	 * @see com.ixora.rms.services.DataLogReplayService#getScheme()
	 */
	public MonitoringSessionDescriptor getScheme() {
		return fScheme;
	}

	/**
	 * @see com.ixora.rms.services.DataLogReplayService#startReplay(com.ixora.rms.logging.DataLogCompareAndReplayConfiguration)
	 */
	public synchronized void startReplay(DataLogCompareAndReplayConfiguration config) throws DataLogException {
	    if(fPaused) {
	        // wake up the paused reader
            notify();
	    } else if(!fPlaying) {
	    	this.fReplayConfig = config;
			initializeReaders();
            if(this.fReader1 == null) {
                throw new NoLogWasLoaded();
            }

			fScheme = fReader1.readSessionDescriptor();
	        this.fPlaying = true;
	        this.fReplayConfig = config;
	        // start reading
        	// this will update fAggPeriod
        	ConfigurationMgr.setInt(LogComponent.NAME,
            		LogConfigurationConstants.LOG_AGGREGATION_PERIOD,
            		config.getAggregationStep());
        	DataLogCompareAndReplayConfiguration.LogRepositoryReplayConfig fc1 = fReplayConfig.getLogOne();
	        fReader1.read(fEventHandler, fc1.getTimeBegin(), fc1.getTimeEnd());
	        DataLogCompareAndReplayConfiguration.LogRepositoryReplayConfig fc2 = fReplayConfig.getLogTwo();
	        if(fc2 != null) {
		        fReader2.read(fEventHandler, fc2.getTimeBegin(), fc2.getTimeEnd());	        	
	        }	        
	    }
	    this.fPaused = false;
	}

	/**
	 * @see com.ixora.rms.services.DataLogReplayService#pauseReplay()
	 */
	public synchronized void pauseReplay() {
		this.fPaused = true;
	}

	/**
	 * @throws DataLogException
	 * @throws InvalidLogRepository
	 * @see com.ixora.rms.services.DataLogReplayService#stopReplay()
	 */
	public synchronized void stopReplay() throws InvalidLogRepository, DataLogException {
	    // this will stop the player thread
		this.fPlaying = false;
		this.fPaused = false;
		if(this.fReader1 != null) {
			closeReaders();
		    // prepare for next start()
		    initializeReaders();
		}
	}

	/**
	 * @throws DataLogException
	 */
	private void closeReaders() throws DataLogException {
		if(fReader1 != null) {
			fReader1.close();
		}
		if(fReader2 != null) {
			fReader2.close();
		}
	}

	/**
	 * @see com.ixora.common.Service#shutdown()
	 */
	public synchronized void shutdown() {
	    try {
	    	reset();
			if(this.fReader1 != null) {
			    this.fReader1.close();
			}
			if(this.fReader2 != null) {
			    this.fReader2.close();
			}
	    } catch(Exception e) {
            logger.error(e);
        }
	}

    /**
     * @see com.ixora.rms.services.DataLogReplayService#setReplaySpeed(int)
     */
    public void setReplaySpeed(int ms) {
        this.fSpeed = ms;
    }

    /**
     *
     */
    private void reset() {
        fBurstBuffer.clear();
        fAggBuffer.clear();
        fAggregationStartTimestamp = 0;
        fStopAggregation = false;
    }

	/**
     * Fires end of log event.
	 * @param logRepositoryInfo 
     */
    private void fireFinishedReading(LogRepositoryInfo logRepositoryInfo) {
        synchronized(fReadListeners) {
            for(ReadListener listener : fReadListeners) {
                try {
                    listener.finishedReadingLog(logRepositoryInfo);
                }catch(Exception e) {
                    logger.error(e);
                }
            }
        }
    }

    /**
	 * @param logRepositoryInfo 
     * @param time
	 */
	private void fireReadProgress(LogRepositoryInfo logRepositoryInfo, long time) {
        synchronized(fReadListeners) {
            for(ReadListener listener : fReadListeners) {
                try {
                    listener.readProgress(logRepositoryInfo, time);
                }catch(Exception ex) {
                    logger.error(ex);
                }
            }
        }
	}

	/**
     * Fires fatal error.
	 * @param logRepositoryInfo 
     * @param e
     */
    private void fireFatalReadError(LogRepositoryInfo logRepositoryInfo, Exception e) {
        synchronized(fReadListeners) {
            for(ReadListener listener : fReadListeners) {
                try {
                    listener.fatalReadError(logRepositoryInfo, e);
                }catch(Exception ex) {
                    logger.error(ex);
                }
            }
        }
    }

    /**
     * @see com.ixora.rms.services.DataLogReplayService#addReadListener(com.ixora.rms.services.DataLogReplayService.Listener)
     */
    public void addReadListener(ReadListener l) {
        synchronized(fReadListeners) {
            if(!fReadListeners.contains(l)) {
            	fReadListeners.add(l);
            }
        }
    }

    /**
     * @see com.ixora.rms.services.DataLogReplayService#removeReadListener(com.ixora.rms.services.DataLogReplayService.Listener)
     */
    public void removeReadListener(ReadListener l) {
        synchronized(fReadListeners) {
        	fReadListeners.remove(l);
        }
    }

    /**
     * Handles new data buffer event.
     * @param logRepositoryInfo 
     * @param buff is null when data ended
     */
    private synchronized void handleDataBuffer(LogRepositoryInfo logRepositoryInfo, AgentDataBuffer buff) {
        try {
            if(buff == null) {
            	reset();
                fireFinishedReading(logRepositoryInfo);
            } else {
                if(fPaused) {
                    synchronized(this) {
                        wait();
                    }
                }

    			if(!buff.isEmpty()) {
        			if(fAggPeriod == 0) {
        				// direct
        				handleDataBufferWithNoAggregation(buff);
        			} else {
	                	// aggregate
	                	handleDataBufferWithAggregation(buff);
	                }
    			}
            }
        } catch(Exception e) {
            logger.error(e);
        }
    }

    /**
	 * @param buff
	 */
	private synchronized void handleDataBufferWithAggregation(AgentDataBuffer buff) {
		ResourceId rid = new ResourceId(buff.getHost(), buff.getAgent(), null, null);
		if(fAggBuffer.size() == 0) {
			fAggBuffer.put(rid, new AggAgentDataBuffer(buff));
			fAggregationStartTimestamp = buff.getBuffers()[0].getTimestamp();
			return;
		}
		// see if we need to fire a data buffer event
        long timeStamp = buff.getBuffers()[0].getTimestamp();
        if(fStopAggregation
        		|| (fAggregationStartTimestamp > 0
        				&& timeStamp - fAggregationStartTimestamp >= fAggPeriod)) {
        	// fire buffer event
        	AgentDataBuffer[] data = new AgentDataBuffer[fAggBuffer.size()];
       	   	int i = 0;
        	for(AggAgentDataBuffer adb : fAggBuffer.values()) {
       	   		data[i] = adb.getAgentDataBuffer();
       	   		++i;
       	   	}
        	try {
        		fSink.receiveDataBuffers(data);
        	} catch(Throwable t) {
        		logger.error(t);
        	}
            fAggBuffer.clear();
            fAggregationStartTimestamp = 0;
            fStopAggregation = false;
        } else {
        	// add to buffer
        	AggAgentDataBuffer aggdb = fAggBuffer.get(rid);
        	if(aggdb == null) {
        		aggdb = new AggAgentDataBuffer();
        		fAggBuffer.put(rid, aggdb);
        	}
        	if(!aggdb.addAgentDataBuffer(buff)) {
        		// time to trigger a data buffer event
        		fStopAggregation = true;
        	}
        }
        // try to be nice to other threads if fSleep is zero
    	try {
			Thread.sleep(15);
		} catch (InterruptedException e) {
			;
		}
	}

	/**
     * @param buff
     * @throws InterruptedException
     */
    private synchronized void handleDataBufferWithNoAggregation(AgentDataBuffer buff) throws InterruptedException {
    	// see if we have a burst ready to send
        int burstSize = fBurstBuffer.size();
        if(burstSize > 0) {
            // see if the timestamp of the current sample is bigger
            // then the threshold and if so send a burst of data
            long burstTimestamp = fBurstBuffer.get(0).getBuffers()[0].getTimestamp();
            long timeStamp = buff.getBuffers()[0].getTimestamp();
            if(burstSize >= BURST_SIZE
                    || timeStamp - burstTimestamp > BURST_THRESHOLD_MILLISECONDS) {
            	// add the last one and fire
            	fBurstBuffer.add(buff);
                fSink.receiveDataBuffers(fBurstBuffer.toArray(
                        new AgentDataBuffer[fBurstBuffer.size()]));
                // clear burst buffer and sleep
                fBurstBuffer.clear();
                if(fSpeed > 200) {
                    Thread.sleep(fSpeed);
                } else {
                    // try to be nice to other threads if fSleep is zero
                	Thread.sleep(200);
                }
            } else {
                fBurstBuffer.add(buff);
            }
        } else {
            fBurstBuffer.add(buff);
        }
    }

    /**
	 * Initializes this object from the given repository info.
	 * @throws InvalidLogRepository
	 * @throws DataLogException
	 */
	private void initializeReaders() throws InvalidLogRepository, DataLogException {
        reset();
        fReader1 = createReader(fReplayConfig.getLogOne().getLogRepository());
        if(fReplayConfig.getLogTwo() != null) {
        	fReader2 = createReader(fReplayConfig.getLogTwo().getLogRepository());
        }
	}

	private DataLogReader createReader(LogRepositoryInfo repositoryInfo1) throws InvalidLogRepository, DataLogException {
		String type = repositoryInfo1.getRepositoryType();
		DataLogReader ret = null;
		if(LogRepositoryInfo.TYPE_XML.equals(type)) {
			ret = new DataLogRepositoryXML().getReader(repositoryInfo1);
		} else if(LogRepositoryInfo.TYPE_DATABASE.equals(type)) {
			ret = new DataLogRepositoryDB().getReader(repositoryInfo1);
		}
		if(ret == null) {
			InvalidLogRepository e = new InvalidLogRepository(
			        Msg.LOGGING_UNRECOGNIZED_LOG_TYPE,
			        new String[]{repositoryInfo1.getRepositoryType()});
			e.setIsInternalAppError();
			throw e;
		}
		return ret;
	}

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
        if(o instanceof ComponentConfiguration) {
            if(LogConfigurationConstants.LOG_REPLAY_SPEED.equals(arg)) {
                this.fSpeed = ConfigurationMgr.getInt(LogComponent.NAME,
                		LogConfigurationConstants.LOG_REPLAY_SPEED);
            } else if(LogConfigurationConstants.LOG_AGGREGATION_PERIOD.equals(arg)) {
                this.fAggPeriod = 1000 * ConfigurationMgr.getInt(LogComponent.NAME,
                		LogConfigurationConstants.LOG_AGGREGATION_PERIOD);
            }
        }
    }
}
