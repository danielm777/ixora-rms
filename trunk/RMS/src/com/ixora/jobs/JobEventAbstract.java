/*
 * Created on 24-Sep-2004
 */
package com.ixora.jobs;

import java.io.Serializable;

/**
 * @author Daniel Moraru
 */
public class JobEventAbstract implements Serializable {
    /** Host on which the job runs */
    protected String host;
    /** Job that generated this event */
    protected JobId jid;

    /**
     * Constructor.
     * @param host
     * @param jid
     */
    public JobEventAbstract(String host, JobId jid) {
        super();
        this.host = host;
        this.jid = jid;
    }

    /**
     * @return the host.
     */
    public String getHost() {
        return host;
    }

    /**
     * @return the jid.
     */
    public JobId getJid() {
        return jid;
    }
}
