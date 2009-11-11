/**
 * 10-Feb-2006
 */
package com.ixora.rms.agents.ui;

import com.ixora.rms.ui.RMSViewContainer;

/**
 * @author Daniel Moraru
 */
public class AgentCustomConfigurationPanelContext {
	private RMSViewContainer fViewContainer;

	/**
	 * @param vc
	 * @param parent
	 */
	public AgentCustomConfigurationPanelContext(RMSViewContainer vc) {
		super();
		fViewContainer = vc;
	}

	/**
	 * @return
	 */
	public RMSViewContainer getViewContainer() {
		return fViewContainer;
	}
}
