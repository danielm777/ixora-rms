/**
 * 12-Jul-2005
 */
package com.ixora.rms;

import java.io.Serializable;

/**
 * @author Daniel Moraru
 */
public interface HostDataBuffer extends Serializable {

	/**
	 * @return the host.
	 */
	HostId getHost();

	/**
	 * @return the timeDelta.
	 */
	long getTimeDelta();

	/**
	 * @param host
	 */
	void setHost(String host);

	/**
	 * @param host
	 */
	void setHost(HostId host);

	/**
	 * Sets the time difference between the local and
	 * the remote host
	 * @param delta
	 */
	void setTimeDelta(long delta);

}