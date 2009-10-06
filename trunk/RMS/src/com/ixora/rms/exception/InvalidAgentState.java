/*
 * Created on 16-Nov-2003
 */
package com.ixora.rms.exception;

/**
 * InvalidAgentState.
 * @author Daniel Moraru
 */
public class InvalidAgentState extends RMSException {

    /**
     * Constructor.
     * @param msg
     */
    public InvalidAgentState(String msg) {
        super(msg, true);
    }
}
