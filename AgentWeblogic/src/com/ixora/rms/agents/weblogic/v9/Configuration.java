/*
 * Created on Jan 25, 2004
 */
package com.ixora.rms.agents.weblogic.v9;

import com.ixora.rms.agents.utils.ConfigurationWithClasspath;
import com.ixora.rms.agents.weblogic.messages.Msg;

/**
 * Configuration for Weblogic agent.
 * @author Daniel Moraru
 */
public class Configuration extends ConfigurationWithClasspath {
	private static final long serialVersionUID = 6650202638016617708L;
	public static final String JMX_CONNECTION_STRING = Msg.JMX_CONNECTION_STRING;

	/**
	 * Empty constructor. Required to allow the associated panel
	 * to create an empty configuration instance for editing.
	 */
	public Configuration() {
		super();
        setProperty(JMX_CONNECTION_STRING, TYPE_STRING, true, true);
		setString(USERNAME, "weblogic");
		setString(PASSWORD, "weblogic");
        setString(JMX_CONNECTION_STRING, "service:jmx:t3://{host}:7001/jndi/weblogic.management.mbeanservers.domainruntime");
        setString(ROOT_FOLDER, "C:/bea/weblogic90");
		setString(CLASSPATH, "/server/lib/wlclient.jar,/server/lib/weblogic.jar");
	}
}