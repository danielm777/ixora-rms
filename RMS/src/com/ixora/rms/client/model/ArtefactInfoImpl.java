/*
 * Created on 28-Jul-2004
 */
package com.ixora.rms.client.model;

import java.util.BitSet;

import com.ixora.common.MessageRepository;
import com.ixora.rms.repository.VersionableAgentArtefact;


/**
 * Class that holds model data on an artefact.
 */
public abstract class ArtefactInfoImpl implements ArtefactInfo {
    /** Flags */
	protected BitSet flags;
	/** In edit value of the enabled flag */
	protected boolean inEditEnabled;
	/** Translated name for the query */
	protected String translatedName;
	/** Translated description for the query */
	protected String translatedDescription;
	/** Name */
	protected String name;
	/** Description */
	protected String description;
	/** SessionModel */
	protected SessionModel model;


	/**
	 * @param messageRepository used to locate the translated
	 * name and description of the artifact
	 * @param name
	 * @param description
	 * @param model
	 */
	public ArtefactInfoImpl(String messageRepository,
	        String name, String description, SessionModel model) {
	    this.model = model;
	    this.name = name != null ? name : "";
	    this.description = description != null ? description : "";
	    this.flags = new BitSet(2);
		this.flags.set(ENABLED, false);
		this.inEditEnabled = this.flags.get(ENABLED);
		if(messageRepository != null) {
			this.translatedName = MessageRepository.get(
					messageRepository, name);
			this.translatedDescription = MessageRepository.get(
					messageRepository, description);
		} else {
			// use the global message repository
			this.translatedName = MessageRepository.get(name);
			this.translatedDescription = MessageRepository.get(description);
		}
	}
	/**
	 * @return the translated query description
	 */
	public String getTranslatedDescription() {
		return translatedDescription;
	}
	/**
	 * @return the translated counter name
	 */
	public String getTranslatedName() {
		return translatedName;
	}
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
	    String text;
	    if(model.getShowIdentifiers()) {
	        text = this.name;
	    } else {
	        text = this.translatedName;
	    }
		return text;
	}
	/**
	 * @return whether or not the counter is enabled
	 */
	public boolean getFlag(int f) {
	    if(f == ENABLED) {
	        return inEditEnabled;
	    }
		return flags.get(f);
	}
	/**
	 * @return whether or not the cube is committed; a cube is
	 * committed when all its counters are committed
	 */
	public boolean isCommitted() {
		return this.inEditEnabled == this.flags.get(ENABLED);
	}

    /**
     * @see com.ixora.rms.client.model.ArtefactInfo#isDisposable()
     */
    public boolean isDisposable() {
        return true;
    }

    /**
     * @see com.ixora.rms.client.model.ArtefactInfo#showIdentifiers()
     */
    public boolean showIdentifiers() {
        return model.getShowIdentifiers();
    }

    /**
     * @see com.ixora.rms.client.model.ArtefactInfo#getVersionableAgentArtefact()
     */
    public VersionableAgentArtefact getVersionableAgentArtefact() {
        return null;
    }

    /**
	 * @see java.lang.Comparable#compareTo(T)
	 */
	public int compareTo(ArtefactInfo o) {
		return this.translatedName.compareToIgnoreCase(o.getTranslatedName());
	}

	// not visible outside this package
	/**
	 * @param f
	 * @param v
	 */
	void setFlag(int f, boolean v) {
	    if(f == ENABLED) {
	        this.inEditEnabled = v;
	    } else {
	        this.flags.set(ACTIVATED, v);
	    }
	}
	/**
	 * Commits the enabled status of this query.
	 */
	void commit() {
		this.flags.set(ENABLED, this.inEditEnabled);
	}
	/**
	 * Resets the enabled flag to the original value.
	 */
	void rollback() {
		setFlag(ENABLED, this.flags.get(ENABLED));
		//this.inEditEnabled = this.flags.get(ENABLED);
	}
}