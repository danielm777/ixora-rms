/*
 * Created on 28-Dec-2004
 */
package com.ixora.rms.providers.exception;

import com.ixora.rms.exception.RMSException;
import com.ixora.rms.providers.ProvidersComponent;
import com.ixora.rms.providers.messages.Msg;

/**
 * @author Daniel Moraru
 */
public final class ProviderNotInstalled extends RMSException {
	private static final long serialVersionUID = 3290327100810148433L;

	/**
	 * Constructor.
	 * @param providerName
	 */
	public ProviderNotInstalled(String providerName) {
		super(ProvidersComponent.NAME, Msg.ERROR_PROVIDER_NOT_INSTALLED, new String[] {providerName});
	}
}
