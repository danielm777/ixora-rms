package com.ixora.rms.remote.providers;

import java.rmi.RemoteException;

import com.ixora.remote.RemoteManaged;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.providers.ProviderConfiguration;
import com.ixora.rms.providers.ProviderId;
import com.ixora.rms.providers.ProviderPollBuffer;
import com.ixora.rms.providers.ProviderState;
import com.ixora.rms.providers.exception.InvalidProviderConfiguration;
import com.ixora.rms.providers.exception.ProviderNotActivated;

/**
 * Manages the creation and provides an execution environment for providers for a host.
 * @author Daniel Moraru
 */
public interface RemoteProviderManager extends RemoteManaged {
	/**
	 * Installs a provider to generate data for the given host and agent.
	 * @param providerName
	 * @param conf
	 * @return
	 * @throws InvalidProviderConfiguration
	 * @throws RMSException
	 * @throws RemoteException
	 */
	ProviderId activateProvider(String providerName, ProviderConfiguration conf) throws RemoteException, InvalidProviderConfiguration, RMSException;
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
		throws RemoteException, InvalidProviderConfiguration, ProviderNotActivated, RMSException;
	/**
	 * Applies the given configuration to all providers.
	 * @param conf
	 * @throws InvalidProviderConfiguration
	 * @throws RMSException
	 * @throws RemoteException
	 */
	void configureAllProviders(ProviderConfiguration conf) throws RemoteException, InvalidProviderConfiguration, RMSException;
	/**
	 * Starts the provider with the given id.
	 * @param pid
	 * @throws StartableError
	 * @throws ProviderNotActivated
	 * @throws RemoteException
	 * @throws RMSException
	 */
	void startProvider(ProviderId pid) throws RemoteException, ProviderNotActivated, RMSException;
	/**
	 * Stops the provider with the given id.
	 * @param pid
	 * @throws StartableError
	 * @throws ProviderNotActivated
	 * @throws RemoteException
	 * @throws RMSException
	 */
	void stopProvider(ProviderId pid) throws RemoteException, ProviderNotActivated, RMSException;
	/**
	 * @return the state of the given agent, null if the
	 * agent is not found
	 * @param agentId
	 * @throws RemoteException
	 * @throws RMSException
	 */
	ProviderState getProviderState(ProviderId providerId) throws RemoteException, RMSException;
	/**
	 * Uninstalls the provider with the given id.
	 * @param pid
	 * @throws ProviderNotActivated
	 * @throws RMSException
	 * @throws RemoteException
	 */
	void deactivateProvider(ProviderId pid) throws RemoteException, ProviderNotActivated, RMSException;
	/**
	 * Uninstalls all providers.
	 * @throws RemoteException
	 * @throws RMSException
	 */
	void deactivateAllProviders() throws RemoteException, RMSException;
	/**
	 * This method accomodates clients that prefer polling for data as oposed to using
	 * the event based approach (the latter, while providing a better user experience, requires
	 * bidirectional communication between the client and this class which is not always available due
	 * to firewalls)
	 * @return
	 * @throws RMSException
	 * @throws RemoteException
	 */
	ProviderPollBuffer getProviderPollBuffer() throws RMSException, RemoteException;
}
