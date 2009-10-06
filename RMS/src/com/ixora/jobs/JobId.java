/*
 * Created on 24-Sep-2004
 */
package com.ixora.jobs;

import java.io.Serializable;

/**
 * @author Daniel Moraru
 */
public final class JobId implements Serializable {
    private static final String DELIMITER = "/";
    /** Id */
    private String id;

    /**
     * Constructor.
     * @param host
     * @param id
     */
    public JobId(String host, String hid) {
       id = host + DELIMITER + hid;
    }

    /**
     * Constructor.
     * @param id
     */
    public JobId(String id) {
        super();
        int idx = id.indexOf(DELIMITER);
        if(idx <= 0) {
            throw new IllegalArgumentException("invalid id string");
        }
        this.id = id;
    }

    /**
     * @return the host portion of this id
     */
    public String getHost() {
        return id.substring(0, id.indexOf(DELIMITER));
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        }
        if(!(obj instanceof JobId)) {
            return false;
        }
        return id.equals(((JobId)obj).id);
    }
    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return id.hashCode();
    }
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return id;
    }
}
