/*
 * Created on 02-Jan-2004
 */
package com.ixora.rms.exception;

import com.ixora.rms.messages.Msg;

/**
 * AgentNotStarted.
 * @author Daniel Moraru
 */
public final class AgentNotStarted extends InvalidAgentState {
	private static final long serialVersionUID = 3872347208514384961L;

	/**
     * Constructor.
     */
    public AgentNotStarted() {
        super(Msg.RMS_ERROR_AGENT_NOT_STARTED);
    }
}
