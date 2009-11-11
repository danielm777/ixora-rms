/*
 * Created on 07-Aug-2004
 */
package com.ixora.rms.repository;

import com.ixora.rms.ResourceId;

/**
 * An id which uniquely identifies a dashboard.
 * @author Daniel Moraru
 */
public final class DashboardId extends TreeArtefactId {
	private static final long serialVersionUID = -7660119877525096005L;
	public static final char MARKER_START = '#';
    public static final char MARKER_END = '#';

	/**
	 * Constructor.
	 * @param cid context of the data view
	 * @param name name of the data view
	 */
	public DashboardId(ResourceId cid, String name) {
	    super(MARKER_START, MARKER_END, cid, name);
	}

	/**
	 * Constructor that builds an id from a string. The string is of the
	 * form <code>toString()</code> outputs.
	 * @param qid
	 */
	public DashboardId(String qid) {
	    super(MARKER_START, MARKER_END, qid);
	}

	/**
	 * Completes missing info from its context with info
	 * from the given resource id.
	 * @param rid
	 * @return a new DataViewId
	 */
	public DashboardId complete(ResourceId rid) {
	    if(context == null) {
	        return new DashboardId(rid, name);
	    }
	    return new DashboardId(context.complete(rid), name);
	}
}
