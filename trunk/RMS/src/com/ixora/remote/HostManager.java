package com.ixora.remote;

import java.rmi.RemoteException;

import com.ixora.remote.exception.RemoteManagedListenerIsUnreachable;
import com.ixora.rms.HostInformation;
import com.ixora.common.remote.PingableRemote;
import com.ixora.common.remote.Shutdownable;
import com.ixora.common.update.multinode.Updateable;
import com.ixora.rms.exception.RMSException;

/**
 * Host manager.
 * @author Daniel Moraru
 */
public interface HostManager extends PingableRemote, Updateable, Shutdownable{
	/**
	 *@return host info
	 */
	HostInformation getHostInfo() throws RMSException, RemoteException;
	/**
	 * @return a remotely managed object
	 * @param clazz the class with the implementation of the
	 * RemoteManaged
	 * @param host the host name of this machine as known by the client
	 * @param listener the listener for the newly created RemoteManaged; it could be null
	 * @throws RemoteManagedListenerIsUnreachable thrown if the passed in <code>listener</code>
	 * is unreachable; if this exception is thrown the caller can switch to polling and invoke
	 * this method again passing in <code>null</code> as <code>listener</code>.
	 * @throws RMSException
	 * @throws RemoteException
	 */
	RemoteManaged createManaged(String clazz,
			String host, RemoteManagedListener listener) throws RemoteManagedListenerIsUnreachable, RMSException, RemoteException;
	/**
	 * @param rm remotly managed object to destroy
	 * @throws RMSException
	 * @throws RemoteException
	 */
	void destroyManaged(RemoteManaged am)
			throws RMSException, RemoteException;
}
