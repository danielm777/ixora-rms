/*
 * Created on 20-Aug-2005
 */
package com.ixora.rms.agents.snmp.v1;

import com.ixora.rms.agents.AgentCustomConfiguration;
import com.ixora.rms.agents.snmp.ConfigurationPanel;
import com.ixora.rms.agents.ui.AgentCustomConfigurationPanelContext;

/**
 * ConfigurationPanel
 */
public class ConfigurationPanelV1 extends ConfigurationPanel {
	/**
	 * Constructor.
	 * @param agentId
	 * @param ctxt
	 */
	public ConfigurationPanelV1(String agentId, AgentCustomConfigurationPanelContext ctxt) {
		super(agentId, ctxt);
	}

	/**
	 * @see com.ixora.rms.ui.AgentCustomConfigurationPanel#createAgentCustomConfiguration()
	 */
	public AgentCustomConfiguration createAgentCustomConfiguration() {
		return new ConfigurationV1();
	}
}
