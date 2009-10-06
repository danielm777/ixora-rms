/*
 * Created on 17-Jun-2004
 */
package com.ixora.common.update.multinode;

import java.rmi.RemoteException;

import com.ixora.common.exception.AppException;
import com.ixora.common.remote.PingableRemote;

/**
 * Updateable remote interface; extends <code>PingableRemote</code>
 * to allow to test for network presence.
 * @author Daniel Moraru
 */
public interface Updateable extends PingableRemote {
	/**
	 * Installs the given updates.
	 * @param parts
	 */
	void update(UpdatePart[] parts) throws RemoteException, AppException;
	/**
	 * Restarts the application.
	 * @throws RemoteException
	 * @throws AppException
	 */
	void restart() throws RemoteException, AppException;
	/**
	 * @return the name of the operating system as returned
	 * by the <code>System.getProperty("os.name")</code>.
	 */
	String getOsName() throws RemoteException, AppException;
}
