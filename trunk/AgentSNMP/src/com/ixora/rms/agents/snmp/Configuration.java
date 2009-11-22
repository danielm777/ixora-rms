/*
 * Created on 20-Aug-2005
 */
package com.ixora.rms.agents.snmp;

import com.ixora.rms.agents.AgentCustomConfiguration;
import com.ixora.rms.agents.snmp.messages.Msg;

/**
 * Configuration
 */
public class Configuration extends AgentCustomConfiguration {
	private static final long serialVersionUID = -6200742078440083716L;
	public static final String USER_MIBS_FOLDER = Msg.USER_MIBS_FOLDER;
	public static final String COMPILED_MIB_FILE_NAME = Msg.COMPILED_MIB_FILE_NAME;
	public static final String PORT_NUMBER = Msg.PORT_NUMBER;
	public static final String PORT_TIMEOUT = Msg.PORT_TIMEOUT;


	/**
	 * Empty constructor. Required to allow the associated panel
	 * to create an empty configuration instance for editing.
	 */
	public Configuration() {
		super();
		setProperty(USER_MIBS_FOLDER, TYPE_STRING, true, false);
		setProperty(COMPILED_MIB_FILE_NAME, TYPE_STRING, false);
		setProperty(PORT_NUMBER, TYPE_INTEGER, true);
		setProperty(PORT_TIMEOUT, TYPE_INTEGER, true);

		setString(USER_MIBS_FOLDER, "");
		setString(COMPILED_MIB_FILE_NAME, "config/repository/agents.snmp/mibs.bin");
		setInt(PORT_NUMBER, 161);
		setInt(PORT_TIMEOUT, 1000);
	}
}
