/*
 * Created on Jan 25, 2004
 */
package com.ixora.rms.agents.hostavailability;

import com.ixora.rms.agents.AgentCustomConfiguration;
import com.ixora.rms.agents.hostavailability.messages.Msg;

/**
 * Configuration for host availability agent.
 * @author Daniel Moraru
 */
public final class Configuration extends AgentCustomConfiguration {
	private static final long serialVersionUID = 933183627619157950L;
	public static final String PING_TIMEOUT = Msg.PING_TIMEOUT;
	public static final String PACKET_SIZE = Msg.PACKET_SIZE;

	/**
	 * Empty constructor. Required to allow the associated panel
	 * to create an empty configuration instance for editing.
	 */
	public Configuration() {
		super();
		setProperty(PING_TIMEOUT, TYPE_INTEGER, true);
		setProperty(PACKET_SIZE, TYPE_INTEGER, true);
		setInt(PING_TIMEOUT, 1000);
		setInt(PACKET_SIZE, 32);
	}
}