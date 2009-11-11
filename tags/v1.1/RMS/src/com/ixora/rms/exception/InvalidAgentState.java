/*
 * Created on 16-Nov-2003
 */
package com.ixora.rms.exception;

/**
 * InvalidAgentState.
 * @author Daniel Moraru
 */
public class InvalidAgentState extends RMSException {
	private static final long serialVersionUID = 5279749348575868906L;

	/**
     * Constructor.
     * @param msg
     */
    public InvalidAgentState(String msg) {
        super(msg, true);
    }
}
