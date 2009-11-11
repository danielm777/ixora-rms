/*
 * Created on 17-Jan-2004
 */
package com.ixora.common.xml.exception;

/**
 * @author Daniel Moraru
 */
public final class XMLTextNodeMissing extends XMLException {
	private static final long serialVersionUID = -5840050956275509395L;

	/**
	 * @param e the node whose first child was supposed to be a
	 * text node
	 */
	public XMLTextNodeMissing(String e) {
		super("Text node is missing: " + e);
	}
}
