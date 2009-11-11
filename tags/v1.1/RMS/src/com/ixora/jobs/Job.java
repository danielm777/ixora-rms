/*
 * Created on 24-Sep-2004
 */
package com.ixora.jobs;

import com.ixora.common.Startable;

/**
 * @author Daniel Moraru
 */
public interface Job extends Startable {
    /**
     * Listener.
     */
    public interface Listener {
        /**
         * @param jid
         * @param data
         */
        void output(JobId jid, String data);
        /**
         * @param jid
         * @param data
         */
        void error(JobId jid, String data);
    }
    /**
     * Set the listener.
     * @param listener
     */
    void setListener(Listener listener);
    /**
     * @return the job's id
     */
    JobId getId();
}
