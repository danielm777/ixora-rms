/*
 * Created on 31-Aug-2004
 *
 */
package com.ixora.rms.exception;

import com.ixora.rms.messages.Msg;

/**
 * QueryEmptyException
 */
public class QueryIsEmptyException extends QueryException {
	/**
	 * Constructor.
	 */
	public QueryIsEmptyException() {
		super(Msg.RMS_QUERY_IS_EMPTY);
	}
}
