/*
 * Created on 07-Aug-2004
 */
package com.ixora.rms.repository;

import com.ixora.rms.ResourceId;

/**
 * An id which uniquely identifies a data view.
 * @author Daniel Moraru
 */
public final class DataViewId extends TreeArtefactId {
    public static final char MARKER_START = ':';
    public static final char MARKER_END = ':';

	/**
	 * Constructor.
	 * @param cid context of the data view
	 * @param name name of the data view
	 */
	public DataViewId(ResourceId cid, String name) {
	    super(MARKER_START, MARKER_END, cid, name);
	}

	/**
	 * Constructor that builds an id from a string. The string is of the
	 * form <code>toString()</code> outputs.
	 * @param qid
	 */
	public DataViewId(String qid) {
	    super(MARKER_START, MARKER_END, qid);
	}

	/**
	 * Completes missing info from its context with info
	 * from the given resource id.
	 * @param rid
	 * @return a new DataViewId
	 */
	public DataViewId complete(ResourceId rid) {
	    if(context == null) {
	        return new DataViewId(rid, name);
	    }
	    return new DataViewId(context.complete(rid), name);
	}
}
