/*
 * Created on 13-Dec-2003
 */
package com.ixora.rms.agents.url;

import java.net.URL;

import com.ixora.rms.agents.AgentId;
import com.ixora.rms.agents.impl.AbstractAgent;
import com.ixora.rms.exception.InvalidConfiguration;

/**
 * URLAgent
 */
public class URLAgent extends AbstractAgent {
	/**
	 * @param agentId
	 * @param listener
	 */
	public URLAgent(AgentId agentId, Listener listener) {
        super(agentId, listener, true); // use private collector
	}

	/**
	 * @see com.ixora.rms.agents.impl.AbstractAgent#configCustomChanged()
	 */
	protected void configCustomChanged() throws InvalidConfiguration, Throwable {
		Configuration conf = (Configuration)fContext.getAgentConfiguration().getAgentCustomConfiguration();
		URL url = new URL(conf.getString(Configuration.URL));
		fRootEntity.addChildEntity(new URLEntity(fRootEntity.getId(), url, fContext));
	}
}
