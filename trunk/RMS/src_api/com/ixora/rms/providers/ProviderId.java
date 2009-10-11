package com.ixora.rms.providers;

import java.io.Serializable;

import com.ixora.rms.HostId;

/**
 * Provider identifier.
 * @author Daniel Moraru
 */
public final class ProviderId implements Serializable {
	private static final long serialVersionUID = -6047329640499464029L;
	private static final String DELIMITER = "/";
	/** Id */
	private String id;

	/**
	 * Constructor.
	 * @param id
	 */
	public ProviderId(HostId host, int id) {
		super();
		if(host == null) {
			throw new IllegalArgumentException("null host id");
		}
		this.id = host.toString() + DELIMITER + id;
	}

    /**
     * Constructor.
     * @param id
     */
    public ProviderId(String id) {
        super();
        int idx = id.indexOf(DELIMITER);
        if(idx <= 0) {
            throw new IllegalArgumentException("invalid id string");
        }
        this.id = id;
    }

    /**
     * @return the host portion of this id
     */
    public HostId getHost() {
        return new HostId(id.substring(0, id.indexOf(DELIMITER)));
    }

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if(!(obj instanceof ProviderId)) {
			return false;
		}
		if(obj == this) {
			return true;
		}
		return this.id.equals(((ProviderId)obj).id);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return this.id.hashCode();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return this.id;
	}
}
