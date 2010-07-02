/*
 * Created on Jan 25, 2004
 */
package com.ixora.rms.agents.sapnw.v7_1;

import com.ixora.rms.agents.sapnw.messages.Msg;
import com.ixora.rms.agents.utils.ConfigurationWithClasspath;

/**
 * Configuration for SAP NetWork agent.
 * @author Daniel Moraru
 */
public class Configuration extends ConfigurationWithClasspath {
	private static final long serialVersionUID = -3773071060315384194L;
	public static final String P4_PORT = Msg.P4_PORT;

	/**
	 * Empty constructor. Required to allow the associated panel
	 * to create an empty configuration instance for editing.
	 */
	public Configuration() {
		super();
        setProperty(P4_PORT, TYPE_INTEGER, true, true);
        setInt(P4_PORT, 50504);
		setString(USERNAME, "administrator");
		setString(PASSWORD, "netw33ver");
		setString(ROOT_FOLDER, "C:/usr/sap/CE1/J05");
		setString(CLASSPATH, "/j2ee/cluster/bin/ext/tc~jmx/lib/private/sap.com~tc~bl~pj_jmx~Impl.jar," +
				"/j2ee/j2eeclient/sap.com~tc~je~clientlib~impl.jar," +
				"/j2ee/j2eeclient/sap.com~tc~exception~impl.jar," +
				"/j2ee/j2eeclient/sap.com~tc~logging~java~impl.jar");
	}
}