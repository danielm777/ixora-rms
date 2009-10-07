/*
 * Created on 12-Jan-2005
 */
package com.ixora.rms.agents.jboss.messages;

/**
 * @author Daniel Moraru
 */
public interface Msg {
// agent name
	public static final String NAME = "agents.jboss";

// configuration entries
	public static final String SERVER_URL = "server_url";
	public static final String JNDI_NAME = "jndi_name";

// errors
	public static final String ERROR_JBOSS_NOT_INSTALLED_ON_HOST =
		"error.jboss_not_installed_on_host";
}
