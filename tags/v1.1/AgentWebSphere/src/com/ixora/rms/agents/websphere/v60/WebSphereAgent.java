/*
 * Created on 24-Jun-2005
 */
package com.ixora.rms.agents.websphere.v60;

import com.ixora.rms.EntityDescriptorTree;
import com.ixora.rms.agents.AgentConfiguration;
import com.ixora.rms.agents.AgentId;
import com.ixora.rms.exception.InvalidConfiguration;

/**
 * WebSphereAgent for WAS 6.0
 */
public class WebSphereAgent extends
        com.ixora.rms.agents.websphere.v50.WebSphereAgent {

    /**
     * Constructor that replaces the version behaviour.
     * @param agentId
     * @param listener
     * @throws Throwable
     */
    public WebSphereAgent(AgentId agentId, Listener listener) throws Throwable {
        super(agentId, listener);
        this.versionBehaviour = new VersionBehaviour();
    }

    /**
     * Overriden to replace tokens in the custom configuration.
     * @see com.ixora.rms.agents.Agent#configure(com.ixora.rms.agents.AgentConfiguration)
     */
	public synchronized EntityDescriptorTree configure(AgentConfiguration newConf) throws InvalidConfiguration, Throwable {
		// first prepare config
		Configuration conf = (Configuration)(newConf.getCustom());
		conf.replaceTokens();
		newConf.setCustom(conf);
		// and now do the usual stuff...
		return super.configure(newConf);
	}
}
