/*
 * Created on 01-Dec-2005
 */
package com.ixora.rms.agents.snmp.exceptions;

import com.ixora.rms.agents.snmp.messages.Msg;

public class SNMPMIBParseException extends SNMPAgentException {

	/**
	 * @param s
	 */
	public SNMPMIBParseException(String fileName, String parsingErrors) {
		super(Msg.ERROR_MIB_PARSE, new String[] { fileName, parsingErrors });
	}
}
