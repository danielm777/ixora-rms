/*
 * Created on 28-Dec-2004
 */
package com.ixora.rms;


/**
 * @author Daniel Moraru
 */
public abstract class HostDataBufferImpl implements HostDataBuffer {
	/** The host from where the data originated */
	protected HostId host;
	/** Time difference between the remote and local host */
	protected transient long timeDelta;

	/**
	 * Constructor.
	 */
	protected HostDataBufferImpl() {
		super();
	}
	
	/**
	 * Constructor.
	 */
	protected HostDataBufferImpl(HostId h) {
		super();
		this.host = h;
	}

    /**
     * @return
     */
    protected boolean isValid() {
        return host != null;
    }

	/**
	 * @see com.ixora.rms.HostDataBuffer#getHost()
	 */
	public HostId getHost() {
		return host;
	}

	/**
	 * @see com.ixora.rms.HostDataBuffer#getTimeDelta()
	 */
	public long getTimeDelta() {
		return timeDelta;
	}

	/**
	 * @see com.ixora.rms.HostDataBuffer#setHost(java.lang.String)
	 */
	public void setHost(String host) {
		this.host = new HostId(host);
	}

	/**
	 * @see com.ixora.rms.HostDataBuffer#setHost(com.ixora.rms.HostId)
	 */
	public void setHost(HostId host) {
		this.host = host;
	}

	/**
	 * @see com.ixora.rms.HostDataBuffer#setTimeDelta(long)
	 */
	public void setTimeDelta(long delta) {
		this.timeDelta = delta;
	}
}
