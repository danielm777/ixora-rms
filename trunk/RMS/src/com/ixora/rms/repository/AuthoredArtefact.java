/**
 * 17-Jul-2005
 */
package com.ixora.rms.repository;

/**
 * @author Daniel Moraru
 */
public interface AuthoredArtefact {
	public final String SYSTEM = "system";
	public final String CUSTOMER = "customer";
	/**
	 * @return the author of the artefact
	 */
	String getAuthor();
	/**
	 * @param author the author of this artefact
	 */
	void setAuthor(String author);
	/**
	 * @return true if the artefact is part of the system
	 */
	boolean isSystem();
}
