/*
 * Created on 20-Aug-2005
 */
package com.ixora.rms.agents.wmi;

import com.ixora.rms.agents.AgentId;
import com.ixora.rms.agents.impl.AbstractAgent;


/**
 * WMIAgent
 * Uses native code to retrieve information from the Windows Management
 * Instrumentation service, on local or remote machines. Relationships
 * between WMI classes are ignored in this version.
 */
public class WMIAgent extends AbstractAgent {

	/**
	 * Constructor.
	 * @throws Throwable
	 */
	public WMIAgent(AgentId agentId, Listener listener) throws Throwable {
        super(agentId, listener, true); // use private collector as each cycle might be expensive
		// Create entities
		fRootEntity = new WMIRootEntity(this.fContext);
		fSafeToRefreshEntitiesRecursivelly = false;
	}

	/**
	 * @see com.ixora.rms.agents.impl.AbstractAgent#deactivate()
	 */
	public synchronized void deactivate() throws Throwable {
		// Close the connection to native code
		((WMIRootEntity)fRootEntity).cleanup();
		super.deactivate();
	}


}
