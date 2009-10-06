/**
 * 30-Jul-2005
 */
package com.ixora.rms.reactions;

import java.io.Serializable;

/**
 * @author Daniel Moraru
 */
public final class ReactionId implements Serializable {
	private int fId;

	/**
	 * @param id
	 */
	public ReactionId(int id) {
		fId = id;
	}

	/**
	 * @return id.
	 */
	public int getId() {
		return fId;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}
		if(!(obj instanceof ReactionId)) {
			return false;
		}
		return fId == ((ReactionId)obj).fId;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return fId;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "" + fId;
	}
}
