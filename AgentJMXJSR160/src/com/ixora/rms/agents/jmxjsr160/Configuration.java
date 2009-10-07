/*
 * Created on Jan 25, 2004
 */
package com.ixora.rms.agents.jmxjsr160;

import com.ixora.rms.agents.jmxjsr160.messages.Msg;
import com.ixora.rms.agents.utils.ConfigurationWithClasspathAndExtraProperties;

/**
 * Configuration for Weblogic agent.
 * @author Daniel Moraru
 */
public class Configuration extends ConfigurationWithClasspathAndExtraProperties {
	public static final String JMX_CONNECTION_STRING = Msg.JMX_CONNECTION_STRING;

	/**
	 * Empty constructor. Required to allow the associated panel
	 * to create an empty configuration instance for editing.
	 */
	public Configuration() {
		super();
        setProperty(JMX_CONNECTION_STRING, TYPE_STRING, true, true);
        setString(JMX_CONNECTION_STRING, "service:jmx:rmi:");
	}
}