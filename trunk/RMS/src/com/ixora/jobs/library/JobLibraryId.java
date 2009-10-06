/**
 * 30-Jul-2005
 */
package com.ixora.jobs.library;

import java.io.Serializable;

/**
 * @author Daniel Moraru
 */
public final class JobLibraryId implements Serializable {
	private String fId;
	
	/**
	 * @param id
	 */
	public JobLibraryId(String id) {
		fId = id;
	}

	/**
	 * @return id.
	 */
	public String getId() {
		return fId;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}
		if(!(obj instanceof JobLibraryId)) {
			return false;
		}
		return fId.equalsIgnoreCase(((JobLibraryId)obj).fId);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return fId.hashCode();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return fId;
	}
}
