/*
 * Created on 07-Aug-2004
 */
package com.ixora.rms.repository;

import com.ixora.rms.ResourceId;

/**
 * An id which uniquely identifies a data view.
 * @author Daniel Moraru
 */
public final class ProviderInstanceId extends TreeArtefactId {
    public static final char MARKER_START = '$';
    public static final char MARKER_END = '$';

	/**
	 * Constructor.
	 * @param cid context of the data view
	 * @param name name of the data view
	 */
	public ProviderInstanceId(ResourceId cid, String name) {
	    super(MARKER_START, MARKER_END, cid, name);
	    validateContext();
	}

	/**
	 * Constructor that builds an id from a string. The string is of the
	 * form <code>toString()</code> outputs.
	 * @param qid
	 */
	public ProviderInstanceId(String qid) {
	    super(MARKER_START, MARKER_END, qid);
	    validateContext();
	}

	/**
	 * Completes missing info from its context with info
	 * from the given resource id.
	 * @param rid
	 * @return
	 */
	public ProviderInstanceId complete(ResourceId rid) {
	    if(context == null) {
	        return new ProviderInstanceId(rid, name);
	    }
	    return new ProviderInstanceId(context.complete(rid), name);
	}

	/**
	 * Validates the context as providers only exists for agents.
	 * @throws IllegalArgumentException if the context is invalid
	 */
	private void validateContext() {
	    if(context == null || (!context.isValid() && context.getAgentId() == null) ||
	    		(context.isValid() && context.getRepresentation() != ResourceId.AGENT)) {
	    	throw new IllegalArgumentException("The context " + context + " must represent an agent");
	    }
	}
}
