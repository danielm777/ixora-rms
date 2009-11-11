/**
 * 24-Dec-2005
 */
package com.ixora.rms.agents.snmp.exceptions;

import com.ixora.rms.agents.snmp.messages.Msg;

/**
 * @author Daniel Moraru
 */
public class SNMPServerUnavailableException extends SNMPAgentException {

	public SNMPServerUnavailableException() {
		super(Msg.ERROR_SERVER_UNAVAILABLE);
	}


}
