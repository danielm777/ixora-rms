/*
 * Created on 13-Jan-2005
 */
package com.ixora.rms.agents.weblogic.exception;

import com.ixora.rms.agents.weblogic.messages.Msg;
import com.ixora.rms.exception.RMSException;

/**
 * @author Daniel Moraru
 */
public final class CommunicationError extends RMSException {

	/**
	 * Constructor.
	 */
	public CommunicationError() {
		super(Msg.NAME, Msg.ERROR_COMMUNICATION_ERROR, true);
	}
}
