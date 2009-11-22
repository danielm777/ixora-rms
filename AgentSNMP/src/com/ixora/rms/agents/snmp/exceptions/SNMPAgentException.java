/*
 * Created on 20-Aug-2005
 */
package com.ixora.rms.agents.snmp.exceptions;


import com.ixora.rms.agents.snmp.messages.Msg;
import com.ixora.rms.exception.RMSException;

/**
 * SNMPAgentException
 */
public class SNMPAgentException extends RMSException {
	private static final long serialVersionUID = 7019814322902702155L;

	/**
     * Constructor
     * @param s
     * @param e
     */
    public SNMPAgentException(String s) {
        super(Msg.SNMP_NAME, s, true);
    }

	/**
	 * Constructor
     * @param s
	 * @param e
	 */
	public SNMPAgentException(String s, Throwable e) {
        super(Msg.SNMP_NAME, s, new String[] { e.getMessage() });
    }

	/**
	 * Constructor
	 * @param component
	 * @param msgKey
	 * @param msgTokens
	 */
	public SNMPAgentException(String msgKey, String[] msgTokens) {
		super(Msg.SNMP_NAME, msgKey, msgTokens);
	}

}
