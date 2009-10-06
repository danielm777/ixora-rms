/**
 * 04-Aug-2005
 */
package com.ixora.rms.dataengine.reactions;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ixora.jobs.library.JobLibraryId;
import com.ixora.rms.HostId;
import com.ixora.rms.ResourceId;
import com.ixora.common.exception.AppRuntimeException;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.rms.CounterId;
import com.ixora.rms.agents.AgentId;
import com.ixora.rms.client.locator.SessionAgentInfo;
import com.ixora.rms.client.locator.SessionArtefactInfoLocator;
import com.ixora.rms.client.locator.SessionCounterInfo;
import com.ixora.rms.client.locator.SessionEntityInfo;
import com.ixora.rms.client.locator.SessionResourceInfo;
import com.ixora.rms.reactions.ReactionDispatcher;
import com.ixora.rms.reactions.ReactionEvent;
import com.ixora.rms.reactions.ReactionId;

/**
 * @author Daniel Moraru
 */
class ReactionEnvironmentImpl implements ReactionEnvironment {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger("ReactionEnvironment");
	/** Artefact info locator */
	private SessionArtefactInfoLocator fInfoLocator;
	/** Time when reaction was first armed */
	private long fArmedTime;
	/** Time when the reaction was last armed */
	private long fLastArmedTime;
	/** Number of times the reaction was armed */
	private long fArmedCount;
	/** Number of times the reaction was fired */
	private long fFiredCount;
	/** Reaction dispatcher */
	private ReactionDispatcher fReactionDispatcher;
	/** Reaction event */
	private ReactionEvent fReactionEvent;
	/** Reaction event for advice */
	private ReactionEvent fReactionEventAdvice;
	/** The id of the email reaction */
	private ReactionId fReactionIdEmail;
	/** The id of the job reaction */
	private ReactionId fReactionIdJob;
	/** The id of the advise reaction */
	private ReactionId fReactionIdAdvise;
	/** Fully qualified, non-regex parameters values on which the reaction operates */
	private List<ResourceId> fMatchedParams;
	/** Store for custom objects */
	private Map<String, Serializable> fStore;

	/**
	 * @param matchedRids
	 * @param reactionDispatcher
	 * @param locator
	 */
	public ReactionEnvironmentImpl(
			List<ResourceId> matchedRids, ReactionDispatcher reactionDispatcher, SessionArtefactInfoLocator locator) {
		super();
		fMatchedParams = matchedRids;
		fInfoLocator = locator;
		fReactionDispatcher = reactionDispatcher;
	}

	/**
	 * @see com.ixora.rms.dataengine.reactions.ReactionEnvironment#getArmedTime()
	 */
	public long getArmedTime() {
		return fArmedTime;
	}

	/**
	 * @see com.ixora.rms.dataengine.reactions.ReactionEnvironment#getArmedCount()
	 */
	public long getArmedCount() {
		return fArmedCount;
	}

	/**
	 * @see com.ixora.rms.dataengine.reactions.ReactionEnvironment#getFiredCount()
	 */
	public long getFiredCount() {
		return fFiredCount;
	}

	/**
	 * @see com.ixora.rms.dataengine.reactions.ReactionEnvironment#getSecondsSinceArmed()
	 */
	public long getSecondsSinceArmed() {
		if(fArmedTime == 0) {
			return 0;
		}
		return (System.currentTimeMillis() - fArmedTime) / 1000;
	}

	/**
	 * @see com.ixora.rms.dataengine.reactions.ReactionEnvironment#getLastArmedTime()
	 */
	public long getLastArmedTime() {
		return fLastArmedTime;
	}

	/**
	 * @see com.ixora.rms.dataengine.reactions.ReactionEnvironment#getSecondsSinceLastArmed()
	 */
	public long getSecondsSinceLastArmed() {
		if(fLastArmedTime == 0) {
			return 0;
		}
		return (System.currentTimeMillis() - fLastArmedTime) / 1000;
	}

	/**
	 * @see com.ixora.rms.dataengine.reactions.ReactionEnvironment#getPath(java.lang.String)
	 */
	public String getPath(String rid) {
		ResourceId matched = getMatchedResourceId(rid);
		SessionResourceInfo rinfo = fInfoLocator.getResourceInfo(matched);
		if(rinfo == null) {
			return matched.toString();
		} else {
			return rinfo.getTranslatedPath();
		}
	}

	/**
	 * @see com.ixora.rms.dataengine.reactions.ReactionEnvironment#getHost(java.lang.String)
	 */
	public String getHost(String rid) {
		ResourceId id = getMatchedResourceId(rid);
		HostId hid = id.getHostId();
		return String.valueOf(hid);
	}

	/**
	 * @see com.ixora.rms.dataengine.reactions.ReactionEnvironment#getAgent(java.lang.String)
	 */
	public String getAgent(String rid) {
		ResourceId id = getMatchedResourceId(rid);
		SessionResourceInfo rinfo = fInfoLocator.getResourceInfo(id);
		AgentId aid = id.getAgentId();
		if(rinfo == null) {
			return String.valueOf(aid);
		} else {
			SessionAgentInfo ai = rinfo.getAgentInfo();
			return ai == null ? String.valueOf(aid) : ai.getTranslatedName();
		}
	}

	/**
	 * @see com.ixora.rms.dataengine.reactions.ReactionEnvironment#getCounter(java.lang.String)
	 */
	public String getCounter(String rid) {
		ResourceId matched = getMatchedResourceId(rid);
		SessionResourceInfo rinfo = fInfoLocator.getResourceInfo(matched);
		CounterId cid = matched.getCounterId();
		if(rinfo == null) {
			return String.valueOf(cid);
		} else {
			SessionCounterInfo ci = rinfo.getCounterInfo();
			return ci == null ? String.valueOf(rid) : ci.getTranslatedName();
		}
	}

	/**
	 * @see com.ixora.rms.dataengine.reactions.ReactionEnvironment#getEntityPart(java.lang.String, int)
	 */
	public String getEntityPart(String rid, int i) {
		ResourceId matched = getMatchedResourceId(rid);
		SessionResourceInfo rinfo = fInfoLocator.getResourceInfo(matched);
		if(rinfo == null) {
			return matched.toString();
		} else {
			SessionEntityInfo einfo = rinfo.getEntityInfo();
			if(einfo == null) {
				return matched.toString();
			}
			String[] fragments = einfo.getTranslatedEntityPathFragments();
			if(i >= fragments.length) {
				return matched.toString();
			}
			return fragments[i];
		}
	}

	/**
	 * @see com.ixora.rms.dataengine.reactions.ReactionEnvironment#getEntityPath(java.lang.String)
	 */
	public String getEntityPath(String rid) {
		ResourceId matched = getMatchedResourceId(rid);
		SessionResourceInfo rinfo = fInfoLocator.getResourceInfo(matched);
		if(rinfo == null) {
			return matched.toString();
		} else {
			SessionEntityInfo einfo = rinfo.getEntityInfo();
			if(einfo == null) {
				return matched.toString();
			}
			return einfo.getTranslatedEntityPath();
		}
	}

	/**
	 * @see com.ixora.rms.dataengine.reactions.ReactionEnvironment#email()
	 */
	public void email() {
		// create reaction only if no outstanding (in ARMED state) one exists
		// for this delivery method
		if(fReactionIdEmail == null) {
			fReactionIdEmail = this.fReactionDispatcher.email(fReactionEvent);
		}
	}

	/**
	 * @see com.ixora.rms.dataengine.reactions.ReactionEnvironment#job(java.lang.String)
	 */
	public void job(String jobLibraryId) {
		// create reaction only if no outstanding (in ARMED state) one exists
		// for this delivery method
		if(fReactionIdJob == null) {
			fReactionIdJob = this.fReactionDispatcher.job(fReactionEvent,
					new JobLibraryId(jobLibraryId));
		}
	}

	/**
	 * @return
	 */
	public boolean isArmed() {
		return fArmedTime != 0;
	}

// package access

	void setReactionEvent(ReactionEvent ev) {
		if(this.fReactionEvent == null) {
			this.fReactionEvent = ev;
		}
	}

	/**
	 * Invoked to fire reactions.
	 */
	void fired() {
		if(fReactionIdEmail != null) {
			fReactionDispatcher.fire(fReactionIdEmail);
			fReactionIdEmail = null;
		}
		if(fReactionIdJob != null) {
			fReactionDispatcher.fire(fReactionIdJob);
			fReactionIdJob = null;
		}
		if(fReactionIdAdvise != null) {
			fReactionDispatcher.fire(fReactionIdAdvise);
			fReactionIdAdvise = null;
		}
		fFiredCount++;
		reset();
	}

	/**
	 * Invoked after reactions have been armed.
	 */
	void armed() {
		if(fArmedTime == 0) {
			fArmedTime = System.currentTimeMillis();
			fLastArmedTime = fArmedTime;
		} else {
			fLastArmedTime = System.currentTimeMillis();
		}
		fArmedCount++;
	}

	/**
	 * Invoked to disarm reactions.
	 */
	void disarmed() {
		reset();
	}

	/**
	 * Resets the environment.
	 */
	private void reset() {
		// reset environment
		fArmedCount = 0;
		fArmedTime = 0;
		fLastArmedTime = 0;
		fReactionEvent = null;
		if(fReactionIdEmail != null) {
			fReactionDispatcher.disarm(fReactionIdEmail);
		}
		if(fReactionIdJob != null) {
			fReactionDispatcher.disarm(fReactionIdJob);
		}
		if(fReactionIdAdvise != null) {
			fReactionDispatcher.disarm(fReactionIdAdvise);
		}
		fReactionIdEmail = null;
		fReactionIdAdvise = null;
		fReactionIdJob = null;
	}

	/**
	 * @param event
	 */
	void advise(ReactionEvent event) {
		fReactionEventAdvice = event;
		fReactionIdAdvise = this.fReactionDispatcher.advise(event);
	}

	/**
	 * @return
	 */
	ReactionId[] getReactionIds() {
		List<ReactionId> ret = new LinkedList<ReactionId>();
		if(fReactionIdEmail != null) {
			ret.add(fReactionIdEmail);
		}
		if(fReactionIdJob != null) {
			ret.add(fReactionIdJob);
		}
		if(fReactionIdAdvise != null) {
			ret.add(fReactionIdAdvise);
		}
		return ret.toArray(new ReactionId[ret.size()]);
	}

	/**
	 * @return
	 */
	ReactionEvent[] getReactionEvents() {
		List<ReactionEvent> ret = new LinkedList<ReactionEvent>();
		if(fReactionIdEmail != null) {
			ret.add(fReactionEvent);
		}
		if(fReactionIdJob != null) {
			ret.add(fReactionEvent);
		}
		if(fReactionIdAdvise != null) {
			ret.add(fReactionEventAdvice);
		}
		return ret.toArray(new ReactionEvent[ret.size()]);
	}

	/**
	 * @see com.ixora.rms.dataengine.reactions.ReactionEnvironment#log(java.lang.String)
	 */
	public void log(String logMsg) {
		logger.error(logMsg);
	}

	/**
	 * @param regexId
	 * @return
	 */
	private ResourceId getMatchedResourceId(String regexId) {
		ResourceId regId = new ResourceId(regexId);
		for(ResourceId rid : fMatchedParams) {
			if(regId.matches(rid)) {
				return rid;
			}
		}
		throw new AppRuntimeException("No matched found for parameter regex rid");
	}

	/**
	 * @see com.ixora.rms.dataengine.reactions.ReactionEnvironment#put(java.lang.String, java.io.Serializable)
	 */
	public void put(String key, Serializable obj) {
		if(fStore == null) {
			fStore = new HashMap<String, Serializable>();
		}
		fStore.put(key, obj);
	}

	/**
	 * @see com.ixora.rms.dataengine.reactions.ReactionEnvironment#get(java.lang.String)
	 */
	public Serializable get(String key) {
		if(fStore == null) {
			return null;
		}
		return fStore.get(key);
	}
}
