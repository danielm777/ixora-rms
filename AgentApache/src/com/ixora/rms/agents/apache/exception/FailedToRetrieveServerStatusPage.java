/*
 * Created on 15-Nov-2004
 */
package com.ixora.rms.agents.apache.exception;

import com.ixora.rms.agents.apache.messages.Msg;
import com.ixora.rms.exception.RMSException;

/**
 * @author Daniel Moraru
 */
public final class FailedToRetrieveServerStatusPage extends RMSException {
	private static final long serialVersionUID = -1920137964881229060L;

	/**
	 * Constructor.
	 * @param cause
	 */
	public FailedToRetrieveServerStatusPage(Throwable cause) {
		super(Msg.APACHE_NAME, Msg.APACHE_ERROR_FAILED_TO_GET_STATUS_PAGE, new String[] {cause.toString()});
	}

}
