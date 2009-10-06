/*
 * Created on 23-Aug-2004
 */
package com.ixora.rms.ui.tools.providermanager.exception;

import com.ixora.rms.exception.RMSException;
import com.ixora.rms.ui.tools.providermanager.ProviderManagerComponent;
import com.ixora.rms.ui.tools.providermanager.messages.Msg;

/**
 * @author Daniel Moraru
 */
public final class SaveConflict extends RMSException {
    /**
     * Constructor.
     */
    public SaveConflict(String providerInstanceName) {
        super(ProviderManagerComponent.NAME,
                Msg.ERROR_PROVIDER_INSTANCE_SAVING_CONFLICT,
                new String[] {providerInstanceName});
    }

}
