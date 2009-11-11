package com.ixora.rms.agents.windows.messages;

/**
 * @author Cristian Costache
 */
public interface Msg
{
	public static final String WINDOWSAGENT_NAME =
		"agents.windows";
	public static final String WINDOWSAGENT_ERROR_REMOTE_CONNECT_FAILED =
		"windows.error.remote_connect_failed";
	public static final String WINDOWSAGENT_ERROR_NOT_SUPPORTED =
		"windows.error.agent_not_supported";
    public static final String WINDOWSAGENT_DOMAIN =
        "config.domain";
    public static final String WINDOWSAGENT_COLLECT_DATA_FAILED =
        "windows.error.collect_data_failed";
}
