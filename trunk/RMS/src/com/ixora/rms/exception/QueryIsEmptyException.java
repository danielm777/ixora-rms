/*
 * Created on 31-Aug-2004
 *
 */
package com.ixora.rms.exception;

import com.ixora.rms.messages.Msg;

/**
 * QueryEmptyException
 * @author Daniel Moraru
 */
public class QueryIsEmptyException extends QueryException {
	private static final long serialVersionUID = 2322344945732713620L;

	/**
	 * Constructor.
	 */
	public QueryIsEmptyException() {
		super(Msg.RMS_QUERY_IS_EMPTY);
	}
}
