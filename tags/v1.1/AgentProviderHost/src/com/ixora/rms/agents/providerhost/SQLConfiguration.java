/*
 * Created on 07-Apr-2005
 */
package com.ixora.rms.agents.providerhost;

import com.ixora.rms.agents.AgentCustomConfiguration;
import com.ixora.rms.agents.messages.Msg;

/**
 * @author Daniel Moraru
 */
public class SQLConfiguration extends AgentCustomConfiguration {

	/**
	 * Constructor.
	 */
	public SQLConfiguration() {
		super();
		setProperty(Msg.SQL_USERNAME, TYPE_STRING, true, false);
		setProperty(Msg.SQL_PASSWORD, TYPE_SECURE_STRING, true, false);
		setProperty(Msg.SQL_DATABASE_NAME, TYPE_STRING, true, false);
		setProperty(Msg.SQL_DATABASE_PORT, TYPE_INTEGER, true, false);
		setProperty(Msg.SQL_JDBC_CLASS, TYPE_STRING, true, false);
		setProperty(Msg.SQL_CLASSPATH, TYPE_STRING, true, false);
	}
}
