/*
 * Created on 30-Dec-2004
 */
package com.ixora.rms.providers.impl.java;

import com.ixora.rms.providers.ProviderCustomConfiguration;
import com.ixora.rms.providers.ProvidersComponent;
import com.ixora.rms.providers.ui.ProviderCustomConfigurationPanel;

/**
 * @author Daniel Moraru
 */
public class ConfigurationPanel extends
		ProviderCustomConfigurationPanel {
	private static final long serialVersionUID = -5795246419246512978L;

	/**
	 * Constructor.
	 */
	public ConfigurationPanel() {
		super(ProvidersComponent.NAME);
	}

	/**
	 * @see com.ixora.rms.providers.ui.ProviderCustomConfigurationPanel#createCustomConfiguration()
	 */
	public ProviderCustomConfiguration createCustomConfiguration() {
		return new Configuration();
	}
}
