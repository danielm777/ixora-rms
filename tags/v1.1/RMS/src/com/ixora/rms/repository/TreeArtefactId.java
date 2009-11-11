/*
 * Created on 07-Aug-2004
 */
package com.ixora.rms.repository;

import java.io.Serializable;

import com.ixora.rms.ResourceId;
import com.ixora.common.utils.Utils;
import com.ixora.rms.EntityId;

/**
 * An id which uniquely identifies an artefact.
 * @author Daniel Moraru
 */
public abstract class TreeArtefactId implements Serializable {
	private static final long serialVersionUID = -989293148057364301L;
	/** A character that is prepended to the name of the artefact */
    private final char markerStart;
    /** A character that is appended to the name of the artefact */
    private final char markerEnd;
    /**
	 * The context of this artefact.
	 * This represents a host, an agent, an entity
	 * or if null the global query name space.
	 */
	protected ResourceId context;
	/** The name of the artefact */
	protected String name;

	/**
	 * Constructor.
	 * @param markerS
	 * @param markerE
	 * @param cid context of the artefact
	 * @param name name of the artefact
	 */
	protected TreeArtefactId(char markerS, char markerE,
	        ResourceId cid, String name) {
	    this.markerStart = markerS;
	    this.markerEnd = markerE;
		this.context = cid;
		this.name = name;
	}

	/**
	 * Constructor that builds an id from a string. The string is of the
	 * form <code>toString()</code> outputs.
	 * @param markerS
	 * @param markerE
	 * @param id the same as returned by <code>toString()</code>
	 */
	protected TreeArtefactId(char markerS, char markerE, String id) {
	    super();
	    this.markerStart = markerS;
	    this.markerEnd = markerE;
	    int idx = id.lastIndexOf(markerE);
	    if(idx < 0) {
            throw new IllegalArgumentException("invalid artefact id format: " + id);
	    }
        // find last EntityDelimiter + markerStart
        String f = getForbiddenSeqInName();
        idx = id.lastIndexOf(f);
        if(idx < 0) {
            this.name = stripMarkers(id);
        } else {
            int lastf = idx;
            int len = id.length();
            if(len > 6) {
                // search for the last single f which is not part of a ff
                for(int i = 0; i < idx; ++i) {
                    if(id.charAt(i) == EntityId.DELIMITER &&
                            id.charAt(i + 1) == this.markerStart) {
                        if(id.charAt(i + 2) != EntityId.DELIMITER ||
                            id.charAt(i + 3) != this.markerStart) {
                            lastf = i;
                        }
                    }
                }
            }
            String tmp = id.substring(lastf + 1);
            this.name = unescape(stripMarkers(tmp));
            tmp = id.substring(0, lastf);
            if(tmp.length() != 0) {
                this.context = new ResourceId(tmp);
            }
        }
	}

	/**
	 * @return the context of the query.
	 */
	public ResourceId getContext() {
		return context;
	}

	/**
	 * @return the name of the query.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}
		if(!(obj instanceof TreeArtefactId)) {
			return false;
		}
		TreeArtefactId	that = (TreeArtefactId)obj;
		return Utils.equals(context, that.context) &&
				Utils.equals(name, that.name);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		int hc = 0;
		if(context != null) {
			hc ^= context.hashCode();
		}
		return hc ^ name.hashCode();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buff = new StringBuffer();
		if(context != null) {
		    buff.append(context);
			buff.append(EntityId.DELIMITER);
		}
		buff.append(markerStart);
		buff.append(escape(name));
		buff.append(markerEnd);
		return buff.toString();
	}

    /**
     * Strips the markers from the begining and end of the given string.
     * @param s
     * @return
     */
    private String stripMarkers(String s) {
       return s.substring(1, s.length() - 1);
    }

    /**
     * Escapes the given string.
     * @param n
     * @return
     */
    private String escape(String n) {
        String f = getForbiddenSeqInName();
        return Utils.replace(n, f, f + f);
    }

    /**
     * Unescapes the given string.
     * @param n
     * @return
     */
    private String unescape(String n) {
        String f = getForbiddenSeqInName();
        return Utils.replace(n, f + f, f);
    }

    /**
     * @return the sequence of chars which is forbidden to appear in the name.
     */
    private String getForbiddenSeqInName() {
        return "" + EntityId.DELIMITER + this.markerStart;
    }
}
