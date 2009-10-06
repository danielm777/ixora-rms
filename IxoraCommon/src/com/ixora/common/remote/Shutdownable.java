/*
 * Created on 27-Jul-2004
 */
package com.ixora.common.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.ixora.common.exception.AppException;

/**
 * @author Daniel Moraru
 */
public interface Shutdownable extends Remote {
	/**
	 * Shuts down this managed object.
	 * @throws AppException
	 * @throws RemoteException
	 */
	void shutdown() throws AppException, RemoteException;
}
