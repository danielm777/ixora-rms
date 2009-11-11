package com.ixora.rms;

import java.io.Serializable;

/**
 * HostId
 * Identifies a host (by its name or ip address). Used in globally
 * identifying counters.
 * @author Cristian Costache
 * @author Daniel Moraru
 */
public final class HostId implements Serializable, Cloneable {
	private static final long serialVersionUID = -2564755690366187159L;
	/** Host's name */
	private String	id;

	public HostId(String id) {
		super();
		if(id == null) {
			throw new IllegalArgumentException("id is null");
		}
		this.id = id;
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	protected Object clone() {
		return new HostId(this.id);
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if(!(obj instanceof HostId)) {
			return false;
		}
		if(obj == this) {
			return true;
		}
		return this.id.equalsIgnoreCase(((HostId)obj).id);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return this.id.toLowerCase().hashCode();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return this.id;
	}
}
