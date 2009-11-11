package com.ixora.common.remote;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.remote.exception.ServiceUnreachable;

/**
 * Monitors the availability of a given service
 * on a set of hosts.
 * @author: Daniel Moraru
 */
public final class ServiceMonitor {
	/**
	 * Listener for ServiceMonitor.
	 */
	public interface Listener {
		/**
		 * Invoked when the state of a remote object changes.
		 * @param host
		 * @param serviceID
		 * @param oldState
		 * @param newState
		 */
		void serviceStateChanged(String host, int serviceID, ServiceState oldState,
					ServiceState newState);
	}

	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(ServiceMonitor.class);
    /**
     * Map to store the host and the associated host handler.
     */
    private HashMap<String, HostHandler> mapHostHandlers;
    /**
     * List of listeners.
     */
    private List<Listener> listeners;
	/**
	 * Timer.
	 */
	private Timer timer;
	/**
	 * Pingable factory.
	 */
	private PingableFactory factory;

    /**
     * ServiceMonitor.
     * @param hosts the initial hosts; it can be null
     * @param factory pingable factory
     * @param pingDelay the ping delay in seconds
     * @throws StartableError
     */
    public ServiceMonitor(Collection<String> hosts,
    			PingableFactory factory,
    			int pingDelay) {
        super();
        if(factory == null) {
        	throw new IllegalArgumentException("null pingable factory");
        }
        if(pingDelay <= 0) {
        	throw new IllegalArgumentException("invalid ping delay value: " + pingDelay);
        }
		this.factory = factory;
        this.listeners = new LinkedList<Listener>();
        this.mapHostHandlers = new HashMap<String, HostHandler>();

		if(hosts != null) {
			for(Iterator<String> iter = hosts.iterator(); iter.hasNext();) {
				String host = iter.next();
	            if(this.mapHostHandlers.get(host) == null) {
	                HostHandler hh = new HostHandler(host, false);
	                this.mapHostHandlers.put(host, hh);
	            }
			}
		}

		this.timer = new Timer(true);
		scheduleForPeriod(pingDelay);
    }

    /**
	 * @param pingDelay
	 */
	private void scheduleForPeriod(int pingDelay) {
		this.timer.schedule(new TimerTask(){
			public void run() {
				ServiceMonitor.this.handleAction();
			}
		}, 0, 1000 * pingDelay);
	}

	/**
     * Returns a remote reference to the remote object
     * on <b>host</b>. Null if the remote object is offline.
     * @param host
     * @return Pingable
     */
    public Pingable getPingable(String host) {
    	HostHandler hh;
    	synchronized (this) {
    		hh = this.mapHostHandlers.get(host);
    	    if(hh == null) {
    	    	return null;
    		}
    	}
        return hh.getPingable();
    }

	/**
	 * Sets the ping delay.
	 * @param delay the ping delay in seconds
	 */
	public void setPingDelay(int delay) {
		this.timer.purge();
		scheduleForPeriod(delay);
	}

	/**
	 * Stops the monitoring.
	 */
	public void stop() {
		this.timer.cancel();
	}

	/**
	 * Adds hosts to monitor.
	 * @param hosts
	 * @param waitForState
	 */
	public void addHosts(Collection<String> hosts, boolean waitForState) {
		synchronized(this) {
			for(Iterator<String> iter = hosts.iterator(); iter.hasNext(); ) {
				String host = iter.next();
				_addHost(host, waitForState);
			}
		}
	}

	/**
	 * Adds host to monitor.
	 * @param host
	 * @param waitForState
	 */
	public void addHost(String host, boolean waitForState) {
	    synchronized(this) {
	    	_addHost(host, waitForState);
	    }
	}

	/**
	 * @param host
	 * @param waitForState
	 */
	private void _addHost(String host, boolean waitForState) {
        if(this.mapHostHandlers.get(host) == null) {
            HostHandler hh = new HostHandler(host, waitForState);
            this.mapHostHandlers.put(host, hh);
        }
	}

	/**
	 * Updates the monitored hosts list.
	 * @param hosts
	 */
	public void updateHosts(Collection<String> hosts) {
		synchronized(this) {
	        Iterator<String> itr = this.mapHostHandlers.keySet().iterator();
	        String host;
	        while(itr.hasNext()) {
	            host = itr.next();
	            if(!hosts.contains(host)) {
	                itr.remove();
	            }
	        }

	        itr = hosts.iterator();
	        while(itr.hasNext()) {
	            host = itr.next();
	            if(this.mapHostHandlers.get(host) == null) {
	                HostHandler hh = new HostHandler(host, false);
	                this.mapHostHandlers.put(host, hh);
	            }
	        }
		}
		handleAction();
	}

	/**
	 * Removes a monitored host.
	 * @param host
	 */
	public void removeHost(String host) {
		synchronized(this) {
            this.mapHostHandlers.remove(host);
		}
	}

	/**
	 * Removes monitored hosts.
	 * @param hosts
	 */
	public void removeHosts(Collection<String> hosts) {
		synchronized(this) {
            this.mapHostHandlers.remove(hosts);
		}
	}

	/**
	 * @return all the registered hosts
	 */
	public Collection<String> getHosts() {
		synchronized(this) {
			return new ArrayList<String>(this.mapHostHandlers.keySet());
		}
	}

    /**
     * Adds a listener.
     * @param listener
     */
    public void addListener(Listener listener) {
        synchronized(this.listeners) {
        	if(!this.listeners.contains(listener)) {
        		this.listeners.add(listener);
        	}
       	}
    }

    /**
     * Removes a listener.
     * @param listener
     */
    public void removeListener(Listener listener) {
        synchronized(this.listeners) {
	        this.listeners.remove(listener);
        }
    }

    /**
     * Fires the object state changed event.
     * @param host
     * @param oldstate
     * @param newstate
     */
    private void fireRemoteObjectStateChanged(String host,
    					ServiceState oldstate,
    					ServiceState newstate) {
        synchronized(this.listeners) {
            Iterator<Listener> itr = this.listeners.iterator();
            while(itr.hasNext()) {
                try {
                    itr.next().serviceStateChanged(
                    		host, factory.getServiceID(), oldstate, newstate);
                } catch(Exception e) {
                    logger.error(e);
                }
            }
        }
    }

    /**
     * @return the service state for all hosts.
     */
    public ServiceInfo[] getServicesStates() {
        synchronized (this) {
            ServiceInfo[] ret = new ServiceInfo[this.mapHostHandlers.size()];
            Iterator<HostHandler> itr = this.mapHostHandlers.values().iterator();
            int i = 0;
            while(itr.hasNext()) {
                HostHandler hh = itr.next();
                ret[i++] = new ServiceInfo(hh.getHost(), hh.getState());
            }
            return ret;
        }
    }

    /**
     * @return the service state for the specified host.
     */
    public ServiceInfo getServiceState(String host) {
    	synchronized(this) {
    		// return state only for the specified host
           	HostHandler hh = this.mapHostHandlers.get(host);
            if(hh == null) {
                return null;
            }
            return new ServiceInfo(host, hh.getState());
    	}
    }

    /**
     * Handler the timer action event.
     */
    private void handleAction() {
        try {
			HostHandler[] tmp;
			// get a copy to iterate over as the ping() call
			// might take a lot if the remote host has gone down
            synchronized (this) {
            	tmp = this.mapHostHandlers.values()
            		.toArray(new HostHandler[this.mapHostHandlers.size()]);
            }
			for(int i = 0; i < tmp.length; ++i) {
				try {
					tmp[i].ping();
				} catch(Exception e) {
					logger.error(e);
				}
            }
        } catch(Exception e) {
            logger.error(e);
        }
    }

    /**
     * Class holding state and rmi obj ref for a particular host
     */
    private final class HostHandler {
    	/**
    	 * State of the pingable object.
    	 */
        private volatile ServiceState state = ServiceState.UNKNOWN;
        /**
         * Reference to a remote pingable object
         */
        private Pingable obj;
        /**
         * Host name
         */
        private String host;

        /**
         * Constructor
         * @param host String
         * @param waitForState
         */
        public HostHandler(String host, boolean waitForState) {
            super();
            this.host = host;
            if(waitForState) {
            	initializeReference();
            }
        }

        /**
         * Initializes the pingable reference.
         */
        private void initializeReference() {
			this.obj = factory.getPingable(this.host);
        }

        /**
         * Pings the RemoteControlAgent object.
         */
        public void ping() {
            try {
                if(this.obj == null) {
                    // try again
                    initializeReference();
                }
                if(this.obj != null) {
                    this.obj.ping();
                    if(this.state == ServiceState.OFFLINE
                    	|| this.state == ServiceState.UNKNOWN) {
                        setState(ServiceState.ONLINE);
                    }
                } else {
                	if(this.state == ServiceState.UNKNOWN) {
                		setState(ServiceState.OFFLINE);
                	}
                }
            } catch(ServiceUnreachable e) {
                if(this.state != ServiceState.OFFLINE) {
                    setState(ServiceState.OFFLINE);
                }
                // reinitialize remote reference & state
                initializeReference();
            } catch(Exception e) {
            	logger.error(e);
            }
        }

        /**
         * @return Pingable
         */
        public Pingable getPingable() {
            return this.obj;
        }

        /**
         * @return ServiceState
         */
        public ServiceState getState() {
            return this.state;
        }

        /**
         * @return String
         */
        public String getHost() {
            return this.host;
        }

        /**
         * @param newstate ServiceState
         */
        private void setState(ServiceState newstate) {
            ServiceState oldState = this.state;
            this.state = newstate;
            fireRemoteObjectStateChanged(this.host, oldState, newstate);
        }
    }
}
