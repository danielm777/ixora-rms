/*
 * Created on 13-Jan-2005
 */
package com.ixora.rms.agents.websphere.exception;

import java.util.Date;

import com.ixora.rms.agents.websphere.messages.Msg;
import com.ixora.rms.exception.RMSException;

/**
 * @author Daniel Moraru
 */
public final class ServerFailedToReturnData extends RMSException {

	/**
	 * Constructor.
	 */
	public ServerFailedToReturnData(Date time) {
		super(Msg.WEBSPHEREAGENT_NAME,
                Msg.WEBSPHEREAGENT_ERROR_SERVER_FAILED_TO_RETURN_DATA,
                new String[] {time.toString()});
	}
}
