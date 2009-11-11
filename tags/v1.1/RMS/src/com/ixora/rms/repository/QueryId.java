/*
 * Created on 07-Aug-2004
 */
package com.ixora.rms.repository;

import com.ixora.rms.ResourceId;

/**
 * An id which uniquely identifies a query.
 * @author Daniel Moraru
 */
public final class QueryId extends TreeArtefactId {
	private static final long serialVersionUID = -6973308108401217856L;
	public static final char MARKER_START = '{';
    public static final char MARKER_END = '}';

	/**
	 * Constructor.
	 * @param cid context of the query
	 * @param name name of the query
	 */
	public QueryId(ResourceId cid, String name) {
	    super(MARKER_START, MARKER_END, cid, name);
	}

	/**
	 * Constructor that builds a query from a string. The string is of the
	 * form <code>toString()</code> outputs.
	 * @param qid
	 */
	public QueryId(String qid) {
	    super(MARKER_START, MARKER_END, qid);
	}

	/**
	 * Completes missing info from its context with info
	 * from the given resource id.
	 * @param rid
	 * @return a new QueryId
	 */
	public QueryId complete(ResourceId rid) {
	    if(context == null) {
	        return new QueryId(rid, name);
	    }
	    return new QueryId(context.complete(rid), name);
	}
}
