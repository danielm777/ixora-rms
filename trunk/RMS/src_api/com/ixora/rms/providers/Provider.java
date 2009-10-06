/*
 * Created on 27-Dec-2004
 */
package com.ixora.rms.providers;

import com.ixora.common.Startable;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.providers.exception.InvalidProviderConfiguration;

/**
 * Raw data provider.
 * @author Daniel Moraru
 */
public interface Provider extends Startable {
	/**
	 * Listener.
	 */
	public interface Listener {
		/**
		 * Invoked when an error occurred.
		 * @param providerId
		 * @param msg
		 * @param t
		 */
		void error(ProviderId providerId, String msg, Throwable t);
		/**
		 * Invoked when data becomes available.
		 * @param buff
		 */
		void data(ProviderDataBuffer buff);
	}
	/**
	 * @param id
	 */
	void setId(ProviderId id);
	/**
	 * Sets a listener for this provider.
	 * @param l
	 */
	void setListener(Listener l);
	/**
	 * Configures the provider.
	 * @param conf
	 * @throws InvalidProviderConfiguration
	 * @throws RMSException
	 * @throws Throwable
	 */
	void configure(ProviderConfiguration conf) throws InvalidProviderConfiguration, RMSException, Throwable;
	/**
	 * This method will be invoked after the first call to <code>configure(ProviderConfiguration)</code>
	 * to allow the provider to use the hint in the configuration when returning this value.
	 * @return true if this provider needs a collector to invoke <code>collectData()</code>
	 */
	boolean requiresExternalCollector();
	/**
	 * This method will be invoked on every provider that needs a collector to signal
	 * that it's time to generate new data.
	 * @throws RMSException
	 * @throws Throwable
	 */
	void collectData() throws RMSException, Throwable;
	/**
	 * @return the current configuration
	 */
	ProviderConfiguration getConfiguration();
	/**
	 * Clean up method that will be invoked when the provider is disposed of.
	 */
	void cleanup() throws RMSException, Throwable;
}
