package com.ixora.common.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author Daniel Moraru
 */
public interface PingableRemote extends Remote {
	/**
	 * Ping.
	 * @throws RemoteException
	 */
	void ping() throws RemoteException;
}
