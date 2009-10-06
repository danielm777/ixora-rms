package com.ixora.common.remote;

/**
 * @author Daniel Moraru
 */
public final class ICMPPingableFactory implements PingableFactory {
	/**
	 * Service ID.
	 */
	private int serviceID;

	/**
	 * Constructor for ICMPPingableFactory.
	 */
	public ICMPPingableFactory(int serviceID) {
		super();
		this.serviceID = serviceID;
	}

	/**
	 * @see com.ixora.common.remote.PingableFactory#getPingable(String)
	 */
	public Pingable getPingable(String host) {
		return new ICMPPingable(host, 5000);
	}

	/**
	 * @see com.ixora.common.remote.PingableFactory#getServiceID()
	 */
	public int getServiceID() {
		return this.serviceID;
	}

}
