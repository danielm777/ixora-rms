/*
 * Created on Jan 25, 2004
 */
package com.ixora.rms.agents.providerhost;

import com.ixora.rms.agents.AgentCustomConfiguration;
import com.ixora.rms.agents.ui.AgentCustomConfigurationPanelContext;
import com.ixora.rms.agents.ui.DefaultAgentCustomConfigurationPanel;

/**
 * @author Daniel Moraru
 */
public final class SQLConfigurationPanel extends DefaultAgentCustomConfigurationPanel {
	private static final long serialVersionUID = -44263050953911244L;

	/**
	 * Constructor.
	 * @param agentId
	 * @param ctxt
	 */
	public SQLConfigurationPanel(String agentId, AgentCustomConfigurationPanelContext ctxt) {
		super(agentId, ctxt);
	}

	/**
	 * @see com.ixora.rms.ui.AgentCustomConfigurationPanel#createAgentCustomConfiguration()
	 */
	public AgentCustomConfiguration createAgentCustomConfiguration() {
		return new SQLConfiguration();
	}
}
