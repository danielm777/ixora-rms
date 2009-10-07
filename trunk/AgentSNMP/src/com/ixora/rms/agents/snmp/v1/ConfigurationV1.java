/*
 * Created on 20-Aug-2005
 */
package com.ixora.rms.agents.snmp.v1;

import com.ixora.rms.agents.snmp.Configuration;
import com.ixora.rms.agents.snmp.messages.Msg;

/**
 * Configuration
 */
public class ConfigurationV1 extends Configuration {
	public static final String COMUNITY = Msg.COMUNITY;

	/**
	 * Empty constructor. Required to allow the associated panel
	 * to create an empty configuration instance for editing.
	 */
	public ConfigurationV1() {
		super();
		setProperty(COMUNITY, TYPE_STRING, true);
		setString(COMUNITY, "public");
	}
}
