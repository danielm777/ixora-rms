package com.ixora.rms.providers;

import com.ixora.rms.exception.RMSException;
import com.ixora.rms.providers.exception.InvalidProviderConfiguration;
import com.ixora.rms.providers.exception.ProviderNotActivated;
import com.ixora.rms.providers.exception.ProviderNotInstalled;

/**
 * Manages the creation and provides an execution environment for providers for a host.
 * @author Daniel Moraru
 */
public interface HostProviderManager {
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
	 * Activates a provider to generate data for the given host and agent.
	 * @param providerName
	 * @param conf
	 * @return
	 * @throws InvalidProviderConfiguration
	 * @throws ProviderNotInstalled
	 * @throws RMSException
	 */
	ProviderId activateProvider(String providerName, ProviderConfiguration conf) throws ProviderNotInstalled, InvalidProviderConfiguration, RMSException;
	/**
	 * Configures the given provider.
	 * @param provider
	 * @param conf
	 * @throws InvalidProviderConfiguration
	 * @throws ProviderNotActivated
	 * @throws RMSException
	 */
	void configureProvider(ProviderId provider, ProviderConfiguration conf)
		throws InvalidProviderConfiguration, ProviderNotActivated, RMSException;
	/**
	 * Applies the given configuration to all providers.
	 * @param conf
	 * @throws InvalidProviderConfiguration
	 * @throws RMSException
	 */
	void configureAllProviders(ProviderConfiguration conf) throws InvalidProviderConfiguration, RMSException;
	/**
	 * Starts the provider with the given id.
	 * @param pid
	 * @throws RMSException
	 * @throws ProviderNotActivated
	 */
	void startProvider(ProviderId pid) throws RMSException, ProviderNotActivated;
	/**
	 * Stops the provider with the given id.
	 * @param pid
	 * @throws StartableError
	 * @throws ProviderNotActivated
	 */
	void stopProvider(ProviderId pid) throws RMSException, ProviderNotActivated;
	/**
	 * @return the state of the given agent, null if the
	 * agent is not found
	 * @param agentId
	 */
	ProviderState getProviderState(ProviderId providerId);
	/**
	 * Deactivates the provider with the given id.
	 * @param pid
	 * @throws ProviderNotActivated
	 * @throws RMSException
	 */
	void deactivateProvider(ProviderId pid) throws ProviderNotActivated, RMSException;
	/**
	 * Deactivates all providers.
	 */
	void deactivateAllProviders();
}
