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
public final class ProviderNotActivated extends RMSException {

	/**
	 * Constructor.
	 */
	public ProviderNotActivated() {
		super(ProvidersComponent.NAME, Msg.ERROR_PROVIDER_NOT_ACTIVATED, true);
	}
}
