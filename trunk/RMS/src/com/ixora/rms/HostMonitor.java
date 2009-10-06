package com.ixora.rms;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import com.ixora.remote.HostManager;
import com.ixora.common.ConfigurationMgr;
import com.ixora.common.RMIServices;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.remote.ICMPPingableFactory;
import com.ixora.common.remote.RMIPingable;
import com.ixora.common.remote.RMIPingableFactory;
import com.ixora.common.remote.ServiceInfo;
import com.ixora.common.remote.ServiceMonitor;
import com.ixora.common.remote.ServiceState;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.services.HostMonitorService;

/**
 * Monitors the availability of a remote host
 * to participate in a monitoring scheme.
 * @author Daniel Moraru
 */
public final class HostMonitor implements HostMonitorService, Observer {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(HostMonitor.class);
	/** Monitor based on host manager */
	private ServiceMonitor monitorHM;
	/** Monitor based on ICMP ping */
	private ServiceMonitor monitorPING;
	/** HostMonitor listeners */
	private List listeners;
	/** Event handler */
	private EventHandler eventHandler;
	/** Factory for the remote HostManager object */
	private RMIPingableFactory rmiPingable;

	/**
	 * Event handler.
	 */
	private final class EventHandler implements ServiceMonitor.Listener {
		/**
		 * @see com.ixora.common.remote.event.ServiceMonitor.Callback#remoteObjectStateChanged(String, HostState, HostState)
		 */
		public void serviceStateChanged(
			String host,
			int serviceID,
			ServiceState oldState,
			ServiceState newState) {
				handleServiceStateChanged(host, serviceID, oldState, newState);
		}
	}

	/**
	 * Constructor for HostMonitor.
	 * @throws StartableError
	 */
	public HostMonitor() throws Throwable {
		super();
		this.eventHandler = new EventHandler();
		this.listeners = new LinkedList();
		int pingDelay = ConfigurationMgr.getInt(
			RMSComponent.NAME,
			RMSConfigurationConstants.HOSTS_PING_DELAY);
		rmiPingable = new RMIPingableFactory(
				"HostManager",
				RMIServices.instance().getRMIRegistryPort(),
				HostReachability.HOST_MANAGER);
		this.monitorHM = new ServiceMonitor(
				null,
				rmiPingable,
				pingDelay
			);
		this.monitorHM.addListener(this.eventHandler);
		this.monitorPING = new ServiceMonitor(
			null,
			new ICMPPingableFactory(HostReachability.ICMP_PING),
				pingDelay
			);
		this.monitorPING.addListener(this.eventHandler);
		ConfigurationMgr.get(RMSComponent.NAME).addObserver(this);
	}

	/**
	 * @see java.util.Observer#update(Observable, Object)
	 */
	public void update(Observable o, Object arg) {
		if(RMSConfigurationConstants.HOSTS_PING_DELAY.equals(arg)) {
			int delay = ConfigurationMgr.getInt(
				RMSComponent.NAME,
				RMSConfigurationConstants.HOSTS_PING_DELAY);
			this.monitorHM.setPingDelay(delay);
			this.monitorPING.setPingDelay(delay);
		}
	}

   /**
     * @param host
     * @return a HostManager reference on the given host. Null if the host is offline.
     */
    public HostManager getHostManager(String host) {
    	RMIPingable p = (RMIPingable)this.monitorHM.getPingable(host);
    	if(p == null) {
    		return null;
    	}
        return (HostManager)p.getRemoteObject();
    }

   /**
     * @param host
     * @param forced if true it will try and get a reference to the host manager
     * even if the host is not part of the monitored set of hosts; if false it is equivalent
     * to <code>getHostManger(String)</code>.
     * @return a HostManager reference on the given host.
     */
    public HostManager getHostManager(String host, boolean forced) {
		HostManager hm = getHostManager(host);
		if(hm != null) {
			return hm;
		}
    	if(!forced) {
    		return null;
    	}
    	RMIPingable ping = (RMIPingable)this.rmiPingable.getPingable(host);
		if(ping == null) {
			return null;
		} else {
			return (HostManager)ping.getRemoteObject();
		}
    }

	/**
	 * @return the host state
	 */
	public HostState getHostState(String host) {
		ServiceInfo info = this.monitorHM.getServiceState(host);
		if(info == null) {
			return null;
		}
		HostState ret = new HostState(host);
		ret.setServiceState(HostReachability.HOST_MANAGER, info.getState());
		info = this.monitorPING.getServiceState(host);
		if(info != null) {
			ret.setServiceState(HostReachability.ICMP_PING, info.getState());
		}
		return ret;
	}

	/**
	 * @return the states of the monitored hosts
	 */
	public HostState[] getHostsStates() {
		Collection hosts = this.monitorPING.getHosts();
		String host;
		HostState hs;
		List ret = new LinkedList();
		for(Iterator iter = hosts.iterator(); iter.hasNext();) {
			host = (String)iter.next();
			hs = getHostState(host);
			if(hs != null) {
				ret.add(hs);
			}
		}
		return (HostState[])ret.toArray(new HostState[ret.size()]);
	}

	/**
	 * @return the host details for the given host
	 * @throws RemoteException
	 * @throws RMSException
	 */
	public HostInformation getHostInfo(String host) throws RMSException, RemoteException {
		HostManager hm = getHostManager(host);
		if(hm == null) {
			return null;
		}
		HostInformation ret = hm.getHostInfo();
		completeHostInfoDtls(ret, host);
		return ret;
	}

	/**
	 * @return the info on all the monitored hosts
	 */
	public HostInformation[] getHostsInfo() throws RMSException, RemoteException {
		Collection hosts = this.monitorHM.getHosts();
		String host;
		HostInformation hi;
		List ret = new LinkedList();
		for(Iterator iter = hosts.iterator(); iter.hasNext();) {
			host = (String)iter.next();
			hi = getHostInfo(host);
			if(hi != null) {
				ret.add(hi);
			}
		}
		return (HostInformation[])ret.toArray(new HostInformation[ret.size()]);
	}

	/**
	 * Adds hosts.
	 * @param hosts
	 * @param waitForState
	 */
	public void addHosts(Collection hosts, boolean waitForState) {
		this.monitorHM.addHosts(hosts, waitForState);
		this.monitorPING.addHosts(hosts, waitForState);
	}

	/**
	 * Removes hosts.
	 * @param hosts
	 */
	public void removeHosts(Collection<String> hosts) {
		String host;
		for(Iterator iter = hosts.iterator(); iter.hasNext();) {
			host = (String)iter.next();
			fireAboutToRemoveHost(host);
			this.monitorHM.removeHost(host);
			this.monitorPING.removeHost(host);
		}
	}

    /**
     * @see com.ixora.rms.services.HostMonitorService#addHost(java.lang.String, boolean)
     */
    public void addHost(String host, boolean waitForState) {
		this.monitorHM.addHost(host, waitForState);
		this.monitorPING.addHost(host, waitForState);
    }

    /**
     * @see com.ixora.rms.services.HostMonitorService#removeHost(java.lang.String)
     */
    public void removeHost(String host) {
		fireAboutToRemoveHost(host);
    	this.monitorHM.removeHost(host);
		this.monitorPING.removeHost(host);
    }

	/**
	 * @see com.ixora.common.Service#shutdown()
	 */
	public void shutdown() {
	}


	/**
	 * Adds a listener.
	 * @param listener
	 */
	public void addListener(Listener listener) {
		if(listener == null) {
			throw new IllegalArgumentException("null listener");
		}
		synchronized (this.listeners) {
			this.listeners.add(listener);
		}
	}

	/**
	 * Removes a listener.
	 * @param listener
	 */
	public void removeListener(Listener listener) {
		synchronized (this.listeners) {
			this.listeners.remove(listener);
		}
	}

	/**
	 * Fires the host manager state changed event.
	 * @param host
	 * @param serviceID
	 * @param oldState
	 * @param newState
	 */
	private void handleServiceStateChanged(
			String host,
			int serviceID,
			ServiceState oldState,
			ServiceState newState) {
		fireHostStateChanged(host, serviceID, oldState, newState);
		switch(serviceID) {
			case HostReachability.HOST_MANAGER :
			// if new state is online check the host info
			if(newState == ServiceState.ONLINE
				&& (oldState == ServiceState.OFFLINE
					|| oldState == ServiceState.UNKNOWN)) {
				HostManager hm = (HostManager)((RMIPingable)this.monitorHM.getPingable(host)).getRemoteObject();
				try {
					fireUpdateHostInfo(host, hm.getHostInfo());
				} catch (RMSException e) {
					logger.error(e);
				} catch (RemoteException e) {
					logger.error(e);
				}
			}
			break;

			default :
				break;
		}
	}

	/**
	 * Fires the host manager state changed event.
	 * @param host
	 * @param serviceID
	 * @param oldState
	 * @param newState
	 */
	private void fireHostStateChanged(
			String host,
			int serviceID,
			ServiceState oldState,
			ServiceState newState) {
		synchronized (this.listeners) {
			for(Iterator iter = listeners.iterator(); iter.hasNext();) {
                try {
                    ((Listener)iter.next()).hostStateChanged(host, serviceID, newState);
                } catch(Exception e) {
                    logger.error(e);
                }
			}
		}
	}

	/**
	 * Fires the update host info event.
	 * @param host
	 * @param info
	 */
	private void fireUpdateHostInfo(String host, HostInformation info) {
		completeHostInfoDtls(info, host);
		synchronized (this.listeners) {
			for(Iterator iter = listeners.iterator(); iter.hasNext();) {
                try {
                    ((Listener)iter.next()).updateHostInfo(host, info);
                } catch(Exception e) {
                    logger.error(e);
                }
			}
		}
	}

	/**
	 * Fires the update host info event.
	 * @param host
	 */
	private void fireAboutToRemoveHost(String host) {
		synchronized (this.listeners) {
			for(Iterator iter = listeners.iterator(); iter.hasNext();) {
                try {
                    ((Listener)iter.next()).aboutToRemoveHost(host);
                } catch(Exception e) {
                    logger.error(e);
                }
			}
		}
	}
	/**
	 * Completes host info details.<br>
	 *  Note: the host in HostInformation has to be
	 * filled in with the name as known by this monitoring service
	 * and the time difference must be calculated.
	 * @param dtls
	 * @param host
	 */
	private void completeHostInfoDtls(HostInformation dtls, String host) {
		dtls.calculateDeltaTime();
		dtls.setHost(host);
	}
}
