/*
 * Created on 15-Nov-2004
 */
package com.ixora.rms.agents.apache.exception;

import com.ixora.rms.agents.apache.messages.Msg;
import com.ixora.rms.exception.RMSException;

/**
 * Thrown when the parsing of Apache data fails. This is an application internal error
 * as it should not happen for a supported agent version.
 * @author Daniel Moraru
 */
public final class InvalidDataPattern extends RMSException {
	private static final long serialVersionUID = -8254767448517171232L;

	/**
	 * Constructor.
	 */
	public InvalidDataPattern() {
		super(Msg.APACHE_NAME, Msg.APACHE_ERROR_INVALID_DATA_PATTERN, true);
		internalAppError = true;
	}
}
