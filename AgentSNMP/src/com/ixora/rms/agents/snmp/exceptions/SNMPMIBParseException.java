/*
 * Created on 01-Dec-2005
 */
package com.ixora.rms.agents.snmp.exceptions;

import com.ixora.rms.agents.snmp.messages.Msg;

public class SNMPMIBParseException extends SNMPAgentException {
	private static final long serialVersionUID = 8402635583637474114L;

	/**
	 * @param s
	 */
	public SNMPMIBParseException(String fileName, String parsingErrors) {
		super(Msg.ERROR_MIB_PARSE, new String[] { fileName, parsingErrors });
	}
}
