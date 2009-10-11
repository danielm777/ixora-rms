/**
 * 30-Jul-2005
 */
package com.ixora.rms.reactions;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Daniel Moraru
 */
public final class ReactionLogRecord implements Serializable {
	private static final long serialVersionUID = 2485058161888624084L;
	private ReactionId fReactionId;
	private LinkedList<ReactionStateRecord> fStates;
	private ReactionEvent fReactionEvent;
	private ReactionDeliveryType fDeliveryType;
	private ReactionDeliveryInfo fDeliveryInfo;

	/**
	 * @param id
	 * @param event
	 * @param state
	 * @param stateTimestamp
	 * @param dt
	 * @param di
	 */
	public ReactionLogRecord(ReactionId id, ReactionEvent event, ReactionState state, long stateTimestamp, ReactionDeliveryType dt, ReactionDeliveryInfo di) {
		super();
		fReactionId = id;
		fReactionEvent = event;
		fStates = new LinkedList<ReactionStateRecord>();
		fStates.add(new ReactionStateRecord(state, stateTimestamp));
		fDeliveryType = dt;
		fDeliveryInfo = di;
	}
	/**
	 * @return reaction event.
	 */
	public ReactionEvent getReactionEvent() {
		return fReactionEvent;
	}
	/**
	 * @return reaction id.
	 */
	public ReactionId getReactionId() {
		return fReactionId;
	}
	/**
	 * @return
	 */
	public ReactionDeliveryInfo getReactionDeliveryInfo() {
		return fDeliveryInfo;
	}
	/**
	 * @return
	 */
	public ReactionDeliveryType getReactionDeliveryType() {
		return fDeliveryType;
	}
	/**
	 * @return states.
	 */
	@SuppressWarnings("unchecked")
	public List<ReactionStateRecord> getStates() {
		return (List<ReactionStateRecord>)fStates.clone();
	}

// package access only

	/**
	 * @param state
	 * @param l
	 */
	void addState(ReactionState state, long l) {
		fStates.add(new ReactionStateRecord(state, l));
	}

	/**
	 * @param err
	 */
	void setDeliveryError(Throwable err) {
		fDeliveryInfo.setError(err);
	}
}
