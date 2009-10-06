/**
 * 29-Jul-2005
 */
package com.ixora.rms.reactions;

import java.util.List;

import com.ixora.jobs.library.JobLibraryId;

/**
 * @author Daniel Moraru
 */
public interface ReactionDispatcher {
	/**
	 * Sends email using the default settings.
	 * @param event
	 */
	ReactionId email(ReactionEvent event);
	/**
	 * Sends email using the given email server.
	 * @param event
	 * @param port
	 * @param server
	 * @param to
	 */
	ReactionId email(ReactionEvent event, String server, int port, List<String> to);
	/**
	 * Runs the job with the given library id.
	 * @param event
	 * @param jobLibraryId
	 */
	ReactionId job(ReactionEvent event, JobLibraryId jobLibraryId);
	/**
	 * Triggers an advise for the given event.
	 * @param event
	 * @return
	 */
	ReactionId advise(ReactionEvent event);
	/**
	 * Fires the reaction with the given id.
	 * @param rid
	 */
	void fire(ReactionId rid);
	/**
	 * Disarms the reaction with the given id.
	 * @param rid
	 */
	void disarm(ReactionId rid);
}
