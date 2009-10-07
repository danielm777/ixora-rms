/*
 * Created on 20-Aug-2005
 */
package com.ixora.rms.agents.snmp;

import com.ixora.rms.agents.AgentId;
import com.ixora.rms.agents.impl.AbstractAgent;

/**
 * SNMPAgent
 */
public class SNMPAgent extends AbstractAgent {

	/**
	 * Constructor.
	 * @throws Throwable
	 */
	public SNMPAgent(AgentId agentId, Listener listener) throws Throwable {
        super(agentId, listener, true); // use private collector as each cycle might be expensive
		// Create entities
		fRootEntity = new SNMPRootEntity(this.fContext);
	}

	/**
	 * @see com.ixora.rms.agents.impl.AbstractAgent#deactivate()
	 */
	public synchronized void deactivate() throws Throwable {
		// Stop again, just in case
		((SNMPRootEntity)fRootEntity).stop();
		super.deactivate();
	}

	/**
	 * @see com.ixora.rms.agents.impl.AbstractAgent#start()
	 */
	public synchronized void start() throws Throwable {
		((SNMPRootEntity)fRootEntity).start();
		super.start();
	}

	/**
	 * @see com.ixora.rms.agents.impl.AbstractAgent#stop()
	 */
	public synchronized void stop() throws Throwable {
		super.stop();
		((SNMPRootEntity)fRootEntity).stop();
	}
}
