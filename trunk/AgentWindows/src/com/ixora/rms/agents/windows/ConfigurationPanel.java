/*
 * Created on 03-Jun-2005
 */
package com.ixora.rms.agents.windows;

import com.ixora.rms.agents.AgentCustomConfiguration;
import com.ixora.rms.agents.ui.AgentCustomConfigurationPanelContext;
import com.ixora.rms.agents.ui.DefaultAgentCustomConfigurationPanel;

/**
 * ConfigurationPanel
 * Panel for configuring Windows Agent
 */
public class ConfigurationPanel extends DefaultAgentCustomConfigurationPanel {
	private static final long serialVersionUID = 4273295904917645341L;

	/**
     * Constructor.
     * @param agentId
     * @param ctxt
     */
    public ConfigurationPanel(String agentId, AgentCustomConfigurationPanelContext ctxt) {
        super(agentId, ctxt);
    }

    /**
     * @see com.ixora.rms.internal.ui.AgentCustomConfigurationPanel#createAgentCustomConfiguration()
     */
    public AgentCustomConfiguration createAgentCustomConfiguration() {
        return new Configuration();
    }
}
