/*
 * Created on Jan 25, 2004
 */
package com.ixora.rms.agents.jboss.v4_2;

import com.ixora.rms.agents.AgentCustomConfiguration;
import com.ixora.rms.agents.ui.AgentCustomConfigurationPanelContext;
import com.ixora.rms.agents.ui.DefaultAgentCustomConfigurationPanel;

/**
 * JBoss v4.2 custom configuration panel.
 * @author Daniel Moraru
 */
public class ConfigurationPanel extends DefaultAgentCustomConfigurationPanel {
	private static final long serialVersionUID = 1531889940701492967L;

	/**
	 * Constructor.
	 * @param agentId
	 * @param ctxt
	 */
	public ConfigurationPanel(String agentId, AgentCustomConfigurationPanelContext ctxt) {
		super(agentId, ctxt);
	}

	/**
	 * @see com.ixora.rms.ui.AgentCustomConfigurationPanel#createAgentCustomConfiguration()
	 */
	public AgentCustomConfiguration createAgentCustomConfiguration() {
		return new Configuration();
	}
}
