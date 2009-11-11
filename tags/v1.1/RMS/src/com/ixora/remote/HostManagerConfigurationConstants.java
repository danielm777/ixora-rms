/*
 * Created on 05-Jun-2004
 */
package com.ixora.remote;

/**
 * Configuration constants for HostManager.
 * @author Daniel Moraru
 */
public interface HostManagerConfigurationConstants {
	// properties
	public static final String CHECK_LOST_CLIENTS_INTERVAL = "hostmanager.check_lost_clients_interval";
	public static final String HOSTS_ALLOWED =	"hostmanager.hosts_allowed";
	public static final String HOST_NAME_SECURITY_ENABLED =	"hostmanager.host_name_security_enabled";
	public static final String SOCKET_FACTORY_LOCAL_IP_ADDRESS = "hostmanager.socket_factory_local_ip_address";
}
