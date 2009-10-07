/*
 * Created on 12-Jan-2005
 */
package com.ixora.rms.agents.hostavailability.messages;

/**
 * @author Daniel Moraru
 */
public interface Msg {
// agent name
	public static final String NAME
		= "agents.hostavailability";

//	 configuration entries
	public static final String PING_TIMEOUT
		= "ping.timeout";
	public static final String PACKET_SIZE
		= "ping.packet_size";

// entities
	public static final String ENTITY_PING
		= "ping";

// counters
	public static final String COUNTER_PING_RESPONSE_TIME
		= "ping.response_time";
	public static final String COUNTER_PING_MISSES
		= "ping.misses";
}
