/*
 * Created on 16-Nov-2003
 */
package com.ixora.rms.exception;

import com.ixora.rms.messages.Msg;

/**
 * @author Daniel Moraru
 */
public final class AgentIsNotInstalled extends RMSException {
	private static final long serialVersionUID = 8715226922792632195L;

	/**
	 * @param agentName
	 */
	public AgentIsNotInstalled(String agentName) {
		super(Msg.RMS_ERROR_AGENT_NOT_INSTALLED, new String[]{agentName});
	}

	/**
	 * @param agentName
	 * @param e
	 */
	public AgentIsNotInstalled(String agentName, Throwable e) {
		this(agentName);
	}
}
