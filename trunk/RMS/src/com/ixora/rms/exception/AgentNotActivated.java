/*
 * Created on 02-Jan-2004
 */
package com.ixora.rms.exception;

import com.ixora.rms.messages.Msg;

/**
 * AgentNotActivated.
 * @author Daniel Moraru
 */
public final class AgentNotActivated extends InvalidAgentState {
    /**
     * Constructor.
     */
    public AgentNotActivated() {
        super(Msg.RMS_ERROR_AGENT_NOT_ACTIVATED);
    }
}
