/*
 * Created on 31-Aug-2004
 *
 */
package com.ixora.rms.exception;

/**
 * QueryException
 * @author Daniel Moraru
 */
public class QueryException extends RMSException {
	private static final long serialVersionUID = 3966544770012399957L;

	/**
	 * @param s
	 */
	public QueryException(String s) {
		super(s);
	}

	/**
	 * @param msgKey
	 * @param msgTokens
	 */
	public QueryException(String msgKey, String[] msgTokens) {
		super(msgKey, msgTokens);
	}

}
