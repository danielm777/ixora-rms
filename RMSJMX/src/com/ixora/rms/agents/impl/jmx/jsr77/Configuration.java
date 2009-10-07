/*
 * Created on Jan 25, 2004
 */
package com.ixora.rms.agents.impl.jmx.jsr77;

import com.ixora.rms.agents.impl.jmx.jsr77.messages.Msg;
import com.ixora.rms.agents.utils.ConfigurationWithClasspathAndExtraProperties;

/**
 * Configuration for JSR77 agent.
 * @author Daniel Moraru
 */
public class Configuration extends ConfigurationWithClasspathAndExtraProperties {
	public static final String SERVER_URL = Msg.SERVER_URL;
	public static final String JNDI_NAME = Msg.JNDI_NAME;

	/**
	 * Empty constructor. Required to allow the associated panel
	 * to create an empty configuration instance for editing.
	 */
	public Configuration() {
		super();
        setProperty(SERVER_URL, TYPE_STRING, true, true);
        setProperty(JNDI_NAME, TYPE_STRING, true, true);
		setString(USERNAME, "");
		setString(PASSWORD, "");
		setString(JNDI_NAME, "ejb/mgmt/MEJB");
	}
}