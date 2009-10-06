package com.ixora.common.update;
/*
 * Created on Feb 12, 2004
 */
/**
 * Update id.s
 * @author Daniel Moraru
 */
public final class UpdateId {
	/** Id */
	private String id;

	/**
	 * @param id
	 */
	public UpdateId(String id) {
		super();
		this.id = id;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return this.id;
	}
}
