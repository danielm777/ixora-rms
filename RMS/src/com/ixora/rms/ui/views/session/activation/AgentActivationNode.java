/**
 * 03-Feb-2006
 */
package com.ixora.rms.ui.views.session.activation;

import javax.swing.tree.DefaultMutableTreeNode;

import com.ixora.rms.agents.AgentActivationData;
import com.ixora.rms.repository.AgentInstallationData;

/**
 * @author Daniel Moraru
 */
public final class AgentActivationNode extends DefaultMutableTreeNode {
	/**
	 * @param aid
	 */
	AgentActivationNode(AgentInstallationData aid) {
		super(new AgentData(aid), false);
	}

	/**
	 * @return
	 */
	public AgentData getAgentData() {
		return (AgentData)getUserObject();
	}
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return ((AgentData)getUserObject()).getAgentName();
	}

	/**
	 * @param ddtls
	 */
	public void setLastAgentActivationData(AgentActivationData ddtls) {
		getAgentData().setLastAgentActivationData(ddtls);
	}
}