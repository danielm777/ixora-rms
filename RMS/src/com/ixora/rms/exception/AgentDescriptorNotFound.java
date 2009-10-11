/*
 * Created on 19-Nov-2003
 */
package com.ixora.rms.exception;

import com.ixora.rms.agents.AgentId;
import com.ixora.rms.messages.Msg;

/**
 * AgentDescriptorNotFound
 * @author Daniel Moraru
 */
public final class AgentDescriptorNotFound extends RMSException {
	private static final long serialVersionUID = -6720320378138336113L;

	/**
	 * Constructor.
	 * @param eid
	 */
	public AgentDescriptorNotFound(AgentId aid) {
		super(Msg.RMS_AGENT_DESCRIPTOR_NOT_FOUND,
				new String[] {aid.toString()});
	}
}
