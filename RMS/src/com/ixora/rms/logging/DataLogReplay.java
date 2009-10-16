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

import com.ixora.rms.DataSink;
import com.ixora.rms.HostId;
import com.ixora.rms.ResourceId;
import com.ixora.common.ConfigurationMgr;
import com.ixora.common.ComponentConfiguration;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.rms.EntityDescriptor;
import com.ixora.rms.agents.AgentDataBuffer;
import com.ixora.rms.agents.AgentDescriptor;
import com.ixora.rms.agents.AgentId;
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

	/** Repository info */
	private LogRepositoryInfo fRepositoryInfo;
    /** Data sink to receive read data buffers */
	private DataSink fSink;
	/** Monitoring scheme */
	private MonitoringSessionDescriptor fScheme;
	/** Data log reader */
	private DataLogReader fReader;
	/** Playing flag */
	private boolean fPlaying;
	/** Paused flag */
	private boolean fPaused;
	/** Scanning flag */
	private boolean fScanning;
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
	/** Listeners */
	private List<ScanListener> fScanListeners;
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
	private long fTimestampBegin;
	private long fTimestampEnd;

	/**
	 * Event handler.
	 */
	private final class EventHandler implements DataLogReader.ReadCallback, DataLogReader.ScanCallback {
        /**
         * @see com.ixora.rms.logging.DataLogReader.Callback#handleDataBuffer(AgentDataBuffer)
         */
        public void handleDataBuffer(AgentDataBuffer db) {
            DataLogReplay.this.handleDataBuffer(db);
        }
        /**
         * @see com.ixora.rms.logging.DataLogReader.Callback#handleReadFatalError(java.lang.Exception)
         */
        public void handleReadFatalError(Exception e) {
            DataLogReplay.this.fireFatalReadError(e);
        }
		/**
		 * @see com.ixora.rms.logging.DataLogReader.ScanCallback#handleEntity(com.ixora.rms.internal.HostId, com.ixora.rms.internal.agents.AgentId, com.ixora.rms.EntityDescriptor)
		 */
		public void handleEntity(HostId host, AgentId aid, EntityDescriptor ed) {
			DataLogReplay.this.fireNewEntity(host, aid, ed);
		}
        /**
         * @see com.ixora.rms.logging.DataLogReader.ScanCallback#handleAgent(com.ixora.rms.HostId, com.ixora.rms.agents.AgentDescriptor)
         */
        public void handleAgent(HostId host, AgentDescriptor ad) {
            DataLogReplay.this.fireNewAgent(host, ad);
        }
		/**
		 * @see com.ixora.rms.logging.DataLogReader.ScanCallback#finishedScanning(long, long)
		 */
		public void finishedScanning(long beginTimestamp, long endTimestamp) {
			DataLogReplay.this.handleFinishedScanning(beginTimestamp, endTimestamp);
		}
		/**
		 * @see com.ixora.rms.logging.DataLogReader.ScanCallback#handleScanFatalError(java.lang.Exception)
		 */
		public void handleScanFatalError(Exception e) {
			DataLogReplay.this.fireFatalScanError(e);
		}
		/**
		 * @see com.ixora.rms.logging.DataLogReader.ReadCallback#handleReadProgress(long)
		 */
		public void handleReadProgress(long time) {
			DataLogReplay.this.fireReadProgress(time);
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
		this.fScanListeners = new LinkedList<ScanListener>();
		this.fSpeed = ConfigurationMgr.get(LogComponent.NAME).getInt(LogConfigurationConstants.LOG_REPLAY_SPEED);
		this.fAggPeriod = 1000 * ConfigurationMgr.get(LogComponent.NAME).getInt(LogConfigurationConstants.LOG_AGGREGATION_PERIOD);
		ConfigurationMgr.get(LogComponent.NAME).addObserver(this);
	}

	/**
	 * @see com.ixora.rms.services.DataLogReplayService#loadLog(com.ixora.rms.control.struct.LogRepositoryInfo)
	 */
	public synchronized void loadLog(LogRepositoryInfo rep) throws InvalidLogRepository, DataLogException {
		if(rep == null) {
			throw new IllegalArgumentException("null repository details");
		}
		this.fRepositoryInfo = rep;
		initialize();
		fScheme = fReader.readSessionDescriptor();
	}

	/**
	 * @see com.ixora.rms.services.DataLogReplayService#getScheme()
	 */
	public MonitoringSessionDescriptor getScheme() {
		return fScheme;
	}

	/**
	 * @see com.ixora.rms.services.DataLogReplayService#startReplay(com.ixora.rms.logging.DataLogReplayConfiguration)
	 */
	public synchronized void startReplay(DataLogReplayConfiguration config) throws DataLogException {
	    if(fPaused) {
	        // wake up the paused reader
	        synchronized(this) {
	            notify();
	        }
	    } else if(!fPlaying) {
            if(this.fReader == null) {
                throw new NoLogWasLoaded();
            }
	        this.fPlaying = true;
	        // start reading
	        if(config != null) {
	        	fTimestampBegin = config.getTimeBegin();
	        	fTimestampEnd = config.getTimeEnd();
	        	// this will update fAggPeriod
	        	ConfigurationMgr.setInt(LogComponent.NAME,
                		LogConfigurationConstants.LOG_AGGREGATION_PERIOD,
                		config.getAggregationStep());
	        }
	        fReader.read(fEventHandler, fTimestampBegin, fTimestampEnd);
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
		if(this.fReader != null) {
		    this.fReader.close();
		    // prepare for next start()
		    initialize();
		}
	}

	/**
	 * @see com.ixora.rms.services.DataLogReplayService#startScanning()
	 */
	public synchronized void startScanning() throws DataLogException {
		this.fScanning = true;
		fReader.scan(fEventHandler);
	}

	/**
	 * @see com.ixora.rms.services.DataLogReplayService#stopScanning()
	 */
	public synchronized void stopScanning() throws DataLogException {
		if(!fScanning) {
			return;
		}
		try {
            if(this.fReader == null) {
                throw new NoLogWasLoaded();
            }
			fReader.close();
		} finally {
			fScanning = false;
		}
	}

	/**
	 * @see com.ixora.common.Service#shutdown()
	 */
	public void shutdown() {
	    try {
	    	reset();
			if(this.fReader != null) {
			    this.fReader.close();
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
     */
    private void fireFinishedReading() {
        synchronized(fReadListeners) {
            for(ReadListener listener : fReadListeners) {
                try {
                    listener.finishedReadingLog();
                }catch(Exception e) {
                    logger.error(e);
                }
            }
        }
    }

	/**
     * Fires end of log event.
     * @param beginTimestamp
     * @param endTimestamp
     */
    private void fireFinishedScanning(long beginTimestamp, long endTimestamp) {
        synchronized(fScanListeners) {
            for(ScanListener listener : fScanListeners) {
                try {
                    listener.finishedScanningLog(beginTimestamp, endTimestamp);
                }catch(Exception e) {
                    logger.error(e);
                }
            }
        }
    }

    /**
	 * @param time
	 */
	private void fireReadProgress(long time) {
        synchronized(fReadListeners) {
            for(ReadListener listener : fReadListeners) {
                try {
                    listener.readProgress(time);
                }catch(Exception ex) {
                    logger.error(ex);
                }
            }
        }
	}

	/**
     * Fires fatal error.
     * @param e
     */
    private void fireFatalReadError(Exception e) {
        synchronized(fReadListeners) {
            for(ReadListener listener : fReadListeners) {
                try {
                    listener.fatalReadError(e);
                }catch(Exception ex) {
                    logger.error(ex);
                }
            }
        }
    }

	/**
     * Fires fatal error.
     * @param e
     */
    private void fireFatalScanError(Exception e) {
        synchronized(fScanListeners) {
            for(ScanListener listener : fScanListeners) {
                try {
                    listener.fatalScanError(e);
                }catch(Exception ex) {
                    logger.error(ex);
                }
            }
        }
    }

	/**
     * Fires new entity.
     * @param e
     */
    private void fireNewEntity(HostId hid, AgentId aid, EntityDescriptor entity) {
        synchronized(fScanListeners) {
            for(ScanListener listener : fScanListeners) {
                try {
                  listener.newEntity(hid, aid, entity);
                }catch(Exception ex) {
                    logger.error(ex);
                }
            }
        }
    }

    /**
     * @param host
     * @param ad
     */
    private void fireNewAgent(HostId host, AgentDescriptor ad) {
        synchronized(fScanListeners) {
            for(ScanListener listener : fScanListeners) {
                try {
                  listener.newAgent(host, ad);
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
	 * @see com.ixora.rms.services.DataLogReplayService#addScanListener(com.ixora.rms.services.DataLogReplayService.ScanListener)
	 */
	public void addScanListener(ScanListener l) {
        synchronized(fScanListeners) {
            if(!fScanListeners.contains(l)) {
            	fScanListeners.add(l);
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
	 * @see com.ixora.rms.services.DataLogReplayService#removeScanListener(com.ixora.rms.services.DataLogReplayService.ScanListener)
	 */
	public void removeScanListener(ScanListener l) {
        synchronized(fScanListeners) {
        	fScanListeners.remove(l);
        }
	}

    /**
     * Handles new data buffer event.
     * @param buff is null when data ended
     */
    private void handleDataBuffer(AgentDataBuffer buff) {
        try {
            if(buff == null) {
            	reset();
                fireFinishedReading();
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
	private void handleDataBufferWithAggregation(AgentDataBuffer buff) {
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
    private void handleDataBufferWithNoAggregation(AgentDataBuffer buff) throws InterruptedException {
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
	private void initialize() throws InvalidLogRepository, DataLogException {
        reset();
		String type = fRepositoryInfo.getRepositoryType();
		if(LogRepositoryInfo.TYPE_XML.equals(type)) {
			fReader = new DataLogRepositoryXML().getReader(fRepositoryInfo);
		} else if(LogRepositoryInfo.TYPE_DATABASE.equals(type)) {
			fReader = new DataLogRepositoryDB().getReader(fRepositoryInfo);
		}
		if(fReader == null) {
			InvalidLogRepository e = new InvalidLogRepository(
			        Msg.LOGGING_UNRECOGNIZED_LOG_TYPE,
			        new String[]{fRepositoryInfo.getRepositoryType()});
			e.setIsInternalAppError();
			throw e;
		}
	}

	/**
	 *
	 */
	private void handleFinishedScanning(long beginTimestamp, long endTimestamp) {
		fScanning = false;
		fTimestampBegin = beginTimestamp;
		fTimestampEnd = endTimestamp;
		fireFinishedScanning(beginTimestamp, endTimestamp);
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
