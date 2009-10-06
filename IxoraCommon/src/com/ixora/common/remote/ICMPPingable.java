package com.ixora.common.remote;

import java.net.InetAddress;

import com.ixora.common.remote.exception.ServiceUnreachable;



/**
 * @author Daniel Moraru
 */
public final class ICMPPingable implements Pingable {
	/** Host to ping */
	private InetAddress hostAddr;
	/** Host name */
	private String host;
	/** Ping timeout */
	private int timeout;

	/**
	 * @param host
	 * @param timeout
	 */
	public ICMPPingable(String host, int timeout) {
		super();
		if(host == null) {
			throw new IllegalArgumentException();
		}
		this.host = host;
		this.timeout = timeout;
	}

	/**
	 * @see com.ixora.common.remote.Pingable#ping()
	 */
	public void ping() throws ServiceUnreachable {
		try {
			if(this.hostAddr == null) {
				this.hostAddr = InetAddress.getByName(host);
			}
			this.hostAddr.isReachable(timeout);
		} catch(Throwable e) {
			throw new ServiceUnreachable();
		}
	}

}
