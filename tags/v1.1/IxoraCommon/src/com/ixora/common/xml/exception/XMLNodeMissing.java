/*
 * Created on 17-Jan-2004
 */
package com.ixora.common.xml.exception;

/**
 * @author Daniel Moraru
 */
public final class XMLNodeMissing extends XMLException {
	private static final long serialVersionUID = -7478081131360318687L;

	/**
	 * @param e
	 */
	public XMLNodeMissing(String e) {
		super("Node is missing: " + e);
	}
}
