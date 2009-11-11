/*
 * Created on 12-Jan-2005
 */
package com.ixora.rms.agents.hotspotjvm.messages;

/**
 * @author Daniel Moraru
 */
public interface Msg {
// agent name
	public static final String NAME
		= "agents.hotspotjvm";

// configuration entries
	public static final String JMX_CONNECTION_STRING = "jmx_connection_string";
	public static final String THREAD_CONTENTION_MON_ENABLED = "thread_contention_mon_enabled";
	public static final String THREAD_CPU_TIME_ENABLED = "thread_cpu_time_enabled";
}
