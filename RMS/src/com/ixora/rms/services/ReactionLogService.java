/**
 * 30-Jul-2005
 */
package com.ixora.rms.services;

import java.util.List;

import com.ixora.rms.exception.RMSException;
import com.ixora.rms.reactions.ReactionId;
import com.ixora.rms.reactions.ReactionLogRecord;
import com.ixora.rms.reactions.ReactionState;

/**
 * @author Daniel Moraru
 */
public interface ReactionLogService {
	/**
	 * Reaction log event.
	 */
	public final class ReactionLogEvent {
		private ReactionId fReactionId;
		private ReactionState fReactionState;

		public ReactionLogEvent(ReactionId rid, ReactionState state) {
			fReactionId = rid;
			fReactionState = state;
		}

		public ReactionId getReactionId() {
			return fReactionId;
		}
		public ReactionState getReactionState() {
			return fReactionState;
		}
	}
	/**
	 * Listener.
	 */
	public interface Listener {
		/**
		 * @param event
		 */
		void reactionLogEvent(ReactionLogEvent event);
	}
	/**
	 * @return all reaction records logged
	 * @throws RMSException
	 */
	List<ReactionLogRecord> getRecords() throws RMSException;
	/**
	 * @return
	 * @throws RMSException
	 */
	ReactionLogRecord getRecord(ReactionId rid) throws RMSException;
	/**
	 * Adds a listener.
	 * @param listener
	 */
	void addListener(Listener listener);
	/**
	 * Removes the listener.
	 * @param listener
	 */
	void removeListener(Listener listener);
}
