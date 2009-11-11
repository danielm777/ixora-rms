/*
 * Created on 24-Sep-2004
 */
package com.ixora.jobs;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.ixora.common.StartableState;
import com.ixora.common.exception.AppException;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.thread.ExecutorEvent;
import com.ixora.common.thread.StandardExecutor;
import com.ixora.rms.exception.RMSException;

/**
 * Job manager.
 * @author Daniel Moraru
 */
public final class HostJobManager {
    /** Logger */
    private static final AppLogger logger = AppLoggerFactory.getLogger(HostJobManager.class);

    /**
     * Listener.
     */
    public interface Listener {
        /**
         * @param ev
         */
        void jobStateEvent(JobStateEvent ev);
        /**
         * @param ev
         */
        void jobLogEvent(JobLogEvent ev);
    }

    /** Host name */
    private String host;
    /** Job factory */
    private JobFactory jobFactory;
    /** Scheduler */
    private Timer scheduler;
    /** List of running jobs */
    private Map<JobId, StandardExecutor> runningJobs;
    /** Listener */
    private Listener listener;
    /** Event handler */
    private EventHandler eventHandler;

    /**
     * Event handler.
     */
    private final class EventHandler implements Job.Listener,
    		StandardExecutor.Listener {
        /**
         * @see com.ixora.jobs.Job.Listener#output(com.ixora.rms.internal.jobs.JobId, java.lang.String)
         */
        public void output(JobId jid, String data) {
            handleJobOutput(jid, data);
        }
        /**
         * @see com.ixora.jobs.Job.Listener#error(com.ixora.rms.internal.jobs.JobId, java.lang.String)
         */
        public void error(JobId jid, String data) {
            handleJobError(jid, data);
        }
        /**
         * @see com.ixora.common.thread.Executor.Listener#startableStateChanged(com.ixora.common.thread.ExecutorEvent)
         */
        public void startableStateChanged(ExecutorEvent ev) {
            handleJobStateChanged(ev);
        }
    }

    /**
     * Constructor.
     * @param host
     * @param jobFactory
     * @param listener
     */
    public HostJobManager(
            String host,
            JobFactory jobFactory,
            Listener listener) {
        super();
        if(jobFactory == null) {
            throw new IllegalArgumentException("null job factory");
        }
        if(listener == null) {
            throw new IllegalArgumentException("null listener");
        }
        this.eventHandler = new EventHandler();
        this.jobFactory = jobFactory;
        this.listener = listener;
        this.host = host;
        this.runningJobs = new HashMap<JobId, StandardExecutor>();
        // make a deamon scheduler
        this.scheduler = new Timer(true);
    }

    /**
     * @param def
     */
    public JobId runJob(JobDefinition def) throws RMSException {
        final Job job = jobFactory.createJob(def);
        job.setListener(eventHandler);
        TimerTask task = new TimerTask(){
            public void run() {
                executeJob(job);
            }};
        Date date = def.getDate();
        if(date == null) {
            this.scheduler.schedule(task, 0);
        } else {
            this.scheduler.schedule(task, date);
        }
        return job.getId();
    }

    /**
     * Cancels the given job.
     * @param jid
     */
    public void cancelJob(JobId jid) {
        StandardExecutor je = null;
        synchronized(this.runningJobs) {
            je = this.runningJobs.get(jid);
        }
        if(je != null) {
        	je.stop();
        }
    }

    /**
     * @param jid
     * @return true if the job with the given id is managed by this job manager
     */
    public boolean hasJob(JobId jid) {
    	synchronized(this.runningJobs) {
    		return this.runningJobs.containsKey(jid);
		}
    }

    /**
     * @param jid
     * @param data
     */
    private void handleJobError(JobId jid, String data) {
        this.listener.jobLogEvent(new JobLogEvent(
                host, jid,
                JobLogEvent.ERROR, data));
    }

    /**
     * @param jid
     * @param data
     */
    private void handleJobOutput(JobId jid, String data) {
        this.listener.jobLogEvent(new JobLogEvent(
                host, jid,
                JobLogEvent.OUTPUT, data));
    }

    /**
     * @param ev
     */
    private void handleJobStateChanged(ExecutorEvent ev) {
        JobState state = mapState(ev.getStartableState());
        if(state == null) {
            return;
        }
        JobId jid = new JobId(ev.getStartableID());
        if(state == JobState.CANCELED
              || state == JobState.ERROR
              || state == JobState.FINISHED) {
            removeJob(jid);
        }
        JobStateEvent je;
        if(state == JobState.ERROR) {
            Throwable e = ev.getException();
            je = new JobStateEvent(host, jid,
                    e != null ? e.getMessage() : "");
        } else {
            je = new JobStateEvent(host, jid, state);
        }
        this.listener.jobStateEvent(je);
    }

    /**
     * Maps the given startable state to a job state.
     * @param state
     * @return
     */
    private JobState mapState(StartableState state) {
        if(state == StartableState.STARTED) {
            return JobState.STARTED;
        }
        if(state == StartableState.FINISHED) {
            return JobState.FINISHED;
        }
        if(state == StartableState.STOPPED) {
            return JobState.CANCELED;
        }
        if(state == StartableState.ERROR) {
            return JobState.ERROR;
        }
        return null;
    }

    /**
     * Executes the given job.
     * @param job
     */
    private void executeJob(Job job) {
       try {
           StandardExecutor je = new StandardExecutor(job,
                   job.getId().toString(), this.eventHandler);
           synchronized(this.runningJobs) {
               this.runningJobs.put(job.getId(), je);
           }
       	   je.start();
        } catch(Throwable e) {
            handleJobStateChanged(new ExecutorEvent(job.getId().toString(),
                    StartableState.ERROR, (Exception)(e instanceof Exception ? e : new AppException(e))));
            logger.error(e);
        }
    }

    /**
     * Removes the job with the given id.
     * @param jid
     */
    private void removeJob(JobId jid) {
        synchronized(this.runningJobs) {
            this.runningJobs.remove(jid);
        }
    }
}
