/*
 * Created on 27-May-2005
 *
 */
package com.ixora.rms.exception;

import com.ixora.rms.messages.Msg;

/**
 * QueryDuplicateIDException
 */
public class QueryDuplicateIDException extends QueryException {
	/**
	 * Constructor.
	 */
	public QueryDuplicateIDException(String id) {
		super(Msg.RMS_QUERY_CONTAINS_DUPLICATE_ID,
				new String[] {id});
	}
}
