/*
 * Created on 17-Jan-2004
 */
package com.ixora.common.xml.exception;

/**
 * @author Daniel Moraru
 */
public final class XMLInvalidFieldValue extends XMLException {
	private static final long serialVersionUID = 5076328922808555419L;

	/**
	 * @param e
	 */
	public XMLInvalidFieldValue(String e) {
		super("Invalid field value: " + e);
	}
}
