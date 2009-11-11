/*
 * Created on 01-Jan-2005
 */
package com.ixora.rms.client.model;

import com.ixora.common.MessageRepository;
import com.ixora.rms.providers.ProviderId;
import com.ixora.rms.providers.ProviderState;
import com.ixora.rms.providers.ProvidersComponent;
import com.ixora.rms.repository.ProviderInstance;

/**
 * Model data for a provider instance.
 * @author Daniel Moraru
 */
public final class ProviderInstanceInfoImpl implements ProviderInstanceInfo {
	/** Data */
	private ProviderInstance data;
	/** Translated name for the provider instance */
	private String translatedName;
// provider runtime data
	/** Provider state */
	private ProviderState providerState;
	/** Error state exception */
	private Throwable errorStateException;

	/**
	 * Constructor.
	 */
	public ProviderInstanceInfoImpl(ProviderInstance data) {
		super();
		this.data = data;
		this.translatedName = MessageRepository.get(ProvidersComponent.NAME, data.getInstanceName());
	}

	/**
	 * @see com.ixora.rms.client.model.ProviderInstanceInfo#getProviderInstanceData()
	 */
	public ProviderInstance getProviderInstanceData() {
		return data;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return this.translatedName;
	}

	/**
	 * @see com.ixora.rms.client.model.ProviderInstanceInfo#getProviderState()
	 */
	public ProviderState getProviderState() {
		return this.providerState;
	}

	/**
	 * @see com.ixora.rms.client.model.ProviderInstanceInfo#getErrorStateException()
	 */
	public Throwable getErrorStateException() {
		return this.errorStateException;
	}

// package access
	/**
	 * @param providerId the providerId to set.
	 */
	void setProviderId(ProviderId providerId) {
	}

	/**
	 * @param providerState the providerState to set.
	 * @param t
	 */
	void setProviderState(ProviderState providerState, Throwable t) {
		this.providerState = providerState;
		this.errorStateException = t;
	}

	/**
	 * @param pi
	 */
	void update(ProviderInstance pi) {
		this.data = pi;
		this.translatedName = MessageRepository.get(ProvidersComponent.NAME, pi.getInstanceName());
	}

	/**
	 * @see com.ixora.rms.client.model.ProviderInstanceInfo#getTranslatedName()
	 */
	public String getTranslatedName() {
		return translatedName;
	}
}
