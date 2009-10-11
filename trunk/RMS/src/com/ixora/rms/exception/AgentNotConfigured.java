/*
 * Created on 02-Jan-2004
 */
package com.ixora.rms.exception;

import com.ixora.rms.messages.Msg;

/**
 * AgentNotConfigured.
 * @author Daniel Moraru
 */
public final class AgentNotConfigured extends InvalidAgentState {
	private static final long serialVersionUID = -454385470182313174L;

	/**
     * Constructor.
     * @param msg
     */
    public AgentNotConfigured(String msg) {
        super(Msg.RMS_ERROR_AGENT_NOT_CONFIGURED);
    }
}
