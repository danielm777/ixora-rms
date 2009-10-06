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
public final class ParsingRulesMissing extends RMSException {
    /**
     * Constructor.
     */
    public ParsingRulesMissing() {
        super(ProviderManagerComponent.NAME, Msg.ERROR_PARSING_RULES_MISSING, true);
    }

}
