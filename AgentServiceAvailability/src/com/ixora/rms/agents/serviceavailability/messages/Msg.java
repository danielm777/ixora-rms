/*
 * Created on 12-Jan-2005
 */
package com.ixora.rms.agents.serviceavailability.messages;

/**
 * @author Daniel Moraru
 */
public interface Msg {
// agent name
	public static final String NAME
		= "agents.serviceavailability";

//	 configuration entries
	public static final String DATA
		= "data";
	public static final String PORT
		= "port";
	public static final String READ_TIMEOUT
		= "read_timeout";

// entities
	public static final String SERVICE
		= "service";

// counters
	public static final String COUNTER_SERVICE_RESPONSE_TIME
		= "service.response_time";
	public static final String COUNTER_SERVICE_MISSES
		= "service.misses";
	public static final String COUNTER_SERVICE_TIME_TO_CONNECT
		= "service.time_to_connect";
	public static final String COUNTER_SERVICE_TIME_TO_WRITE
		= "service.time_to_write";
	public static final String COUNTER_SERVICE_BYTES_IN_REPLY
		= "service.bytes_in_reply";
    public static final String COUNTER_SERVICE_REPLY
        = "service.reply";
}
