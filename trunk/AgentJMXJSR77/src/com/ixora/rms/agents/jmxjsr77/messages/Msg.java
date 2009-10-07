/*
 * Created on 12-Jan-2005
 */
package com.ixora.rms.agents.jmxjsr77.messages;

/**
 * @author Daniel Moraru
 */
public interface Msg {
// agent name
	public static final String NAME = "agents.jmxjsr77";

// configuration entries
	public static final String SERVER_URL = "server_url";
	public static final String ROOT_FOLDER = "root_folder";
	public static final String CLASSPATH = "classpath";
	public static final String EXTRA_PROPERTIES = "extra_properties";
	public static final String INITIAL_CTXT_FACTORY = "initial_ctxt_factory";
	public static final String JNDI_NAME = "jndi_name";
}
