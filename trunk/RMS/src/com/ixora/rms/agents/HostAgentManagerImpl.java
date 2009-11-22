package com.ixora.rms.agents;

import java.awt.image.DataBuffer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ixora.rms.Collector;
import com.ixora.common.exception.AppException;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.plugin.ClassLoadingHelper;
import com.ixora.rms.EntityConfiguration;
import com.ixora.rms.EntityDescriptorTree;
import com.ixora.rms.EntityId;
import com.ixora.rms.exception.AgentIsNotInstalled;
import com.ixora.rms.exception.AgentNotActivated;
import com.ixora.rms.exception.InvalidAgentState;
import com.ixora.rms.exception.InvalidConfiguration;
import com.ixora.rms.exception.InvalidEntity;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.repository.AgentInstallationData;
import com.ixora.rms.repository.VersionableAgentInstallationData;
import com.ixora.rms.services.AgentRepositoryService;

/**
 * HostAgentManager manages a set of agents that monitor the same host.
 * It's used both on the remote
 * hosts to manage remote monitoring agents and on the control host to
 * manage agents based on monitoring protocols(SNMP, JMX...)
 * @author Daniel Moraru
 */
// NOTE: this class ensures a thread safe environment for all
// managed agents
public final class HostAgentManagerImpl implements HostAgentManager {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(HostAgentManagerImpl.class);

	/**
	 * Agent repository.
	 */
	private AgentRepositoryService fAgentRepository;

	/**
	 * Event handler.
	 */
	private final class EventHandler implements Agent.Listener {
		/**
		 * @see com.ixora.rms.agents.Agent.Listener#newMonitoringData(DataBuffer)
		 */
		public void receiveDataBuffer(AgentDataBuffer data) {
			handleReceiveDataBuffer(data);
		}

		/**
		 * @see com.ixora.rms.agents.Agent.Listener#monitoredEntitiesChanged(String host, AgentId, EntityDescriptorTree)
		 */
		public void entitiesChanged(
			String host,
			AgentId agentId,
			EntityDescriptorTree entities) {
			handleEntitiesChanged(host, agentId, entities);
		}

		/**
		 * @see com.ixora.rms.agents.Agent.Listener#nonFatalError(AgentId, java.lang.Throwable)
		 */
		public void nonFatalError(String host, AgentId agentClass, Throwable t) {
			handleNonFatalError(host, agentClass, t);
		}
	}

	/**
	 * Agent data.
	 */
	private static final class AgentData {
		/** Seconds left untile next data collection cycle */
		int tick;
		/** Managed agent */
		Agent agent;
		/** Agent state */
		AgentState state;
		/**
		 * Whether or not this agent is using the global
		 * settings (like sampling interval and monitoring level).
		 */
		boolean usingGlobalSettings;
		AgentId agentId;

		AgentData(int tick,
				boolean globalSettings,
				Agent agent, AgentState state, AgentId agentId) {
			this.tick = tick;
			this.usingGlobalSettings = globalSettings;
			this.agent = agent;
			this.state = state;
			this.agentId = agentId;
		}
	}

	/**
	 * Host name as known by the registered listener.
	 */
	private String fHost;
	/**
	 * Listener.
	 */
	private Listener fListener;
	/**
	 * Agent data keyed by agent id.
	 */
	private Map<AgentId, AgentData> fAgents;
	/**
	 * Event handler.
	 */
	private EventHandler fEventHandler;
	/**
	 * List of data buffers to send at the end of the
	 * collection cycle.
	 */
	private List<AgentDataBuffer> fDataBuffers;
	/** Collector thread */
	private Collector fCollector;
	/**
	 * Set of activated agents that have their own collectors.
	 * Required to decide whether to send an agent buffer directly or
	 * add it to the list and send it at the end of the collection cycle.
	 */
	private Set<AgentId> fAgentsWithPrivateCollectors;
	/**
	 * Class loading helpler.
	 */
	private ClassLoadingHelper fClassLoadingHelper;

	/**
	 * Constructor for HostAgentManager.
	 * @param h the host name
	 * @param l listener
	 */
	public HostAgentManagerImpl(AgentRepositoryService agentRepository, String h, Listener l) {
		super();
		if(agentRepository == null) {
			throw new IllegalArgumentException("null agent repository");
		}
		if(h == null) {
			throw new IllegalArgumentException("null host name");
		}
		if(l == null) {
			throw new IllegalArgumentException("null agent manager listener");
		}
		this.fAgentRepository = agentRepository;
		this.fHost = h;
		this.fListener = l;
		this.fAgents = new HashMap<AgentId, AgentData>();
		this.fDataBuffers = new LinkedList<AgentDataBuffer>();
		this.fEventHandler = new EventHandler();
		this.fAgentsWithPrivateCollectors = Collections.synchronizedSet(new HashSet<AgentId>());
		this.fClassLoadingHelper = new ClassLoadingHelper();
		this.fCollector = new Collector("AgentDataCollector") {
			protected void collect() {
				HostAgentManagerImpl.this.collectCycle();
			}
		};
	}

	/**
	 * Runs data collection.
	 */
	private void collectCycle() {
		cleanDataBufferList();
		synchronized(this.fAgents) {
			boolean exit = true;
			for(Map.Entry<AgentId, AgentData> entry : this.fAgents.entrySet()) {
				AgentData ad = entry.getValue();
				AgentId agentId = entry.getKey();
				if(ad.agent.requiresExternalCollector() && ad.state == AgentState.STARTED) {
					exit = false;
					ad.tick -= fCollector.getCollectionInterval()/1000;
					if(ad.tick == 0) {
						// reset tick
						ad.tick = ad.agent.getConfiguration().getSamplingInterval().intValue();
						try {
							fClassLoadingHelper.prepareThreadOnEnter(agentId.toString());
							ad.agent.collectData();
						} catch(Throwable e) {
							ad.state = AgentState.ERROR;
							fireAgentErrorState(agentId, e);
						} finally {
							fClassLoadingHelper.prepareThreadOnExit();
						}
					}
				}
			}
			if(exit) {
				// stop the collector
				// if no started agents
				fCollector.stopCollector();
			}
		}
		sendDataBufferList();
	}

	/**
	 * Activates the given agent.
	 * @param agentInstallationId
	 * @param agentClass
	 * @param conf
	 * @return the available entities
	 * @throws AgentIsNotInstalled
	 * @throws InvalidAgentState
	 * @throws InvalidConfiguration
	 * @throws RMSException
	 */
	public AgentActivationTuple activateAgent(
			String agentInstallationId,
			AgentConfiguration conf)
		throws AgentIsNotInstalled, InvalidAgentState, InvalidConfiguration, RMSException {
		if(agentInstallationId == null || conf == null) {
			throw new IllegalArgumentException("null params");
		}

		AgentInstallationData agentInstallationData = this.fAgentRepository.getInstalledAgents()
				.get(agentInstallationId);
		if(agentInstallationData == null) {
			throw new AgentIsNotInstalled(agentInstallationId);
		}

		synchronized(this.fAgents) {
			int agentIdx = 0;
			for(AgentId aid : this.fAgents.keySet()) {
				if(aid.getInstallationId().equals(agentInstallationId)) {
					agentIdx++;
				}
			}
			AgentId agentId;
			if(agentIdx == 0) {
				agentId = new AgentId(agentInstallationId);
			} else {
				agentId = new AgentId(agentInstallationId, agentIdx);
			}
			boolean error = false;
			Agent agent = null;
			try {
			    // prepare the loading of the agent class, if jars are specified or
			    // the custom classpath is specified, the agent will require it's own
			    // classloader
                String suoVersion = conf.getSystemUnderObservationVersion();
                VersionableAgentInstallationData vad = agentInstallationData.getVersionData(suoVersion);
                Class<?> clazz;
                try {
                    clazz = fClassLoadingHelper.getClass(
                    		agentId.toString(),
			    		vad.getAgentImplClass(),
                        vad.getJars(),
			    		conf.getCustom());
                } catch(NoClassDefFoundError e) {
                    logger.error(e);
                    throw new AgentIsNotInstalled(agentId.getInstallationId(), e);
                } catch(ClassNotFoundException e) {
                    logger.error(e);
                    throw new AgentIsNotInstalled(agentId.getInstallationId(), e);
                }
                Constructor<?> cons = clazz.getConstructor(new Class[] {AgentId.class, Agent.Listener.class});
			    agent = (Agent)cons.newInstance(new Object[] {agentId, this.fEventHandler});
				EntityDescriptorTree entities = agent.configure(conf);
				// everything ok, register the agent
				int si = conf.getSamplingInterval().intValue();
				AgentData ad =
					new AgentData(
							si,
							conf.isGlobalSamplingInterval(),
							agent,
							AgentState.READY,
							agentId);
				this.fAgents.put(agentId, ad);
				if(ad.agent.requiresExternalCollector()) {
					fCollector.recalibrateCollector(1000 * si);
				} else {
					fAgentsWithPrivateCollectors.add(agentId);
				}
				fireAgentStateChanged(agentId, AgentState.READY);
				return new AgentActivationTuple(agent.getDescriptor(), entities);
            } catch(InvocationTargetException e) {
                // the agent constructor might have thrown this error
                error = true;
                Throwable cause = e.getCause();
                if(cause == null || !(cause instanceof RMSException)) {
                    RMSException ex = new RMSException(e);
                    ex.setIsInternalAppError();
                    throw ex;
                }
                throw (RMSException)cause;
			} catch(InvalidConfiguration e) {
				error = true;
				throw e;
			} catch(InvalidEntity e) {
				// not possible
				error = true;
				new RMSException(e);
			} catch(RMSException e) {
				// this should be an agent specific logic exception
				error = true;
				throw e;
			} catch(AppException e) {
				// all infrastructure exceptions should have localized messages
				error = true;
				throw new RMSException(e.getLocalizedMessage());
			} catch(Throwable e) {
				// this should mark an internal/unexpected agent error
				error = true;
				RMSException ex = new RMSException(e);
				ex.setIsInternalAppError();
				throw ex;
			} finally {
				// this will undo any changes to the thread's context
				fClassLoadingHelper.prepareThreadOnExit();
				if(error) {
					// rollback any changes
					if(agent != null) {
						try {
							fAgents.remove(agentId);
							agent.deactivate();
						} catch (Throwable t) {
							logger.error(t);
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * Deactivates the given agent.
	 * @param agentId
	 * @throws InvalidAgentState
	 * @throws RMSException
	 */
	public void deactivateAgent(AgentId agentId) throws InvalidAgentState, RMSException {
		synchronized(this.fAgents) {
			AgentData ad = this.fAgents.get(agentId);
			if(ad == null) {
				throw new AgentNotActivated();
			}
			deactivateAgent(agentId, ad, true);
		}
	}

	/**
	 * Fires the agent error event.
	 * @param agentId
	 * @param e
	 */
	private void fireAgentErrorState(AgentId agentId, Throwable e) {
		try {
			this.fListener.agentStateChanged(this.fHost, agentId,
					AgentState.ERROR, e);
		} catch(Throwable t) {
			logger.error(t);
		}
	}

	/**
	 * Fires the agent state change event.
	 * @param agentId
	 * @param state
	 */
	private void fireAgentStateChanged(AgentId agentId, AgentState state) {
		try {
			this.fListener.agentStateChanged(this.fHost, agentId,	state, null);
		} catch(Throwable t) {
			logger.error(t);
		}
	}

	/**
	 * Deactivates all agents.
	 */
	public void deactivateAllAgents() {
		synchronized(this.fAgents) {
			for(Iterator<Map.Entry<AgentId, AgentData>> iter = this.fAgents.entrySet().iterator();
					iter.hasNext();) {
				Map.Entry<AgentId, AgentData> entry = iter.next();
				AgentData ad = entry.getValue();
				AgentId agentId = entry.getKey();
				try {
					deactivateAgent(agentId, ad, false);
				} catch(RMSException e) {
					logger.error(e);
				} finally {
					iter.remove();
				}
			}
		}
	}

	/**
	 * Starts all agents.
	 * @throws StartableError
	 */
	public void startAllAgents() {
		synchronized(this.fAgents) {
			AgentData ad;
			AgentId agentId;
			boolean startCollector = false;
			for(Map.Entry<AgentId, AgentData> entry : this.fAgents.entrySet()) {
				ad = entry.getValue();
				agentId = entry.getKey();
				try {
					fClassLoadingHelper.prepareThreadOnEnter(agentId.toString());
					ad.agent.start();
					if(ad.agent.requiresExternalCollector()) {
						startCollector = true;
					}
					ad.state = AgentState.STARTED;
					fireAgentStateChanged(agentId, AgentState.STARTED);
				} catch(Throwable e) {
					ad.state = AgentState.ERROR;
					fireAgentErrorState(agentId, e);
				} finally {
					fClassLoadingHelper.prepareThreadOnExit();
				}
			}
			if(startCollector) {
				fCollector.startCollector();
			}
		}
	}

	/**
	 * Stops all agents.
	 */
	public void stopAllAgents() {
		synchronized(this.fAgents) {
			AgentData ad;
			AgentId agentId;
			for(Map.Entry<AgentId, AgentData> entry : this.fAgents.entrySet()) {
				ad = entry.getValue();
				agentId = entry.getKey();
				try {
					fClassLoadingHelper.prepareThreadOnEnter(agentId.toString());
					ad.agent.stop();
					fireAgentStateChanged(agentId, AgentState.STOPPED);
				} catch(Throwable e) {
					fireAgentErrorState(agentId, e);
				} finally {
					fClassLoadingHelper.prepareThreadOnExit();
				}
			}
			fCollector.stopCollector();
		}
	}

	/**
	 * Configures all agents.
	 * @throws InvalidConfiguration
	 * @throws RMSException
	 */
	public AgentConfigurationTuple[] configureAllAgents(AgentConfiguration conf)
		throws InvalidConfiguration, RMSException {
		try {
			synchronized(this.fAgents) {
				int si = conf.getSamplingInterval().intValue();
				boolean recalibrate = false;
				AgentConfigurationTuple[] ret = new AgentConfigurationTuple[fAgents.size()];
				int i = 0;
				for(AgentData ad : this.fAgents.values()) {
					fClassLoadingHelper.prepareThreadOnEnter(ad.agentId.toString());
					try {
						if(ad.usingGlobalSettings) {
							EntityDescriptorTree entities = ad.agent.configure(conf);
							ret[i] = new AgentConfigurationTuple(ad.agent.getDescriptor(), entities);
							// conf can be a delta value so check
							// if the sampling interval is valid
							if(ad.agent.requiresExternalCollector() && conf.hasValidSamplingInterval()) {
								ad.tick = si;
								recalibrate = true;
							}
						}
					} finally {
						fClassLoadingHelper.prepareThreadOnExit();
					}
					++i;
				}
				if(recalibrate) {
					fCollector.recalibrateCollector(1000 * si);
				}
				return ret;
			}
		} catch(InvalidConfiguration e) {
			throw e;
		} catch(RMSException e) {
			// this should be an agent specific logic exception
			throw e;
		} catch(Throwable e) {
			// this should mark an internal/unexpected agent error
			RMSException ex = new RMSException(e);
			ex.setIsInternalAppError();
			throw ex;
		}
	}

	/**
	 * Starts the given agent.
	 * @param agentId
	 */
	public void startAgent(AgentId agentId)
				throws InvalidAgentState, RMSException {
		synchronized(this.fAgents) {
			AgentData ad = this.fAgents.get(agentId);
			if(ad == null) {
				// agent has not been activated
				throw new AgentNotActivated();
			}
			try {
				fClassLoadingHelper.prepareThreadOnEnter(agentId.toString());
				ad.agent.start();
				ad.state = AgentState.STARTED;
				fireAgentStateChanged(agentId, AgentState.STARTED);
				if(ad.agent.requiresExternalCollector()) {
					fCollector.startCollector();
				}
			} catch(RMSException e) {
				throw e;
			} catch(Throwable e) {
				throw new RMSException(e);
			} finally {
				fClassLoadingHelper.prepareThreadOnExit();
			}
		}
	}

	/**
	 * Stops the given agent.
	 * @param agentId
	 * @throws StartableError
	 */
	public void stopAgent(AgentId agentId) throws InvalidAgentState, RMSException {
		synchronized(this.fAgents) {
			AgentData ad = this.fAgents.get(agentId);
			if(ad == null) {
				// agent has not been activated
				throw new AgentNotActivated();
			}
			ad.state = AgentState.STOPPED;
			try {
				fClassLoadingHelper.prepareThreadOnEnter(agentId.toString());
				ad.agent.stop();
				fireAgentStateChanged(agentId, AgentState.STOPPED);
			} catch(RMSException e) {
				throw e;
			} catch(Throwable t) {
				throw new RMSException(t);
			} finally {
				fClassLoadingHelper.prepareThreadOnExit();
			}
		}
	}

	/**
	 * Configures the given agent.
	 * @param agent
	 * @param conf
	 * @throws InvalidAgentState
	 * @throws InvalidConfiguration
	 * @throws RMSException
	 */
	public AgentConfigurationTuple configureAgent(
		AgentId agent,
		AgentConfiguration conf)
		throws InvalidConfiguration, InvalidAgentState, RMSException {
		synchronized(this.fAgents) {
			AgentData ad = this.fAgents.get(agent);
			if(ad == null) {
				// agent has not been activated
				throw new AgentNotActivated();
			}
			try {
				fClassLoadingHelper.prepareThreadOnEnter(agent.toString());
				EntityDescriptorTree ret = ad.agent.configure(conf);
				// cache this piece of information
				// as it is used to select agents
				// that are using global configuration
				// settings during a global configuration
				// change broadcast
				// @see configureAllAgents()
				ad.usingGlobalSettings = ad.agent.getConfiguration().isGlobalSamplingInterval();
				// conf can be a delta value so check
				// if the sampling interval is valid
				if(ad.agent.requiresExternalCollector() && conf.hasValidSamplingInterval()) {
					int si = conf.getSamplingInterval().intValue();
					ad.tick = si;
					fCollector.recalibrateCollector(1000 * si);
				}
				return new AgentConfigurationTuple(ad.agent.getDescriptor(), ret);
			} catch(InvalidConfiguration e) {
				throw e;
			} catch(RMSException e) {
				// this should be an agent specific logic exception
				throw e;
			} catch(Throwable e) {
				// this should mark an internal/unexpected agent error
				RMSException ex = new RMSException(e);
				ex.setIsInternalAppError();
				throw ex;
			} finally {
				fClassLoadingHelper.prepareThreadOnExit();
			}
		}
	}

	/**
	 * Configures the given entity.
	 * @param agent
	 * @param conf
	 * @throws InvalidAgentState
	 * @throws InvalidConfiguration
	 * @throws InvalidEntity
	 */
	public EntityDescriptorTree configureEntity(
		AgentId agent,
		EntityId entity,
		EntityConfiguration conf)
		throws
			InvalidConfiguration,
			InvalidAgentState,
			InvalidEntity,
			RMSException {
		synchronized(this.fAgents) {
			AgentData ad = this.fAgents.get(agent);
			if(ad == null) {
				// agent has not been activated
				throw new AgentNotActivated();
			}
			try {
				fClassLoadingHelper.prepareThreadOnEnter(agent.toString());
				EntityDescriptorTree ret = ad.agent.configureEntity(entity, conf);
				// conf can be a delta value so check
				// if the sampling interval is valid
				if(ad.agent.requiresExternalCollector() && conf.hasValidSamplingInterval()) {
					fCollector.recalibrateCollector(1000 * conf.getSamplingInterval().intValue());
				}
				return ret;
			} catch(InvalidEntity e) {
				throw e;
			} catch(InvalidConfiguration e) {
				throw e;
			} catch(RMSException e) {
				// this should be an agent specific logic exception
				throw e;
			} catch(Throwable e) {
				// this should mark an internal/unexpected agent error
				RMSException ex = new RMSException(e);
				ex.setIsInternalAppError();
				throw ex;
			} finally {
				fClassLoadingHelper.prepareThreadOnExit();
			}
		}
	}

	/**
	 * Returns the children entities of the given entity on the
	 * given agent.
	 * @param agent
	 * @param parent
	 * @param recursive
	 * @param refresh
	 * @return the available entities for the given agent.
	 * @throws InvalidAgentState
	 * @throws RMSException
	 */
	public EntityDescriptorTree getEntities(
		AgentId agent,
		EntityId parent,
		boolean recursive,
		boolean refresh) throws InvalidAgentState, RMSException {
		synchronized(this.fAgents) {
			fClassLoadingHelper.prepareThreadOnEnter(agent.toString());
			try {
				AgentData ad = this.fAgents.get(agent);
				if(ad == null) {
					// agent has not been activated
					throw new AgentNotActivated();
				}
				try {
					return ad.agent.getEntities(parent, recursive, refresh);
				} catch(InvalidEntity e) {
					throw e;
				} catch(RMSException e) {
					// this should be an agent specific logic exception
					throw e;
				} catch(Throwable e) {
					// this should mark an internal/unexpected agent error
					RMSException ex = new RMSException(e);
					ex.setIsInternalAppError();
					throw ex;
				}
			} finally {
				fClassLoadingHelper.prepareThreadOnExit();
			}
		}
	}

	/**
	 * @return the state of the given agent, null if the
	 * agent is not found
	 * @param agentId
	 */
	public AgentState getAgentState(AgentId agentId) {
		synchronized(this.fAgents) {
			AgentData ad = this.fAgents.get(agentId);
			return ad == null ? null : ad.state;
		}
	}

	/**
	 * Handles new monitored data buffer.
	 * @param data
	 */
	private void handleReceiveDataBuffer(AgentDataBuffer data) {
		if(this.fAgentsWithPrivateCollectors.contains(data.getAgent())) {
			// independent collector, send data now
			this.fListener.receiveDataBuffers(new AgentDataBuffer[] {data});
		} else {
			// global collector buffer, it will be sent at the end
			// of the collection cycle
			synchronized(this.fDataBuffers) {
				this.fDataBuffers.add(data);
			}
		}
	}

	/**
	 * Handles monitored entities changed event.
	 * @param host
	 * @param agent
	 * @param parent
	 * @param entities
	 */
	private void handleEntitiesChanged(
		String host, AgentId agent,
		EntityDescriptorTree entities) {
		try {
			this.fListener.entitiesChanged(host, agent, entities);
		} catch(Throwable t) {
			logger.error(t);
		}
	}

	/**
	 * Handles a non fatal error from the agent.
	 * @param agentId
	 * @param e
	 */
	private void handleNonFatalError(String host, AgentId agentId, Throwable e) {
		try {
			this.fListener.agentNonFatalError(host, agentId, e);
		} catch(Throwable t) {
			logger.error(t);
		}
	}

	/**
	 * Clears the data buffer list.
	 */
	private void cleanDataBufferList() {
		synchronized(this.fDataBuffers) {
			this.fDataBuffers.clear();
		}
	}

	/**
	 * Sends the data buffer list.
	 */
	private void sendDataBufferList() {
		synchronized(this.fDataBuffers) {
			if(this.fDataBuffers.size() > 0) {
				try {
					this.fListener.receiveDataBuffers(
							this.fDataBuffers.toArray(
									new AgentDataBufferImpl[this.fDataBuffers.size()]));
				} catch(Throwable t) {
					logger.error(t);
				}
			}
		}
	}

	/**
	 * Helper method that deactivates an agent.
	 * @param agentId
	 * @param ad
	 * @param remove
	 * @throws RMSException
	 */
	private void deactivateAgent(AgentId agentId, AgentData ad, boolean remove) throws RMSException {
		try {
			fClassLoadingHelper.prepareThreadOnEnter(agentId.toString());
			// stop it if started
			try {
				ad.agent.stop();
				ad.state = AgentState.STOPPED;
				fireAgentStateChanged(agentId, AgentState.STOPPED);
			} catch(Throwable e) {
				ad.state = AgentState.ERROR;
				fireAgentErrorState(agentId, e);
			}

			// and deactivate it
			try {
				ad.agent.deactivate();
				if(remove) {
					this.fAgents.remove(agentId);
				}
				fAgentsWithPrivateCollectors.remove(agentId);
				ad.state = AgentState.DEACTIVATED;
				fireAgentStateChanged(agentId, AgentState.DEACTIVATED);
			} catch(Throwable e) {
				ad.state = AgentState.ERROR;
				fireAgentErrorState(agentId, e);
				if(e instanceof RMSException) {
					throw (RMSException)e;
				}
				throw new RMSException(e);
			}
		} finally {
			fClassLoadingHelper.prepareThreadOnExit();
			fClassLoadingHelper.identifierExpired(agentId.toString());
		}
	}
}
