package com.ixora.rms.agents;

import java.io.Serializable;

/**
 * AgentId
 * Identifier for an agent
 * @author Cristian Costache
 * @author Daniel Moraru
 */
/*
 * Modification history
 * -----------------------------------------------
 * 07 July 2004 - DM removed getName() method, use instead
 * 					toString() it's more natural
 * 10 Jan 2004 - DM added support for mutliple similar agents per host;
 * 					if just one agent is activated on the host then toString()
 * 					will return the installation id else the string will be
 * 					installationid:idx where idx >= 1 is the index of of this agent
 */
public final class AgentId implements Serializable {
	private static final String DELIMITER = ":";
    /** Id */
    private String id;

    /**
     * Constructor.
     * @param installationId
     * @param idx
     */
	public AgentId(String installationId, int idx) {
		super();
		if(installationId == null) {
			throw new IllegalArgumentException("installationId is null");
		}
		this.id = installationId + DELIMITER + idx;
	}

	/**
	 * Constructor.
	 * @param id
	 */
	public AgentId(String id) {
        super();
        this.id = id;
	}

    /**
     * @return the installation id portion of this id
     */
    public String getInstallationId() {
    	int idx = id.indexOf(DELIMITER);
    	if(idx < 0) {
    		return id;
    	}
        return id.substring(0, id.indexOf(DELIMITER));
    }

    /**
     * @return the installation id portion of this id
     */
    public int getInstallationIdx() {
    	int idx = id.indexOf(DELIMITER);
    	if(idx < 0) {
    		return 0;
    	}
        return Integer.parseInt(id.substring(id.indexOf(DELIMITER) + 1, id.length()));
    }

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if(!(obj instanceof AgentId)) {
			return false;
		}
		if(obj == this) {
			return true;
		}
		return this.id.equals(((AgentId)obj).id);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return this.id.hashCode();
	}


	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return this.id;
	}
}
