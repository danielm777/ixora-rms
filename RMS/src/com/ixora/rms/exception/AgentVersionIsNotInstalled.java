/*
 * Created on 16-Nov-2003
 */
package com.ixora.rms.exception;

import com.ixora.rms.messages.Msg;



public final class AgentVersionIsNotInstalled extends RMSException {
	/**
	 * @param agentName
     * @param version
	 */
	public AgentVersionIsNotInstalled(String agentName, String version) {
		super(Msg.RMS_ERROR_AGENT_VERSION_NOT_INSTALLED, new String[]{agentName, version});
	}
}
