/*
 * Created on 25-Sep-2004
 */
package com.ixora.jobs;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.ixora.jobs.services.JobEngineService;
import com.ixora.common.ConfigurationMgr;

/**
 * JobHistory.
 * @author Daniel Moraru
 */
public final class JobHistory {
    /**
     * Jobs.
     * Key: JobId
     * Value: JobHistoryDetails
     */
    private Map jobs;
    /** Max log size for jobs */
    private int maxLogSize;

    /**
     * Event handler.
     */
    private final class EventHandler implements JobEngineService.Listener {
        /**
         * @see com.ixora.jobs.HostJobManager.Listener#jobStateEvent(com.ixora.rms.internal.jobs.JobStateEvent)
         */
        public void jobStateEvent(JobStateEvent ev) {
            handleJobStateEvent(ev);
        }
        /**
         * @see com.ixora.jobs.HostJobManager.Listener#jobLogEvent(com.ixora.rms.internal.jobs.JobLogEvent)
         */
        public void jobLogEvent(JobLogEvent ev) {
            handleJobLogEvent(ev);
        }
    }

    /**
     * Constructor.
     */
    JobHistory(JobEngine je) {
        super();
        je.addListener(new EventHandler());
        this.jobs = new LinkedHashMap();
        maxLogSize = ConfigurationMgr.getInt(JobsComponent.NAME,
        	JobsConfigurationConstants.MAX_JOB_LOG_SIZE);
        // this val is in MB, convert it roughly into chars
        maxLogSize *= Math.pow(2, 10);
    }


    /**
     * @return the jobs in the history
     * Key: JobId
     * Value: JobHistoryDetails
     */
    public Map getJobs() {
        synchronized(this.jobs) {
            return Collections.unmodifiableMap(this.jobs);
        }
    }

    /**
     * Removes the job with the given id.
     * @param jid
     */
    public void removeJob(JobId jid) {
        synchronized(this.jobs) {
            this.jobs.remove(jid);
        }
    }

    /**
     * @param ev
     */
    private void handleJobStateEvent(JobStateEvent ev) {
        synchronized(this.jobs) {
            JobHistoryDetails hd = (JobHistoryDetails)
            		this.jobs.get(ev.getJid());
            if(hd != null) {
                JobState state = ev.getState();
                hd.setState(state);
                if(state == JobState.ERROR) {
                    if(ev.getErrorData() != null) {
                       hd.append(new JobLogEvent(
                               ev.getHost(), ev.getJid(),
                               JobLogEvent.ERROR, ev.getErrorData()));
                    }
                }
            }
        }
    }

    /**
     * @param ev
     */
    private void handleJobLogEvent(JobLogEvent ev) {
        synchronized(this.jobs) {
            JobHistoryDetails hd = (JobHistoryDetails)
            		this.jobs.get(ev.getJid());
            if(hd != null) {
                long size = hd.getLogSize();
                if(size <= maxLogSize) {
                    hd.append(ev);
            	}
            }
        }
    }

//  package access
    /**
     * Records a job in the history.
     * @param jid
     * @param def
     * @param state
     */
    void recordJob(JobId jid, JobDefinition def, JobState state) {
        synchronized(this.jobs) {
            this.jobs.put(jid, new JobHistoryDetails(jid, def, state));
        }
    }
}
