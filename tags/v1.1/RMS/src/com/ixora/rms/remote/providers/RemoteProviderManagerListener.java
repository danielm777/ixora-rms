/*
 * Created on 28-Dec-2004
 */
package com.ixora.rms.remote.providers;

import java.rmi.RemoteException;

import com.ixora.remote.RemoteManagedListener;
import com.ixora.rms.providers.ProviderDataBuffer;
import com.ixora.rms.providers.ProviderId;
import com.ixora.rms.providers.ProviderState;

/**
 * @author Daniel Moraru
 */
public interface RemoteProviderManagerListener extends RemoteManagedListener {
	/**
	 * Invoked when the state of a provider has changed.
	 * @param host
	 * @param provider
	 * @param state
	 * @param err migh be not null if <code>state</code> is ERROR
	 */
	void providerStateChanged(ProviderId provider, ProviderState state, Throwable err) throws RemoteException;
	/**
	 * Invoked when provided data becomes available.
	 * @param buff
	 */
	void data(ProviderDataBuffer[] buff) throws RemoteException;
}
