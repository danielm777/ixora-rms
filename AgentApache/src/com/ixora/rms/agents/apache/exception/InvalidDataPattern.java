/*
 * Created on 15-Nov-2004
 */
package com.ixora.rms.agents.apache.exception;

import com.ixora.rms.agents.apache.messages.Msg;
import com.ixora.rms.exception.RMSException;

/**
 * Thrown when the parsing of apache data fails. This is an application internal error
 * as it should not happen for a supported agent version.
 * @author Daniel Moraru
 */
public final class InvalidDataPattern extends RMSException {

	/**
	 * Constructor.
	 */
	public InvalidDataPattern() {
		super(Msg.APACHE_NAME, Msg.APACHE_ERROR_INVALID_DATA_PATTERN, true);
		internalAppError = true;
	}
}
