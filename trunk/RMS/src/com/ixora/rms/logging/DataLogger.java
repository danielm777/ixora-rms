/*
 * Created on 07-Jun-2004
 */
package com.ixora.rms.logging;

import java.util.HashSet;
import java.util.Set;

import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.rms.DataSinkTrimmed;
import com.ixora.rms.EntityDataBuffer;
import com.ixora.rms.EntityId;
import com.ixora.rms.HostId;
import com.ixora.rms.RecordDefinition;
import com.ixora.rms.RecordDefinitionCache;
import com.ixora.rms.ResourceId;
import com.ixora.rms.agents.AgentDataBuffer;
import com.ixora.rms.agents.AgentDescriptor;
import com.ixora.rms.agents.AgentId;
import com.ixora.rms.client.session.MonitoringSessionDescriptor;
import com.ixora.rms.exception.AgentDescriptorNotFound;
import com.ixora.rms.exception.RecordDefinitionNotFound;
import com.ixora.rms.logging.exception.DataLogException;
import com.ixora.rms.logging.exception.InvalidLogRepository;
import com.ixora.rms.services.DataLogService;

/**
 * Logs data.
 * @author Daniel Moraru
 */
public final class DataLogger implements DataLogService,
	DataSinkTrimmed, LogConfigurationConstants {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(DataLogger.class);
	/** Whether or not logging is in progress */
	private boolean fLogging;
	/** Data writer */
	private DataLogWriter fWriter;
	/** Record definition cache */
	private RecordDefinitionCache fRdCache;
	/** Set of all logged entities */
	private Set<ResourceId> fLoggedEntities;
    /** Set of all logged agents */
    private Set<ResourceId> fLoggedAgents;
    /** Listener */
    private Listener fListener;

	/**
	 * Constructor.
	 */
	public DataLogger() {
		super();
		fLoggedEntities = new HashSet<ResourceId>();
        fLoggedAgents = new HashSet<ResourceId>();
	}

	/**
	 * @see com.ixora.rms.DataSinkTrimmed#setRecordDefinitionCache(com.ixora.rms.RecordDefinitionCache)
	 */
	public void setRecordDefinitionCache(RecordDefinitionCache rdc) {
		this.fRdCache = rdc;
	}

	/**
	 * @see com.ixora.rms.DataSink#receiveDataBuffer(AgentDataBuffer)
	 */
	public synchronized void receiveDataBuffers(AgentDataBuffer[] buffs) {
		if(fLogging) {
            if(buffs == null) {
                logger.error("No agent data buffers");
                return;
            }
			// the buffer is critical if it contains
			// record definitions, if an error occurs during
			// the logging of such a buffer the logging will
			// be stopped
			boolean isCriticalBuffer = false;
			try {
				for(AgentDataBuffer buff : buffs) {
					try {
						// NOTE: make sure we are not ignoring empty buffers
						// as they might contain an agent descriptor
	                    if(!buff.isValid()) {
	                        if(logger.isWarnEnabled()) {
	                            logger.error("Invalid agent data buffer.");
	                        }
	                        continue;
	                    }
	                    if(buff.isEmpty()) {
	                        continue;
	                    }
						HostId host = buff.getHost();
						AgentId agent = buff.getAgent();
	                    // see if the buffer contains new agents and if so
	                    // make sure the agent descriptor gets logged as well
	                    ResourceId rid = new ResourceId(host, agent, null, null);
	                    if(!fLoggedAgents.contains(rid)) {
	                        // get the agent descriptor from the cache
	                        // and log it
	                        AgentDescriptor ad = fRdCache.getAgentDescriptor(host, agent);
	                        if(ad == null) {
	                        	isCriticalBuffer = true;
	                            throw new AgentDescriptorNotFound(agent);
	                        } else {
	                            buff.setAgentDescriptor(ad);
	                            fLoggedAgents.add(rid);
	                        }
	                    }
						// see if the buffer contains new entities
						// (that haven't been logged yet) and if
						// so make sure the record definition will
						// be writen to the log file as well...
						EntityDataBuffer[] ebs = buff.getBuffers();
	                    if(ebs != null) {
							for(EntityDataBuffer eb : ebs) {
								try {
									if(eb.getDefinition() == null) {
										// see if not logged before
										EntityId eid = eb.getEntityId();
										rid = new ResourceId(host, agent, eid, null);
										if(!fLoggedEntities.contains(rid)) {
											// get the record definition from the cache
											// and log it
											RecordDefinition rd = fRdCache.getRecordDefinition(
													host, agent, eid);
											if(rd == null) {
												isCriticalBuffer = true;
												throw new RecordDefinitionNotFound(eb.getEntityId());
											} else {
												eb.setDefinition(rd);
												fLoggedEntities.add(rid);
											}
										}
									}
								} catch(Throwable t) {
									handleError(t, isCriticalBuffer);
								}
							}
							if(fWriter != null) {
								try {
									// if anything happens here bail out
									fWriter.writeBuffer(buff);
								} catch(Throwable e) {
									isCriticalBuffer = true;
									throw e;
								}
							}
						}
					} catch(Throwable t) {
						handleError(t, isCriticalBuffer);
					}
				}
			} catch(Exception e) {
				handleError(e, isCriticalBuffer);
			}
		}
	}

    /**
	 * @param t
	 */
	private void handleError(Throwable t, boolean fatal) {
		if(fatal) {
			this.fListener.error(t);
			reset();
		} else {
			logger.error(t);
		}
	}

	/**
     * @see com.ixora.rms.services.DataLogService#startLogging(com.ixora.rms.logging.LogRepositoryInfo, com.ixora.rms.client.session.MonitoringSessionDescriptor, Listener)
     */
    public synchronized void startLogging(LogRepositoryInfo rep,
    		MonitoringSessionDescriptor scheme,
    		Listener listener) throws InvalidLogRepository, DataLogException {
		if(rep == null || listener == null) {
			throw new IllegalArgumentException("null params");
		}
		this.fListener = listener;
		fWriter = DataLogUtils.createWriter(rep);
		fWriter.writeSessionDescriptor(scheme);
		fLogging = true;
    }

    /**
     * @see com.ixora.rms.services.DataLogService#stopLogging()
     */
    public synchronized void stopLogging() throws DataLogException {
		fLogging = false;
		if(fWriter != null) {
			fWriter.close();
			fWriter = null;
		}
		fLoggedEntities.clear();
		fLoggedAgents.clear();
    }

	/**
	 * @see com.ixora.common.Service#shutdown()
	 */
	public synchronized void shutdown() {
		reset();
	}

	/**
	 *
	 */
	private void reset() {
	    try {
		    fLogging = false;
		    if(fWriter != null) {
		        fWriter.close();
		        fWriter = null;
		    }
		    fListener = null;
			fLoggedEntities.clear();
			fLoggedAgents.clear();
	    } catch(DataLogException e) {
            logger.error(e);
        }
	}
}
