/*
 * Created on 17-Jan-2004
 */
package com.ixora.common.xml.exception;

/**
 * @author Daniel Moraru
 */
public final class XMLAttributeMissing extends XMLException {
	private static final long serialVersionUID = -8545747142826092020L;

	/**
	 * @param e
	 */
	public XMLAttributeMissing(String e) {
		super("Attribute is missing: " + e);
	}
}
