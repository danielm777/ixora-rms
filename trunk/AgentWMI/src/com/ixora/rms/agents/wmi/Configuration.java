/*
 * Created on Aug 20, 2005
 */
package com.ixora.rms.agents.wmi;

import com.ixora.rms.agents.utils.AuthenticationConfiguration;
import com.ixora.rms.agents.wmi.messages.Msg;

/**
 * Configuration
 */
public class Configuration extends AuthenticationConfiguration {
	public static final String NAMESPACE = Msg.WMI_CONFIG_NAMESPACE;

	/**
	 * Empty constructor. Required to allow the associated panel
	 * to create an empty configuration instance for editing.
	 */
	public Configuration() {
		super();
		//setProperty(NAMESPACE, TYPE_STRING, true);
		//setString(NAMESPACE, "root\\cimv2");
	}
}
