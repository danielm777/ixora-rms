/*
 * Created on 16-Dec-2003
 */
package com.ixora.rms;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.ixora.common.remote.ServiceState;

/**
 * @author Daniel Moraru
 */
public final class HostState implements Serializable {
	/** Host */
	private String host;
	/** Map of service IDs and service states */
	private Map<Integer, ServiceState> services;

	/**
	 * Constructor.
	 * @param host
	 * @param state
	 * @param serviceID
	 */
	public HostState(String host) {
		super();
		if(host == null) {
			throw new IllegalArgumentException("null host name");
		}
		this.host = host;
		this.services = new HashMap<Integer, ServiceState>();
	}

	/**
	 * @return the state for the service with the given id
	 */
	public ServiceState getServiceState(int id) {
		return this.services.get(new Integer(id));
	}

	/**
	 * @return the host name
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @return all the services available on this host
	 */
	public int[] getServices() {
	    Set sids = this.services.keySet();
	    int[] ret = new int[sids.size()];
	    int i = 0;
	    for(Iterator<Integer> iter = sids.iterator(); iter.hasNext();++i) {
            ret[i] = iter.next().intValue();
        }
	    return ret;
	}

	/**
	 * Adds a service.
	 * @param id the service id
	 * @param state the state of the service
	 */
	public void setServiceState(int id, ServiceState state) {
		this.services.put(new Integer(id), state);
	}
}
