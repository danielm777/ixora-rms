/*
 * Created on 12-Jan-2005
 */
package com.ixora.rms.agents.hostavailability;

import com.ixora.rms.agents.AgentId;
import com.ixora.rms.agents.impl.AbstractAgent;

/**
 * @author Daniel Moraru
 */
public final class HostAvailabilityAgent extends AbstractAgent {

	/**
	 * Constructor.
	 * @throws Throwable
	 */
	public HostAvailabilityAgent(AgentId agentId, Listener listener) throws Throwable {
        super(agentId, listener, true); // use private collector as each cycle might be expensive
		this.fRootEntity.addChildEntity(new PingEntity(this.fRootEntity.getId(), fContext));
		//System.loadLibrary("ping");
	}
}
