/*
 * Created on 28-Dec-2004
 */
package com.ixora.rms.remote.providers;

import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.Unreferenced;

import com.ixora.RMIServiceNames;
import com.ixora.remote.RemoteManagedListener;
import com.ixora.remote.exception.RemoteManagedListenerIsUnreachable;
import com.ixora.rms.HostId;
import com.ixora.common.RMIServices;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.providers.HostProviderManager;
import com.ixora.rms.providers.HostProviderManagerImpl;
import com.ixora.rms.providers.ProviderConfiguration;
import com.ixora.rms.providers.ProviderDataBuffer;
import com.ixora.rms.providers.ProviderId;
import com.ixora.rms.providers.ProviderPollBuffer;
import com.ixora.rms.providers.ProviderState;
import com.ixora.rms.providers.exception.InvalidProviderConfiguration;
import com.ixora.rms.providers.exception.ProviderNotActivated;
import com.ixora.rms.repository.ProviderRepositoryManager;
import com.ixora.rms.services.ProviderRepositoryService;

/**
 * @author Daniel Moraru
 */
public final class RemoteProviderManagerImpl
		extends UnicastRemoteObject implements RemoteProviderManager, Unreferenced {
	private static final long serialVersionUID = -3806485367360707316L;
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(RemoteProviderManagerImpl.class);
	/** Agent manager */
	private HostProviderManager fProviderManager;
	/** Listener */
	private RemoteProviderManagerListener fListener;
	/** Event handler */
	private EventHandler fEventHandler;
	/** Provider repository */
	private ProviderRepositoryService fProviderRepository;
    /** True if in the process of shutting down */
	private volatile boolean fShuttingDown;
    /** Used only when the listener is null, otherwise it's null */
	private ProviderPollBufferManager fPollBuffManager;

	/**
	 * Event handler.
	 */
    // event dispatchig will be disable during shutdown
	private final class EventHandler implements HostProviderManager.Listener {
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
			handleProviderData(buff);
		}
	}

	/**
	 * Constructor.
	 * @throws RemoteException
	 * @throws FileNotFoundException
	 * @throws XMLException
	 */
	public RemoteProviderManagerImpl() throws RemoteException, XMLException, FileNotFoundException {
		super(RMIServices.instance().getPort(RMIServiceNames.REMOTEPROVIDERMANAGER));
		// entry point on the remote host for providers,
		// this class will play the part of application container
		// so objects required by all other objects in this application
		// must be initialized by this class (lazy or not)
		this.fProviderRepository = new ProviderRepositoryManager();
	}

	/**
	 * @see com.ixora.rms.remote.providers.RemoteProviderManager#activateProvider(String, com.ixora.rms.providers.ProviderConfiguration)
	 */
	public ProviderId activateProvider(String providerName,
			ProviderConfiguration conf) throws RemoteException,
			InvalidProviderConfiguration, RMSException {
		try {
			return this.fProviderManager.activateProvider(providerName, conf);
		} catch (RMSException e) {
			throw e;
		} catch (Exception e) {
			RMSException ex = new RMSException(e);
			logger.error(ex);
			throw ex;
		}
	}

	/**
	 * @see com.ixora.rms.remote.providers.RemoteProviderManager#configureProvider(com.ixora.rms.providers.ProviderId, com.ixora.rms.providers.ProviderConfiguration)
	 */
	public void configureProvider(ProviderId provider,
			ProviderConfiguration conf) throws RemoteException,
			InvalidProviderConfiguration, ProviderNotActivated, RMSException {
		try {
			this.fProviderManager.configureProvider(provider, conf);
		} catch (RMSException e) {
			throw e;
		} catch (Exception e) {
			RMSException ex = new RMSException(e);
			logger.error(ex);
			throw ex;
		}
	}

	/**
	 * @see com.ixora.rms.remote.providers.RemoteProviderManager#configureAllProviders(com.ixora.rms.providers.ProviderConfiguration)
	 */
	public void configureAllProviders(ProviderConfiguration conf)
			throws RemoteException, InvalidProviderConfiguration, RMSException {
		try {
			this.fProviderManager.configureAllProviders(conf);
		} catch (RMSException e) {
			throw e;
		} catch (Exception e) {
			RMSException ex = new RMSException(e);
			logger.error(ex);
			throw ex;
		}
	}

	/**
	 * @throws RMSException
	 * @see com.ixora.rms.remote.providers.RemoteProviderManager#startProvider(com.ixora.rms.providers.ProviderId)
	 */
	public void startProvider(ProviderId pid) throws RemoteException,
			RMSException {
		try {
			this.fProviderManager.startProvider(pid);
		} catch (RMSException e) {
			throw e;
		} catch (Throwable e) {
			RMSException ex = new RMSException(e);
			logger.error(ex);
			throw ex;
		}
	}

	/**
	 * @throws RMSException
	 * @see com.ixora.rms.remote.providers.RemoteProviderManager#stopProvider(com.ixora.rms.providers.ProviderId)
	 */
	public void stopProvider(ProviderId pid) throws RemoteException, RMSException {
		try {
			this.fProviderManager.stopProvider(pid);
		} catch (RMSException e) {
			throw e;
		} catch (Throwable e) {
			RMSException ex = new RMSException(e);
			logger.error(ex);
			throw ex;
		}
	}

	/**
	 * @throws RMSException
	 * @see com.ixora.rms.remote.providers.RemoteProviderManager#getProviderState(com.ixora.rms.providers.ProviderId)
	 */
	public ProviderState getProviderState(ProviderId providerId)
			throws RemoteException, RMSException {
		try {
			return this.fProviderManager.getProviderState(providerId);
		} catch (Exception e) {
			RMSException ex = new RMSException(e);
			logger.error(ex);
			throw ex;
		}
	}

	/**
	 * @see com.ixora.rms.remote.providers.RemoteProviderManager#deactivateProvider(com.ixora.rms.providers.ProviderId)
	 */
	public void deactivateProvider(ProviderId pid) throws RemoteException,
			ProviderNotActivated, RMSException {
		try {
			this.fProviderManager.deactivateProvider(pid);
		} catch (RMSException e) {
			throw e;
		} catch (Exception e) {
			RMSException ex = new RMSException(e);
			logger.error(ex);
			throw ex;
		}
	}

	/**
	 * @throws RMSException
	 * @see com.ixora.rms.remote.providers.RemoteProviderManager#deactivateAllProviders()
	 */
	public void deactivateAllProviders() throws RemoteException, RMSException {
		try {
			this.fProviderManager.deactivateAllProviders();
		} catch (Exception e) {
			RMSException ex = new RMSException(e);
			logger.error(ex);
			throw ex;
		}
	}

	/**
	 * @see com.ixora.remote.RemoteManaged#initialize(java.lang.String, com.ixora.rms.remote.RemoteManagedListener)
	 */
	public void initialize(String host, RemoteManagedListener listener) throws RMSException, RemoteException {
		if(host == null) {
			throw new IllegalArgumentException("null host name");
		}
		if(listener != null) {
			testRemoteListener(listener);
		} else {
			fPollBuffManager = new ProviderPollBufferManager();
		}
		this.fEventHandler = new EventHandler();
		this.fListener = (RemoteProviderManagerListener)listener;
		this.fProviderManager = new HostProviderManagerImpl(this.fProviderRepository, new HostId(host), this.fEventHandler);
	}

	/**
	 * @see com.ixora.remote.RemoteManaged#shutdown()
	 */
	public void shutdown() throws RMSException, RemoteException {
		_shutdown();
	}

	/**
	 * @return
	 */
	private void testRemoteListener(RemoteManagedListener listener) throws RemoteManagedListenerIsUnreachable {
		if(listener == null) {
			return;
		}
		try {
			((RemoteProviderManagerListener)listener).data(null);
		} catch(Exception e) {
			throw new RemoteManagedListenerIsUnreachable(e);
		}
	}

	/**
	 * @param buff
	 */
	private void handleProviderData(ProviderDataBuffer[] buff) {
		try {
		    if(!fShuttingDown) {
		    	if(fListener != null) {
		    		fListener.data(buff);
		    	} else {
		    		fPollBuffManager.data(buff);
		    	}
		    }
		} catch (RemoteException e) {
			logger.error(e);
		}
	}

	/**
	 * @param provider
	 * @param state
	 * @param err
	 */
	private void handleProviderStateChanged(ProviderId provider, ProviderState state, Throwable err) {
		try {
		    if(!fShuttingDown) {
		    	if(fListener != null) {
		    		fListener.providerStateChanged(provider, state, err);
		    	} else {
		    		fPollBuffManager.providerStateChanged(provider, state, err);
		    	}
		    }
		} catch (RemoteException e) {
			logger.error(e);
		}
	}

	/**
	 * @see com.ixora.rms.remote.providers.RemoteProviderManager#getProviderPollBuffer()
	 */
	public ProviderPollBuffer getProviderPollBuffer() throws RMSException, RemoteException {
		return fPollBuffManager == null ? null : fPollBuffManager.getProviderPullBuffer();
	}

	/**
	 * @see java.rmi.server.Unreferenced#unreferenced()
	 */
	public void unreferenced() {
		try {
			_shutdown();
		} catch(Exception e) {
			logger.error(e);
		}
	}

	/**
	 * @throws RMSException
	 */
	private void _shutdown() throws RMSException {
        fShuttingDown = true;
		this.fProviderManager.deactivateAllProviders();
	}
}
