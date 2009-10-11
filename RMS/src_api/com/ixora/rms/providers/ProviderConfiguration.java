/*
 * Created on 28-Dec-2004
 */
package com.ixora.rms.providers;

import com.ixora.rms.MonitoringConfiguration;


/**
 * Provider configuration.
 * @author Daniel Moraru
 */
public class ProviderConfiguration extends MonitoringConfiguration {
	private static final long serialVersionUID = -2473817228403781367L;
	/** The host where the provider is deployed */
	private String host;

	/**
	 * Constructor.
	 */
	public ProviderConfiguration() {
		super();
	}

	/**
	 * @param host
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the host.
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @return
	 */
	public ProviderCustomConfiguration getProviderCustomConfiguration() {
		return (ProviderCustomConfiguration) getCustom();
	}
}
