package com.ixora.rms;

import java.rmi.RemoteException;

import com.ixora.remote.HostManager;
import com.ixora.remote.RemoteManaged;
import com.ixora.remote.RemoteManagedListener;
import com.ixora.remote.exception.RemoteManagedListenerIsUnreachable;
import com.ixora.common.ConfigurationMgr;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.exception.UnreachableHostManager;

/**
 * This class handles an instance of RemoteManaged for the monitoring session.
 * @author Daniel Moraru
 */
public abstract class HostRemoteManagedHandler {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(HostRemoteManagedHandler.class);
	/** Host monitor */
	protected HostMonitor fHostMonitor;
	/** Remote agent manager */
	protected volatile RemoteManaged fRemoteManaged;
	/** Host */
	protected String fHost;
	/** Remote listener for agents */
	private RemoteManagedListener fRemoteManagedListener;
	/**
	 * True if this host requires the use of the global collector; this happens
	 * if the bidirectional communication is not possible between the remote host
	 * and the console(and hence the remote listener is unusable).
	 */
	protected boolean fNeedsGlobalCollector;

	/**
	 * @param hm
	 * @param host
	 * @param listenerRemoteManaged
	 */
	protected HostRemoteManagedHandler(
			HostMonitor hm,
			String host,
			RemoteManagedListener listenerRemoteManaged) {
		if(hm == null) {
			throw new IllegalArgumentException("null host monitor");
		}
		if(listenerRemoteManaged == null) {
			throw new IllegalArgumentException("null remote managed listener");
		}
		this.fHost = host;
		this.fHostMonitor = hm;
		this.fRemoteManagedListener = listenerRemoteManaged;
	}
	/**
	 * @return a remote agent reference
	 * @throws RMSException
	 * @throws RemoteException
	 */
	protected RemoteManaged createRemoteManaged(String className) throws RMSException, RemoteException {
		if(this.fRemoteManaged != null) {
			return this.fRemoteManaged;
		}
		HostManager hm = fHostMonitor.getHostManager(fHost);
		if(hm == null) {
			throw new UnreachableHostManager(fHost);
		}
		boolean allowDataPush = ConfigurationMgr.get(RMSComponent.NAME)
			.getBoolean(RMSConfigurationConstants.ALLOW_DATA_PUSH);
		if(allowDataPush) {
			try {
				this.fRemoteManaged = hm.createManaged(
					className,
					fHost, fRemoteManagedListener);
			} catch(RemoteManagedListenerIsUnreachable e) {
				this.fRemoteManaged = hm.createManaged(
						className,
						fHost, null);
				this.fNeedsGlobalCollector = true;
				if(logger.isTraceEnabled()) {
					logger.error("The global collector will be used for the HostManager on host "
							+ fHost + " because of exception", e);
				}
			}
		} else {
			// this will disable data push and force the use of the
			// global collector
			this.fRemoteManaged = hm.createManaged(
					className,
					fHost, null);
			fNeedsGlobalCollector = true;
		}
		return this.fRemoteManaged;
	}

	/**
	 * Invoked when the remote host went offline.
	 */
	public void hostWentOffline() {
		this.fRemoteManaged = null;
	}

	/**
	 * @return true if the host requires the global collector
	 */
	public boolean hostNeedsGlobalCollector() {
		return fNeedsGlobalCollector;
	}

	/**
	 *
	 */
	public void destroyRemoteManaged() {
		HostManager hm = fHostMonitor.getHostManager(fHost);
		if(hm != null && fRemoteManaged != null) {
			try {
				hm.destroyManaged(fRemoteManaged);
			} catch(Exception e) {
				logger.error(e);
			}
			fRemoteManaged = null;
		}
	}

	/**
	 * @return
	 */
	public String getHost() {
		return fHost;
	}
}