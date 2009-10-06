/*
 * Created on 23-Aug-2004
 */
package com.ixora.rms.exception;

import com.ixora.rms.messages.Msg;

/**
 * @author Daniel Moraru
 */
public final class NoParsersInstalledForProvider extends RMSException {
    /**
     * Constructor.
     * @param provider
     */
    public NoParsersInstalledForProvider(String provider) {
        super(Msg.RMS_ERROR_NO_PARSERS_INSTALLED_FOR_PROVIDER,
        		new String[]{provider});
    }
}
