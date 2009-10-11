/*
 * Created on 17-Jan-2004
 */
package com.ixora.rms.agents.ui;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import com.ixora.common.typedproperties.exception.InvalidPropertyValue;
import com.ixora.common.typedproperties.exception.VetoException;
import com.ixora.common.typedproperties.ui.TypedPropertiesEditor;
import com.ixora.rms.agents.AgentCustomConfiguration;

/**
 * A default implementation of an agent custom configuration panel that is based
 * on a TypedPropertiesEditor.
 * @author Daniel Moraru
 */
public abstract class DefaultAgentCustomConfigurationPanel extends AgentCustomConfigurationPanel {
	private static final long serialVersionUID = -7104126710250456380L;
	/** Editor for the agent configuration */
	protected TypedPropertiesEditor	fTypedPropsEditor;

	/**
	 * Constructor.
	 * @param agentMsgCatalog
	 * @param ctxt
	 */
	protected DefaultAgentCustomConfigurationPanel(
			String agentMsgCatalog,
			AgentCustomConfigurationPanelContext ctxt) {
		super(agentMsgCatalog, ctxt);
		fTypedPropsEditor = new TypedPropertiesEditor(true);
		setLayout(new BorderLayout());
		add(createContentPanel(), BorderLayout.CENTER);
	}

	/**
	 * Subclasses can customize here the content panel.
	 */
	protected JPanel createContentPanel() {
		JPanel ret = new JPanel(new BorderLayout());
		ret.add(fTypedPropsEditor);
		return ret;
	}

	/**
	 * @param configuration
	 */
	public void setAgentCustomConfiguration(AgentCustomConfiguration configuration) {
		super.setAgentCustomConfiguration(configuration);
		fTypedPropsEditor.setTypedProperties(fAgentMessageCatalog, configuration);
	}

	/**
	 * @see com.ixora.rms.agents.ui.AgentCustomConfigurationPanel#applyChanges()
	 */
	public void applyChanges() throws InvalidPropertyValue, VetoException {
		fTypedPropsEditor.applyChanges();
	}

	/**
	 * @see com.ixora.rms.agents.ui.AgentCustomConfigurationPanel#removeListener()
	 */
	public void removeListener() {
		fTypedPropsEditor.removeListener();
	}

	/**
	 * @see com.ixora.rms.agents.ui.AgentCustomConfigurationPanel#setListener(com.ixora.rms.agents.ui.AgentCustomConfigurationPanel.Listener)
	 */
	public void setListener(Listener listener) {
		fTypedPropsEditor.setListener(listener);
	}

	/**
	 * @see java.awt.Component#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		fTypedPropsEditor.setEnabled(enabled);
	}
}
