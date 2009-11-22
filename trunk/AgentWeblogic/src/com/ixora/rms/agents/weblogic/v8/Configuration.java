/*
 * Created on Jan 25, 2004
 */
package com.ixora.rms.agents.weblogic.v8;

import com.ixora.rms.agents.utils.ConfigurationWithClasspath;
import com.ixora.rms.agents.weblogic.messages.Msg;

/**
 * Configuration for Weblogic agent.
 * @author Daniel Moraru
 */
public class Configuration extends ConfigurationWithClasspath {
	private static final long serialVersionUID = -4451399203512643710L;
	public static final String JMX_PROVIDER_URL = Msg.JMX_PROVIDER_URL;
	public static final String SHOW_JUST_RUNTIME_DATA = Msg.SHOW_JUST_RUNTIME_DATA;
	public static final String JNDI_NAME = Msg.JNDI_NAME;

	/**
	 * Empty constructor. Required to allow the associated panel
	 * to create an empty configuration instance for editing.
	 */
	public Configuration() {
		super();
        setProperty(JMX_PROVIDER_URL, TYPE_STRING, true, true);
        setProperty(JNDI_NAME, TYPE_STRING, true, true);
		setProperty(SHOW_JUST_RUNTIME_DATA, TYPE_BOOLEAN, true, true);
        setString(USERNAME, "weblogic");
		setString(PASSWORD, "weblogic");
        setString(JMX_PROVIDER_URL, "t3://{host}:7001");
        setString(ROOT_FOLDER, "C:/bea80/weblogic81");
		setString(CLASSPATH, "/server/lib/weblogic.jar");
		setString(CLASSPATH, "/server/lib/weblogic.jar");
		setString(JNDI_NAME, "weblogic.management.home.localhome");
		setBoolean(SHOW_JUST_RUNTIME_DATA, true);
	}

	/**
	 * A parent last class loader is needed in order to load javax.management classes
	 * from weblogic.jar which are an older version than the one in JSE 5.0
	 * @see com.ixora.common.plugin.PluginDescriptor#useParentLastClassloader()
	 */
	public boolean useParentLastClassloader() {
		return true;
	}
}