/*
 * Created on 26-Dec-2005
 */
package com.ixora.rms.agents.mysql.v50;

import com.ixora.rms.agents.mysql.Msg;

/**
 * Configuration
 */
public class Configuration extends com.ixora.rms.agents.mysql.Configuration {
	private static final long serialVersionUID = 739422512565473016L;

	/**
	 * Empty constructor. Required to allow the associated panel
	 * to create an empty configuration instance for editing.
	 */
	public Configuration(){
		super();
		setString(Msg.JDBC_DRIVER_JAR_NAME, "/lib/mysql-connector-java-5.0.0-beta-bin.jar");
	}
}
