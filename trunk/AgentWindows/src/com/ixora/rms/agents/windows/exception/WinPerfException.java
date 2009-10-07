package com.ixora.rms.agents.windows.exception;

import com.ixora.rms.agents.windows.messages.Msg;
import com.ixora.rms.exception.RMSException;

/**
 * WinPerfException
 * Thrown by the native code when remote machine is not available
 */
public class WinPerfException extends RMSException {
    /**
     * WARNING: this constructor is called from the native
     * code, edit with care.
     * @param s
     */
    public WinPerfException(String s) {
        super(Msg.WINDOWSAGENT_NAME, s, true);
    }

	/**
	 * Constructor
     * @param s
	 * @param e
	 */
	public WinPerfException(String s, Throwable e) {
        super(Msg.WINDOWSAGENT_NAME, s, new String[] { e.getMessage() });
    }
}
