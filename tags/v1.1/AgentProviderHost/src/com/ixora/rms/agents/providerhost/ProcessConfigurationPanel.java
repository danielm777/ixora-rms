/*
 * Created on Jan 25, 2004
 */
package com.ixora.rms.agents.providerhost;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.SwingConstants;

import com.ixora.common.typedproperties.TypedProperties;
import com.ixora.common.typedproperties.exception.InvalidPropertyValue;
import com.ixora.common.typedproperties.exception.VetoException;
import com.ixora.common.typedproperties.ui.TypedPropertiesEditor;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.forms.FormPanel;
import com.ixora.common.utils.Utils;
import com.ixora.rms.agents.AgentCustomConfiguration;
import com.ixora.rms.agents.ui.AgentCustomConfigurationPanel;
import com.ixora.rms.agents.ui.AgentCustomConfigurationPanelContext;
import com.ixora.rms.providers.impl.process.ProcessExecutionMode;

/**
 * @author Daniel Moraru
 */
public final class ProcessConfigurationPanel extends AgentCustomConfigurationPanel {
	// TODO localize
	private static final String LABEL_PROCESS_EXEC_MODE = "Process Execution Mode";
	private static final String LABEL_REMOTE_EXEC_CONFIG = "Remote Execution Configuration";
	private Listener fListener;
	private JComboBox fComboExecMode;
	private TypedPropertiesEditor fEditor;
	private TypedPropertiesSSH2 fSSH2Props;
	private FormPanel fForm;
	private EventHandler fEventHandler;

	private class TypedPropertiesSSH2 extends TypedProperties {
		TypedPropertiesSSH2() {
			super();
	        setProperty(ProcessConfiguration.USERNAME, TYPE_STRING, true, true);
	        setProperty(ProcessConfiguration.PASSWORD, TYPE_SECURE_STRING, true, true);
	        setProperty(ProcessConfiguration.PORT, TYPE_STRING, true, false);

	        setString(ProcessConfiguration.USERNAME, "-");
	        setString(ProcessConfiguration.PASSWORD, "-");
	        setString(ProcessConfiguration.PORT, String.valueOf(ProcessConfiguration.PORT_SSH2));
		}

		TypedPropertiesSSH2(ProcessConfiguration pc) {
			this();
			String username = pc.getString(ProcessConfiguration.USERNAME);
	        setString(ProcessConfiguration.USERNAME, Utils.isEmptyString(username) ? "-" : username);
	        String password = pc.getString(ProcessConfiguration.PASSWORD);
	        setString(ProcessConfiguration.PASSWORD, Utils.isEmptyString(password) ? "-" : password);
	        String port = pc.getString(ProcessConfiguration.PORT);
	        setString(ProcessConfiguration.PORT,
	        		(Utils.isEmptyString(port) ||
	        				String.valueOf(ProcessConfiguration.PORT_DEFAULT).equals(port))
	        			? getDefaultPortNumber() : port);
		}
		protected String getDefaultPortNumber() {
			return String.valueOf(ProcessConfiguration.PORT_SSH2);
		}
	}

	private class TypedPropertiesTelnet extends TypedPropertiesSSH2 {
		TypedPropertiesTelnet() {
			super();
	        setProperty(ProcessConfiguration.USERNAME_PROMPT, TYPE_STRING, true, true);
	        setProperty(ProcessConfiguration.PASSWORD_PROMPT, TYPE_STRING, true, true);
	        setProperty(ProcessConfiguration.SHELL_PROMPT, TYPE_STRING, true, true);

	        setString(ProcessConfiguration.USERNAME_PROMPT, "login:");
	        setString(ProcessConfiguration.PASSWORD_PROMPT, "Password:");
	        setString(ProcessConfiguration.SHELL_PROMPT, "]$");
	        setString(ProcessConfiguration.PORT, String.valueOf(ProcessConfiguration.PORT_TELNET));
		}
		TypedPropertiesTelnet(ProcessConfiguration pc) {
			super(pc);
	        setProperty(ProcessConfiguration.USERNAME_PROMPT, TYPE_STRING, true, true);
	        setProperty(ProcessConfiguration.PASSWORD_PROMPT, TYPE_STRING, true, true);
	        setProperty(ProcessConfiguration.SHELL_PROMPT, TYPE_STRING, true, true);

	        String usernamePrompt = pc.getString(ProcessConfiguration.USERNAME_PROMPT);
	        setString(ProcessConfiguration.USERNAME_PROMPT, Utils.isEmptyString(usernamePrompt) ? "login:" : usernamePrompt);
	        String passwordPrompt = pc.getString(ProcessConfiguration.PASSWORD_PROMPT);
	        setString(ProcessConfiguration.PASSWORD_PROMPT, Utils.isEmptyString(passwordPrompt) ? "Password:" : passwordPrompt);
			String shellPrompt = pc.getString(ProcessConfiguration.SHELL_PROMPT);
	        setString(ProcessConfiguration.SHELL_PROMPT, Utils.isEmptyString(shellPrompt) ? "]$" : shellPrompt);
		}
		protected String getDefaultPortNumber() {
			return String.valueOf(ProcessConfiguration.PORT_TELNET);
		}
	}

	private class EventHandler implements ItemListener {
		/**
		 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
		 */
		public void itemStateChanged(ItemEvent e) {
			if(e.getStateChange() == ItemEvent.SELECTED) {
				handleExecModeSelected();
			}
		}
	}

	/**
	 * Constructor.
	 * @param agentId
	 * @param ctxt
	 */
	public ProcessConfigurationPanel(String agentId, AgentCustomConfigurationPanelContext ctxt) {
		super(agentId, ctxt);
		setLayout(new BorderLayout());
		fEventHandler = new EventHandler();
		fComboExecMode = UIFactoryMgr.createComboBox();
		fComboExecMode.setModel(new DefaultComboBoxModel(
				new Object[]{ProcessExecutionMode.NORMAL, ProcessExecutionMode.TELNET, ProcessExecutionMode.SSH2}));
		fComboExecMode.setMaximumSize(new Dimension(fComboExecMode.getPreferredSize().width, fComboExecMode.getMinimumSize().height));
		fEditor = new TypedPropertiesEditor(true);
		fEditor.setPreferredSize(new Dimension(250, 200));

		fForm = new FormPanel(FormPanel.VERTICAL2, SwingConstants.TOP);
		fForm.addPairs(
				new String[]{
						LABEL_PROCESS_EXEC_MODE,
						LABEL_REMOTE_EXEC_CONFIG
				},
				new Component[]{
						fComboExecMode,
						fEditor
				});
		add(fForm, BorderLayout.CENTER);
		fComboExecMode.addItemListener(fEventHandler);
	}

	/**
	 * @see com.ixora.rms.ui.AgentCustomConfigurationPanel#createAgentCustomConfiguration()
	 */
	public AgentCustomConfiguration createAgentCustomConfiguration() {
		return new ProcessConfiguration();
	}

	/**
	 * @see com.ixora.rms.agents.ui.AgentCustomConfigurationPanel#setAgentCustomConfiguration(com.ixora.rms.agents.AgentCustomConfiguration)
	 */
	public void setAgentCustomConfiguration(AgentCustomConfiguration configuration) {
		super.setAgentCustomConfiguration(configuration);
		String sem = fConfiguration.getString(ProcessConfiguration.EXECUTION_MODE);
		ProcessExecutionMode execMode = ProcessExecutionMode.resolve(sem);
		fComboExecMode.setSelectedItem(execMode);
		setUpEditorForExecutionMode(execMode);
	}

	/**
	 * @param execMode
	 */
	private void setUpEditorForExecutionMode(ProcessExecutionMode execMode) {
		fSSH2Props = null;
		if(execMode == ProcessExecutionMode.TELNET) {
			fSSH2Props = new TypedPropertiesTelnet((ProcessConfiguration)fConfiguration);
		} else if(execMode == ProcessExecutionMode.SSH2) {
			fSSH2Props = new TypedPropertiesSSH2((ProcessConfiguration)fConfiguration);
		}
		fEditor.setTypedProperties(null, fSSH2Props);
		if(execMode == ProcessExecutionMode.NORMAL) {
			fForm.setVisible(LABEL_REMOTE_EXEC_CONFIG, false);
		} else {
			fForm.setVisible(LABEL_REMOTE_EXEC_CONFIG, true);
		}
	}

	/**
	 * @see com.ixora.rms.agents.ui.AgentCustomConfigurationPanel#applyChanges()
	 */
	public void applyChanges() throws InvalidPropertyValue, VetoException {
		fEditor.applyChanges();
		ProcessExecutionMode execMode = (ProcessExecutionMode)fComboExecMode.getSelectedItem();
		fConfiguration.setObject(ProcessConfiguration.EXECUTION_MODE, execMode.getStringKey());
		if(fSSH2Props != null) {
			fConfiguration.setString(ProcessConfiguration.USERNAME,
					fSSH2Props.getString(ProcessConfiguration.USERNAME));
			fConfiguration.setString(ProcessConfiguration.PASSWORD,
					fSSH2Props.getString(ProcessConfiguration.PASSWORD));
			fConfiguration.setString(ProcessConfiguration.PORT,
					fSSH2Props.getString(ProcessConfiguration.PORT));
		}
		if(fSSH2Props instanceof TypedPropertiesTelnet) {
			fConfiguration.setString(ProcessConfiguration.SHELL_PROMPT,
					fSSH2Props.getString(ProcessConfiguration.SHELL_PROMPT));
			fConfiguration.setString(ProcessConfiguration.USERNAME_PROMPT,
					fSSH2Props.getString(ProcessConfiguration.USERNAME_PROMPT));
			fConfiguration.setString(ProcessConfiguration.PASSWORD_PROMPT,
					fSSH2Props.getString(ProcessConfiguration.PASSWORD_PROMPT));
		}
	}

	/**
	 * @see com.ixora.rms.agents.ui.AgentCustomConfigurationPanel#setListener(com.ixora.rms.agents.ui.AgentCustomConfigurationPanel.Listener)
	 */
	public void setListener(Listener listener) {
		fListener = listener;
		fEditor.setListener(listener);
	}

	/**
	 * @see com.ixora.rms.agents.ui.AgentCustomConfigurationPanel#removeListener()
	 */
	public void removeListener() {
		fListener = null;
		fEditor.removeListener();
	}

	/**
	 * @see java.awt.Component#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		fComboExecMode.setEnabled(enabled);
		fEditor.setEnabled(enabled);
	}

	/**
	 *
	 */
	private void handleExecModeSelected() {
		try {
			if(fListener != null) {
				fListener.componentStateChanged();
			}
			setUpEditorForExecutionMode((ProcessExecutionMode)fComboExecMode.getSelectedItem());
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}
}
