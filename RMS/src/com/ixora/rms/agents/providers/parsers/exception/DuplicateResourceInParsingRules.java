/*
 * Created on 12-Jun-2005
 */
package com.ixora.rms.agents.providers.parsers.exception;

import com.ixora.rms.ResourceId;
import com.ixora.rms.agents.providers.parsers.messages.Msg;
import com.ixora.rms.providers.parsers.exception.InvalidParsingRules;

/**
 * DuplicateResourceInParsingRules.
 */
public final class DuplicateResourceInParsingRules extends InvalidParsingRules {

    /**
     * Constructor.
     * @param duplicate
     */
    public DuplicateResourceInParsingRules(ResourceId duplicate) {
        super(null, Msg.PROVIDERS_PARSERS_ERROR_DUPLICATE_RESOURCE_IN_PARSING_RULES,
                new String[] {duplicate.toString()});
    }
}
