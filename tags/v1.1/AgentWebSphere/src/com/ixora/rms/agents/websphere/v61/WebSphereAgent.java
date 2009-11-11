/*
 * Created on 24-Jun-2005
 */
package com.ixora.rms.agents.websphere.v61;

import com.ixora.rms.agents.AgentId;

/**
 * WebSphereAgent for WAS 6.1
 */
public class WebSphereAgent extends
        com.ixora.rms.agents.websphere.v60.WebSphereAgent {

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
}
