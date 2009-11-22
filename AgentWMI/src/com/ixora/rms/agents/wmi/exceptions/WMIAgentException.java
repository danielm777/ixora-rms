/*
 * Created on 20-Aug-2005
 */
package com.ixora.rms.agents.wmi.exceptions;

import com.ixora.rms.agents.wmi.messages.Msg;
import com.ixora.rms.exception.RMSException;

/**
 * WMINativeException
 * Some problem occurred with native code while extracting WMI data
 */
public class WMIAgentException extends RMSException {
	private static final long serialVersionUID = 1L;

	/**
     * Constructor
     * @param s
     * @param e
     */
    public WMIAgentException(String s) {
        super(Msg.WMI_NAME, s, true);
    }

	/**
	 * Constructor
     * @param s
	 * @param e
	 */
	public WMIAgentException(String s, Throwable e) {
        super(Msg.WMI_NAME, s, new String[] { e.getMessage() });
    }

}
