/*
 * Created on 28-Dec-2004
 */
package com.ixora.rms.providers.parsers.exception;

import com.ixora.rms.exception.RMSException;

/**
 * @author Daniel Moraru
 */
public class InvalidParsingRules extends RMSException {
	/**
	 * Constructor.
     * @param component
	 * @param msg
     * @param tokens
	 */
	public InvalidParsingRules(String component, String msg, String[] tokens) {
		super(component, msg, tokens);
	}

    /**
     * @param string
     */
    public InvalidParsingRules(String string) {
        super(string);
    }
}
