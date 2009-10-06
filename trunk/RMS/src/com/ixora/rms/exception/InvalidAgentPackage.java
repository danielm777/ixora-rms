/*
 * Created on 02-Jan-2004
 */
package com.ixora.rms.exception;

import com.ixora.rms.messages.Msg;

/**
 * AgentNotStarted.
 * @author Daniel Moraru
 */
public final class InvalidAgentPackage extends InvalidAgentState {
    /**
     * Constructor.
     */
    public InvalidAgentPackage() {
        super(Msg.RMS_ERROR_INVALID_AGENT_PACKAGE);
    }
}
