/**
 * 30-Jul-2005
 */
package com.ixora.rms.reactions;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ixora.common.collections.CircullarLinkedList;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.utils.Utils;
import com.ixora.rms.exception.RMSException;

/**
 * @author Daniel Moraru
 */
public final class ReactionLogImpl implements ReactionLog {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(ReactionLogImpl.class);
	/** Log records */
	private Map<ReactionId, ReactionLogRecord> fRecords;
	/** Listeners */
	private List<Listener> fListeners;
	/***/
	private CircullarLinkedList<ReactionId> fBuffer;

	/**
	 *
	 */
	public ReactionLogImpl() {
		super();
		fRecords = new LinkedHashMap<ReactionId, ReactionLogRecord>();
		fBuffer = new CircullarLinkedList<ReactionId>(300);
		fListeners = new LinkedList<Listener>();
	}

	/**
	 * @see com.ixora.rms.reactions.ReactionLog#addReaction(com.ixora.rms.reactions.ReactionId, com.ixora.rms.reactions.ReactionEvent, com.ixora.rms.reactions.ReactionState, com.ixora.rms.reactions.ReactionDeliveryType, com.ixora.rms.reactions.ReactionDeliveryInfo)
	 */
	public void addReaction(ReactionId rid, ReactionEvent event, ReactionState state, ReactionDeliveryType dt, ReactionDeliveryInfo di) {
		ReactionLogRecord rec = new ReactionLogRecord(rid, event, state, System.currentTimeMillis(), dt, di);
		synchronized(this) {
			fRecords.put(rid, rec);
			ReactionId removed = fBuffer.addAndReturnRemoved(rid);
			if(removed != null) {
				fRecords.remove(removed);
				if(logger.isWarnEnabled()) {
					logger.info("Reaction " + rid + " was removed from the reaction log: " + di.getMessage());
				}
			}
		}
		fireReactionLogEvent(new ReactionLogEvent(rid, state));
	}

	/**
	 * @see com.ixora.rms.reactions.ReactionLog#setReactionDeliveryInfo(ReactionId, Throwable)
	 */
	public void setReactionDeliveryError(ReactionId rid, Throwable err) {
		synchronized(this) {
			ReactionLogRecord rec = fRecords.get(rid);
			if(rec != null) {
				rec.setDeliveryError(err);
				rec.addState(ReactionState.FIRED_ERROR, System.currentTimeMillis());
			}
		}
		fireReactionLogEvent(new ReactionLogEvent(rid, ReactionState.FIRED_ERROR));
	}

	/**
	 * @see com.ixora.rms.reactions.ReactionLog#setReactionState(com.ixora.rms.reactions.ReactionId, com.ixora.rms.reactions.ReactionState)
	 */
	public void setReactionState(ReactionId rid, ReactionState state) {
		synchronized(this) {
			ReactionLogRecord rec = fRecords.get(rid);
			if(rec != null) {
				rec.addState(state, System.currentTimeMillis());
			}
		}
		fireReactionLogEvent(new ReactionLogEvent(rid, state));
	}

	/**
	 * @throws RMSException
	 * @see com.ixora.rms.services.ReactionLogService#getRecords()
	 */
	@SuppressWarnings("unchecked")
	public List<ReactionLogRecord> getRecords() throws RMSException {
		try {
			synchronized(this) {
				return (List<ReactionLogRecord>)Utils.deepClone(new ArrayList<ReactionLogRecord>(fRecords.values()));
			}
		} catch(Exception e) {
			throw new RMSException(e);
		}
	}

	/**
	 * @throws RMSException
	 * @see com.ixora.rms.reactions.ReactionLog#getRecord(com.ixora.rms.reactions.ReactionId)
	 */
	public ReactionLogRecord getRecord(ReactionId rid) throws RMSException {
		try {
			synchronized(this) {
				ReactionLogRecord rec = fRecords.get(rid);
				return (ReactionLogRecord)Utils.deepClone(rec);
			}
		} catch(Exception e) {
			throw new RMSException(e);
		}
	}

	/**
	 * @param event
	 */
	private void fireReactionLogEvent(ReactionLogEvent event) {
		synchronized(fListeners) {
			for(Listener listener : fListeners) {
				try {
					listener.reactionLogEvent(event);
				} catch(Exception e) {
					logger.error(e);
				}
			}
		}
	}

	/**
	 * @see com.ixora.rms.services.ReactionLogService#addListener(com.ixora.rms.services.ReactionLogService.Listener)
	 */
	public void addListener(Listener listener) {
		if(listener != null && !fListeners.contains(listener)) {
			synchronized(fListeners) {
				fListeners.add(listener);
			}
		}
	}

	/**
	 * @see com.ixora.rms.services.ReactionLogService#removeListener(com.ixora.rms.services.ReactionLogService.Listener)
	 */
	public void removeListener(Listener listener) {
		synchronized(fListeners) {
			fListeners.remove(listener);
		}
	}
}
