/*
 * Created on Jan 26, 2004
 */
package com.ixora.rms;

import java.io.Serializable;

/**
 * Counter identifier. It uniquely identifies a counter in an entity's set.
 * @author Daniel Moraru
 */
public final class CounterId implements Serializable, Cloneable, Comparable<CounterId> {
	private static final long serialVersionUID = -8482734090199587506L;
	/** Counter identifier */
	private String id;

	/**
	 * Constructor.
	 * @param id
	 */
	public CounterId(String id) {
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
		return new CounterId(this.id);
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		if (obj instanceof String)
			return this.id.equals(obj);
		if (obj instanceof CounterId)
			return this.id.equals(((CounterId)obj).id);
		return false;
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

	/**
	 * @see java.lang.Comparable#compareTo(CounterId)
	 */
	public int compareTo(CounterId o) {
		return this.id.compareTo(o.id);
	}
}
