/**
 * 30-Jul-2005
 */
package com.ixora.rms.reactions;

import java.io.Serializable;


/**
 * @author Daniel Moraru
 */
public final class ReactionStateRecord implements Serializable {
	private static final long serialVersionUID = -8576818090939401456L;
	private ReactionState fState;
	private long fTimestamp;

	ReactionStateRecord(ReactionState state, long ts) {
		fState = state;
		fTimestamp = ts;
	}

	/**
	 * @return state.
	 */
	public ReactionState getState() {
		return fState;
	}

	/**
	 * @return timestamp.
	 */
	public long getTimestamp() {
		return fTimestamp;
	}
}
