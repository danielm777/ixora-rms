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
public final class InvalidProviderConfiguration extends RMSException {

	/**
	 * Constructor.
	 */
	public InvalidProviderConfiguration(String error) {
		super(ProvidersComponent.NAME, Msg.ERROR_INVALID_PROVIDER_CONFIGURATION, new String[] {error});
	}
}
