/*
 * Created on 29-Dec-2004
 */
package com.ixora.rms.providers;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import com.ixora.common.ConfigurationMgr;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.remote.ClientSocketFactory;
import com.ixora.common.remote.ServerSocketFactory;
import com.ixora.common.remote.ServiceState;
import com.ixora.common.thread.RunQueue;
import com.ixora.common.utils.Utils;
import com.ixora.rms.HostId;
import com.ixora.rms.HostInformation;
import com.ixora.rms.HostMonitor;
import com.ixora.rms.HostReachability;
import com.ixora.rms.RMSComponent;
import com.ixora.rms.RMSConfigurationConstants;
import com.ixora.rms.TimeDeltaCache;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.providers.exception.InvalidProviderConfiguration;
import com.ixora.rms.providers.exception.ProviderNotActivated;
import com.ixora.rms.remote.providers.RemoteProviderManager;
import com.ixora.rms.remote.providers.RemoteProviderManagerEventHandler;
import com.ixora.rms.services.HostMonitorService;
import com.ixora.rms.services.ProviderRepositoryService;
import com.ixora.rms.services.ProvidersManagerService;

/**
 * @author Daniel Moraru
 */
public final class ProvidersManager implements ProvidersManagerService, RMSConfigurationConstants {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(ProvidersManager.class);

	/** Time delta cache */
	private TimeDeltaCache fTimeDeltaCache;
	/** Host monitor */
	private HostMonitor fHostMonitor;
	/** Remote agent manager event handler */
	private RemoteProviderManagerEventHandler fRemoteEventHandler;
	/** Event handler */
	private EventHandler fEventHandler;
	/** Event handler for remote events */
	private EventHandlerRemoteEvents fEventHandlerRemoteEvents;
	/** Map with the host handlers */
	private Map<HostId, HostHandler> fHostManagers;
	/** Listeners */
	private List<Listener> fListeners;
	/**
	 * Event queue to dispatch events to listeners. The processing of events is done
	 * asynchronously to break the call chain and avoid deadlocks.
	 */
	private RunQueue fEventDispatcher;
	/** Provider repository */
	private ProviderRepositoryService fProviderRepository;
	/** Global collector, used for hosts where bidirectional communication failed */
	private GlobalCollector fGlobalCollector;

	/**
	 * Event handler for remote events.
	 */
	private final class EventHandler implements
		HostProviderManager.Listener,
			HostMonitorService.Listener,
				Observer,
					GlobalCollector.Callback {
		/**
		 * @see com.ixora.rms.providers.HostProviderManager.Listener#providerStateChanged(com.ixora.rms.providers.ProviderId, com.ixora.rms.internal.providers.ProviderState, java.lang.Throwable)
		 */
		public void providerStateChanged(ProviderId provider, ProviderState state, Throwable err) {
			handleProviderStateChanged(provider, state, err);
		}
		/**
		 * @see com.ixora.rms.providers.HostProviderManager.Listener#data(com.ixora.rms.providers.ProviderDataBuffer[])
		 */
		public void data(ProviderDataBuffer[] buff) {
			handleProviderDataAsLocalEvent(buff);
		}
		/**
		 * @see com.ixora.rms.services.HostMonitorService.Listener#hostStateChanged(java.lang.String, int, com.ixora.common.remote.ServiceState)
		 */
		public void hostStateChanged(String host, int serviceID, ServiceState state) {
			handleHostStateChanged(host, serviceID, state);
		}

		/**
		 * @see com.ixora.rms.services.HostMonitorService.Listener#updateHostInfo(java.lang.String, com.ixora.rms.internal.HostInformation)
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
		 * @see com.ixora.rms.services.HostMonitorService.Listener#aboutToRemoveHost(java.lang.String)
		 */
		public void aboutToRemoveHost(String host) {
			handleAboutToRemoveHost(host);
		}
		/**
		 * @see com.ixora.rms.providers.GlobalCollector.Callback#getRemoteProviderManagers()
		 */
		public List<RemoteProviderManager> getRemoteProviderManagers() {
			return getRemoteProviderManagersForGlobalCollector();
		}
	}

	/**
	 * Event handler for remote events.
	 */
	private final class EventHandlerRemoteEvents implements HostProviderManager.Listener {
		/**
		 * @see com.ixora.rms.providers.HostProviderManager.Listener#providerStateChanged(com.ixora.rms.providers.ProviderId, com.ixora.rms.internal.providers.ProviderState, java.lang.Throwable)
		 */
		public void providerStateChanged(ProviderId provider, ProviderState state, Throwable err) {
			handleProviderStateChanged(provider, state, err);
		}

		/**
		 * @see com.ixora.rms.providers.HostProviderManager.Listener#data(com.ixora.rms.providers.ProviderDataBuffer[])
		 */
		public void data(ProviderDataBuffer[] buff) {
			handleProviderDataAsRemoteEvent(buff);
		}
	}

	/**
	 * Constructor.
	 * @param prs
	 * @param hm
	 * @throws StartableError
	 * @throws RemoteException
	 * @throws UnknownHostException
	 */
	public ProvidersManager(ProviderRepositoryService prs, HostMonitor hm) throws RemoteException, Throwable, UnknownHostException {
		super();
		if(prs == null) {
			throw new IllegalArgumentException("null provider repository");
		}
		if(hm == null) {
			throw new IllegalArgumentException("null host monitor");
		}
		this.fProviderRepository = prs;
		this.fHostMonitor = hm;
		this.fTimeDeltaCache = new TimeDeltaCache(hm);
		this.fHostManagers = new HashMap<HostId, HostHandler>();
		this.fEventHandler = new EventHandler();
		this.fEventHandlerRemoteEvents = new EventHandlerRemoteEvents();

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
        this.fRemoteEventHandler = new RemoteProviderManagerEventHandler(csf, ssf, fEventHandlerRemoteEvents);

		this.fListeners = new LinkedList<Listener>();

        this.fHostMonitor.addListener(fEventHandler);
		ConfigurationMgr.get(RMSComponent.NAME).addObserver(fEventHandler);
		fEventDispatcher = new RunQueue(true);
		fEventDispatcher.start();
	}

	/**
	 * Completes the provider's configuration.
	 * @param conf
	 * @param host
	 * @param jars
	 */
	private void completeProviderConfiguration(ProviderConfiguration conf, String host) {
		if(conf.isGlobalSamplingInterval()) {
			conf.setSamplingInterval(ConfigurationMgr.getInt(
					RMSComponent.NAME,
					RMSConfigurationConstants.AGENT_CONFIGURATION_SAMPLING_INERVAL));
		}
		conf.setHost(host);
	}

	/**
	 * Handles the new data buffer events coming from providers running
	 * on remote machines.
	 * @param buff
	 */
	private void handleProviderDataAsRemoteEvent(ProviderDataBuffer[] buffs) {
		// fix timestamp differences
		for(int i = 0; i < buffs.length; i++) {
			ProviderDataBuffer db = buffs[i];
			long delta = fTimeDeltaCache.getDelta(db.getHost().toString());
			if(delta != 0) {
				// apply to data buffer
				db.setTimeDelta(delta);
			}
		}
		handleProviderData(buffs);
	}

	/**
	 * @param host
	 * @param serviceID
	 * @param state
	 */
	private void handleHostStateChanged(String host, int serviceID, ServiceState state) {
		if(serviceID == HostReachability.HOST_MANAGER && state == ServiceState.OFFLINE) {
			HostHandler hh = this.fHostManagers.get(new HostId(host));
			if(hh == null) {
				return;
			}
			List<ProviderId> pids = hh.getProviders(ProviderLocation.REMOTE);
			hh.hostWentOffline();
			for(ProviderId pid : pids) {
				handleProviderStateChanged(pid, ProviderState.UNKNOWN, null);
			}
		}
	}

	/**
	 * Reconfigures all providers.
	 */
	private void handleDefaultSamplingIntervalChanged() {
		try {
			ProviderConfiguration conf = new ProviderConfiguration();
			conf.setGlobalSamplingInterval(true);
			conf.setSamplingInterval(
				ConfigurationMgr.getInt(
					RMSComponent.NAME,
					AGENT_CONFIGURATION_SAMPLING_INERVAL));
			_configureAllProviders(conf);
		} catch(Throwable e) {
			logger.error(e);
		}
	}

	/**
	 * @param host
	 */
	private void handleAboutToRemoveHost(String host) {
		HostHandler hh;
		synchronized(this) {
			// destroy the remote provider manager
			hh = this.fHostManagers.remove(new HostId(host));
			if(hh == null) {
				return;
			}
		}
		hh.destroyRemoteProviderManager();
	}

	/**
	 * @param provider
	 * @param state
	 * @param err
	 */
	private void handleProviderStateChanged(final ProviderId provider,
			final ProviderState state, final Throwable err) {
		fEventDispatcher.run(new Runnable() {
			public void run() {
				fireProviderStateChanged(provider, state, err);
			}
		});
	}

	/**
	 * @param provider
	 * @param state
	 * @param err
	 */
	private void fireProviderStateChanged(ProviderId provider, ProviderState state, Throwable err) {
		synchronized(this.fListeners) {
			for(Listener listener : this.fListeners) {
				try {
					listener.providerStateChanged(provider, state, err);
				} catch(Exception e) {
					logger.error(e);
				}
			}
		}
	}

	/**
	 * @param buff
	 */
	private void handleProviderDataAsLocalEvent(ProviderDataBuffer[] buff) {
		handleProviderData(buff);
	}

	/**
	 * @param buff
	 */
	private void handleProviderData(final ProviderDataBuffer[] buff) {
		fEventDispatcher.run(new Runnable() {
			public void run() {
				fireProviderData(buff);
			}
		});
	}

	/**
	 * @param buff
	 */
	private void fireProviderData(ProviderDataBuffer[] buff) {
		synchronized(this.fListeners) {
			for(Listener listener : this.fListeners) {
				try {
					listener.data(buff);
				} catch(Exception e) {
					logger.error(e);
				}
			}
		}
	}

	/**
	 * @throws RMSException
	 * @throws RemoteException
	 * @see com.ixora.rms.services.ProvidersManagerService#installProvider(java.lang.String, com.ixora.rms.providers.ProviderDefinition, com.ixora.rms.providers.ProviderConfiguration)
	 */
	public ProviderId installProvider(HostId host, String providerName,
			ProviderConfiguration conf, boolean remote) throws RMSException, RemoteException {
		if(providerName == null || host == null || conf == null) {
			throw new IllegalArgumentException("null parameters");
		}
		RemoteProviderManager rpm = null;
		HostProviderManager hpm = null;
		// make sure just one host handler per host
		// is created
		synchronized (this) {
			HostHandler hh = this.fHostManagers.get(host);

			if(hh == null) {
				hh = new HostHandler(
						fProviderRepository,
						fHostMonitor,
						host,
						this.fEventHandler,
						this.fRemoteEventHandler);
				this.fHostManagers.put(host, hh);
			}
			if(remote) {
				rpm = hh.createRemoteProviderManager();
				if(hh.hostNeedsGlobalCollector()) {
					if(fGlobalCollector == null) {
						fGlobalCollector = new GlobalCollector(fEventHandler, fEventHandler);
						fGlobalCollector.setCollectionInterval(1000 * conf.getSamplingInterval());
						fGlobalCollector.startCollector();
					}
				}
			} else {
				hpm = hh.createLocalProviderManager();
			}
			// remote
			if(rpm != null) {
				ProviderId ret = rpm.activateProvider(providerName, conf);
				hh.registerProvider(ret, ProviderLocation.REMOTE);
				return ret;
			}
			// local
			ProviderId ret = hpm.activateProvider(providerName, conf);
			hh.registerProvider(ret, ProviderLocation.LOCAL);
			return ret;
		}
	}

	/**
	 * @throws InvalidProviderConfiguration
	 * @throws ProviderNotActivated
	 * @throws RMSException
	 * @throws RemoteException
	 * @see com.ixora.rms.services.ProvidersManagerService#configureProvider(com.ixora.rms.providers.ProviderId, com.ixora.rms.providers.ProviderConfiguration)
	 */
	public void configureProvider(ProviderId provider,
			ProviderConfiguration conf) throws InvalidProviderConfiguration, ProviderNotActivated, RemoteException, RMSException {
		synchronized(this) {
			HostHandler hh = this.fHostManagers.get(provider.getHost());
			if(hh == null || !hh.isProviderRegistered(provider)) {
				throw new ProviderNotActivated();
			}
			completeProviderConfiguration(conf, provider.getHost().toString());
			// reconfigure global collector
			if(fGlobalCollector != null) {
				fGlobalCollector.recalibrateCollector(1000 * conf.getSamplingInterval());
			}
			// check first if the provider is local
			HostProviderManager hpm = hh.getLocalProviderManager();
			if(hpm != null) {
				if(hpm.getProviderState(provider) != null) {
					hpm.configureProvider(provider, conf);
					return;
				}
			}
			// the provider might be remote
			RemoteProviderManager rpm = hh.getRemoteProviderManager();
			if(rpm == null) {
				throw new ProviderNotActivated();
			}
			rpm.configureProvider(provider, conf);
		}
	}

	/**
	 * @throws InvalidProviderConfiguration
	 * @throws RMSException
	 * @throws RemoteException
	 * @see com.ixora.rms.services.ProvidersManagerService#configureAllProviders(com.ixora.rms.providers.ProviderConfiguration)
	 */
	public void configureAllProviders(ProviderConfiguration conf)
			throws InvalidProviderConfiguration, RemoteException, RMSException {
		synchronized (this) {
			_configureAllProviders(conf);
		}
	}

	/**
	 * @throws InvalidProviderConfiguration
	 * @throws RMSException
	 * @throws RemoteException
	 */
	private void _configureAllProviders(ProviderConfiguration conf)
			throws InvalidProviderConfiguration, RemoteException, RMSException {
		conf.setGlobalSamplingInterval(true);
		for(HostHandler hh : this.fHostManagers.values()) {
			completeProviderConfiguration(conf, hh.getHost());
			// reconfigure global collector
			if(fGlobalCollector != null) {
				fGlobalCollector.recalibrateCollector(1000 * conf.getSamplingInterval());
			}
			HostProviderManager hpm = hh.getLocalProviderManager();
			if(hpm != null) {
				hpm.configureAllProviders(conf);
			}
			RemoteProviderManager rpm = hh.getRemoteProviderManager();
			if(rpm != null) {
				rpm.configureAllProviders(conf);
			}
		}
	}

	/**
	 * @return
	 */
	private List<RemoteProviderManager> getRemoteProviderManagersForGlobalCollector() {
		synchronized(this) {
			List<RemoteProviderManager> ret = new LinkedList<RemoteProviderManager>();
			for(HostHandler hh : fHostManagers.values()) {
				if(hh.hostNeedsGlobalCollector()) {
					ret.add(hh.getRemoteProviderManager());
				}
			}
			return ret;
		}
	}

	/**
	 * @throws RMSException
	 * @throws StartableError
	 * @throws RemoteException
	 * @throws ProviderNotActivated
	 * @see com.ixora.rms.services.ProvidersManagerService#startProvider(com.ixora.rms.providers.ProviderId)
	 */
	public void startProvider(ProviderId pid) throws RMSException, ProviderNotActivated, RemoteException {
		synchronized (this) {
			HostHandler hh = this.fHostManagers.get(pid.getHost());
			if(hh == null  || !hh.isProviderRegistered(pid)) {
				throw new ProviderNotActivated();
			}
			// check first if the provider is local
			HostProviderManager hpm = hh.getLocalProviderManager();
			if(hpm != null) {
				if(hpm.getProviderState(pid) != null) {
					try {
						hpm.startProvider(pid);
					} catch(RMSException e) {
						throw e;
					} catch (Throwable e) {
						throw new RMSException(e);
					}
					return;
				}
			}
			// the provider might be remote
			RemoteProviderManager rpm = hh.getRemoteProviderManager();
			if(rpm == null) {
				throw new ProviderNotActivated();
			}
			rpm.startProvider(pid);
		}
	}

	/**
	 * @throws RMSException
	 * @throws RemoteException
	 * @throws ProviderNotActivated
	 * @see com.ixora.rms.services.ProvidersManagerService#stopProvider(com.ixora.rms.providers.ProviderId)
	 */
	public void stopProvider(ProviderId pid) throws RMSException, ProviderNotActivated, RemoteException {
		synchronized (this) {
			HostHandler hh = this.fHostManagers.get(pid.getHost());
			if(hh == null  || !hh.isProviderRegistered(pid)) {
				throw new ProviderNotActivated();
			}
			// check first if the provider is local
			HostProviderManager hpm = hh.getLocalProviderManager();
			if(hpm != null) {
				if(hpm.getProviderState(pid) != null) {
					try {
						hpm.stopProvider(pid);
					} catch(RMSException e) {
						throw e;
					} catch(Throwable e) {
						throw new RMSException(e);
					}
					return;
				}
			}
			// the provider might be remote
			RemoteProviderManager rpm = hh.getRemoteProviderManager();
			if(rpm == null) {
				throw new ProviderNotActivated();
			}
			rpm.stopProvider(pid);
		}
	}

	/**
	 * @throws RemoteException
	 * @throws RMSException
	 * @see com.ixora.rms.services.ProvidersManagerService#getProviderState(com.ixora.rms.providers.ProviderId)
	 */
	public ProviderState getProviderState(ProviderId providerId) throws RemoteException, RMSException {
		synchronized (this) {
			HostHandler hh = this.fHostManagers.get(providerId.getHost());
			if(hh == null  || !hh.isProviderRegistered(providerId)) {
				throw new ProviderNotActivated();
			}
			// check first if the provider is local
			HostProviderManager hpm = hh.getLocalProviderManager();
			if(hpm != null) {
				ProviderState state = hpm.getProviderState(providerId);
				if(state != null) {
					return state;
				}
			}
			// the provider might be remote
			RemoteProviderManager rpm = hh.getRemoteProviderManager();
			if(rpm == null) {
				throw new ProviderNotActivated();
			}
			ProviderState state = rpm.getProviderState(providerId);
			if(state == null) {
				throw new ProviderNotActivated();
			}
			return state;
		}
	}

	/**
	 * @throws ProviderNotActivated
	 * @throws RMSException
	 * @throws RemoteException
	 * @see com.ixora.rms.services.ProvidersManagerService#uninstallProvider(com.ixora.rms.providers.ProviderId)
	 */
	public void uninstallProvider(ProviderId pid) throws ProviderNotActivated, RemoteException, RMSException {
		synchronized (this) {
			HostHandler hh = this.fHostManagers.get(pid.getHost());
			if(hh == null || !hh.isProviderRegistered(pid)) {
				throw new ProviderNotActivated();
			}
			// check first if the provider is local
			HostProviderManager hpm = hh.getLocalProviderManager();
			if(hpm != null) {
				if(hpm.getProviderState(pid) != null) {
					hpm.deactivateProvider(pid);
					hh.unregisterProvider(pid);
					return;
				}
			}
			// the provider might be remote
			RemoteProviderManager rpm = hh.getRemoteProviderManager();
			if(rpm == null) {
				throw new ProviderNotActivated();
			}
			rpm.deactivateProvider(pid);
			hh.unregisterProvider(pid);
		}
	}

	/**
	 * @see com.ixora.rms.services.ProvidersManagerService#uninstallAllProvidersOnHost(com.ixora.rms.internal.HostId)
	 */
	public void uninstallAllProvidersOnHost(HostId host) {
		// TODO IMPLEMENT
	}

	/**
	 * @see com.ixora.rms.services.ProvidersManagerService#uninstallAllProviders()
	 */
	public void uninstallAllProviders() {
		synchronized (this) {
			for(HostHandler hh : this.fHostManagers.values()) {
				try {
					HostProviderManager hpm = hh.getLocalProviderManager();
					if(hpm != null) {
                        try {
                            hpm.deactivateAllProviders();
                        } catch(Throwable e) {
                            logger.error(e);
                        }
					}
					RemoteProviderManager rpm = hh.getRemoteProviderManager();
					if(rpm != null) {
						rpm.deactivateAllProviders();
					}
					hh.unregisterAllProviders();
				} catch(Exception e) {
					// log and keep going
					logger.error(e);
				}
			}
		}
	}

	/**
	 * @see com.ixora.common.Service#shutdown()
	 */
	public void shutdown() {
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
						hh.destroyRemoteProviderManager();
					} catch(Exception e) {
						logger.error("Failed to shutdown the RemoteProviderManager on host " + hh.getHost(), e);
					}
				}
			}
		} catch(Exception e) {
			logger.error(e);
		}
	}

	/**
	 * @see com.ixora.rms.services.ProvidersManagerService#addListener(com.ixora.rms.services.ProvidersManagerService.Listener)
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
	 * @see com.ixora.rms.services.ProvidersManagerService#removeListener(com.ixora.rms.services.ProvidersManagerService.Listener)
	 */
	public void removeListener(Listener listener) {
		synchronized(this.fListeners) {
			this.fListeners.remove(listener);
		}
	}
}
