package com.ixora.common.remote;

/**
 * PingableFactory
 * @author Daniel Moraru
 */
public interface PingableFactory {
	/**
	 * @return a Pingable service for the remote host.
	 * @param host host hosting the pingable service
	 */
	Pingable getPingable(String host);
	/**
	 * @return the service id.
	 */
	int getServiceID();
}
