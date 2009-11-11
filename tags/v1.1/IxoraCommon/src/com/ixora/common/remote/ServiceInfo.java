package com.ixora.common.remote;

/**
 * Structure holding remote object state info.
 * @author: Daniel Moraru
 */
public final class ServiceInfo {
	/** Host */
    private String host;
    /** Remote object state */
    private ServiceState state;

    /**
     * RemoteObjectStateInfo constructor.
     */
    public ServiceInfo() {
        super();
        this.state = ServiceState.OFFLINE;
    }

    /**
     * RemoteObjectStateInfo constructor.
     * @param host
     * @param state
     */
    public ServiceInfo(String host, ServiceState state) {
        super();
        this.host = host;
        this.state = state;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return this.host + ":" + this.state.toString();
    }

    /**
     * Returns the host.
     * @return String
     */
    public String getHost() {
        return host;
    }

    /**
     * @return the state of the remote object.
     */
    public ServiceState getState() {
        return state;
    }

    /**
     * Sets the host.
     * @param host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Sets the state.
     * @param state
     */
    public void setState(ServiceState state) {
        this.state = state;
    }
}
