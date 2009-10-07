/*
 * Created on 13-Dec-2003
 */
package com.ixora.rms.agents.sqlserver;

import com.ixora.rms.agents.AgentId;
import com.ixora.rms.agents.impl.AbstractAgent;
import com.ixora.rms.exception.InvalidConfiguration;

/**
 * SQLServerAgent
 */
public class SQLServerAgent extends AbstractAgent {

    private boolean isRunning = false;

    /**
     * Default constructor, creates root entity
     * @throws Throwable
     */
	public SQLServerAgent(AgentId agentId, Listener listener) {
        super(agentId, listener);
		fRootEntity = new SqlTrace(this.fContext);
	}

    /**
     * @see com.ixora.common.Startable#start()
     */
    public void start() throws Throwable {
    	((SqlTrace)fRootEntity).start();
        // Only now we've entered the running state
        super.start();
        isRunning = true;
    }

    /**
     * @see com.ixora.common.Startable#stop()
     */
    public void stop() throws Throwable {
        try {
            ((SqlTrace)fRootEntity).stop();
        } catch (Throwable e) {
            // Non fatal error
            fContext.error(e);
        }

        // Only now we've exited the running state
        super.stop();
        isRunning = false;
    }

	/**
	 * @see com.ixora.rms.agents.impl.AbstractAgent#deactivate()
	 */
	public synchronized void deactivate() throws Throwable {
	    // Trace must be stopped at this point
	    stop();
	    super.deactivate();
	}

	/* (non-Javadoc)
	 * @see com.ixora.rms.Agent#configCustomChanged()
	 */
	protected void configCustomChanged() throws InvalidConfiguration, Throwable {
	    // Create children for the trace entity the first time configure is called
	    if (fRootEntity.getChildrenCount() == 0) {
	        ((SqlTrace)fRootEntity).createChildren();
	    }

	    // Restart the agent to pick up user/password/database changes
	    if (isRunning) {
			stop();
			start();
	    }
	}
}
