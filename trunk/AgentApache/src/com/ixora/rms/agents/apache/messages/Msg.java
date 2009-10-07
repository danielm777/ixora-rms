/*
 * Created on 15-Mar-2004
 */
package com.ixora.rms.agents.apache.messages;

/**
 * Messages for Apache agent.
 * @author Daniel Moraru
 */
public interface Msg {
// agent name
	public static final String APACHE_NAME
		= "agents.apache";

// configuration entries
	public static final String APACHE_PORT
		= "port";

// entities
	public static final String APACHE_ENTITY_STATUS
		= "status";
	public static final String APACHE_ENTITY_STATUS_DESCRIPTION
		= "status.description";

	public static final String APACHE_ENTITY_SERVERS
		= "servers";
	public static final String APACHE_ENTITY_SERVERS_DESCRIPTION
		= "servers.description";
	public static final String APACHE_ENTITY_REQUESTS
		= "requests";
	public static final String APACHE_ENTITY_REQUESTS_DESCRIPTION
		= "requests.description";

// counters
	public static final String APACHE_ENTITY_STATUS_COUNTER_TOTAL_ACCESSES
		= "total_accesses";
	public static final String APACHE_ENTITY_STATUS_COUNTER_TOTAL_TRAFFIC
		= "total_traffic";
	public static final String APACHE_ENTITY_STATUS_COUNTER_TOTAL_NUMBER_OF_SERVERS
		= "total_number_of_servers";
	public static final String APACHE_ENTITY_STATUS_COUNTER_REQUESTS_PER_SECOND
		= "requests_per_second";
	public static final String APACHE_ENTITY_STATUS_COUNTER_KILOBYTES_PER_SECOND
		= "kilobytes_per_second";
	public static final String APACHE_ENTITY_STATUS_COUNTER_KILOBYTES_PER_REQUEST
		= "kilobytes_per_request";
	public static final String APACHE_ENTITY_STATUS_COUNTER_IDLE_SERVERS
		= "idle_servers";
	public static final String APACHE_ENTITY_STATUS_COUNTER_SERVERS_IN_WAITING_FOR_CONNECTION
		= "servers_waiting_for_connection";
	public static final String APACHE_ENTITY_STATUS_COUNTER_SERVERS_IN_STARTING_UP
		= "servers_in_starting_up";
	public static final String APACHE_ENTITY_STATUS_COUNTER_SERVERS_IN_READING_REQUEST
		= "servers_in_reading_request";
	public static final String APACHE_ENTITY_STATUS_COUNTER_SERVERS_IN_SENDING_REPLY
		= "servers_in_sending_reply";
	public static final String APACHE_ENTITY_STATUS_COUNTER_SERVERS_IN_KEEPALIVE
		= "servers_in_keepalive";
	public static final String APACHE_ENTITY_STATUS_COUNTER_SERVERS_IN_DNSLOOKUP
		= "servers_in_dnslookup";
	public static final String APACHE_ENTITY_STATUS_COUNTER_SERVERS_IN_LOGGING
		= "servers_in_logging";
	public static final String APACHE_ENTITY_STATUS_COUNTER_SERVERS_IN_GRACEFULLY_FINISHING
		= "servers_in_gracefully_finishing";

	public static final String APACHE_ENTITY_SERVER_COUNTER_CHILD_SERVER_NUMBER
		= "child_server_number";
	public static final String APACHE_ENTITY_SERVER_COUNTER_OS_PROCESS_ID
		= "os_process_id";
	public static final String APACHE_ENTITY_SERVER_COUNTER_ACCESSES_THIS_CONNECTION
		= "accesses_this_connection";
	public static final String APACHE_ENTITY_SERVER_COUNTER_ACCESSES_THIS_CHILD
		= "accesses_this_child";
	public static final String APACHE_ENTITY_SERVER_COUNTER_ACCESSES_THIS_SLOT
		= "accesses_this_slot";
	public static final String APACHE_ENTITY_SERVER_COUNTER_SECONDS_SINCE_MOST_RECENT_REQUEST
		= "seconds_since_most_recent_request";
	public static final String APACHE_ENTITY_SERVER_COUNTER_KILOBYTES_TRANSFERRED_THIS_CONNECTION
		= "kilobytes_transferred_this_connection";
	public static final String APACHE_ENTITY_SERVER_COUNTER_MEGABYTES_TRANSFERRED_THIS_CHILD
		= "megabytes_transferred_this_child";
	public static final String APACHE_ENTITY_SERVER_COUNTER_TOTAL_MEGABYTES_TRANSFERRED_THIS_SLOT
		= "total_megabytes_transferred_this_slot";

	public static final String APACHE_ENTITY_REQUEST_COUNTER_MILLS_REQUIRED_TO_PROCESS_REQUEST
		= "mills_required_to_process_request";
	public static final String APACHE_ENTITY_REQUEST_COUNTER_CLIENT
		= "client";
	public static final String APACHE_ENTITY_REQUEST_COUNTER_VHOST
		= "vhost";

// errors
	public static final String APACHE_ERROR_FAILED_TO_GET_STATUS_PAGE
		= "error.failed_to_get_status_page";
	public static final String APACHE_ERROR_INVALID_DATA_PATTERN
		= "error.invalid_data_pattern";
}
