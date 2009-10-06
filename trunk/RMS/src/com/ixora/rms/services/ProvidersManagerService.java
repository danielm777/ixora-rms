/*
 * Created on 29-Dec-2004
 */
package com.ixora.rms.services;

import java.rmi.RemoteException;

import com.ixora.rms.HostId;
import com.ixora.common.Service;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.providers.ProviderConfiguration;
import com.ixora.rms.providers.ProviderDataBuffer;
import com.ixora.rms.providers.ProviderId;
import com.ixora.rms.providers.ProviderState;
import com.ixora.rms.providers.exception.InvalidProviderConfiguration;
import com.ixora.rms.providers.exception.ProviderNotActivated;

/**
 * @author Daniel Moraru
 */
public interface ProvidersManagerService extends Service {
	/**
	 * Listener.
	 */
	public interface Listener {
		/**
		 * Invoked when the state of a provider has changed.
		 * @param provider
		 * @param state
		 * @param err migh be not null if <code>state</code> is ERROR
		 */
		void providerStateChanged(ProviderId provider, ProviderState state, Throwable err);
		/**
		 * Invoked when provided data becomes available.
		 * @param buff
		 */
		void data(ProviderDataBuffer[] buff);
	}

	/**
	 * @param listener
	 */
	void addListener(Listener listener);

	/**
	 * @param listener
	 */
	void removeListener(Listener listener);

	/**
	 * Installs a provider to generate data for the given host and agent.
	 * @param host
	 * @param provider
	 * @param conf
	 * @param remote
	 * @return
	 * @throws InvalidProviderConfiguration
	 * @throws RMSException
	 * @throws RemoteException
	 */
	ProviderId installProvider(HostId host, String provider, ProviderConfiguration conf, boolean remote) throws InvalidProviderConfiguration, RMSException, RemoteException;
	/**
	 * Configures the given provider.
	 * @param provider
	 * @param conf
	 * @throws InvalidProviderConfiguration
	 * @throws ProviderNotActivated
	 * @throws RMSException
	 * @throws RemoteException
	 */
	void configureProvider(ProviderId provider, ProviderConfiguration conf)
		throws InvalidProviderConfiguration, ProviderNotActivated, RMSException, RemoteException;
	/**
	 * Applies the given configuration to all providers.
	 * @param conf
	 * @throws InvalidProviderConfiguration
	 * @throws RMSException
	 * @throws RemoteException
	 */
	void configureAllProviders(ProviderConfiguration conf) throws InvalidProviderConfiguration, RMSException, RemoteException;
	/**
	 * Starts the provider with the given id.
	 * @param pid
	 * @throws ProviderNotActivated
	 * @throws RMSException
	 * @throws RemoteException
	 */
	void startProvider(ProviderId pid) throws ProviderNotActivated, RMSException, RemoteException;
	/**
	 * Stops the provider with the given id.
	 * @param pid
	 * @throws ProviderNotActivated
	 * @throws RMSException
	 * @throws RemoteException
	 */
	void stopProvider(ProviderId pid) throws ProviderNotActivated, RMSException, RemoteException;
	/**
	 * @return the state of the given agent, null if the
	 * agent is not found
	 * @param agentId
	 * @throws ProviderNotActivated
	 * @throws RemoteException
	 * @throws RMSException
	 */
	ProviderState getProviderState(ProviderId providerId) throws ProviderNotActivated, RemoteException, RMSException;
	/**
	 * Uninstalls the provider with the given id.
	 * @param pid
	 * @throws ProviderNotActivated
	 * @throws RMSException
	 * @throws RemoteException
	 */
	void uninstallProvider(ProviderId pid) throws ProviderNotActivated, RMSException, RemoteException;
	/**
	 * Uninstalls all providers on the given host.
	 */
	void uninstallAllProvidersOnHost(HostId host);
	/**
	 * Uninstalls all providers.
	 */
	void uninstallAllProviders();
}
