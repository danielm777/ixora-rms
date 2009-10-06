/*
 * Created on 31-Aug-2004
 *
 */
package com.ixora.rms.exception;

/**
 * QueryException
 */
public class QueryException extends RMSException {

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
