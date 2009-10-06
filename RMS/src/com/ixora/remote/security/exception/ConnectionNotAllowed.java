/*
 * Created on 19-Feb-2005
 */
package com.ixora.remote.security.exception;

import com.ixora.remote.messages.Msg;
import com.ixora.rms.exception.RMSException;

/**
 * @author Daniel Moraru
 */
public final class ConnectionNotAllowed extends RMSException {
	/**
	 * Constructor.
	 */
	public ConnectionNotAllowed() {
		super(Msg.HOST_MANAGER_ERROR_CONNECTION_NOT_ALLOWED, true);
	}
}
