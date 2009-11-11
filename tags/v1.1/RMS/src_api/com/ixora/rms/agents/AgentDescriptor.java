/*
 * Created on 01-Jul-2005
 */
package com.ixora.rms.agents;

import com.ixora.common.xml.XMLExternalizable;
import com.ixora.rms.agents.AgentId;

/**
 * AgentDescriptor
 */
public interface AgentDescriptor extends XMLExternalizable, Cloneable {
    /**
     * @return the agent configuration
     */
    AgentConfiguration getAgentConfiguration();
    /**
     * @return agent id
     */
    AgentId getAgentId();
    /**
     * @return true if it's safe to refresh its entities recursivelly
     */
    boolean safeToRefreshRecursivelly();
	/**
	 * @return
	 */
	Object clone();
}
