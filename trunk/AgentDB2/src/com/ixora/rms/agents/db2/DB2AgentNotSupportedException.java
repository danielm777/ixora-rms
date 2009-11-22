/*
 * Created on 31-Mar-2005
 */
package com.ixora.rms.agents.db2;

/**
 * DB2AgentNotSupportedException
 * Thrown if the native DLLs are not available
 */
public class DB2AgentNotSupportedException extends DB2AgentException {
	private static final long serialVersionUID = 3665760851393922941L;

	/**
	 * Constructor
	 * @param s
	 */
	public DB2AgentNotSupportedException(Throwable cause) {
        super(Msg.DB2_ERROR_NOT_SUPPORTED, cause);
    }
}
