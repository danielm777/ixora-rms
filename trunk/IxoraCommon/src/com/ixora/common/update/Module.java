package com.ixora.common.update;

import com.ixora.common.ComponentVersion;

/*
 * Created on Feb 12, 2004
 */
/**
 * Updateable module.
 * @author Daniel Moraru
 */
public class Module {
	/** Component version */
	private ComponentVersion version;
	/** Component name */
	private String name;

	/**
	 * Constructor.
	 * @param name
	 * @param version
	 */
	public Module(String name, ComponentVersion version) {
		super();
		this.name = name;
		this.version = version;
	}

	/**
	 * @return the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the version.
	 */
	public ComponentVersion getVersion() {
		return version;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return name + "(" + version + ")";
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}
		if(!(obj instanceof Module)) {
			return false;
		}
		Module that = (Module)obj;
		return this.name.equals(that.name)
			&& this.version.equals(that.version);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return this.name.hashCode() ^ this.version.hashCode();
	}
}
