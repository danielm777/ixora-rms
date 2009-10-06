/*
 * Created on 02-Jan-2004
 */
package com.ixora.rms.exception;

import com.ixora.rms.messages.Msg;

/**
 * AgentAlreadyStarted.
 * @author Daniel Moraru
 */
public final class AgentAlreadyStarted extends InvalidAgentState {

    /**
     * Constructor.
     * @param msg
     */
    public AgentAlreadyStarted() {
        super(Msg.RMS_ERROR_AGENT_ALREADY_STARTED);
    }
}
