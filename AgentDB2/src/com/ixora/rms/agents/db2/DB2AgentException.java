/*
 * Created on 17-Oct-2004
 */
package com.ixora.rms.agents.db2;

import com.ixora.rms.exception.RMSException;

/**
 * DB2AgentException
 * Converts the native exception to an application exception and wraps
 * the description in a localized string.
 */
public class DB2AgentException extends RMSException {
    /**
     * Constructor
     * @param s
     * @param e
     */
    public DB2AgentException(String s) {
        super(Msg.DB2_NAME, s, true);
    }

    /**
     * Constructor
     * @param s
     * @param e
     */
    public DB2AgentException(String s, Throwable t) {
        super(Msg.DB2_NAME, s, t, true);
    }

	/**
	 * Constructor
	 * @param e
	 */
	public DB2AgentException(String msgKey, String[] msgTokens) {
        super(Msg.DB2_NAME, msgKey, msgTokens);
    }
}
