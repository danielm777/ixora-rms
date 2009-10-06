package com.ixora.common.remote;

import com.ixora.common.remote.exception.ServiceUnreachable;



/**
 * @author Daniel Moraru
 */
public interface Pingable {
	/**
	 * Ping.
	 */
	void ping() throws ServiceUnreachable;
}
