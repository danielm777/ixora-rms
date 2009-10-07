/*
 * Created on 17-Oct-2004
 */
package com.ixora.rms.agents.db2;

import com.ixora.rms.agents.AgentId;
import com.ixora.rms.agents.impl.AbstractAgent;
import com.ixora.rms.exception.InvalidConfiguration;

/**
 * DB2SystemAgent
 */

public class DB2Agent extends AbstractAgent {

	/**
	 * Default constructor
	 * @throws DB2AgentNotSupportedException
	 */
	public DB2Agent(AgentId agentId, Listener listener) throws DB2AgentNotSupportedException {
        super(agentId, listener);

		// Create entities
		fRootEntity = new DB2RootEntity(this.fContext);
	}

	/**
	 * Called when configuration has changed
	 * @see com.ixora.rms.agents.impl.AbstractAgent#configCustomChanged()
	 */
	protected void configCustomChanged() throws InvalidConfiguration, Throwable	{
	    // Config changed (user, password, monitors etc). Disconnect and re-attach
		((DB2RootEntity)fRootEntity).beginSession();
	}
}
