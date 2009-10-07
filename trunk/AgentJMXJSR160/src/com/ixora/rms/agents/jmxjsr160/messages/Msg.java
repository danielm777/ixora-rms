/*
 * Created on 12-Jan-2005
 */
package com.ixora.rms.agents.jmxjsr160.messages;

/**
 * @author Daniel Moraru
 */
public interface Msg {
// agent name
	public static final String NAME = "agents.jmxjsr160";

// configuration entries
	public static final String JMX_CONNECTION_STRING = "jmx_connection_string";
	public static final String ROOT_FOLDER = "root_folder";
	public static final String CLASSPATH = "classpath";
	public static final String EXTRA_PROPERTIES = "extra_properties";
}
