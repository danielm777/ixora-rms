/**
 * 30-Jul-2005
 */
package com.ixora.rms.reactions;

import com.ixora.rms.services.ReactionLogService;


/**
 * @author Daniel Moraru
 */
public interface ReactionLog extends ReactionLogService {
	/**
	 * @param rid
	 * @param event
	 * @param state
	 * @param dt
	 * @param di
	 */
	void addReaction(
			ReactionId rid,
			ReactionEvent event,
			ReactionState state,
			ReactionDeliveryType dt,
			ReactionDeliveryInfo di);
	/**
	 * @param rid
	 * @param state
	 */
	void setReactionState(ReactionId rid, ReactionState state);
	/**
	 * @param rid
	 * @param err
	 */
	void setReactionDeliveryError(ReactionId rid, Throwable err);
}
