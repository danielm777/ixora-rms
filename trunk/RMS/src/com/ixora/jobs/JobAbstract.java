/*
 * Created on 24-Sep-2004
 */
package com.ixora.jobs;

/**
 * @author Daniel Moraru
 */
public abstract class JobAbstract implements Job {
    /** Job id */
    protected JobId jobId;
    /** Listener */
    protected Listener listener;

    /**
     * Constructor.
     * @param jid
     */
    public JobAbstract(JobId jid) {
        super();
        if(jid == null) {
            throw new IllegalArgumentException("null job id");
        }
        this.jobId = jid;
    }

    /**
     * @see com.ixora.jobs.Job#getId()
     */
    public JobId getId() {
        return jobId;
    }

	/**
     * @param listener the listener to set.
     */
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    /**
     * @param data
     */
    protected void fireOutput(String data) {
        this.listener.output(jobId, data);
    }

    /**
     * @param data
     */
    protected void fireError(String data) {
        this.listener.error(jobId, data);
    }
}
