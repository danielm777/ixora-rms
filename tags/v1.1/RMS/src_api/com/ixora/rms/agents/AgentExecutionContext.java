/*
 * Created on Feb 25, 2004
 */
package com.ixora.rms.agents;

import com.ixora.rms.EntityDescriptorTree;


/**
 * Agent execution context. Used to be passed down to
 * all agent components. This is where the error handling is
 * done and where data can be shared.
 * @author Daniel Moraru
 */
public interface AgentExecutionContext {
	/**
	 * Invoked to signal a non fatal error.
	 * @param e
	 */
	void error(Throwable e);
	/**
	 * Returns true if the agent is currently running
	 */
	boolean isRunning();
	/**
	 * @return the agent custom configuration
	 */
	AgentConfiguration getAgentConfiguration();
	/**
	 * @return the agent id
	 */
	AgentId getAgentId();
	/**
	 * Invoked by an entity when the children entities
	 * change.
	 * @param descs
	 */
	void childrenEntitiesChanged(EntityDescriptorTree descs);
	/**
	 * @return whether or not entities should be sorted
	 */
	boolean sortEntities();
}
