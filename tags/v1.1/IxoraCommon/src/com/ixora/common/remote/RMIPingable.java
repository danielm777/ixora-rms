package com.ixora.common.remote;

import java.rmi.RemoteException;

import com.ixora.common.remote.exception.ServiceUnreachable;


/**
 * @author Daniel Moraru
 */
public final class RMIPingable implements Pingable {
	/** Remote object */
	private PingableRemote obj;

	/**
	 * Constructor for RMIPingable.
	 * @param PingableRemote
	 */
	public RMIPingable(PingableRemote obj) {
		super();
		if(obj == null) {
			throw new IllegalArgumentException("null remote pingable");
		}
		this.obj = obj;
	}

	/**
	 * @see com.ixora.common.remote.Pingable#ping()
	 */
	public void ping() throws ServiceUnreachable {
		try {
			this.obj.ping();
		} catch (RemoteException e) {
			throw new ServiceUnreachable();
		}
	}

	/**
	 * @return the underlying remote object
	 */
	public PingableRemote getRemoteObject() {
		return this.obj;
	}

}
