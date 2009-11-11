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
	private static final long serialVersionUID = -2386084217427990225L;

	/**
     * Constructor.
     */
    public AgentNotActivated() {
        super(Msg.RMS_ERROR_AGENT_NOT_ACTIVATED);
    }
}
