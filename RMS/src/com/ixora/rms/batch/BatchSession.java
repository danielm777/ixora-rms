/**
 * 21-Mar-2006
 */
package com.ixora.rms.batch;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.ixora.common.remote.Shutdownable;
import com.ixora.rms.exception.RMSException;

/**
 * @author Daniel Moraru
 */
public interface BatchSession extends Remote, Shutdownable {
	/**
	 * Configures the batch session.
	 * @param conf
	 * @throws RemoteException
	 */
	void configure(BatchSessionConfiguration conf) throws RemoteException, RMSException;
	/**
	 * Starts the batch session.
	 * @throws RemoteException
	 */
	void start() throws RemoteException, RMSException;
	/**
	 * Stops the batch session.
	 * @throws RemoteException
	 */
	void stop() throws RemoteException, RMSException;
}
