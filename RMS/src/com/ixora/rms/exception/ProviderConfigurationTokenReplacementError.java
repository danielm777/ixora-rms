/*
 * Created on 23-Aug-2004
 */
package com.ixora.rms.exception;

import com.ixora.rms.messages.Msg;

/**
 * @author Daniel Moraru
 */
public final class ProviderConfigurationTokenReplacementError extends RMSException {
	private static final long serialVersionUID = 4431466080101197252L;

	/**
     * Constructor.
     * @param eid
     */
    public ProviderConfigurationTokenReplacementError(String token, String property) {
        super(Msg.RMS_ERROR_PROVIDER_CONFIGURATION_TOKEN_REPLACEMENT_ERROR,
        		new String[]{token, property});
    }
}
