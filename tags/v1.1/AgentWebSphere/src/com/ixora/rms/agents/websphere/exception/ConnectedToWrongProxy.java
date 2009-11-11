/*
 * Created on 13-Jan-2005
 */
package com.ixora.rms.agents.websphere.exception;

import com.ixora.rms.agents.websphere.messages.Msg;
import com.ixora.rms.exception.RMSException;

/**
 * @author Daniel Moraru
 */
public final class ConnectedToWrongProxy extends RMSException {

	/**
	 * Constructor.
	 */
	public ConnectedToWrongProxy() {
		super(Msg.WEBSPHEREAGENT_NAME, Msg.WEBSPHEREAGENT_ERROR_CONNECTED_TO_WRONG_PROXY, true);
	}
}
