/*
 * Created on 25-Sep-2004
 */
package com.ixora.jobs;

import java.util.Date;

import com.ixora.common.utils.Utils;

/**
 * Details of the jobs as stored in the job history
 * @author Daniel Moraru
 */
public final class JobHistoryDetails {
    /** Job ID */
    private JobId jobId;
    /** Job definition */
    private JobDefinition jobDefinition;
    /** JobState */
    private JobState jobState;
    /** Jobs output log */
    private StringBuffer jobLogOut;
    /** Job error log */
    private StringBuffer jobLogErr;
    /** Date when job was started */
    private Date dateStarted;
    /** Date when the job ended */
    private Date dateEnded;
    /** The size of cumulated output and error logs */
    private long logSize;

    /**
     * Constructor.
     * @param jid
     * @param def
     * @param state
     */
    public JobHistoryDetails(JobId jid, JobDefinition def, JobState state) {
        super();
        this.jobId = jid;
        this.jobDefinition = def;
        this.jobState = state;
        this.jobLogErr = new StringBuffer();
        this.jobLogOut = new StringBuffer();
    }

    /**
     * @return the jobDefinition.
     */
    public JobDefinition getJobDefinition() {
        return jobDefinition;
    }
    /**
     * @return the jobLogErr.
     */
    public String getJobLogErr() {
        return jobLogErr.toString();
    }
    /**
     * @return the jobLogOut.
     */
    public String getJobLogOut() {
        return jobLogOut.toString();
    }
    /**
     * @return the jobState.
     */
    public JobState getJobState() {
        return jobState;
    }

    /**
     * @return the jobId.
     */
    public JobId getJobId() {
        return jobId;
    }

    /**
     * @return the dateEnded.
     */
    public Date getDateEnded() {
        return dateEnded;
    }
    /**
     * @return the dateStarted.
     */
    public Date getDateStarted() {
        return dateStarted;
    }

    /**
     * @return the logSize.
     */
    public long getLogSize() {
        return logSize;
    }

// package access
    /**
     * Appends data from the given job log event.
     */
    void append(JobLogEvent ev) {
        if(ev.getType() == JobLogEvent.OUTPUT) {
            this.jobLogOut.append(ev.getData());
            this.jobLogOut.append(Utils.getNewLine());
        } else {
            this.jobLogErr.append(ev.getData());
            this.jobLogErr.append(Utils.getNewLine());
        }
        logSize += ev.getData().length();
    }

    /**
     * Updates the state.
     * @param state
     */
    void setState(JobState state) {
        this.jobState = state;
        if(state == JobState.CANCELED
                || state == JobState.ERROR
                || state == JobState.FINISHED) {
            dateEnded = new Date();
        } else if(state == JobState.STARTED) {
            dateStarted = new Date();
        }

    }
}
