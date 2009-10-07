/*
 * Created on 12-Jan-2005
 */
package com.ixora.rms.agents.weblogic.messages;

/**
 * @author Daniel Moraru
 */
public interface Msg {
// agent name
	public static final String NAME = "agents.weblogic";

// configuration entries
	// 9.0
	public static final String JMX_CONNECTION_STRING = "jmx_connection_string";
	public static final String WLS_HOME = "wls_home";
	public static final String WLS_CLASSPATH = "wls_classpath";
	// 8.0
	public static final String JMX_PROVIDER_URL = "jmx_provider_url";
	public static final String JNDI_NAME = "jndi_name";
	public static final String SHOW_JUST_RUNTIME_DATA = "show_just_runtime_data";

// errors
	public static final String ERROR_WEBLOGIC_NOT_INSTALLED_ON_HOST =
		"error.weblogic_not_installed_on_host";
	public static final String ERROR_COMMUNICATION_ERROR =
		"error.communication_error";
}
