/*
 * Created on 25-Dec-2005
 */
package com.ixora.rms.agents.url.messages;

/**
 * @author Daniel Moraru
 */
public interface Msg {
	public static final String AGENT_URL_NAME = "agents.url";

	// config
	public static final String URL = "config.url";
	public static final String HTTP_METHOD = "config.http_method";
	public static final String HTTP_PARAMETERS = "config.http_parameters";

	// counters
	public static final String COUNTER_RESPONSE_TIME = "response_time";
	public static final String COUNTER_RESPONSE_SIZE = "response_size";
	public static final String COUNTER_RESPONSE_CONTENT = "response_content";
	public static final String COUNTER_RESPONSE_STATUS_CODE = "response_status_code";
}
