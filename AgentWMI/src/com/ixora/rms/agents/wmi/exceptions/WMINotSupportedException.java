/*
 * Created on 20-Aug-2005
 */
package com.ixora.rms.agents.wmi.exceptions;

import com.ixora.rms.agents.wmi.messages.Msg;

/**
 * WMINotSupportedException
 * Thrown when the native DLL cannot be loaded
 */
public class WMINotSupportedException extends WMIAgentException {
	/**
	 * Constructor
	 * @param s
	 * @param cause
	 */
	public WMINotSupportedException(Throwable cause) {
		super(Msg.WMI_ERROR_NOT_SUPPORTED, cause);
	}

}
