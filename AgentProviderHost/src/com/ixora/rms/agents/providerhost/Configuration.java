/*
 * Created on Jan 25, 2004
 */
package com.ixora.rms.agents.providerhost;

import com.ixora.rms.agents.AgentCustomConfiguration;
import com.ixora.rms.agents.messages.Msg;

/**
 * Configuration for host availability agent.
 * @author Daniel Moraru
 */
public final class Configuration extends AgentCustomConfiguration {
	private static final long serialVersionUID = -865400529308223549L;

	/**
	 * Empty constructor. Required to allow the associated panel
	 * to create an empty configuration instance for editing.
	 */
	public Configuration() {
		super();
		setProperty(Msg.PARAMETER1, TYPE_STRING, true, false);
		setProperty(Msg.PARAMETER2, TYPE_STRING, true, false);
		setProperty(Msg.PARAMETER3, TYPE_STRING, true, false);
		setProperty(Msg.PARAMETER4, TYPE_STRING, true, false);
		setProperty(Msg.PARAMETER5, TYPE_STRING, true, false);
		setProperty(Msg.PARAMETER6, TYPE_STRING, true, false);
		setProperty(Msg.PARAMETER7, TYPE_STRING, true, false);
		setProperty(Msg.PARAMETER8, TYPE_STRING, true, false);
		setProperty(Msg.PARAMETER9, TYPE_STRING, true, false);
	}
}