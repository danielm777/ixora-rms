/*
 * Created on 31-Aug-2004
 *
 */
package com.ixora.rms.exception;

import com.ixora.rms.messages.Msg;

/**
 * QueryNoSuchResultException
 */
public class QueryNoSuchResultException extends QueryException {
	/**
	 * Constructor.
	 */
	public QueryNoSuchResultException(String id) {
		super(Msg.RMS_QUERY_NO_SUCH_RESULT,
				new String[] {id});
	}
}
