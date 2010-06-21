/*
 * Created on Jan 25, 2004
 */
package com.ixora.rms.agents.weblogic.v10;

import com.ixora.rms.agents.AgentCustomConfiguration;
import com.ixora.rms.agents.ui.AgentCustomConfigurationPanelContext;
import com.ixora.rms.agents.ui.DefaultAgentCustomConfigurationPanel;

/**
 * Weblogic custom configuration panel for Weblogic 10.3.
 * @author Daniel Moraru
 */
public class ConfigurationPanel extends DefaultAgentCustomConfigurationPanel {
	private static final long serialVersionUID = 5217898646128380773L;

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
