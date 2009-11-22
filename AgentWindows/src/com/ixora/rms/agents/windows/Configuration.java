/*
 * Created on 03-Jun-2005
 */
package com.ixora.rms.agents.windows;

import com.ixora.rms.agents.utils.AuthenticationConfiguration;
import com.ixora.rms.agents.windows.messages.Msg;

/**
 * Configuration
 * Parameters for Windows Agent
 */
public class Configuration extends AuthenticationConfiguration {
	private static final long serialVersionUID = -1654744804032767567L;
	// value keys
    public static final String DOMAIN = Msg.WINDOWSAGENT_DOMAIN;

    /**
     * Empty constructor. Required to allow the associated panel
     * to create an empty configuration instance for editing.
     */
    public Configuration() {
        super();

        setProperty(DOMAIN, TYPE_STRING, true, false);
    }
}
