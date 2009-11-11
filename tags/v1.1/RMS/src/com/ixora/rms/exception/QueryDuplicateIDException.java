/*
 * Created on 27-May-2005
 *
 */
package com.ixora.rms.exception;

import com.ixora.rms.messages.Msg;

/**
 * QueryDuplicateIDException
 * @author Daniel Moraru
 */
public class QueryDuplicateIDException extends QueryException {
	private static final long serialVersionUID = -8223987110643734426L;

	/**
	 * Constructor.
	 */
	public QueryDuplicateIDException(String id) {
		super(Msg.RMS_QUERY_CONTAINS_DUPLICATE_ID,
				new String[] {id});
	}
}
