/*
 * Created on 16-Jan-2005
 */
package com.ixora.rms.dataengine.external;

import com.ixora.rms.reactions.ReactionId;


/**
 * QueryListener
 * A client who registers a query will implement this interface to
 * receive data resulted after query processing.
 */
public interface QueryListener {
	/**
	 * Data flowing through this method is already processed
	 * and ready for display
	 * @param data The QueryData resulted after processing the query
	 */
	public void dataAvailable(QueryData data);
	/**
	 * Invoked when the query associated with this executor was unregistered
	 * from the data engine.
	 */
	public void expired();
	/**
	 * Invoked when a reaction has been armed.
	 * @param rids
	 */
	public void reactionsArmed(ReactionId[] rids);
	/**
	 * Invoked when a reaction has been disarmed.
	 * @param rids
	 */
	public void reactionsDisarmed(ReactionId[] rids);
	/**
	 * Invoked when a reaction has been disarmed.
	 * @param rids
	 */
	public void reactionsFired(ReactionId[] rids);
};
