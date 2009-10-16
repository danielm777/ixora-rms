/**
 * 30-Jul-2005
 */
package com.ixora.rms.reactions;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import com.ixora.jobs.JobDefinition;
import com.ixora.jobs.JobEngine;
import com.ixora.jobs.library.JobLibrary;
import com.ixora.jobs.library.JobLibraryDefinition;
import com.ixora.jobs.library.JobLibraryId;
import com.ixora.common.ConfigurationMgr;
import com.ixora.common.ComponentConfiguration;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.net.NetUtils;
import com.ixora.common.thread.RunQueue;
import com.ixora.common.utils.Utils;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.reactions.email.ReactionsEmailComponent;
import com.ixora.rms.reactions.email.ReactionsEmailComponentConfigurationConstants;

/**
 * @author Daniel Moraru
 */
public final class ReactionDispatcherImpl implements ReactionDispatcher, Observer {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(ReactionDispatcherImpl.class);
	/** Queue holding reactions to be triggered */
	private RunQueue fQueue;
	/** Reaction log */
	private ReactionLog fLog;
	/** Job engine */
	private JobEngine fJobEngine;
	/** Job library */
	private JobLibrary fJobLibrary;

// email cached default properties
	private String fServer;
	private int fPort;
	private String fFrom;
	private List<String> fTo;
	private String fSubject;

	/**
	 * Email reactor.
	 */
	private final class EmailReactor implements Runnable {
		private ReactionDeliveryInfoEmail fDeliveryInfo;
		private ReactionId fReactionId;

		EmailReactor(ReactionId rid, ReactionDeliveryInfoEmail di) {
			fReactionId = rid;
			fDeliveryInfo = di;
		}
		/**
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			try {
				List<String> to = fDeliveryInfo.getTo();
				if(Utils.isEmptyCollection(to)) {
					throw new RMSException("Destination email addresses missing");
				}
				String server = fDeliveryInfo.getServer();
				if(Utils.isEmptyString(server)) {
					throw new RMSException("SMTP server name is missing");
				}
				for(String address : to) {
					if(!Utils.isEmptyString(address)) {
						NetUtils.sendEmail(
								server,
								fDeliveryInfo.getPort(),
								address,
								fDeliveryInfo.getFrom(),
								fDeliveryInfo.getSubject(),
								fDeliveryInfo.getMessage());
					}
				}
				fLog.setReactionState(fReactionId, ReactionState.FIRED_OK);
			} catch(Throwable e) {
				fLog.setReactionDeliveryError(fReactionId, e);
				logger.error(e);
			}
		}
	}

	/**
	 * Job reactor.
	 */
	private final class JobReactor implements Runnable {
		private ReactionId fReactionId;
		private ReactionDeliveryInfoJob fDeliveryInfo;

		JobReactor(ReactionId rid, ReactionDeliveryInfoJob di) {
			fReactionId = rid;
			fDeliveryInfo = di;
		}
		/**
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			try {
				JobLibraryDefinition jobLib = fJobLibrary.getJob(fDeliveryInfo.getJobLibrayId());
				if(jobLib == null) {
					// TODO localize
					RMSException ex = new RMSException("Job " + fDeliveryInfo.getJobLibrayId() + " not found in library");
					fLog.setReactionDeliveryError(fReactionId, ex);
					logger.error(ex);
				} else {
					JobDefinition job = new JobDefinition(jobLib.getName(), jobLib.getHost(), null, jobLib.getJobData());
					fJobEngine.runJob(job);
					fLog.setReactionState(fReactionId, ReactionState.FIRED_OK);
				}
			} catch(Throwable e) {
				fLog.setReactionDeliveryError(fReactionId, e);
				logger.error(e);
			}
		}
	}

	/**
	 * Advice reactor.
	 */
	private final class AdviceReactor implements Runnable {
		private ReactionId fReactionId;
		//private ReactionDeliveryInfoAdvice fDeliveryInfo;

		AdviceReactor(ReactionId rid, ReactionDeliveryInfoAdvice di) {
			fReactionId = rid;
			//fDeliveryInfo = di;
		}
		/**
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			try {
				fLog.setReactionState(fReactionId, ReactionState.FIRED_OK);
			} catch(Throwable e) {
				fLog.setReactionDeliveryError(fReactionId, e);
				logger.error(e);
			}
		}
	}

	/**
	 * Constructor.
	 */
	public ReactionDispatcherImpl(ReactionLog log, JobEngine jobEngine, JobLibrary jobLibrary) {
		super();
		fLog = log;
		fJobEngine = jobEngine;
		fJobLibrary = jobLibrary;
		fQueue = new RunQueue(true);
		fQueue.start();

		// cache default email props
		setUpEmailConfig();

		ConfigurationMgr.get(ReactionsEmailComponent.NAME).addObserver(this);
	}

	/**
	 *
	 */
	private synchronized void setUpEmailConfig() {
		ComponentConfiguration config = ConfigurationMgr.get(ReactionsEmailComponent.NAME);
		fServer = config.getString(ReactionsEmailComponentConfigurationConstants.EMAIL_SERVER);
		fPort = config.getInt(ReactionsEmailComponentConfigurationConstants.EMAIL_SERVER_PORT);
		fFrom = config.getString(ReactionsEmailComponentConfigurationConstants.EMAIL_FROM);
		fTo = config.getList(ReactionsEmailComponentConfigurationConstants.EMAIL_TO);
		fSubject = config.getString(ReactionsEmailComponentConfigurationConstants.EMAIL_SUBJECT);
	}

	/**
	 * @see com.ixora.rms.reactions.ReactionDispatcher#email(com.ixora.rms.reactions.ReactionEvent)
	 */
	public synchronized ReactionId email(ReactionEvent event) {
		return email(event, fServer, fPort, fTo);
	}

	/**
	 * @see com.ixora.rms.reactions.ReactionDispatcher#email(com.ixora.rms.reactions.ReactionEvent, java.lang.String, int, java.lang.String)
	 */
	public synchronized ReactionId email(ReactionEvent event, String server, int port, List<String> to) {
		ReactionId rid = generateReactionId();
		ReactionDeliveryInfoEmail di = new ReactionDeliveryInfoEmail(
				server, port, fFrom, to, fSubject, getMessage(event));
		fLog.addReaction(rid, event, ReactionState.ARMED, ReactionDeliveryType.EMAIL, di);
		return rid;
	}

	/**
	 * @see com.ixora.rms.reactions.ReactionDispatcher#job(com.ixora.rms.reactions.ReactionEvent, java.lang.String)
	 */
	public synchronized ReactionId job(ReactionEvent event, JobLibraryId jobLibraryId) {
		ReactionId rid = generateReactionId();
		ReactionDeliveryInfoJob di = new ReactionDeliveryInfoJob(
				jobLibraryId, getMessage(event));
		fLog.addReaction(rid, event, ReactionState.ARMED, ReactionDeliveryType.JOB, di);
		return rid;
	}

	/**
	 * @see com.ixora.rms.reactions.ReactionDispatcher#advise(com.ixora.rms.reactions.ReactionEvent)
	 */
	public synchronized ReactionId advise(ReactionEvent event) {
		ReactionId rid = generateReactionId();
		ReactionDeliveryInfoAdvice di = new ReactionDeliveryInfoAdvice(getMessage(event));
		fLog.addReaction(rid, event, ReactionState.ARMED, ReactionDeliveryType.ADVICE, di);
		return rid;
	}

	/**
	 * This method must not throw any exception but try to log any errors
	 * encountered to the job log or if this is not possible the normal application log.
	 * @see com.ixora.rms.reactions.ReactionDispatcher#fire(com.ixora.rms.reactions.ReactionId)
	 */
	public synchronized void fire(ReactionId rid) {
		ReactionLogRecord rec = null;
		try {
			rec = fLog.getRecord(rid);
		} catch(RMSException e) {
			// job log not available, write error to the application log
			logger.error("Failed to find log record for reaction with id " + rid, e);
			return;
		}
		ReactionDeliveryType type = rec.getReactionDeliveryType();
		if(type == ReactionDeliveryType.EMAIL) {
			fLog.setReactionState(rid, ReactionState.FIRED);
			fQueue.run(new EmailReactor(rid, (ReactionDeliveryInfoEmail)rec.getReactionDeliveryInfo()));
		} else if(type == ReactionDeliveryType.JOB) {
			fLog.setReactionState(rid, ReactionState.FIRED);
			fQueue.run(new JobReactor(rid, (ReactionDeliveryInfoJob)rec.getReactionDeliveryInfo()));
		} else if(type == ReactionDeliveryType.ADVICE) {
			fLog.setReactionState(rid, ReactionState.FIRED);
			fQueue.run(new AdviceReactor(rid, (ReactionDeliveryInfoAdvice)rec.getReactionDeliveryInfo()));
		}
	}

	/**
	 * @see com.ixora.rms.reactions.ReactionDispatcher#disarm(com.ixora.rms.reactions.ReactionId)
	 */
	public synchronized void disarm(ReactionId rid) {
		fLog.setReactionState(rid, ReactionState.DISARMED);
	}

	/**
	 * @return
	 */
	private static ReactionId generateReactionId() {
		return new ReactionId(Utils.getRandomInt());
	}

	/**
	 * @param event
	 * @return
	 */
	private static String getMessage(ReactionEvent event) {
		return event.toString();
	}

	/**
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg) {
		if(o == ConfigurationMgr.get(ReactionsEmailComponent.NAME)) {
			setUpEmailConfig();
		}
	}
}
