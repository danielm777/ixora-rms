/*
 * Created on 24-Sep-2004
 */
package com.ixora.jobs.impl.commandtelnet;

import java.io.IOException;

import com.ixora.jobs.JobAbstract;
import com.ixora.jobs.JobId;
import com.ixora.common.net.telnet.TelnetConnection;
import com.ixora.common.process.RemoteProcessWrapper;
import com.ixora.common.process.TelnetProcessWrapper;

/**
 * A job representing an external command run via a telnet session.
 * @author Daniel Moraru
 */
public final class TelnetCommandJob extends JobAbstract {
    /** Process wrapper */
    private TelnetProcessWrapper process;
    private TelnetCommandJobData jobData;
    private TelnetConnection auth;

    /**
     * Event handler.
     */
    private final class EventHandler implements RemoteProcessWrapper.Listener {
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
		/**
		 * @see com.ixora.common.process.RemoteProcessWrapper.Listener#remoteError(java.lang.Exception)
		 */
		public void remoteError(Exception ex) {
			fireError(ex.getMessage());
		}
    }

    /**
     * Constructor.
     * @param tjd
     * @param jid
     * @throws IOException
     */
    public TelnetCommandJob(TelnetCommandJobData tjd, JobId jid) throws IOException {
        super(jid);
        this.jobData = tjd;
    }

    /**
     * @see com.ixora.common.Startable#start()
     */
    public void start() throws Throwable {
        synchronized(this) {
        	if(this.auth == null) {
	    		this.auth = new TelnetConnection(
	            		this.jobId.getHost(), this.jobData.getShellPrompt(),
	            		this.jobData.getUsername(), this.jobData.getPassword(),
	            		"not found");
        	}
        }
        // try and connect, must be outside synch block so stop() could work
        this.auth.connect();
        synchronized(this) {
	        if(this.process == null) {
	            this.process = new TelnetProcessWrapper(this.auth,
	            		this.jobData.getCommand(),
	                	new EventHandler());
	        }
	        this.process.start();
		}
    }

    /**
     * @throws Throwable
     * @see com.ixora.common.Startable#stop()
     */
    public void stop() throws Throwable {
    	synchronized(this) {
    		if(this.auth != null) {
    			this.auth.disconnect();
    		}
        	if(this.process != null) {
        		this.process.stop();
        	}
		}
    }
}
