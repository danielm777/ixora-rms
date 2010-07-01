/*
 * Created on 12-Jan-2005
 */
package com.ixora.rms.agents.sapnw.messages;

/**
 * @author Daniel Moraru
 */
public interface Msg {
// agent name
	public static final String NAME = "agents.sapnw";

// configuration entries
	// 7.1
	public static final String P4_PORT = "p4_port";

// errors
	public static final String ERROR_SAPNW_NOT_INSTALLED_ON_HOST = 
		"error.sapnw_not_installed_on_host";
}
