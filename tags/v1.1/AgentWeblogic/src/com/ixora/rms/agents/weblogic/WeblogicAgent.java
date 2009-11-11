/*
 * Created on 03-Mar-2004
 */
package com.ixora.rms.agents.weblogic;

import java.io.File;

import com.ixora.rms.agents.Agent;
import com.ixora.rms.agents.AgentConfiguration;
import com.ixora.rms.agents.AgentId;
import com.ixora.rms.agents.impl.AbstractAgentVersionProxy;
import com.ixora.rms.agents.weblogic.exception.WeblogicNotInstalledOnHost;
import com.ixora.rms.agents.weblogic.v9.Configuration;
import com.ixora.rms.exception.InvalidConfiguration;

/**
 * Weblogic agent. This is actually an adapter to
 * various versions of agents.
 * @author Daniel Moraru
 */
public final class WeblogicAgent extends AbstractAgentVersionProxy implements WeblogicConstants {

	/**
	 * Constructor.
	 */
	public WeblogicAgent(AgentId agentId, Listener listener) {
		super(agentId, listener);
	}

	/**
	 * @see com.ixora.rms.agents.impl.AbstractAgentVersionProxy#createAgent(com.ixora.rms.agents.AgentConfiguration)
	 */
	protected Agent createAgent(AgentConfiguration conf) throws Throwable {
        String wlsHome = conf.getAgentCustomConfiguration().getString(Configuration.ROOT_FOLDER);
        File f = new File(wlsHome);
        if(!f.exists()) {
            throw new WeblogicNotInstalledOnHost(conf.getDeploymentHost());
        }
		// create the required version
		String v = conf.getSystemUnderObservationVersion();
        if(v.equals(VERSION_WEBLOGIC_9)) {
            return new com.ixora.rms.agents.weblogic.v9.WeblogicAgent(fAgentId, fListener);
        } else if(v.equals(VERSION_WEBLOGIC_8)) {
            return new com.ixora.rms.agents.weblogic.v8.WeblogicAgent(fAgentId, fListener);
        } else {
			throw new InvalidConfiguration("Unknown agent version: " + v);
		}
	}
}
