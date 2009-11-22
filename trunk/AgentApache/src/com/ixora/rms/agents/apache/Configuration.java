/*
 * Created on Apr 15, 2004
 */
package com.ixora.rms.agents.apache;

import com.ixora.rms.agents.AgentCustomConfiguration;
import com.ixora.rms.agents.apache.messages.Msg;

/**
 * Configuration for Apache agent.
 * @author Daniel Moraru
 */
public final class Configuration extends AgentCustomConfiguration {
	private static final long serialVersionUID = 3316735766977692607L;
	// value keys
	public static final String PORT = Msg.APACHE_PORT;

	/**
	 * Empty constructor. Required to allow the associated panel
	 * to create an empty configuration instance for editing.
	 */
	public Configuration() {
		super();
		setProperty(PORT, TYPE_INTEGER, true);
		setInt(PORT, 80);
	}
}