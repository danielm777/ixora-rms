/*
 * Created on 17-Jan-2004
 */
package com.ixora.rms.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;

import com.ixora.rms.RMSConfigurationConstants;
import com.ixora.common.ComponentConfiguration;
import com.ixora.common.MessageRepository;
import com.ixora.common.typedproperties.exception.InvalidPropertyValue;
import com.ixora.common.typedproperties.exception.VetoException;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.utils.Utils;
import com.ixora.rms.agents.AgentConfiguration;
import com.ixora.rms.agents.AgentCustomConfiguration;
import com.ixora.rms.agents.ui.AgentCustomConfigurationPanel;
import com.ixora.rms.exception.InvalidConfiguration;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.repository.AgentInstallationData;
import com.ixora.rms.repository.ProviderInstance;
import com.ixora.rms.repository.ProviderInstanceMap;
import com.ixora.rms.repository.VersionableAgentInstallationData;
import com.ixora.rms.services.ProviderInstanceRepositoryService;
import com.ixora.rms.ui.messages.Msg;

/**
 * The common agent configuration panel.
 * @author Daniel Moraru
 */
public final class AgentConfigurationPanel extends JPanel {
	/** Custom panel. It might be null */
	private AgentCustomConfigurationPanel fAgentCustomConfigPanel;
	/** Current custom agent configuration, being edited */
	private AgentCustomConfiguration fAgentCustomConfig;
	/** Original custom agent configuration */
	private AgentCustomConfiguration fAgentCustomConfigOriginal;
	/** Agent configuration */
	private AgentConfiguration fAgentConfig;
	/** Agent installation details */
	private AgentInstallationData fAgentInstallationDtls;
	/** RMS Configuration reference */
	private ComponentConfiguration fRMSConfig;
	/** ProviderInstanceRepositoryService */
	private ProviderInstanceRepositoryService fProviderInstanceRepository;
	/** View container */
	private RMSViewContainer fViewContainer;
	/** Button for selecting providers */
	private JButton fButtonSelectProviders;
	/** List of provider instances to activate */
	private List<String> fProviderInstancesToActivate;
	/** Sampling and level panel */
	private SamplingAndLevelPanel fSamplingAndLevelPanel;
	/** True if the agent custom config must be read only */
	private boolean fReadOnlyCustomConfig;
	/** Current provider instances */
	private ProviderInstance[] fAvailableOptionalProviderInstances;

	// actions
	private ActionSelectProviders fActionSelectProviders;

	/**
	 * Select providers action.
	 */
	private final class ActionSelectProviders extends AbstractAction {
		public ActionSelectProviders() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.ACTIONS_AGENT_CONFIGURATION_SELECT_PROVIDERS), this);
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			handleSelectProviders();
		}
	}


	/**
	 * Constructor.
	 * @param vc
	 * @param pirs if not null the select providers components will be shown
	 * @param readOnlyCustomConfig
	 */
	public AgentConfigurationPanel(RMSViewContainer vc,
			ProviderInstanceRepositoryService pirs,
			boolean readOnlyCustomConfig) {
		super(new BorderLayout());
		this.fViewContainer = vc;
		this.fProviderInstanceRepository = pirs;
		this.fReadOnlyCustomConfig = readOnlyCustomConfig;
		fSamplingAndLevelPanel = new SamplingAndLevelPanel();
		// hide the recursive level check box
		fSamplingAndLevelPanel.getRecursiveLevelCheckBox().setVisible(false);

		this.fActionSelectProviders = new ActionSelectProviders();

		JPanel panelNorth = new JPanel(new BorderLayout());
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(fSamplingAndLevelPanel, BorderLayout.NORTH);
		panelNorth.add(panel, BorderLayout.WEST);

		this.fButtonSelectProviders = UIFactoryMgr.createButton(fActionSelectProviders);
		panel = new JPanel(new BorderLayout());
		JPanel panel2 = new JPanel();
		panel2.add(this.fButtonSelectProviders);
		panel.add(panel2, BorderLayout.NORTH);
		panelNorth.add(panel, BorderLayout.CENTER);

		this.fButtonSelectProviders.setVisible(false);
		add(panelNorth, BorderLayout.NORTH);
	}

	/**
	 * Sets the configuration to show/edit.
	 * @param conf agent configuration
	 * @param dtls agent installation dtls
	 * @param panelCustomClass
	 */
	public void setAgentConfiguration(AgentConfiguration conf, AgentInstallationData dtls, AgentCustomConfigurationPanel customConfigPanel)
			throws RMSException {
        this.fAgentConfig = conf;
		this.fAgentInstallationDtls = dtls;
		this.fProviderInstancesToActivate = null;
		this.fAvailableOptionalProviderInstances = null;

		AgentConfiguration displayValues = null;
		if(!this.fAgentConfig.isSamplingIntervalSet()) {
			// set a configuration with basic data if this agent has never been
			// configured before so that the panel can display values
			displayValues = new AgentConfiguration();
			displayValues.setGlobalSamplingInterval(true);
			displayValues.setSamplingInterval(this.fRMSConfig.getInt(
					RMSConfigurationConstants.AGENT_CONFIGURATION_SAMPLING_INERVAL));
		}

        String suoVersion = fAgentConfig.getSystemUnderObservationVersion();
        VersionableAgentInstallationData vad = fAgentInstallationDtls.getVersionData(suoVersion);

        fSamplingAndLevelPanel.setMonitoringConfiguration(
				displayValues != null ? displayValues : fAgentConfig,
				vad.getMonitoringLevels(), true, false);

		// remove the existing panel if any
		if(this.fAgentCustomConfigPanel != null) {
			remove(this.fAgentCustomConfigPanel);
		}
		if(customConfigPanel != null) {
		    fAgentCustomConfigOriginal = conf.getAgentCustomConfiguration();
		    if(fAgentCustomConfigOriginal == null) {
		    	// this can happen when an agent has been updated to have custom config
		    	// but an old monitoring session still refers to the old version
		    	fAgentCustomConfigOriginal = customConfigPanel.createAgentCustomConfiguration();
		    	conf.setCustom(fAgentCustomConfigOriginal);
		    }
		    fAgentCustomConfig = (AgentCustomConfiguration)fAgentCustomConfigOriginal.clone();
			customConfigPanel.setEnabled(!fReadOnlyCustomConfig);
			if(!fReadOnlyCustomConfig) {
				customConfigPanel.setEnabled(isEnabled());
			}
		    customConfigPanel.setAgentCustomConfiguration(fAgentCustomConfig);
			add(customConfigPanel, BorderLayout.CENTER);
		}
		this.fAgentCustomConfigPanel = customConfigPanel;
		validate();
		repaint();
	}

    /**
     * @return
     */
    public AgentConfiguration getAgentConfiguration() {
        return fAgentConfig;
    }

	/**
	 * Updates the configuration panel to reflect the given agent version.
	 * @param version
	 */
	public void setAgentVersion(String version) {
		if(this.fProviderInstanceRepository != null) {
			ProviderInstanceMap pim = this.fProviderInstanceRepository.getOptionalAgentProviderInstances(
					fAgentInstallationDtls.getAgentInstallationId());
			if(pim != null) {
				Collection<ProviderInstance> coll = pim.getForAgentVersion(version);
                if(!Utils.isEmptyCollection(coll)) {
                    this.fAvailableOptionalProviderInstances = coll.toArray(new ProviderInstance[coll.size()]);
                }
			}
		}
		if(Utils.isEmptyArray(fAvailableOptionalProviderInstances)) {
			this.fButtonSelectProviders.setVisible(false);
		} else {
			this.fButtonSelectProviders.setVisible(true);
		}

		validate();
		repaint();
	}

	/**
	 * Applies the changes made to the initial configuration.
	 * @throws InvalidConfiguration
	 */
	public void applyChanges() throws InvalidConfiguration {
		fSamplingAndLevelPanel.apply(this.fAgentConfig);

		if(this.fAgentCustomConfigPanel != null) {
			try {
				this.fAgentCustomConfigPanel.applyChanges();
				// apply the new configuration to the original one
				this.fAgentCustomConfigOriginal.apply(fAgentCustomConfig);
				this.fAgentConfig.setCustom(this.fAgentCustomConfigOriginal);
			} catch(InvalidPropertyValue e) {
				throw new InvalidConfiguration(e);
			} catch (VetoException e) {
                throw new InvalidConfiguration(e);
            }
		}

	    // if no optional providers has been selected, activate the ones which are selected
        if(this.fProviderInstancesToActivate == null
                && !Utils.isEmptyArray(this.fAvailableOptionalProviderInstances)) {
            for(ProviderInstance pid : this.fAvailableOptionalProviderInstances) {
                if(pid.isSelectedByDefault()) {
                    if(this.fProviderInstancesToActivate == null) {
                        this.fProviderInstancesToActivate = new LinkedList<String>();
                    }
                    this.fProviderInstancesToActivate.add(pid.getInstanceName());
                }
            }
        }
        if(this.fProviderInstancesToActivate != null) {
            this.fAgentConfig.setProviderInstances(this.fProviderInstancesToActivate.toArray(
                        new String[this.fProviderInstancesToActivate.size()]));
        }
	}

	/**
	 * Resets the current changes.
	 */
	public void resetChanges() throws RMSException {
		setAgentConfiguration(this.fAgentConfig, this.fAgentInstallationDtls, fAgentCustomConfigPanel);
	}

	/**
	 * @see javax.swing.JComponent#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if(fAgentCustomConfigPanel != null) {
			fAgentCustomConfigPanel.setEnabled(enabled);
		}
		fButtonSelectProviders.setEnabled(enabled);
		fSamplingAndLevelPanel.setEnabled(enabled);
	}

	/**
	 * Selects optional providers to attach to the currently selected agent.
	 */
	private void handleSelectProviders() {
		try {
			AgentOptionalProvidersSelectorDialog dlg = new AgentOptionalProvidersSelectorDialog(
					this.fViewContainer.getAppFrame(), this.fAvailableOptionalProviderInstances, this.fAgentConfig.getProviderInstances());
			UIUtils.centerDialogAndShow(fViewContainer.getAppFrame(), dlg);
			this.fProviderInstancesToActivate = dlg.getSelectedProviders();
		} catch(Exception e) {
			UIExceptionMgr.exception(e);
		}
	}

// not visible outside this package

	/**
	 * @return the conf
	 */
	AgentConfiguration getConf() {
		return fAgentConfig;
	}
	/**
	 * @return the globalCheckBox
	 */
	JCheckBox getGlobalCheckBox() {
		return fSamplingAndLevelPanel.getGlobalSamplingCheckBox();
	}
	/**
	 * @return the sampling interval spinner
	 */
	JSpinner getSamplingIntervalSpinner() {
		return fSamplingAndLevelPanel.getSamplingSpinner();
	}
	/**
	 * @return the monitoring level spinner
	 */
	JSpinner getMonitoringLevelSpinner() {
		return fSamplingAndLevelPanel.getLevelSpinner();
	}
	/**
	 * @return the panelCustom
	 */
	AgentCustomConfigurationPanel getPanelCustom() {
		return fAgentCustomConfigPanel;
	}
}
