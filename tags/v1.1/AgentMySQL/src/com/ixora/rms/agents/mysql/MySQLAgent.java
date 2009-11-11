/**
 * 26-Dec-2005
 */
package com.ixora.rms.agents.mysql;

import com.ixora.rms.agents.Agent;
import com.ixora.rms.agents.AgentConfiguration;
import com.ixora.rms.agents.AgentId;
import com.ixora.rms.agents.impl.AbstractAgentVersionProxy;

/**
 * @author Daniel Moraru
 */
public class MySQLAgent extends AbstractAgentVersionProxy {
	/**
	 * @param agentId
	 * @param listener
	 */
	public MySQLAgent(AgentId agentId, Listener listener) {
		super(agentId, listener);
	}

	/**
	 * @see com.ixora.rms.agents.impl.AbstractAgentVersionProxy#createAgent(com.ixora.rms.agents.AgentConfiguration)
	 */
	protected Agent createAgent(AgentConfiguration conf) throws Throwable {
		if(conf.getSystemUnderObservationVersion().equals(MySQLConstants.VERSION50)) {
			return new com.ixora.rms.agents.mysql.v50.MySQLAgent(fAgentId, fListener);
		} else if(conf.getSystemUnderObservationVersion().equals(MySQLConstants.VERSION40)) {
			return new com.ixora.rms.agents.mysql.v40.MySQLAgent(fAgentId, fListener);
		}
		return new com.ixora.rms.agents.mysql.v40.MySQLAgent(fAgentId, fListener);
	}
}
