/*
 * Created on 03-Mar-2004
 */
package com.ixora.rms.agents.sunapp;

import java.io.File;

import com.ixora.rms.agents.Agent;
import com.ixora.rms.agents.AgentConfiguration;
import com.ixora.rms.agents.AgentId;
import com.ixora.rms.agents.impl.AbstractAgentVersionProxy;
import com.ixora.rms.agents.sunapp.exception.SunAppNotInstalledOnHost;
import com.ixora.rms.agents.sunapp.v8.Configuration;
import com.ixora.rms.exception.InvalidConfiguration;

/**
 * Weblogic agent. This is actually an adapter to
 * various versions of agents.
 * @author Daniel Moraru
 */
public final class SunAppAgent extends AbstractAgentVersionProxy implements SunAppConstants {

	/**
	 * Constructor.
	 */
	public SunAppAgent(AgentId agentId, Listener listener) {
		super(agentId, listener);
	}

	/**
	 * @see com.ixora.rms.agents.impl.AbstractAgentVersionProxy#createAgent(com.ixora.rms.agents.AgentConfiguration)
	 */
	protected Agent createAgent(AgentConfiguration conf) throws Throwable {
        String home = conf.getAgentCustomConfiguration().getString(Configuration.ROOT_FOLDER);
        File f = new File(home);
        if(!f.exists()) {
            throw new SunAppNotInstalledOnHost(conf.getDeploymentHost());
        }
		// create the required version
		String v = conf.getSystemUnderObservationVersion();
        if(v.equals(VERSION_SUNAPP_8)) {
            return new com.ixora.rms.agents.sunapp.v8.SunAppAgent(fAgentId, fListener);
        } else {
			throw new InvalidConfiguration("Unknown agent version: " + v);
		}
	}
}
