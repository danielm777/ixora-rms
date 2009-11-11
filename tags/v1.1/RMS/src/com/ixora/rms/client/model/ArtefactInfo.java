/*
 * Created on 08-Aug-2004
 */
package com.ixora.rms.client.model;

import com.ixora.rms.repository.VersionableAgentArtefact;

/**
 * Information about an artefact(an entity that applies
 * to all elements in the session data tree).
 * @author Daniel Moraru
 */
public interface ArtefactInfo extends Comparable<ArtefactInfo> {
	/** Enabled flag */
    public static final int ENABLED = 0;
    /** Activated flag */
    public static final int ACTIVATED = 1;

	/**
	 * @return the translated artefact description
	 */
	String getTranslatedDescription();

	/**
	 * @return the translated artefact name
	 */
	String getTranslatedName();

	/**
	 * @return the value of the given flag
	 */
	boolean getFlag(int f);

	/**
	 * @return whether or not the artefact is committed;
	 */
	boolean isCommitted();

	/**
	 * @return true if this artefact is only used
	 * by one subject and if so it should
	 * be safely disabled in the monitoring session
	 */
	boolean isDisposable();

    /**
     * @return true if identifiers must be shown
     */
    boolean showIdentifiers();

    /**
     * @return the versionable agent artefact represented by this class
     * or null if the item represented is not a versionable agent artefact
     */
    VersionableAgentArtefact getVersionableAgentArtefact();
}
