/*
 * Created on 23-Aug-2004
 */
package com.ixora.rms.exception;

import com.ixora.rms.messages.Msg;

/**
 * @author Daniel Moraru
 */
public final class ProviderError extends RMSException {
    /**
     * Constructor.
     * @param providerInstanceName
     * @param e
     */
    public ProviderError(String providerInstanceName, String err) {
        super(Msg.RMS_ERROR_PROVIDER_ERROR, new String[]{providerInstanceName, err});
    }
}
