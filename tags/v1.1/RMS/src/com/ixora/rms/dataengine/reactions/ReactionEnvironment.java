/**
 * 02-Aug-2005
 */
package com.ixora.rms.dataengine.reactions;

import java.io.Serializable;

/**
 * Environment available to authors of Reactions. Because this is a public interface
 * care must be taken at what methods are exposed.
 * @author Daniel Moraru
 */
public interface ReactionEnvironment {
	/**
	 * @return the timestamp when the reaction was armed for the first time
	 */
	long getArmedTime();
	/**
	 * @return the number of seconds since the reaction was armed
	 */
	long getSecondsSinceArmed();
	/**
	 * @return the number of times the reaction was armed.
	 */
	long getArmedCount();
	/**
	 * @return the number of times the reaction was fired.
	 */
	long getFiredCount();
	/**
	 * @return the timestamp when the reaction was last armed
	 */
	long getLastArmedTime();
	/**
	 * @return the number of seconds since the reaction was last armed
	 */
	long getSecondsSinceLastArmed();
	/**
	 * @param rid
	 * @return the translated path of the resource path with the given id
	 */
	String getPath(String rid);
	/**
	 * @param rid
	 * @return the name of the host for the resource with the specified id
	 */
	String getHost(String rid);
	/**
	 * @param rid
	 * @return the name of the agent for the resource with the specified id
	 */
	String getAgent(String rid);
	/**
	 * @param rid
	 * @return the name of the part <code>i</code> of the entity for the resource with the specified id
	 */
	String getEntityPart(String rid, int i);
	/**
	 * @param rid
	 * @return the translated path of the entity for the resource with the specified id
	 */
	String getEntityPath(String rid);
	/**
	 * @param id
	 * @return the translated name of the counter for the resource with the specified id
	 */
	String getCounter(String id);
	/**
	 * Invoked by code fragments to send email.
	 */
	void email();
	/**
	 * Invoked by code fragment to run jobs.
	 * @param jobLibraryId
	 */
	void job(String jobLibraryId);
	/**
	 * Writes the given message to the application's log.
	 * @param logMsg
	 */
	void log(String logMsg);
	/**
	 * Store a custom object for later retrieval.
	 * @param key
	 * @param obj
	 */
	void put(String key, Serializable obj);
	/**
	 * Retrieves an object that was previously stored with <code>put(String, Serializable)</code>.
	 * @param key
	 * @return
	 */
	Serializable get(String key);
}
