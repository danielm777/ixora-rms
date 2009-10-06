/*
 * Created on 27-Feb-2005
 */
package com.ixora.rms.client.locator;


/**
 * @author Daniel Moraru
 */
public abstract class AbstractSessionArtefactInfo implements SessionArtefactInfo {
	protected String fTranslatedName;
	protected String fTranslatedDescription;

	/**
	 * Constructor.
	 */
	protected AbstractSessionArtefactInfo() {
		super();
	}

	/**
	 * @see com.ixora.rms.client.locator.SessionArtefactInfo#getTranslatedName()
	 */
	public String getTranslatedName() {
		return fTranslatedName;
	}

	/**
	 * @see com.ixora.rms.client.locator.SessionArtefactInfo#getTranslatedDescription()
	 */
	public String getTranslatedDescription() {
		return fTranslatedDescription;
	}
}
