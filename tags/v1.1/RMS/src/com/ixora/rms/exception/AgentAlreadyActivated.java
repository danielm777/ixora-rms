/*
 * Created on 02-Jan-2004
 */
package com.ixora.rms.exception;

import com.ixora.rms.messages.Msg;

/**
 * AgentAlreadyActivated.
 * @author Daniel Moraru
 */
public final class AgentAlreadyActivated extends InvalidAgentState {
	private static final long serialVersionUID = 8733723095262752871L;

	/**
     * Constructor.
     * @param msg
     */
    public AgentAlreadyActivated() {
        super(Msg.RMS_ERROR_AGENT_ALREADY_ACTIVATED);
    }
}
