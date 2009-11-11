/*
 * Created on 28-Dec-2004
 */
package com.ixora.rms.providers;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ixora.rms.Collector;
import com.ixora.rms.HostId;
import com.ixora.common.exception.AppException;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.plugin.ClassLoadingHelper;
import com.ixora.common.utils.Utils;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.providers.exception.InvalidProviderConfiguration;
import com.ixora.rms.providers.exception.ProviderNotActivated;
import com.ixora.rms.providers.exception.ProviderNotInstalled;
import com.ixora.rms.repository.ProviderInstallationData;
import com.ixora.rms.services.ProviderRepositoryService;

/**
 * @author Daniel Moraru
 */
public final class HostProviderManagerImpl implements HostProviderManager {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(HostProviderManagerImpl.class);
	/** The id of the host as passed to the constructor */
	private HostId fHost;
	/** Provider data */
	private Map<ProviderId, ProviderData> fProviders;
	/**
	 * List of data buffers to send at the end of the
	 * collection cycle.
	 */
	private List<ProviderDataBuffer> fDataBuffers;
	/** Listener */
	private Listener fListener;
	/** Event handler */
	private EventHandler fEventHandler;
	/** Collector thread */
	private Collector fCollector;
	/** Provider repository */
	private ProviderRepositoryService fProviderRepository;

	/**
	 * Set of activated providers that have their own collectors.
	 * Required to decide whether to send an data buffer directly or
	 * add it to the list and send it at the end of the collection cycle.
	 */
	private Set<ProviderId> fProvidersWithPrivateCollectors;
	/**
	 * Class loading helpler.
	 */
	private ClassLoadingHelper fClassLoadingHelper;

	/**
	 * Event handler.
	 */
	private final class EventHandler implements Provider.Listener {
		/**
		 * @see com.ixora.rms.providers.Provider.Listener#error(com.ixora.rms.providers.ProviderId, java.lang.String, java.lang.Throwable)
		 */
		public void error(ProviderId providerId, String msg, Throwable t) {
			handleProviderError(providerId, msg, t);
		}
		/**
		 * @see com.ixora.rms.providers.Provider.Listener#data(com.ixora.rms.providers.ProviderDataBuffer)
		 */
		public void data(ProviderDataBuffer buff) {
			handleProviderData(buff);
		}
	}

	/**
	 * Provider data.
	 */
	private static final class ProviderData {
		/** Provider id */
		ProviderId providerId;
		/** Seconds left untile next data collection cycle */
		int tick;
		/** Managed provider */
		Provider provider;
		/** Provider state */
		ProviderState state;
		/**
		 * Whether or not this provider is using the global
		 * settings (like sampling interval and monitoring level).
		 */
		boolean usingGlobalSettings;

		ProviderData(ProviderId pid, int tick, boolean usingGlobalSettings,
				Provider p, ProviderState state) {
			this.providerId = pid;
			this.tick = tick;
			this.usingGlobalSettings = usingGlobalSettings;
			this.provider = p;
			this.state = state;
		}
	}

	/**
	 * Constructor.
	 * @param host
	 * @param listener
	 */
	public HostProviderManagerImpl(ProviderRepositoryService pr, HostId host, Listener listener) {
		super();
		if(pr == null) {
			throw new IllegalArgumentException("null provider repository");
		}
		if(host == null) {
			throw new IllegalArgumentException("null host id");
		}
		if(listener == null) {
			throw new IllegalArgumentException("null provider manager listener");
		}
		this.fProviderRepository = pr;
		this.fHost = host;
		this.fListener = listener;
		this.fProviders = new HashMap<ProviderId, ProviderData>();
		this.fDataBuffers = new LinkedList<ProviderDataBuffer>();
		this.fEventHandler = new EventHandler();
		this.fProvidersWithPrivateCollectors = Collections.synchronizedSet(new HashSet<ProviderId>());
		this.fClassLoadingHelper = new ClassLoadingHelper();
		this.fCollector = new Collector("ProviderDataCollector") {
			protected void collect() {
				HostProviderManagerImpl.this.collectCycle();
			}
		};

	}

	/**
	 * @see com.ixora.rms.providers.ProviderManager#activateProvider(String, com.ixora.rms.providers.ProviderConfiguration)
	 */
	public ProviderId activateProvider(String providerName, ProviderConfiguration conf) throws ProviderNotInstalled, InvalidProviderConfiguration, RMSException {
		if(providerName == null || conf == null) {
			throw new IllegalArgumentException("null params");
		}

		ProviderInstallationData providerInstallationData = this.fProviderRepository.getInstalledProviders().get(providerName);
		if(providerInstallationData == null) {
			throw new ProviderNotInstalled(providerName);
		}

		ProviderId ret = new ProviderId(fHost, Utils.getRandomInt());
		synchronized(this.fProviders) {
			boolean error = false;
			Provider provider = null;
			try {
				Class<?> clazz = fClassLoadingHelper.getClass(
						ret.toString(),
						providerInstallationData.getProviderClass(),
						providerInstallationData.getJars(), conf.getCustom());
			    provider = (Provider)clazz.newInstance();
			    provider.setId(ret);
				provider.setListener(this.fEventHandler);
				provider.configure(conf);
				int si = conf.getSamplingInterval().intValue();
				ProviderData pd =
					new ProviderData(
							ret,
							si,
							conf.isGlobalSamplingInterval(),
							provider,
							ProviderState.READY);
				this.fProviders.put(ret, pd);
				if(pd.provider.requiresExternalCollector()) {
					fCollector.recalibrateCollector(1000 * si);
				} else {
					fProvidersWithPrivateCollectors.add(ret);
				}
				fireProviderStateChanged(ret, ProviderState.READY);
				return ret;
			} catch(InvalidProviderConfiguration e) {
				error = true;
				throw e;
			} catch(RMSException e) {
				// this should be a provider specific logic exception
				error = true;
				throw e;
			} catch(AppException e) {
				// all infrastructure exceptions should have localized messages
				error = true;
				throw new RMSException(e.getLocalizedMessage());
			} catch(Throwable e) {
				// this should mark an internal/unexpected provider error
				error = true;
				RMSException ex = new RMSException(e);
				ex.setIsInternalAppError();
				throw ex;
			} finally {
				// this will undo any changes to the thread's context
				fClassLoadingHelper.prepareThreadOnExit();
				if(error) {
					// rollback any changes
					if(provider != null) {
						try {
							fProviders.remove(ret);
							provider.cleanup();
						} catch (Throwable t) {
							logger.error(t);
						}
					}
				}
			}
		}
	}

	/**
	 * @throws RMSException
	 * @see com.ixora.rms.providers.ProviderManager#configureProvider(com.ixora.rms.providers.ProviderId, com.ixora.rms.providers.ProviderConfiguration)
	 */
	public void configureProvider(ProviderId provider, ProviderConfiguration conf) throws RMSException {
		synchronized(this.fProviders) {
			ProviderData pd = this.fProviders.get(provider);
			if(pd == null) {
				// provider has not been activated
				throw new ProviderNotActivated();
			}
			try {
				fClassLoadingHelper.prepareThreadOnEnter(pd.providerId.toString());
				pd.provider.configure(conf);
				// cache this piece of information
				// as it is used to select providers
				// that are using global configuration
				// settings during a global configuration
				// change broadcast
				// @see configureAllProviders()
				pd.usingGlobalSettings = pd.provider.getConfiguration().isGlobalSamplingInterval();
				// conf can be a delta value so check
				// if the sampling interval is valid
				if(pd.provider.requiresExternalCollector() && conf.hasValidSamplingInterval()) {
					int si = conf.getSamplingInterval().intValue();
					pd.tick = si;
					fCollector.recalibrateCollector(1000 * si);
				}
			} catch(InvalidProviderConfiguration e) {
				throw e;
			} catch(RMSException e) {
				// this should be an provider specific logic exception
				throw e;
			} catch(Throwable e) {
				// this should mark an internal/unexpected provider error
				RMSException ex = new RMSException(e);
				ex.setIsInternalAppError();
				throw ex;
			} finally {
				fClassLoadingHelper.prepareThreadOnExit();
			}
		}
	}

	/**
	 * @throws ProviderNotActivated
	 * @see com.ixora.rms.providers.ProviderManager#startProvider(com.ixora.rms.providers.ProviderId)
	 */
	public void startProvider(ProviderId pid) throws RMSException, ProviderNotActivated {
		synchronized(this.fProviders) {
			ProviderData pd = this.fProviders.get(pid);
			if(pd == null) {
				// provider has not been activated
				throw new ProviderNotActivated();
			}
			try {
				fClassLoadingHelper.prepareThreadOnEnter(pd.providerId.toString());
				pd.provider.start();
				pd.state = ProviderState.STARTED;
				fireProviderStateChanged(pid, ProviderState.STARTED);
				if(pd.provider.requiresExternalCollector()) {
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
	 * @throws ProviderNotActivated
	 * @see com.ixora.rms.providers.ProviderManager#stopProvider(com.ixora.rms.providers.ProviderId)
	 */
	public void stopProvider(ProviderId pid) throws RMSException, ProviderNotActivated {
		synchronized(this.fProviders) {
			ProviderData pd = this.fProviders.get(pid);
			if(pd == null) {
				// provider has not been installed
				throw new ProviderNotActivated();
			}
			pd.state = ProviderState.STOPPED;
			try {
				fClassLoadingHelper.prepareThreadOnEnter(pd.providerId.toString());
				pd.provider.stop();
				fireProviderStateChanged(pid, ProviderState.STOPPED);
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
	 * @throws RMSException
	 * @see com.ixora.rms.providers.ProviderManager#deactivateProvider(com.ixora.rms.providers.ProviderId)
	 */
	public void deactivateProvider(ProviderId pid) throws RMSException {
		synchronized(this.fProviders) {
			ProviderData pd = this.fProviders.get(pid);
			if(pd == null) {
				throw new ProviderNotActivated();
			}
			deactivateProvider(pid, pd, true);
		}
	}

	/**
	 * @see com.ixora.rms.providers.ProviderManager#getProviderState(com.ixora.rms.providers.ProviderId)
	 */
	public ProviderState getProviderState(ProviderId providerId) {
		synchronized(this.fProviders) {
			ProviderData pd = this.fProviders.get(providerId);
			return pd == null ? null : pd.state;
		}
	}

	/**
	 * @see com.ixora.rms.providers.HostProviderManager#configureAllProviders(com.ixora.rms.providers.ProviderConfiguration)
	 */
	public void configureAllProviders(ProviderConfiguration conf) throws InvalidProviderConfiguration, RMSException {
		try {
			synchronized(this.fProviders) {
				int si = conf.getSamplingInterval().intValue();
				boolean recalibrate = false;
				for(ProviderData pd : this.fProviders.values()) {
					if(pd.usingGlobalSettings) {
						try {
							fClassLoadingHelper.prepareThreadOnEnter(pd.providerId.toString());
							pd.provider.configure(conf);
							// conf can be a delta value so check
							// if the sampling interval is valid
							if(pd.provider.requiresExternalCollector() && conf.hasValidSamplingInterval()) {
								pd.tick = si;
								recalibrate = true;
							}
						} finally {
							fClassLoadingHelper.prepareThreadOnExit();
						}
					}
				}
				if(recalibrate) {
					fCollector.recalibrateCollector(1000 * si);
				}
			}
		} catch(InvalidProviderConfiguration e) {
			throw e;
		} catch(RMSException e) {
			// this should be a provider specific logic exception
			throw e;
		} catch(Throwable e) {
			// this should mark an internal/unexpected provider error
			RMSException ex = new RMSException(e);
			ex.setIsInternalAppError();
			throw ex;
		}
	}

	/**
	 * @see com.ixora.rms.providers.HostProviderManager#deactivateAllProviders()
	 */
	public void deactivateAllProviders() {
		synchronized(this.fProviders) {
			for (Iterator<Map.Entry<ProviderId, ProviderData>> iter = this.fProviders.entrySet().iterator(); iter.hasNext();) {
				Map.Entry<ProviderId, ProviderData> entry = iter.next();
				ProviderData pd = entry.getValue();
				ProviderId providerId = entry.getKey();
				try {
					fClassLoadingHelper.prepareThreadOnEnter(pd.providerId.toString());
					deactivateProvider(providerId, pd, false);
				} catch(Throwable e) {
					logger.error(e);
				} finally {
					fClassLoadingHelper.prepareThreadOnExit();
					iter.remove();
				}
			}
		}
	}

	/**
	 * Runs a collection cycle.
	 */
	private void collectCycle() {
		cleanDataBufferList();
		synchronized(this.fProviders) {
			boolean exit = true;
			for(ProviderData pd : this.fProviders.values()) {
				try {
					fClassLoadingHelper.prepareThreadOnEnter(pd.providerId.toString());
					if(pd.provider.requiresExternalCollector() && pd.state == ProviderState.STARTED) {
						exit = false;
						pd.tick -= fCollector.getCollectionInterval()/1000;
						if(pd.tick == 0) {
							// reset tick
							pd.tick = pd.provider.getConfiguration().getSamplingInterval().intValue();
							try {
								pd.provider.collectData();
							} catch(Throwable e) {
								pd.state = ProviderState.ERROR;
								fireProviderErrorState(pd.providerId, e);
							}
						}
					}
				} finally {
					fClassLoadingHelper.prepareThreadOnExit();
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
	 * Fires the provider error event.
	 * @param providerId
	 * @param e
	 */
	private void fireProviderErrorState(ProviderId providerId, Throwable e) {
		try {
			this.fListener.providerStateChanged(providerId,
				ProviderState.ERROR, e);
		} catch(Throwable t) {
			logger.error(t);
		}
	}

	/**
	 * Fires the provider state change event.
	 * @param providerId
	 * @param state
	 */
	private void fireProviderStateChanged(ProviderId providerId, ProviderState state) {
		try {
			this.fListener.providerStateChanged(providerId, state, null);
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
					this.fListener.data(
						this.fDataBuffers.toArray(
							new ProviderDataBuffer[this.fDataBuffers.size()]));
				} catch(Throwable t) {
					logger.error(t);
				}
			}
		}
	}

	/**
	 * Helper method that deactivates a provider.
	 * @param providerId
	 * @param pd
	 * @param remove
	 * @throws RMSException
	 */
	private void deactivateProvider(ProviderId providerId, ProviderData pd, boolean remove) throws RMSException {
		try {
			fClassLoadingHelper.prepareThreadOnEnter(pd.providerId.toString());
			try {
				pd.provider.stop();
				pd.state = ProviderState.STOPPED;
				fireProviderStateChanged(providerId, ProviderState.STOPPED);
			} catch(Throwable e) {
				pd.state = ProviderState.ERROR;
				fireProviderErrorState(providerId, e);
			}

			// and unistall it
			try {
				pd.provider.cleanup();
				if(remove) {
					this.fProviders.remove(providerId);
				}
				this.fProvidersWithPrivateCollectors.remove(providerId);
				pd.state = ProviderState.UNINSTALLED;
				fireProviderStateChanged(providerId, ProviderState.UNINSTALLED);
			} catch(Throwable e) {
				pd.state = ProviderState.ERROR;
				fireProviderErrorState(providerId, e);
				if(e instanceof RMSException) {
					throw (RMSException)e;
				}
				throw new RMSException(e);
			}
		} finally {
			fClassLoadingHelper.prepareThreadOnExit();
		}
	}

	/**
	 * @param providerId
	 * @param msg
	 * @param e
	 */
	private void handleProviderError(ProviderId providerId, String msg, Throwable e) {
		try {
			fListener.providerStateChanged(providerId, ProviderState.ERROR, e);
		} catch(Throwable t) {
			logger.error(t);
		}
	}

	/**
	 * @param buff
	 */
	private void handleProviderData(ProviderDataBuffer buff) {
		try {
			buff.setHost(fHost);
			if(this.fProvidersWithPrivateCollectors.contains(buff.getProviderId())) {
				// independent collector, send data now
				this.fListener.data(new ProviderDataBuffer[] {buff});
			} else {
				// global collector buffer, it will be sent at the end
				// of the collection cycle
				synchronized(this.fDataBuffers) {
					this.fDataBuffers.add(buff);
				}
			}
		} catch(Throwable t) {
			logger.error(t);
		}
	}
}
