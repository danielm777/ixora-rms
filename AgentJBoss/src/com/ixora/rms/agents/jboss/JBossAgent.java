/*
 * Created on 03-Mar-2004
 */
package com.ixora.rms.agents.jboss;

import java.io.File;

import com.ixora.rms.agents.Agent;
import com.ixora.rms.agents.AgentConfiguration;
import com.ixora.rms.agents.AgentId;
import com.ixora.rms.agents.impl.AbstractAgentVersionProxy;
import com.ixora.rms.agents.jboss.exception.JBossNotInstalledOnHost;
import com.ixora.rms.agents.jboss.v4.Configuration;
import com.ixora.rms.exception.InvalidConfiguration;

/**
 * Weblogic agent. This is actually an adapter to
 * various versions of agents.
 * @author Daniel Moraru
 */
public final class JBossAgent extends AbstractAgentVersionProxy implements JBossConstants {

	/**
	 * Constructor.
	 */
	public JBossAgent(AgentId agentId, Listener listener) {
		super(agentId, listener);
	}

	/**
	 * @see com.ixora.rms.agents.impl.AbstractAgentVersionProxy#createAgent(com.ixora.rms.agents.AgentConfiguration)
	 */
	protected Agent createAgent(AgentConfiguration conf) throws Throwable {
        String wlsHome = conf.getAgentCustomConfiguration().getString(Configuration.ROOT_FOLDER);
        File f = new File(wlsHome);
        if(!f.exists()) {
            throw new JBossNotInstalledOnHost(conf.getDeploymentHost());
        }
		// create the required version
		String v = conf.getSystemUnderObservationVersion();
        if(v.equals(VERSION_JBOSS_4) || v.equals(VERSION_JBOSS_4_2)) {
            return new com.ixora.rms.agents.jboss.v4.JBossAgent(fAgentId, fListener);
        } else {
			throw new InvalidConfiguration("Unknown agent version: " + v);
		}
	}
}
