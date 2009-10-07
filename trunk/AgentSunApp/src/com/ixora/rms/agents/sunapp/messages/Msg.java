/*
 * Created on 12-Jan-2005
 */
package com.ixora.rms.agents.sunapp.messages;

/**
 * @author Daniel Moraru
 */
public interface Msg {
// agent name
	public static final String NAME = "agents.sunapp";

// configuration entries
	public static final String JMX_CONNECTION_STRING = "jmx_connection_string";
	public static final String SHOW_JUST_RUNTIME_DATA = "show_just_runtime_data";

// errors
	public static final String ERROR_SUNAPP_NOT_INSTALLED_ON_HOST =
			"error.sunapp_not_installed_on_host";
}
