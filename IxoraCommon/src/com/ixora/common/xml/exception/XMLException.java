/*
 * Created on 13-Jan-2004
 */
package com.ixora.common.xml.exception;

import com.ixora.common.exception.AppException;

/**
 * @author Daniel Moraru
 */
public class XMLException extends AppException {
	/**
	 * @param e
	 */
	public XMLException(Exception e) {
		super(e);
	}
	/**
	 * @param e
	 */
	public XMLException(Throwable e) {
		super(e);
	}
	/**
	 * @param e
	 */
	public XMLException(String e) {
		super(e);
	}
}
