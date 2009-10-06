package com.ixora.remote.messages;

/**
 * Application messages keys.
 * @author Daniel Moraru
 */
public interface Msg {
	// message keys
	public static final String HOST_MANAGER_HOST_MANAGER_STARTED =
		"hostmanager.host_manager_started";
	public static final String HOST_MANAGER_LOST_CLIENT_DETECTED =
		"hostmanager.lost_client_detected";
	public static final String HOST_MANAGER_ERROR_CONNECTION_NOT_ALLOWED =
		"hostmanager.error.connection_not_allowed";
    public static final String HOST_MANAGER_ERROR_NO_NETWORK_INTERFACES =
        "hostmanager.error.no_network_interfaces";
    public static final String HOST_MANAGER_MULTIPLE_IP_ADDRESSES_DETECTED =
        "hostmanager.multiple_ip_addresses_detected";
    public static final String HOST_MANAGER_SELECT_IP_ADDRESS =
        "hostmanager.select_ip_address";
    public static final String HOST_MANAGER_ERROR_INVALID_ENTRY =
        "hostmanager.error.invalid_entry";
	public static final String HOST_MANAGER_HOST_MANAGER_STARTED_ON_IP_ADDRESS = 
		"hostmanager.host_manager_started_on_ip_address";
	public static final String HOST_MANAGER_ERROR_NOT_STARTED = 
		"hostmanager.error.host_manager_not_started";
	public static final String HOST_MANAGER_STOPPED = 
		"hostmanager.host_manager_stopped";
	public static final String HOST_MANAGER_ERROR_IP_ADDRESS_MISSING = 
		"hostmanager.error.ip_address_missing";
}
