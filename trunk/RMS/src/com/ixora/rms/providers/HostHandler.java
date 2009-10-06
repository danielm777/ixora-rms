package com.ixora.rms.providers;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ixora.rms.HostId;
import com.ixora.rms.HostMonitor;
import com.ixora.rms.HostRemoteManagedHandler;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.remote.providers.RemoteProviderManager;
import com.ixora.rms.remote.providers.RemoteProviderManagerImpl;
import com.ixora.rms.remote.providers.RemoteProviderManagerListener;
import com.ixora.rms.services.ProviderRepositoryService;

/**
 * HostHandlers(remote and local).
 */
final class HostHandler extends HostRemoteManagedHandler {
	static final class ProviderData {
		ProviderLocation location;
		ProviderData(ProviderLocation location) {
			this.location = location;
		}
	}
	/** Manager for the local providers */
	private HostProviderManager fHostProviderManager;
	/** Registered providers */
	private Map<ProviderId, HostHandler.ProviderData> fProviderData;
	/**
	 * HostHandlers.
	 * @param prs
	 * @param hm
	 * @param host host to handle
	 * @param listenerLocal HostProviderManager listener
	 * @param listenerRemote RemoteProviderManager listener
	 */
	public HostHandler(
			ProviderRepositoryService prs,
			HostMonitor hm,
			HostId host,
			HostProviderManager.Listener listenerLocalProvider,
			RemoteProviderManagerListener listenerRemoteProvider) {
		super(hm, host.toString(), listenerRemoteProvider);
		if(prs == null) {
			throw new IllegalArgumentException("null provider repository");
		}
		if(listenerLocalProvider == null) {
			throw new IllegalArgumentException("null local provider listener");
		}
		// create the local manager immediately and use
		// lazy initialization for the remote one
		this.fHostProviderManager = new HostProviderManagerImpl(prs, host, listenerLocalProvider);
		this.fProviderData = new HashMap<ProviderId, HostHandler.ProviderData>();
	}
	/**
	 * @return the HostProviderManager for this host.
	 */
	public HostProviderManager createLocalProviderManager() {
		return fHostProviderManager;
	}
	/**
	 * @return the HostAgentManager for this host.
	 */
	public HostProviderManager getLocalProviderManager() {
		return fHostProviderManager;
	}
	/**
	 * @return a remote provider manager reference
	 * @throws RMSException
	 * @throws RemoteException
	 */
	public RemoteProviderManager createRemoteProviderManager() throws RMSException, RemoteException {
		return (RemoteProviderManager)createRemoteManaged(RemoteProviderManagerImpl.class.getName());
	}

	/**
	 *
	 */
	public void destroyRemoteProviderManager() {
		destroyRemoteManaged();
	}

	/**
	 * @return the RemoteProviderManager for this host.
	 */
	public RemoteProviderManager getRemoteProviderManager() {
		return (RemoteProviderManager)this.fRemoteManaged;
	}

	/**
	 * Registers an agent as being deployed according to <code>location</code>.
	 * @param pid
	 * @param location
	 */
	public synchronized void registerProvider(ProviderId pid, ProviderLocation location) {
		this.fProviderData.put(pid, new ProviderData(location));
	}

	/**
	 * Unregisters a provider as being deployed remotely.
	 * @param id
	 */
	public synchronized void unregisterProvider(ProviderId pid) {
		this.fProviderData.remove(pid);
	}

	/**
	 * Unregisters all agents.
	 */
	public synchronized void unregisterAllProviders() {
		this.fProviderData.clear();
	}

	/**
	 * @return the list of agents that have been deployed
	 * according to <code>location</code>
	 */
	// this is why synch is necessary on this class,
	// this method is called from MonitoringSession when
	// a HostMonitor event is trigerred
	public synchronized List<ProviderId> getProviders(ProviderLocation location) {
		List<ProviderId> ret = new LinkedList<ProviderId>();
		for(Map.Entry<ProviderId, HostHandler.ProviderData> me : this.fProviderData.entrySet()) {
			HostHandler.ProviderData pd = me.getValue();
			if((pd.location == location)) {
				ret.add(me.getKey());
			}
		}
		return ret;
	}

	/**
	 * @param providerId
	 * @return true if the given provider has already been
	 * installed on this host
	 */
	public synchronized boolean isProviderRegistered(ProviderId pid) {
		if(this.fProviderData.get(pid) != null) {
			return true;
		}
		return false;
	}
}