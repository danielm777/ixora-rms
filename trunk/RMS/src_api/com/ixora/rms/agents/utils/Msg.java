/*
 * Created on 27-Dec-2003
 */
package com.ixora.rms.agents.utils;

/**
 * @author Daniel Moraru
 */
public interface Msg {
// configuration entries
	public static final String AGENT_CONFIGURATION_USERNAME =
		"config.username";
	public static final String AGENT_CONFIGURATION_PASSWORD =
		"config.password";
	public static final String AGENT_CONFIGURATION_PORT =
		"config.port";
	public static final String AGENT_CONFIGURATION_JDBCSTRING =
		"config.jdbcstring";
	public static final String AGENT_CONFIGURATION_JDBCCLASS =
		"config.jdbcclass";
    public static final String AGENT_CONFIGURATION_ODBCDRIVER =
        "config.odbcdriver";
    public static final String ROOT_FOLDER =
        "root_folder";
    public static final String CLASSPATH =
        "classpath";
    public static final String EXTRA_PROPERTIES =
        "extra_properties";
}
