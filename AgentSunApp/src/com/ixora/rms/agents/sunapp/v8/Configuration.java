/*
 * Created on Jan 25, 2004
 */
package com.ixora.rms.agents.sunapp.v8;

import com.ixora.rms.agents.sunapp.messages.Msg;
import com.ixora.rms.agents.utils.ConfigurationWithClasspath;

/**
 * Configuration for Weblogic agent.
 * @author Daniel Moraru
 */
public class Configuration extends ConfigurationWithClasspath {
	public static final String JMX_CONNECTION_STRING = Msg.JMX_CONNECTION_STRING;
	public static final String SHOW_JUST_RUNTIME_DATA = Msg.SHOW_JUST_RUNTIME_DATA;

	/**
	 * Empty constructor. Required to allow the associated panel
	 * to create an empty configuration instance for editing.
	 */
	public Configuration() {
		super();
        setProperty(JMX_CONNECTION_STRING, TYPE_STRING, true, true);
		setProperty(SHOW_JUST_RUNTIME_DATA, TYPE_BOOLEAN, true, true);

        setString(USERNAME, "admin");
		setString(PASSWORD, "password");
        setString(JMX_CONNECTION_STRING, "service:jmx:rmi:///jndi/rmi://{host}:8686/management/rmi-jmx-connector");
        setString(ROOT_FOLDER, "C:/Sun/AppServer");
		setString(CLASSPATH, "/lib/appserv-rt.jar,/lib/appserv-admin.jar,/lib/j2ee.jar");
		setBoolean(SHOW_JUST_RUNTIME_DATA, true);
	}

	/**
	 * @see com.ixora.rms.CustomConfiguration#useParentLastClassloader()
	 */
	public boolean useParentLastClassloader() {
		// use parent class loader first as we already have
		// in the global classpath the j2ee/jmx libraries
		return false;
	}
}