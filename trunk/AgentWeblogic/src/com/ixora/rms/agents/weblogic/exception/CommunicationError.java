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
	private static final long serialVersionUID = -4174754389463689908L;

	/**
	 * Constructor.
	 */
	public CommunicationError() {
		super(Msg.NAME, Msg.ERROR_COMMUNICATION_ERROR, true);
	}
}
