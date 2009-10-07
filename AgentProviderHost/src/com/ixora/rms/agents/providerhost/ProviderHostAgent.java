/*
 * Created on 13-Feb-2005
 */
package com.ixora.rms.agents.providerhost;

import com.ixora.rms.agents.AgentId;
import com.ixora.rms.agents.impl.AbstractAgent;

/**
 * Host for providers.
 * @author Daniel Moraru
 */
public final class ProviderHostAgent extends AbstractAgent {
	/**
	 * Constructor.
	 */
	public ProviderHostAgent(AgentId agentId, Listener listener) {
        super(agentId, listener);
	}
}
