/*
 * Created on 24-Sep-2004
 */
package com.ixora.jobs.impl.command;

import com.ixora.jobs.JobAbstract;
import com.ixora.jobs.JobId;
import com.ixora.common.process.LocalProcessWrapper;
import com.ixora.common.process.ProcessWrapper;

/**
 * A job representing an external command.
 * @author Daniel Moraru
 */
public final class ExternalCommandJob extends JobAbstract {
    /** Process wrapper */
    private LocalProcessWrapper process;

    /**
     * Event handler.
     */
    private final class EventHandler implements ProcessWrapper.Listener {
        /**
         * @see com.ixora.common.process.ProcessWrapper.Listener#error(java.lang.String)
         */
        public void error(String line) {
            fireError(line);
        }
        /**
         * @see com.ixora.common.process.ProcessWrapper.Listener#output(java.lang.String)
         */
        public void output(String line) {
            fireOutput(line);
        }
    }

    /**
     * Constructor.
     * @param cmd
     * @param jid
     */
    public ExternalCommandJob(String cmd, JobId jid) {
        super(jid);
        this.process = new LocalProcessWrapper(cmd, new EventHandler());
    }

    /**
     * @see com.ixora.common.Startable#start()
     */
    public void start() throws Throwable {
    	synchronized(this) {
            this.process.start();
		}
        this.process.waitFor();
    }

    /**
     * @throws Throwable
     * @see com.ixora.common.Startable#stop()
     */
    public void stop() throws Throwable {
    	synchronized(this) {
    		this.process.stop();
		}
    }
}
