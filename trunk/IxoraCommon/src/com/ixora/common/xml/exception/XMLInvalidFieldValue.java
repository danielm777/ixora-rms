/*
 * Created on 17-Jan-2004
 */
package com.ixora.common.xml.exception;

/**
 * @author Daniel Moraru
 */
public final class XMLInvalidFieldValue extends XMLException {
	/**
	 * @param e
	 */
	public XMLInvalidFieldValue(String e) {
		super("Invalid field value: " + e);
	}
}
