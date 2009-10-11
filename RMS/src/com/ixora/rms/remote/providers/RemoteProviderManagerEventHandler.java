/*
 * Created on 28-Dec-2004
 */
package com.ixora.rms.remote.providers;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import com.ixora.RMIServiceNames;
import com.ixora.common.RMIServices;
import com.ixora.common.remote.ClientSocketFactory;
import com.ixora.common.remote.ServerSocketFactory;
import com.ixora.common.thread.RunQueue;
import com.ixora.rms.providers.HostProviderManager;
import com.ixora.rms.providers.ProviderDataBuffer;
import com.ixora.rms.providers.ProviderId;
import com.ixora.rms.providers.ProviderState;

/**
 * @author Daniel Moraru
 */
public class RemoteProviderManagerEventHandler extends UnicastRemoteObject
		implements RemoteProviderManagerListener {
	private static final long serialVersionUID = 648093285654701018L;
	/**
	 * Local listener.
	 */
	private HostProviderManager.Listener localListener;
	/**
	 * Event queue. The processing of events is done
	 * asynchronously to break the call chain and avoid
	 * deadlocks.
	 */
	private RunQueue processor;

    /**
     * Constructor.
     * @throws java.rmi.RemoteException
     */
    public RemoteProviderManagerEventHandler(HostProviderManager.Listener listener)
                throws RemoteException {
        super(RMIServices.instance().getPort(RMIServiceNames.REMOTEPROVIDERMANAGEREVENTHANDLER));
        init(listener);
    }

	/**
	 * Constructor.
	 * @throws java.rmi.RemoteException
	 */
	public RemoteProviderManagerEventHandler(ClientSocketFactory csf,
            ServerSocketFactory ssf, HostProviderManager.Listener listener)
				throws RemoteException {
		super(RMIServices.instance().getPort(RMIServiceNames.REMOTEPROVIDERMANAGEREVENTHANDLER),
				csf, ssf);
        init(listener);
	}

    /**
     * @param listener
     */
    private void init(HostProviderManager.Listener listener) {
        if(listener == null) {
            throw new IllegalArgumentException("null local listener");
        }
        this.localListener = listener;
        this.processor = new RunQueue();
        this.processor.start();
    }

	/**
	 * @see com.ixora.rms.remote.providers.RemoteProviderManagerListener#providerStateChanged(com.ixora.rms.internal.HostId, com.ixora.rms.providers.ProviderId, com.ixora.rms.internal.providers.ProviderState, java.lang.Throwable)
	 */
	public void providerStateChanged(final ProviderId provider,
			final ProviderState state, final Throwable err) throws RemoteException {
		this.processor.run(new Runnable() {
			public void run() {
				localListener.providerStateChanged(provider, state, err);
			}});
	}

	/**
	 * @see com.ixora.rms.remote.providers.RemoteProviderManagerListener#data(com.ixora.rms.providers.ProviderDataBuffer[])
	 */
	public void data(final ProviderDataBuffer[] buff) throws RemoteException {
		if(buff == null) {
			return;
		}
		this.processor.run(new Runnable() {
			public void run() {
				localListener.data(buff);
			}});
	}
}
