/*
 * Created on 31-Mar-2005
 */
package com.ixora.rms.agents.windows.exception;

import com.ixora.rms.agents.windows.messages.Msg;

/**
 * WinPerfNotSupportedException
 */
public class WinPerfNotSupportedException extends WinPerfException {
	private static final long serialVersionUID = -5943987639765937437L;

	/**
	 * Constructor
	 * @param s
	 * @param cause
	 */
	public WinPerfNotSupportedException(Throwable cause) {
		super(Msg.WINDOWSAGENT_ERROR_NOT_SUPPORTED, cause);
	}

}
