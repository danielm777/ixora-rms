/*
 * Created on Jan 25, 2004
 */
package com.ixora.rms.agents.hotspotjvm;

import com.ixora.rms.agents.hotspotjvm.messages.Msg;
import com.ixora.rms.agents.utils.AuthenticationConfiguration;

/**
 * Configuration for host availability agent.
 * @author Daniel Moraru
 */
public final class Configuration extends AuthenticationConfiguration {
	/**
	 * Empty constructor. Required to allow the associated panel
	 * to create an empty configuration instance for editing.
	 */
	public Configuration() {
		super();
		setProperty(Msg.JMX_CONNECTION_STRING, TYPE_STRING, true, true);
		// TODO next release
        //setProperty(Msg.THREAD_CONTENTION_MON_ENABLED, TYPE_BOOLEAN, true, false);
		setProperty(Msg.THREAD_CPU_TIME_ENABLED, TYPE_BOOLEAN, true, false);

		setString(Msg.JMX_CONNECTION_STRING, "service:jmx:rmi:///jndi/rmi://{host}:9999/jmxrmi");
		//setBoolean(Msg.THREAD_CONTENTION_MON_ENABLED, true);
		setBoolean(Msg.THREAD_CPU_TIME_ENABLED, true);
	}
}