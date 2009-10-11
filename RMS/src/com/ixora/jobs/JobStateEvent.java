/*
 * Created on 24-Sep-2004
 */
package com.ixora.jobs;

/**
 * @author Daniel Moraru
 */
public final class JobStateEvent extends JobEventAbstract {
	private static final long serialVersionUID = 7650671274553804984L;
	/** State of the job that generated this event */
    private JobState state;
    /** Error data associated with a state of ERROR */
    private String errorData;

    /**
     * Constructor.
     * @param host
     * @param jid
     * @param state
     */
    public JobStateEvent(String host,
            JobId jid, JobState state) {
        super(host, jid);
        this.state = state;
    }
    /**
     * Constructor.
     * @param host
     * @param jid
     * @param errorData
     */
    public JobStateEvent(String host,
            JobId jid, String errorData) {
        super(host, jid);
        this.state = JobState.ERROR;
        this.errorData = errorData;
    }
    /**
     * @return the state.
     */
    public JobState getState() {
        return state;
    }
    /**
     * @return the errorData.
     */
    public String getErrorData() {
        return errorData;
    }
}
