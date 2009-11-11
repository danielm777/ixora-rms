package com.ixora.rms.agents;

import java.awt.image.DataBuffer;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import com.ixora.rms.DataSink;
import com.ixora.rms.DataSinkTrimmed;
import com.ixora.rms.HostId;
import com.ixora.rms.HostInformation;
import com.ixora.rms.HostMonitor;
import com.ixora.rms.HostReachability;
import com.ixora.rms.RMSComponent;
import com.ixora.rms.RMSConfigurationConstants;
import com.ixora.rms.RecordDefinitionCache;
import com.ixora.rms.ResourceId;
import com.ixora.rms.TimeDeltaCache;
import com.ixora.common.ConfigurationMgr;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.remote.ClientSocketFactory;
import com.ixora.common.remote.ServerSocketFactory;
import com.ixora.common.remote.ServiceState;
import com.ixora.common.utils.Utils;
import com.ixora.rms.EntityConfiguration;
import com.ixora.rms.EntityDataBuffer;
import com.ixora.rms.EntityDescriptorTree;
import com.ixora.rms.EntityId;
import com.ixora.rms.RecordDefinition;
import com.ixora.rms.agents.providers.AgentShadow;
import com.ixora.rms.exception.AgentDescriptorNotFound;
import com.ixora.rms.exception.AgentIsNotInstalled;
import com.ixora.rms.exception.AgentNotActivated;
import com.ixora.rms.exception.InvalidAgentState;
import com.ixora.rms.exception.InvalidConfiguration;
import com.ixora.rms.exception.InvalidEntity;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.exception.RecordDefinitionNotFound;
import com.ixora.rms.exception.UnreachableHostManager;
import com.ixora.rms.providers.ProviderState;
import com.ixora.rms.providers.ProvidersManager;
import com.ixora.rms.remote.agents.RemoteAgentManager;
import com.ixora.rms.remote.agents.RemoteAgentManagerEventHandler;
import com.ixora.rms.repository.AgentInstallationData;
import com.ixora.rms.repository.AgentRepositoryManager;
import com.ixora.rms.repository.ParserRepositoryManager;
import com.ixora.rms.repository.ProviderInstanceRepositoryManager;
import com.ixora.rms.repository.ProviderRepositoryManager;
import com.ixora.rms.services.MonitoringSessionService;


/**
 * @author Daniel Moraru
 */
public final class MonitoringSession
	implements MonitoringSessionService,
		RMSConfigurationConstants {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(MonitoringSession.class);

	/**
	 * Event handler for remote events.
	 */
	private final class EventHandlerForRemoteEvents implements
		HostAgentManager.Listener {
		/**
		 * @see com.ixora.rms.agents.HostAgentManager.Listener#agentStateChanged(java.lang.String, AgentId, com.ixora.rms.AgentCategory, java.lang.Throwable)
		 */
		public void agentStateChanged(String host, AgentId agentId, AgentState state, Throwable e) {
			handleAgentStateChanged(host, agentId, state, e);
		}
		/**
		 * @see com.ixora.rms.agents.HostAgentManager.Listener#entitiesChanged(java.lang.String, AgentId, com.ixora.rms.EntityDescriptorTree)
		 */
		public void entitiesChanged(String host, AgentId agentId, EntityDescriptorTree entities) {
			handleEntitiesChanged(host, agentId, entities);
		}
		/**
		 * @see com.ixora.rms.agents.HostAgentManager.Listener#receiveDataBuffers(AgentDataBuffer[])
		 */
		public void receiveDataBuffers(AgentDataBuffer[] buff) {
			handleDataBuffersAsRemoteEvent(buff);
		}
		/**
		 * @see com.ixora.rms.agents.HostAgentManager.Listener#agentNonFatalError(java.lang.String, AgentId, java.lang.Throwable)
		 */
		public void agentNonFatalError(String host, AgentId agentId, Throwable t) {
			handleAgentNonFatalError(host, agentId, t);
		}
	}

	/**
	 * Event handler for local events.
	 */
	private final class EventHandler implements
		HostAgentManager.Listener,
			HostMonitor.Listener,
				Agent.Listener,
					AgentShadow.AgentShadowListener,
						Observer,
							GlobalCollector.Callback {
		/**
		 * @see com.ixora.rms.agents.HostAgentManager.Listener#addDataBuffer(DataBuffer)
		 */
		public void receiveDataBuffers(AgentDataBuffer[] buff) {
			handleDataBuffersAsLocalEvent(buff);
		}

		/**
		 * @see com.ixora.rms.agents.Agent.Listener#receiveDataBuffer(AgentDataBuffer)
		 */
		public void receiveDataBuffer(AgentDataBuffer data) {
			handleDataBuffersAsLocalEvent(new AgentDataBuffer[] {data});
		}

		/**
		 * @see com.ixora.rms.agents.HostAgentManager.Listener#entitiesChanged(String, AgentId, EntityDescriptorTree)
		 */
		public void entitiesChanged(
			String host,
			AgentId agentName,
			EntityDescriptorTree entities) {
			handleEntitiesChangedAsLocalEvent(host, agentName, entities);
		}

		/**
		 * @see com.ixora.rms.agents.HostAgentManager.Listener#agentStateChanged(String, String, AgentState, Exception)
		 */
		public void agentStateChanged(
			String host,
			AgentId agentId,
			AgentState state,
			Throwable e) {
			handleAgentStateChangedAsLocalEvent(host, agentId, state, e);
		}

		/**
		 * @see com.ixora.rms.control.HostMonitor.Listener#hostStateChanged(java.lang.String, int, com.ixora.common.remote.ServiceState)
		 */
		public void hostStateChanged(String host, int serviceID, ServiceState state) {
			handleHostStateChanged(host, serviceID, state);
		}

		/**
		 * @see com.ixora.rms.control.HostMonitor.Listener#updateHostInfo(java.lang.String, com.ixora.rms.struct.HostInformation)
		 */
		public void updateHostInfo(String host, HostInformation info) {
			; // nothing
		}

		/**
		 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
		 */
		public void update(Observable o, Object arg) {
			if(RMSConfigurationConstants.AGENT_CONFIGURATION_SAMPLING_INERVAL.equals(arg)) {
				handleDefaultSamplingIntervalChanged();
			}
		}

		/**
		 * @see com.ixora.rms.agents.HostAgentManager.Listener#agentNonFatalError(java.lang.String, AgentId, java.lang.Throwable)
		 */
		public void agentNonFatalError(String host, AgentId agentClass, Throwable t) {
			handleAgentNonFatalError(host, agentClass, t);
		}

		/**
		 * @see com.ixora.rms.agents.Agent.Listener#nonFatalError(java.lang.String, com.ixora.rms.internal.agents.AgentId, java.lang.Throwable)
		 */
		public void nonFatalError(String host, AgentId agentId, Throwable t) {
			agentNonFatalError(host, agentId, t);
		}

		/**
		 * @see com.ixora.rms.agents.providers.AgentShadow.AgentShadowListener#providerStateChanged(java.lang.String, com.ixora.rms.internal.agents.AgentId, java.lang.String, com.ixora.rms.internal.providers.ProviderState, java.lang.Throwable)
		 */
		public void providerStateChanged(String host, AgentId agentId, String providerInstanceName, ProviderState state, Throwable t) {
			handleProviderStateChanged(host, agentId, providerInstanceName, state, t);
		}

		/**
		 * @see com.ixora.rms.agents.GlobalCollector.Callback#getRemoteAgentManagers()
		 */
		public List<RemoteAgentManager> getRemoteAgentManagers() {
			return getRemoteAgentManagersForGlobalCollector();
		}

		/**
		 * @see com.ixora.rms.services.HostMonitorService.Listener#aboutToRemoveHost(java.lang.String)
		 */
		public void aboutToRemoveHost(String host) {
			handleAboutToRemoveHost(host);
		}
	}

	/** Map with the host handlers */
	private Map<String, HostHandler> fHostManagers;
	/**
	 * Map of agent shadows, each activated agent will have a shadow
	 * created for it that will handle provider generated data.
	 */
	private Map<ResourceId, AgentShadow> fAgentShadows;
	/** Data sinks */
	private DataSink[] fDataSinks;
	/** Trimmed data sinks */
	private DataSinkTrimmed[] fDataSinksTrimmed;
	/** Record definition cache */
	private RecordDefinitionCache fRecordDefinitionCache;
	/** Time delta cache */
	private TimeDeltaCache fTimeDeltaCache;
	/** Host monitor */
	private HostMonitor fHostMonitor;
	/** Agent repository */
	private AgentRepositoryManager fAgentRepository;
	/** Provider repository */
	private ProviderRepositoryManager fProviderRepository;
	/** Parser repository */
	private ParserRepositoryManager fParserRepository;
	/** Providers manager */
	private ProvidersManager fProvidersManager;
	/** Provider instance repository */
	private ProviderInstanceRepositoryManager fProviderInstanceRepository;
	/** Remote agent manager event handler */
	private RemoteAgentManagerEventHandler fRemoteEventHandler;
	/** Event handler */
	private EventHandler fEventHandler;
	/**
	 * Event handler for remote events. We need to segregate between
	 * local events and events coming from remote hosts in order
	 * to adjust the time difference(remote) and redirect the local
	 * events to a asynchronous event dispatcher to allow the
	 * reaction mechanism to act upon the current session as a result
	 * of events wihtout getting into synchronization issues.
	 */
	private EventHandlerForRemoteEvents fEventHandlerForRemoteEvents;
	/** Listeners */
	private List<Listener> fListeners;
	/**
	 * Cached local host name. Important to use a name instead of 'localhost'
	 * to avoid issues for multihosted systems.
	 */
	private String fLocalHost;
	/** Global collector, used for hosts where bidirectional communication failed */
	private GlobalCollector fGlobalCollector;

	/**
	 * Constructor for AgentController.
	 * @param providerRepository
	 * @param parserRepository
	 * @param providersManager
	 * @param arm the agent repository
	 * @param hm the host monitor
	 * @param dataSinks the data sinks for monitored data
	 * @throws RemoteException if it failes to create
	 * the handler for the remote agents events
	 * @throws UnknownHostException
	 */
	public MonitoringSession(
			ProviderRepositoryManager providerRepository,
			ParserRepositoryManager parserRepository,
			ProvidersManager providersManager,
			ProviderInstanceRepositoryManager providerInstanceRepository,
			AgentRepositoryManager arm,
			HostMonitor hm,
			DataSink[] dataSinks, DataSinkTrimmed[] dataSinksTrimmed) throws RemoteException, Throwable, UnknownHostException {
		super();
		if(providerRepository == null || parserRepository == null || providersManager == null
				|| providerInstanceRepository == null || arm == null || hm == null || dataSinks == null || dataSinksTrimmed == null) {
			throw new IllegalArgumentException("null parameters");
		}
		this.fProviderRepository = providerRepository;
		this.fParserRepository = parserRepository;
		this.fProvidersManager = providersManager;
		this.fProviderInstanceRepository = providerInstanceRepository;
		this.fAgentRepository = arm;
		this.fHostMonitor = hm;
		this.fRecordDefinitionCache = new RecordDefinitionCache();
		this.fTimeDeltaCache = new TimeDeltaCache(hm);
		this.fDataSinksTrimmed = dataSinksTrimmed;
		for(DataSinkTrimmed ds : dataSinksTrimmed) {
			if(ds == null) {
				throw new IllegalArgumentException("null data sink");
			}
			ds.setRecordDefinitionCache(fRecordDefinitionCache);
		}
		this.fDataSinks = dataSinks;
		this.fHostManagers = new HashMap<String, HostHandler>();
		this.fAgentShadows = new HashMap<ResourceId, AgentShadow>();
		this.fEventHandler = new EventHandler();
		this.fEventHandlerForRemoteEvents = new EventHandlerForRemoteEvents();

        String ip = ConfigurationMgr.getString(RMSComponent.NAME, RMSConfigurationConstants.NETWORK_ADDRESS);
        ClientSocketFactory csf = null; // default
        ServerSocketFactory ssf = null; // default
        if(Utils.isEmptyString(ip)) {
            ssf = new ServerSocketFactory(null); // listen on all ip addresses
        } else {
            InetAddress ia = InetAddress.getByName(ip);
            csf = new ClientSocketFactory(ia);
            ssf = new ServerSocketFactory(ia);
        }
        this.fRemoteEventHandler = new RemoteAgentManagerEventHandler(csf, ssf, fEventHandlerForRemoteEvents);

        this.fListeners = new LinkedList<Listener>();
		hm.addListener(fEventHandler);
		ConfigurationMgr.get(RMSComponent.NAME).addObserver(fEventHandler);
		this.fLocalHost = InetAddress.getLocalHost().getCanonicalHostName();
	}

	/**
	 * @return
	 */
	private List<RemoteAgentManager> getRemoteAgentManagersForGlobalCollector() {
		synchronized(this) {
			List<RemoteAgentManager> ret = new LinkedList<RemoteAgentManager>();
			for(HostHandler hh : fHostManagers.values()) {
				if(hh.hostNeedsGlobalCollector()) {
					ret.add(hh.getRemoteAgentManager());
				}
			}
			return ret;
		}
	}

	/**
	 * Activates the given agent.
	 * @param agentId
	 * @param host
	 * @param activationData
	 * @throws AgentIsNotInstalled
	 * @throws InvalidAgentState
	 * @throws InvalidConfiguration
	 * @return the available entities and the agent id
	 * @throws RMSException
	 * @throws RemoteException
	 * @throws Throwable
	 */
	public AgentActivationTuple activateAgent(
			AgentActivationData activationData)
		throws
			RMSException, RemoteException {
		if(activationData == null) {
			throw new IllegalArgumentException("null parameters");
		}
        String host = activationData.getHost();
        String agentInstallationId = activationData.getAgentInstallationId();
		AgentInstallationData inst = this.fAgentRepository.getInstalledAgents().get(agentInstallationId);
		if(inst == null) {
			throw new AgentIsNotInstalled(agentInstallationId);
		}
        AgentConfiguration conf = activationData.getConfiguration();
        boolean remote = (activationData.getLocation() == AgentLocation.REMOTE);
		completeAgentConfiguration(conf, host, remote);
		RemoteAgentManager ram = null;
		HostAgentManager ham = null;
		HostHandler hh;
		// make sure just one host handler per host
		// is created
		synchronized (this) {
			hh = this.fHostManagers.get(host);
			if(hh == null) {
				hh = new HostHandler(
						fAgentRepository,
						fHostMonitor,
						host,
						this.fEventHandler,
						this.fRemoteEventHandler);
				this.fHostManagers.put(host, hh);
			}
			if(remote) {
				ram = hh.createRemoteAgentManager();
				if(hh.hostNeedsGlobalCollector()) {
					if(fGlobalCollector == null) {
						fGlobalCollector = new GlobalCollector(fEventHandler, fEventHandler);
						fGlobalCollector.setCollectionInterval(1000 * conf.getSamplingInterval());
						fGlobalCollector.startCollector();
					}
				}
			} else {
				ham = hh.createLocalAgentManager();
			}
			AgentActivationTuple tuple;
			if(ram != null) {
				// remote
				tuple = ram.activateAgent(agentInstallationId , conf);
				hh.registerAgent(tuple.getAgentId(), AgentLocation.REMOTE);
			} else {
				// local
				tuple = ham.activateAgent(agentInstallationId, conf);
				hh.registerAgent(tuple.getAgentId(), AgentLocation.LOCAL);
			}

			// create a shadow for this agent
			boolean error = false;
			AgentShadow shadow = null;
			try {
				// get defined provider intstances for this agent first
				shadow = new AgentShadow(
						this.fProviderRepository,
						this.fParserRepository,
						this.fProvidersManager,
						this.fProviderInstanceRepository,
						(AgentConfiguration)conf.clone(), // very important to clone the config as it will be modified
						remote,
						host,
						agentInstallationId,
						tuple.getAgentId(),
						this.fEventHandler,
                        this.fEventHandler);
				// deactivate the old shadow if somehow it's there
				AgentShadow oldShadow = this.fAgentShadows.put(createResourceId(host, tuple.getAgentId()), shadow);
				if(oldShadow != null) {
					oldShadow.deactivate();
				}
			} catch(RMSException e) {
				error = true;
				throw e;
			} catch(Throwable e) {
				error = true;
				throw new RMSException(e);
			} finally {
				if(error) {
					// must do this at this stage
					deactivateAgent(host, tuple.getAgentId());
				}
			}
			try {
				EntityDescriptorTree descriptorsProviders = shadow.getEntities(null, false, true);
				EntityDescriptorTree retDesc = merge(host, tuple.getAgentId(), descriptorsProviders, tuple.getEntities());
				return new AgentActivationTuple(
						// use the agent's descriptor
						tuple.getDescriptor(), retDesc);
			} catch(Throwable t) {
				throw new RMSException(t);
			}
		}
	}

	/**
	 * @param host
	 * @param agentId
	 * @return
	 */
	private static ResourceId createResourceId(String host, AgentId agentId) {
		return new ResourceId(new HostId(host), agentId, null, null);
	}

	/**
	 * Adds a listener
	 * @param listener
	 */
	public void addListener(Listener listener) {
		if(listener == null) {
			throw new IllegalArgumentException("null listener");
		}
		synchronized (this.fListeners) {
			if(!this.fListeners.contains(listener)) {
				this.fListeners.add(listener);
			}
		}
	}


	/**
	 * Configures the given agent.
	 * @param host
	 * @param agentId
	 * @param conf
	 * @throws InvalidAgentState
	 * @throws InvalidConfiguration
	 * @throws RMSException
	 * @throws RemoteException
	 */
	public AgentConfigurationTuple configureAgent(
			String host,
			AgentId agentId,
			AgentConfiguration conf)
		throws
			UnreachableHostManager, RMSException, RemoteException {
		synchronized(this) {
			HostHandler hh = this.fHostManagers.get(host);
			if(hh == null || !hh.isAgentActivated(agentId)) {
				throw new AgentNotActivated();
			}
			// configure for remote for the moment
			completeAgentConfiguration(conf, host, true);
			// reconfigure global collector
			if(fGlobalCollector != null) {
				fGlobalCollector.recalibrateCollector(1000 * conf.getSamplingInterval());
			}
			// configure shadow first
			AgentConfigurationTuple providerTuple = null;
			AgentShadow shadow = this.fAgentShadows.get(createResourceId(host, agentId));
			try {
				EntityDescriptorTree entities = shadow.configure(conf);
				providerTuple = new AgentConfigurationTuple(shadow.getDescriptor(), entities);
			} catch(RMSException e) {
				throw e;
			} catch(Throwable t) {
				throw new RMSException(t);
			}

			// check first if the agent is local
			HostAgentManager ham = hh.getLocalAgentManager();
			if(ham != null) {
				if(ham.getAgentState(agentId) != null) {
					// agent was local, complete config for local
					completeAgentConfiguration(conf, host, false);
					AgentConfigurationTuple agentTuple = ham.configureAgent(agentId, conf);
					return merge(host, agentId, providerTuple, agentTuple);
				}
			}
			// the agent might be remote
			RemoteAgentManager ram = hh.getRemoteAgentManager();
			if(ram == null) {
				throw new AgentNotActivated();
			}
			AgentConfigurationTuple agentTuple = ram.configureAgent(agentId, conf);
			return merge(host, agentId, providerTuple, agentTuple);
		}
	}

	/**
	 * Configures the given entity.
	 * @param host
	 * @param agentId
	 * @param entity
	 * @param conf
	 * @throws InvalidAgentState
	 * @throws InvalidConfiguration
	 * @throws InvalidEntity
	 * @throws RMSException
	 * @throws RemoteException
	 */
	public EntityDescriptorTree configureEntity(
			String host,
			AgentId agentId,
			EntityId entity,
			EntityConfiguration conf)
		throws
			InvalidConfiguration, InvalidEntity, InvalidAgentState, RMSException, RemoteException {
		synchronized(this) {
			HostHandler hh = this.fHostManagers.get(host);
			if(hh == null || !hh.isAgentActivated(agentId)) {
				throw new AgentNotActivated();
			}
			// complete configuration
			completeEntityConfiguration(conf);
			// reconfigure global collector
			if(conf.hasValidSamplingInterval() && fGlobalCollector != null) {
				fGlobalCollector.recalibrateCollector(1000 * conf.getSamplingInterval());
			}
			// check if it's for shadow
			AgentShadow shadow = this.fAgentShadows.get(createResourceId(host, agentId));
			if(shadow.providesForEntity(entity)) {
				try {
					return shadow.configureEntity(entity, conf);
				} catch(RMSException e) {
					throw e;
				} catch(Throwable t) {
					throw new RMSException(t);
				}
			} else {
				// check first if the agent is local
				HostAgentManager ham = hh.getLocalAgentManager();
				if(ham != null) {
					if(ham.getAgentState(agentId) != null) {
						return ham.configureEntity(agentId, entity, conf);
					}
				}
				// the agent might be remote
				RemoteAgentManager ram = hh.getRemoteAgentManager();
				if(ram == null) {
					throw new AgentNotActivated();
				}
				return ram.configureEntity(agentId, entity, conf);
			}
		}
	}

	/**
	 * Deactivates the given agent.
	 * @param host
	 * @param agentId
	 * @throws RMSException
	 * @throws RemoteException
	 */
	public void deactivateAgent(String host, AgentId agentId)
			throws RMSException, RemoteException {
		synchronized (this) {
			HostHandler hh = this.fHostManagers.get(host);
			if(hh == null || !hh.isAgentActivated(agentId)) {
				throw new AgentNotActivated();
			}
			// deactivate shadow first
			ResourceId rid = createResourceId(host, agentId);
			AgentShadow shadow = this.fAgentShadows.get(rid);
			// normally the shadow is not null but this method is also called from
			// the activate method if the shadow failed to be created
			if(shadow != null) {
				try {
					shadow.deactivate();
				} catch(Throwable t) {
					// log and keep going...
					logger.error(t);
				} finally {
					this.fAgentShadows.remove(rid);
				}
			}
			// check first if the agent is local
			HostAgentManager ham = hh.getLocalAgentManager();
			if(ham != null) {
				if(ham.getAgentState(agentId) != null) {
                    try {
                        ham.deactivateAgent(agentId);
                    } catch(Throwable t) {
                        logger.error(t);
                    }
					hh.unregisterAgent(agentId);
					return;
				}
			}
			// the agent might be remote
			RemoteAgentManager ram = hh.getRemoteAgentManager();
			if(ram != null) {
                try {
                    ram.deactivateAgent(agentId);
                } catch(Throwable t) {
                    logger.error(t);
                }
            }
			hh.unregisterAgent(agentId);
		}
	}

	/**
	 * Gets the children entities of parent entity from the given agent.
	 * @param agentId
	 * @param host
	 * @param parent
	 * @throws InvalidAgentState
	 * @throws RMSException
	 * @throws RemoteException
	 */
	public EntityDescriptorTree getAgentEntities(
			String host,
			AgentId agentId,
			EntityId parent,
			boolean recursive,
			boolean refresh)
			throws
				RMSException, RemoteException {
		synchronized (this) {
			HostHandler hh = this.fHostManagers.get(host);
			if(hh == null || !hh.isAgentActivated(agentId)) {
				throw new AgentNotActivated();
			}
			EntityDescriptorTree descriptorsProviders = null;
			// check if it's for shadow
			AgentShadow shadow = this.fAgentShadows.get(createResourceId(host, agentId));
			if(shadow.providesForEntity(parent)) {
				try {
					descriptorsProviders = shadow.getEntities(parent, recursive, true);
				} catch(RMSException e) {
					throw e;
				} catch(Throwable t) {
					throw new RMSException(t);
				}
			}
			EntityDescriptorTree descriptorsAgent = null;
			// check first if the agent is local
			HostAgentManager ham = hh.getLocalAgentManager();
			boolean isLocal = false;
			if(ham != null) {
				if(ham.getAgentState(agentId) != null) {
					isLocal = true;
					descriptorsAgent = ham.getEntities(agentId, parent, recursive, refresh);
				}
			}
			if(!isLocal) {
				// the agent might be remote
				RemoteAgentManager ram = hh.getRemoteAgentManager();
				if(ram == null) {
					throw new AgentNotActivated();
				}
				descriptorsAgent = ram.getEntities(agentId, parent, recursive, refresh);
			}
			return merge(host, agentId, descriptorsProviders, descriptorsAgent);
		}
	}

	/**
	 * @param provider
	 * @param agent
	 * @return
	 */
	private AgentConfigurationTuple merge(String host, AgentId agentId, AgentConfigurationTuple provider, AgentConfigurationTuple agent) {
		return new AgentConfigurationTuple(agent.getAgentDescriptor(),
				merge(host, agentId, provider.getEntities(), agent.getEntities()));
	}

	/**
	 * Merges provider and agent entity descriptors. Agent entities have priority, i.e if the
	 * same entity has descriptors in both arrays only the descriptor in the agent array will
	 * make to the returned array.
	 * @param host
	 * @param agentId
	 * @param provider
	 * @param agent
	 * @return
	 */
	private EntityDescriptorTree merge(
			String host, AgentId agentId,
			EntityDescriptorTree provider, EntityDescriptorTree agent) {
// this should test for the following:
		// fire a non-fatal error as we do not support
		// this situation when an agent and a provider have entities that overlap
/*		handleAgentNonFatalError(host, agentId,
			new ProviderOverlappingAgentEntity(eid));
*/
		// now merge the two trees
		// if both are non-null use the first element in the agent's array
		if(provider == null) {
			return agent;
		}
		if(agent == null) {
			return provider;
		}
		agent.merge(provider);
		return agent;
	}

	/**
	 * @return the state of the given agent
	 * @throws RMSException
	 * @throws RemoteExceptions
	 */
	public AgentState getAgentState(
			String host, AgentId agentId) throws RMSException, RemoteException {
		synchronized (this) {
			HostHandler hh = this.fHostManagers.get(host);
			if(hh == null  || !hh.isAgentActivated(agentId)) {
				throw new AgentNotActivated();
			}
			// check first if the agent is local
			HostAgentManager ham = hh.getLocalAgentManager();
			if(ham != null) {
				AgentState state = ham.getAgentState(agentId);
				if(state != null) {
					return state;
				}
			}
			// the agent might be remote
			RemoteAgentManager ram = hh.getRemoteAgentManager();
			if(ram == null) {
				throw new AgentNotActivated();
			}
			AgentState state = ram.getAgentState(agentId);
			if(state == null) {
				throw new AgentNotActivated();
			}
			return state;
		}
	}

	/**
	 * Removes a listener.
	 * @param listener
	 */
	public void removeListener(Listener listener) {
		synchronized(this.fListeners) {
			this.fListeners.remove(listener);
		}
	}

	/**
	 * Starts the given agent on the given host.
	 * @param agentId
	 * @param host
	 * @throws InvalidAgentState
	 * @throws RMSException
	 * @throws StartableError
	 */
	public void startAgent(String host, AgentId agentId)
		throws UnreachableHostManager, RMSException, RemoteException {
		synchronized (this) {
			HostHandler hh = this.fHostManagers.get(host);
			if(hh == null  || !hh.isAgentActivated(agentId)) {
				throw new AgentNotActivated();
			}
			// start shadow
			AgentShadow shadow = this.fAgentShadows.get(createResourceId(host, agentId));
			try {
				shadow.start();
			} catch(Throwable t) {
				throw new RMSException(t);
			}

			// check first if the agent is local
			HostAgentManager ham = hh.getLocalAgentManager();
			if(ham != null) {
				if(ham.getAgentState(agentId) != null) {
					ham.startAgent(agentId);
					return;
				}
			}
			// the agent might be remote
			RemoteAgentManager ram = hh.getRemoteAgentManager();
			if(ram == null) {
				throw new AgentNotActivated();
			}
			ram.startAgent(agentId);
		}
	}

	/**
	 * Stops the given agent on the given host.
	 * @param host
	 * @param agentId
	 * @throws InvalidAgentState
	 * @throws RMSException
	 * @throws RemoteException
	 * @throws InvalidAgentState
	 */
	public void stopAgent(String host, AgentId agentId)
		throws UnreachableHostManager, RMSException, RemoteException, InvalidAgentState {
		synchronized (this) {
			HostHandler hh = this.fHostManagers.get(host);
			if(hh == null  || !hh.isAgentActivated(agentId)) {
				throw new AgentNotActivated();
			}
			// stop shadow
			AgentShadow shadow = this.fAgentShadows.get(createResourceId(host, agentId));
			try {
				shadow.stop();
			} catch(Throwable t) {
				logger.error(t);
			}

			// check first if the agent is local
			HostAgentManager ham = hh.getLocalAgentManager();
			if(ham != null) {
				if(ham.getAgentState(agentId) != null) {
					ham.stopAgent(agentId);
					return;
				}
			}
			// the agent might be remote
			RemoteAgentManager ram = hh.getRemoteAgentManager();
			if(ram == null) {
				throw new AgentNotActivated();
			}
			ram.stopAgent(agentId);
		}
	}

	/**
	 * Starts all agents.
	 * @throws RMSException
	 * @throws RemoteException
	 * @throws InvalidAgentState
	 * @throws StartableError
	 */
	public void startAllAgents() throws RMSException, RemoteException, InvalidAgentState {
		synchronized (this) {
			// start all shadows
			for(AgentShadow shadow : this.fAgentShadows.values()) {
				try {
					shadow.start();
				} catch(RMSException e) {
					throw e;
				} catch(Throwable t) {
					throw new RMSException(t);
				}
			}
			// start all agents
			for(Iterator <HostHandler>iter = this.fHostManagers.values().iterator(); iter.hasNext();) {
				HostHandler hh = iter.next();
				HostAgentManager ham = hh.getLocalAgentManager();
				if(ham != null) {
					ham.startAllAgents();
				}
				RemoteAgentManager ram = hh.getRemoteAgentManager();
				if(ram != null) {
					ram.startAllAgents();
				}
			}
		}
	}

	/**
	 * Stops all agents.
	 */
	public void stopAllAgents() {
		synchronized (this) {

			// stop all shadows
			for(AgentShadow shadow : this.fAgentShadows.values()) {
				try {
					shadow.stop();
				} catch(Throwable t) {
					// log and keep going
					logger.error(t);
				}
			}
			// stop all agents
			for(Iterator<HostHandler> iter = this.fHostManagers.values().iterator(); iter.hasNext();) {
				try {
					HostHandler hh = iter.next();
					HostAgentManager ham = hh.getLocalAgentManager();
					if(ham != null) {
                        try {
                            ham.stopAllAgents();
                        } catch(Throwable t) {
                            logger.error(t);
                        }
					}
					RemoteAgentManager ram = hh.getRemoteAgentManager();
					if(ram != null) {
					    ram.stopAllAgents();
					}
				} catch(Throwable t) {
					// log and keep going
					logger.error(t);
				}
			}
		}
	}

	/**
	 * Deactivates all agents.
	 */
	public void deactivateAllAgents() {
		synchronized (this) {
			// stop all shadows
			for(AgentShadow shadow : this.fAgentShadows.values()) {
				try {
					shadow.deactivate();
				} catch(Throwable t) {
					// log and keep going
					logger.error(t);
				}
			}
			for(Iterator<HostHandler> iter = this.fHostManagers.values().iterator(); iter.hasNext();) {
				try {
					HostHandler hh = iter.next();
					HostAgentManager ham = hh.getLocalAgentManager();
					if(ham != null) {
						ham.deactivateAllAgents();
					}
					RemoteAgentManager ram = hh.getRemoteAgentManager();
					if(ram != null) {
						ram.deactivateAllAgents();
					}
					hh.unregisterAllAgents();
				} catch(Exception e) {
					// log and keep going
					logger.error(e);
				}
			}
		}
	}

	/**
	 * Cleanup.
	 */
	public void shutdown() {
		try {
			deactivateAllAgents();
		} catch(Exception e) {
			logger.error(e);
		}
		try {
			fHostMonitor.removeListener(fEventHandler);
		} catch(Exception e) {
			logger.error(e);
		}
		try {
			synchronized(this) {
				if(fGlobalCollector != null) {
					fGlobalCollector.stopCollector();
				}
				fGlobalCollector = null;
				for(HostHandler hh : fHostManagers.values()) {
					try {
						hh.destroyRemoteAgentManager();
					} catch(Exception e) {
						logger.error("Failed to shutdown the RemoteAgentManager on host " + hh.getHost(), e);
					}
				}
			}
		} catch(Exception e) {
			logger.error(e);
		}
	}

	/**
	 * Handles the new data buffers.
	 * @param buff
	 * @throws AgentDescriptorNotFound
	 * @throws RecordDefinitionNotFound
	 */
	private void handleDataBuffers(AgentDataBuffer[] buffs) throws AgentDescriptorNotFound, RecordDefinitionNotFound {
		try {
			// first see if we need to update the record
			// defintion cache
			for(int i = 0; i < buffs.length; i++) {
				try {
					AgentDataBuffer buff = buffs[i];
					long delta = buff.getTimeDelta();
					EntityDataBuffer[] ebs = buff.getBuffers();
					if(ebs != null) {
		                // see if AgentDescriptor is in buffer
		                AgentDescriptor agentDesc = buff.getAgentDescriptor();
		                if(agentDesc != null) {
		                    // put it in the cache
		                    fRecordDefinitionCache.putAgentDescriptor(buff.getHost(), buff.getAgent(), agentDesc);
		                    //activeAgents.add(new ResourceId(buff.getHost(), buff.getAgent(), null, null));
		                }
						for(int j = 0; j < ebs.length; j++) {
							try {
								EntityDataBuffer eb = ebs[j];
								if(eb == null) {
									// should not happen
									logger.error("Null entity buffer in agent data buffer for agent " + buff.getAgent());
									continue;
								}
								RecordDefinition rd = eb.getDefinition();
								if(rd != null) {
									// put it in the cache
									fRecordDefinitionCache.putRecordDefinition(buff.getHost(), buff.getAgent(),
										eb.getEntityId(), rd);
								} else if(delta > 0) {
									// only if we need to fix time
									// do this
									rd = fRecordDefinitionCache.getRecordDefinition(
											buff.getHost(), buff.getAgent(), eb.getEntityId());
								}
								if(delta > 0 && rd != null) {
									// do not set the RecordDefinition
									// on the entity buffer as not
									// all data sinks will need it
									eb.applyTimeDelta(delta, rd);
								}
							} catch(Throwable e) {
								logger.error(e);
							}
						}
					}
				} catch(Throwable e) {
					logger.error(e);
				}
			}
			// then send buffers to the trimmed data sinks before definitions are set
			for(DataSinkTrimmed dst : fDataSinksTrimmed) {
				try {
					dst.receiveDataBuffers(buffs);
				} catch(Exception e) {
					logger.error(e);
				}
			}

			// now set the definitions for the full data sinks
			for(int i = 0; i < buffs.length; i++) {
				AgentDataBuffer buff = buffs[i];
				EntityDataBuffer[] ebs = buff.getBuffers();
				if(ebs != null) {
	                // set AgentDescriptor
	                AgentDescriptor agentDesc = buff.getAgentDescriptor();
	                if(agentDesc == null) {
	                    // put it in the cache
	                    agentDesc = fRecordDefinitionCache.getAgentDescriptor(buff.getHost(), buff.getAgent());
	                    if(agentDesc == null) {
	                    	throw new AgentDescriptorNotFound(buff.getAgent());
	                    }
	                    buff.setAgentDescriptor(agentDesc);
	                }

					for(int j = 0; j < ebs.length; j++) {
						EntityDataBuffer eb = ebs[j];
						RecordDefinition rd = eb.getDefinition();
						if(rd == null) {
							// put it in the cache
							rd = fRecordDefinitionCache.getRecordDefinition(buff.getHost(), buff.getAgent(),
								eb.getEntityId());
		                    if(rd == null) {
		                    	throw new RecordDefinitionNotFound(eb.getEntityId());
		                    }
		                    eb.setDefinition(rd);
						}
					}
				}
			}

			// then send buffers to data sinks
			for(DataSink ds : fDataSinks) {
				try {
					ds.receiveDataBuffers(buffs);
				} catch(Exception e) {
					logger.error(e);
				}
			}
		} catch(Exception ex) {
			logger.error(ex);
		}
	}

	/**
	 * Handles the entities changed event.
	 * @param host
	 * @param agentId
	 * @param entities
	 */
	private void handleEntitiesChanged(
		String host,
		AgentId agentId,
		EntityDescriptorTree entities) {
		synchronized (this.fListeners) {
			for(Iterator<Listener> iter = this.fListeners.iterator(); iter.hasNext();) {
				try {
					iter.next().entitiesChanged(host, agentId, entities);
				} catch(Exception ex) {
					logger.error(ex);
				}
			}
		}
	}

	/**
	 * Handles the agent state changed event.
	 * @param host
	 * @param agentId
	 * @param state
	 */
	private void handleAgentStateChanged(
		String host,
		AgentId agentId,
		AgentState state,
		Throwable e) {
		synchronized (this.fListeners) {
			for(Listener listener : this.fListeners) {
                try {
                    listener.agentStateChanged(host, agentId, state, e);
                } catch(Exception ex) {
                    logger.error(ex);
                }
			}
		}
	}

	/**
	 * @param host
	 * @param agentId
	 * @param providerInstanceName
	 * @param state
	 * @param t
	 */
	private void handleProviderStateChanged(String host, AgentId agentId, String providerInstanceName, ProviderState state, Throwable t) {
		synchronized (this.fListeners) {
			for(Listener listener : this.fListeners) {
				try {
					listener.providerStateChanged(host, agentId, providerInstanceName, state, t);
				} catch(Exception ex) {
					logger.error(ex);
				}
			}
		}
	}

	/**
	 * Handles the agent state changed event from agents running on the
	 * local machine.
	 * @param host
	 * @param agentName
	 * @param state
	 */
	private void handleAgentStateChangedAsLocalEvent(
		String host,
		AgentId agentId,
		AgentState state,
		Throwable e) {
		try {
			// TODO run asynch...
			handleAgentStateChanged(host, agentId, state, e);
		} catch(Exception ex) {
			logger.error(ex);
		}
	}

	/**
	 * Handles the new data buffer events coming from agents running
	 * on remote machines.
	 * @param buff
	 */
	private void handleDataBuffersAsRemoteEvent(AgentDataBuffer[] buffs) {
		try {
			// fix timestamp differences
			AgentDataBuffer db;
			long delta;
			for(int i = 0; i < buffs.length; i++) {
				db = buffs[i];
				delta = fTimeDeltaCache.getDelta(db.getHost().toString());
				if(delta != 0) {
					// apply to data buffer
					db.setTimeDelta(delta);
				}
			}
			handleDataBuffers(buffs);
		} catch(Exception ex) {
			logger.error(ex);
		}
	}

	/**
	 * Handles the new data buffer events coming from agents running
	 * on the local machine.
	 * @param buff
	 */
	private void handleDataBuffersAsLocalEvent(AgentDataBuffer[] buffs) {
		try {
			// TODO run asynch...
			handleDataBuffers(buffs);
		} catch(Exception ex) {
			logger.error(ex);
		}
	}

	/**
	 * Handles the entities changed event from agents running on the local
	 * host.
	 * @param host
	 * @param agentName
	 * @param parent
	 * @param entities
	 */
	private void handleEntitiesChangedAsLocalEvent(
		String host,
		AgentId agentId,
		EntityDescriptorTree entities) {
		try {
			// TODO run asynch...
			handleEntitiesChanged(host, agentId, entities);
		} catch(Exception ex) {
			logger.error(ex);
		}
	}

	/**
	 * Handles the agent state changed event.
	 * @param host
	 * @param agentId
	 * @param t
	 */
	private void handleAgentNonFatalError(
			String host,
			AgentId agentId,
			Throwable t) {
		synchronized (this.fListeners) {
			for(Listener listener : this.fListeners) {
				try {
					listener.agentNonFatalError(host, agentId, t);
				} catch(Exception ex) {
					logger.error(ex);
				}
			}
		}
	}

	/**
	 * @param host
	 */
	private void handleAboutToRemoveHost(String host) {
		HostHandler hh;
		synchronized(this) {
			// destroy the remote agent manager
			hh = this.fHostManagers.remove(host);
			if(hh == null) {
				return;
			}
		}
		hh.destroyRemoteAgentManager();
	}

	/**
	 * When a remote host goes offline all the agents deployed remotely
	 * on that host are considered to be into the error state.
	 * @param host
	 * @param serviceID
	 * @param state
	 */
	private void handleHostStateChanged(String host, int serviceID, ServiceState state) {
		try {
			if(serviceID == HostReachability.HOST_MANAGER && state == ServiceState.OFFLINE) {
				HostHandler hh = null;
				synchronized(this) {
					hh = this.fHostManagers.get(host);
					if(hh == null) {
						return;
					}
				}
				List<AgentId> agents = hh.getAgents(AgentLocation.REMOTE);
				hh.hostWentOffline();
				for(AgentId agentId : agents) {
					handleAgentStateChanged(host, agentId, AgentState.UNKNOWN,
	                        new UnreachableHostManager(host));
				}
			}
		} catch(Exception ex) {
			logger.error(ex);
		}
	}

	/**
	 * Reconfigures all agents.
	 */
	private void handleDefaultSamplingIntervalChanged() {
		try {
			int samplingInterval = ConfigurationMgr.getInt(
					RMSComponent.NAME,
					AGENT_CONFIGURATION_SAMPLING_INERVAL);
			AgentConfiguration conf = new AgentConfiguration();
			conf.setGlobalSamplingInterval(true);
			conf.setSamplingInterval(samplingInterval);
			synchronized (this) {
				if(fGlobalCollector != null) {
					fGlobalCollector.setCollectionInterval(1000 * samplingInterval);
				}
				HostAgentManager ham;
				RemoteAgentManager ram;
				for(HostHandler hh : this.fHostManagers.values()) {
					ham = hh.getLocalAgentManager();
					if(ham != null) {
						ham.configureAllAgents(conf);
					}
					ram = hh.getRemoteAgentManager();
					if(ram != null) {
						ram.configureAllAgents(conf);
					}
				}

				// configure shadows
				for(AgentShadow shadow : this.fAgentShadows.values()) {
					try {
						shadow.configure(conf);
					} catch(RMSException e) {
						throw e;
					} catch(Throwable t) {
						throw new RMSException(t);
					}
				}
			}
		} catch(Throwable e) {
			logger.error(e);
		}
	}

	/**
	 * Completes the agent configuration.
	 * @param conf
	 * @param host
	 * @param jars non null only for activation
	 * @param remote
	 */
	private void completeAgentConfiguration(AgentConfiguration conf, String host, boolean remote) {
		if(conf.isGlobalSamplingInterval()) {
			conf.setSamplingInterval(ConfigurationMgr.getInt(
					RMSComponent.NAME,
					RMSConfigurationConstants.AGENT_CONFIGURATION_SAMPLING_INERVAL));
		}
		conf.setMonitoredHost(host);
		if(remote) {
			conf.setDeploymentHost(host);
		} else {
			conf.setDeploymentHost(this.fLocalHost);
		}
	}

	/**
	 * Completes the entity configuration.
	 * @param conf
	 */
	private void completeEntityConfiguration(EntityConfiguration conf) {
		if(conf.isGlobalSamplingInterval()) {
			conf.setSamplingInterval(ConfigurationMgr.getInt(
					RMSComponent.NAME,
					RMSConfigurationConstants.AGENT_CONFIGURATION_SAMPLING_INERVAL));
		}
	}
}