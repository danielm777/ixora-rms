/*
 * Created on 19-Jun-2004
 */
package com.ixora.common.update.multinode;

import com.ixora.common.remote.RMIPingable;
import com.ixora.common.remote.RMIPingableFactory;

/**
 * Applications provide here access to their updateable objects.
 * @author Daniel Moraru
 */
public final class UpdateableFactory {
	/** Pingable factory */
	private RMIPingableFactory pingableFactory;

	/**
	 * Constructor.
	 * @param remoteObjectName
	 * @param rmiRegistryPort
	 */
	public UpdateableFactory(
			String remoteObjectName,
			int rmiRegistryPort) {
		this.pingableFactory = new RMIPingableFactory(
				remoteObjectName, rmiRegistryPort, 0);
	}
	/**
	 * @return updateable object
	 */
	public Updateable getUpdateable(String host) {
		return (Updateable)
			((RMIPingable)this.pingableFactory.getPingable(host))
					.getRemoteObject();
	}

// not visible outside this package
	/**
	 * @return the RMI pingable factory
	 */
	RMIPingableFactory getRMIPingableFactory() {
		return this.pingableFactory;
	}
}
