/*
 * Created on 31-Aug-2004
 *
 */
package com.ixora.rms.exception;

import com.ixora.rms.messages.Msg;

/**
 * QueryNoSuchResultException
 * @author Daniel Moraru
 */
public class QueryNoSuchResultException extends QueryException {
	private static final long serialVersionUID = 768527542196219529L;

	/**
	 * Constructor.
	 */
	public QueryNoSuchResultException(String id) {
		super(Msg.RMS_QUERY_NO_SUCH_RESULT,
				new String[] {id});
	}
}
