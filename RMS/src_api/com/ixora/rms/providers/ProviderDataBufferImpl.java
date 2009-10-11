/*
 * Created on 28-Dec-2004
 */
package com.ixora.rms.providers;

import java.io.Serializable;

import com.ixora.rms.HostDataBufferImpl;

/**
 * @author Daniel Moraru
 */
public class ProviderDataBufferImpl extends HostDataBufferImpl implements ProviderDataBuffer {
	private static final long serialVersionUID = -596782216138286228L;
	/** The id of the provider that generated this buffer */
	private ProviderId provider;
	/** Raw data */
	private Serializable data;

	/**
	 * Constructor.
	 * @param provider the id of the provider that generated the data
	 * @param data raw data
	 */
	public ProviderDataBufferImpl(ProviderId provider, Serializable data) {
		super();
		this.provider = provider;
		this.data = data;
	}

	/**
	 * @see com.ixora.rms.providers.ProviderDataBuffer#getData()
	 */
	public Serializable getData() {
		return data;
	}

	/**
	 * @see com.ixora.rms.providers.ProviderDataBuffer#getProviderId()
	 */
	public ProviderId getProviderId() {
		return provider;
	}
}
