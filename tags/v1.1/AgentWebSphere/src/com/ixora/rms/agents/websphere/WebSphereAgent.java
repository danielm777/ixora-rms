/*
 * Created on 03-Mar-2004
 */
package com.ixora.rms.agents.websphere;

import java.io.File;

import com.ibm.websphere.pmi.PmiException;
import com.ixora.rms.agents.Agent;
import com.ixora.rms.agents.AgentConfiguration;
import com.ixora.rms.agents.AgentId;
import com.ixora.rms.agents.impl.AbstractAgentVersionProxy;
import com.ixora.rms.agents.websphere.exception.WebSphereNotInstalledOnHost;
import com.ixora.rms.agents.websphere.v50.Configuration;
import com.ixora.rms.exception.InvalidConfiguration;

/**
 * WebSphere agent. This is actually an adapter to
 * various versions of agents.
 * @author Daniel Moraru
 */
public final class WebSphereAgent extends AbstractAgentVersionProxy implements WebSphereConstants {

	/**
	 * Constructor.
	 * @throws PmiException
	 */
	public WebSphereAgent(AgentId agentId, Listener listener) {
		super(agentId, listener);
	}

	/**
	 * @see com.ixora.rms.agents.impl.AbstractAgentVersionProxy#createAgent(com.ixora.rms.agents.AgentConfiguration)
	 */
	protected Agent createAgent(AgentConfiguration conf) throws Throwable {
        String wasHome = conf.getAgentCustomConfiguration().getString(Configuration.WAS_HOME);
        File f = new File(wasHome);
        if(!f.exists()) {
            throw new WebSphereNotInstalledOnHost(conf.getDeploymentHost());
        }
		// create the required version
		String v = conf.getSystemUnderObservationVersion();
        if(v.equals(VERSION_WEBPSHERE_6_1)) {
            return new com.ixora.rms.agents.websphere.v61.WebSphereAgent(fAgentId, fListener);
        } else if(v.equals(VERSION_WEBPSHERE_6_0)) {
            return new com.ixora.rms.agents.websphere.v60.WebSphereAgent(fAgentId, fListener);
        } else if(v.equals(VERSION_WEBPSHERE_5_1)) {
			return new com.ixora.rms.agents.websphere.v50.WebSphereAgent(fAgentId, fListener);
		} else if(v.equals(VERSION_WEBPSHERE_5_0)) {
			return new com.ixora.rms.agents.websphere.v50.WebSphereAgent(fAgentId, fListener);
		} else {
			throw new InvalidConfiguration("Unknown agent version: " + v);
		}
	}
}
