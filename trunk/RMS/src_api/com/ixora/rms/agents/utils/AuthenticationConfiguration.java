/*
 * Created on Jan 25, 2004
 */
package com.ixora.rms.agents.utils;

import com.ixora.rms.agents.AgentCustomConfiguration;


/**
 * Configuration for agents that need authentication details.
 * @author Daniel Moraru
 */
public class AuthenticationConfiguration extends AgentCustomConfiguration {
	// value keys
	public static final String USERNAME = Msg.AGENT_CONFIGURATION_USERNAME;
	public static final String PASSWORD = Msg.AGENT_CONFIGURATION_PASSWORD;

	/**
	 * Empty constructor. Required to allow the associated panel
	 * to create an empty configuration instance for editing.
	 */
	public AuthenticationConfiguration() {
		super();
		setProperty(USERNAME, TYPE_STRING, true, false); // not required
		setProperty(PASSWORD, TYPE_SECURE_STRING, true, false); // not required
	}
}