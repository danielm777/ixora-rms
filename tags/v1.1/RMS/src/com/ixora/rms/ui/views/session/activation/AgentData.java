package com.ixora.rms.ui.views.session.activation;

import com.ixora.common.MessageRepository;
import com.ixora.rms.agents.AgentActivationData;
import com.ixora.rms.repository.AgentInstallationData;

/**
 * Holds agent data.
 * @author Daniel Moraru
 */
public final class AgentData implements Cloneable, Comparable<AgentData> {
	private AgentInstallationData agentInstallationDtls;
	private AgentActivationData lastAgentActivationDtls;
	/** Localized agent name */
	private String agentName;
	/** Localized description */
	private String agentDescription;

	/**
	 * Constructor.
	 * @param dtls
	 */
	AgentData(AgentInstallationData dtls) {
		this.agentInstallationDtls = dtls;
		this.lastAgentActivationDtls = new AgentActivationData();
		this.agentName = MessageRepository.get(dtls.getMessageCatalog(), dtls.getAgentName());
		this.agentDescription = MessageRepository.get(dtls.getMessageCatalog(), dtls.getAgentDescription());
	}
	/**
	 * @return
	 */
	public AgentInstallationData getAgentInstallationDtls() {
		return agentInstallationDtls;
	}
	/**
	 * @return the agent activation details
	 */
	public AgentActivationData getLastAgentActivationDtls() {
		return this.lastAgentActivationDtls;
	}
	/**
	 * @return the localized agent name
	 */
	public String getAgentName(){
		return this.agentName;
	}
	/**
	 * @return the localized agent description
	 */
	public String getAgentDescription() {
		return this.agentDescription;
	}
	/**
	 * @see java.lang.Comparable#compareTo(T)
	 */
	public int compareTo(AgentData o) {
		return this.agentName.compareToIgnoreCase(o.agentName);
	}
// not public
	/**
	 * @param aad
	 */
	void setLastAgentActivationData(AgentActivationData aad) {
		this.lastAgentActivationDtls = aad;
	}
}