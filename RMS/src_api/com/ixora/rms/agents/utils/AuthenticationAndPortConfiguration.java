/*
 * Created on Jan 26, 2004
 */
package com.ixora.rms.agents.utils;

/**
 * @author Daniel Moraru
 */
public class AuthenticationAndPortConfiguration
	extends AuthenticationConfiguration {
	/** Server port number key */
	public static final String PORT = Msg.AGENT_CONFIGURATION_PORT;

	/**
	 * Constructor.
	 */
	public AuthenticationAndPortConfiguration() {
		super();
		setProperty(PORT, TYPE_INTEGER, true);
	}
}
