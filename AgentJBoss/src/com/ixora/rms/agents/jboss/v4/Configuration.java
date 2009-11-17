/*
 * Created on Jan 25, 2004
 */
package com.ixora.rms.agents.jboss.v4;

import com.ixora.rms.agents.jboss.messages.Msg;
import com.ixora.rms.agents.utils.ConfigurationWithClasspath;

/**
 * Configuration for JBoss agent.
 * @author Daniel Moraru
 */
public class Configuration extends ConfigurationWithClasspath {
	private static final long serialVersionUID = 4113841713468775415L;
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

		setString(USERNAME, "admin");
		setString(PASSWORD, "admin");
        setString(SERVER_URL, "jnp://{host}:1099");
        setString(JNDI_NAME, "jmx/rmi/RMIAdaptor");
        setString(ROOT_FOLDER, "C:/jboss-4.0.3RC1");
		setString(CLASSPATH,
				"/lib/dom4j.jar,/server/default/lib/jboss-management.jar," +
				"/client/jboss-jmx.jar,/client/jboss-common.jar," +
				"/client/jboss-system.jar,/client/jbossall-client.jar," +
				"/client/jboss.jar,/client/concurrent.jar,/client/log4j.jar,/client/jboss-jsr77-client.jar," +
				"/client/jboss-transaction.jar,/client/dom4j.jar,/client/jnp-client.jar,/client/jmx-rmi-connector-client.jar," +
				"/client/jboss-j2ee.jar");
	}

	/**
	 * @see com.ixora.rms.CustomConfiguration#useParentLastClassloader()
	 */
	public boolean useParentLastClassloader() {
		return true;
	}
}