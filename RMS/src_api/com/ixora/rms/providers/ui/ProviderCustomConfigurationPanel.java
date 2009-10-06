/*
 * Created on 28-Dec-2004
 */
package com.ixora.rms.providers.ui;

import com.ixora.common.typedproperties.ui.TypedPropertiesEditor;
import com.ixora.rms.providers.ProviderCustomConfiguration;

/**
 * Panel for editing provider configuration.
 * @author Daniel Moraru
 */
public abstract class ProviderCustomConfigurationPanel extends TypedPropertiesEditor {

	/**
	 * Listener.
	 */
	public interface Listener extends TypedPropertiesEditor.Listener {
	}

	/**
	 * Subclasses must provide a default constructor.
	 * @param component
	 */
	protected ProviderCustomConfigurationPanel(String component) {
		super(true);
		this.fComponent = component;
	}

	/**
	 * @return a custom configuration
	 */
	public abstract ProviderCustomConfiguration createCustomConfiguration();

	/**
	 * @param conf
	 */
	public void setConfiguration(ProviderCustomConfiguration conf) {
		setTypedProperties(fComponent, conf);
	}

	/**
	 * @return
	 */
	public ProviderCustomConfiguration getConfiguration() {
		stopEditing();
		return (ProviderCustomConfiguration)fProperties;
	}
}
