/*
 * Created on 27-Jul-2004
 */
package com.ixora.remote;

import java.rmi.RemoteException;

import com.ixora.remote.exception.RemoteManagedListenerIsUnreachable;
import com.ixora.common.remote.Shutdownable;
import com.ixora.rms.exception.RMSException;

/**
 * Object managed by HostManager.<br>
 * Implementation note: All implementations must have a
 * default constructor.
 * @author Daniel Moraru
 */
public interface RemoteManaged extends Shutdownable {
	/**
	 * Initializes this managed object.
	 * @param host
	 * @param listener it could be null
	 * @throws RemoteManagedListenerIsUnreachable thrown if the passed in <code>listener</code>
	 * is unreachable; if this exception is thrown the caller can switch to polling and invoke
	 * this method again passing in <code>null</code> as <code>listener</code>.
	 * @throws RMSException
	 * @throws RemoteException
	 */
	void initialize(String host, RemoteManagedListener listener)
		throws RemoteManagedListenerIsUnreachable, RMSException, RemoteException;
}
