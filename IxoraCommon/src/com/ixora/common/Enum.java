/*
 * Created on 17-Jan-2004
 */
package com.ixora.common;

import java.io.Serializable;

/**
 * @author Daniel Moraru
 */
public abstract class Enum implements Serializable, Comparable {
	/**
	 * Key is a localization independent way of describibg
	 * this element.
	 */
	protected int key;
	/**
	 * Localized name of this element.
	 */
	protected String name;

	/**
	 * @param key
	 * @param name
	 */
	protected Enum(int key, String name) {
		this.key = key;
		this.name = name;
	}

	/**
	 * @return the integer representation of this enumeration element.
	 */
	public int getKey() {
		return key;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return this.name;
	}

	/**
	 * Needed to perform the correct object resolution during deserialization.
	 */
	abstract protected Object readResolve() throws java.io.ObjectStreamException;

	/**
	 * Default comparation based on the value of the key
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		Enum that = (Enum)o;
		if(key == that.key) {
			return 0;
		}
		if(key < that.key) {
			return -1;
		} else {
			return 1;
		}
	}
}
