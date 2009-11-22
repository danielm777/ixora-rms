/*
 * Created on 20-Aug-2005
 */
package com.ixora.rms.agents.snmp.v2c;

import com.ixora.rms.agents.snmp.Configuration;
import com.ixora.rms.agents.snmp.messages.Msg;

/**
 * Configuration
 */
public class ConfigurationV2c extends Configuration {
	private static final long serialVersionUID = 6507952640732915062L;
	public static final String COMUNITY = Msg.COMUNITY;

	/**
	 * Empty constructor. Required to allow the associated panel
	 * to create an empty configuration instance for editing.
	 */
	public ConfigurationV2c() {
		super();
		setProperty(COMUNITY, TYPE_STRING, true);
		setString(COMUNITY, "public");
	}
}
