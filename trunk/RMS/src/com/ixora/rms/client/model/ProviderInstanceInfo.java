/*
 * Created on 01-Jan-2005
 */
package com.ixora.rms.client.model;

import com.ixora.rms.providers.ProviderState;
import com.ixora.rms.repository.ProviderInstance;

/**
 * @author Daniel Moraru
 */
public interface ProviderInstanceInfo {
	/**
	 * @return
	 */
	ProviderInstance getProviderInstanceData();
	/**
	 * @return the translated name of the provider instance
	 */
	String getTranslatedName();
	/**
	 * @return
	 */
	ProviderState getProviderState();
	/**
	 * @return
	 */
	Throwable getErrorStateException();
}
