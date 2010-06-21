/**
 * 
 */
package com.ixora.rms.agents.weblogic.v10;

import com.ixora.rms.agents.AgentId;

/**
 * @author Daniel Moraru
 */
public class WeblogicAgent extends
		com.ixora.rms.agents.weblogic.v9.WeblogicAgent {

	/**
	 * @param agentId
	 * @param listener
	 */
	public WeblogicAgent(AgentId agentId, Listener listener) {
		super(agentId, listener);
	}

}
