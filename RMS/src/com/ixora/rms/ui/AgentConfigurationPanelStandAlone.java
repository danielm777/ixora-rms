/*
 * Created on 17-Jan-2004
 */
package com.ixora.rms.ui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.ixora.common.MessageRepository;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.actions.ActionApply;
import com.ixora.common.ui.actions.ActionCancel;
import com.ixora.common.ui.jobs.UIWorkerJobDefault;
import com.ixora.rms.agents.AgentConfiguration;
import com.ixora.rms.agents.AgentConfigurationTuple;
import com.ixora.rms.agents.ui.AgentCustomConfigurationPanel;
import com.ixora.rms.agents.ui.AgentCustomConfigurationPanelContext;
import com.ixora.rms.client.model.AgentNode;
import com.ixora.rms.client.model.SessionModel;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.repository.VersionableAgentInstallationData;
import com.ixora.rms.services.MonitoringSessionService;
import com.ixora.rms.ui.messages.Msg;

/**
 * The common agent configuration panel.
 * @author Daniel Moraru
 */
public final class AgentConfigurationPanelStandAlone
	extends JPanel {
	/** Monitoring session service */
	private MonitoringSessionService sessionService;
	/** Session model */
	private SessionModel sessionModel;
	/** View container */
	private RMSViewContainer viewContainer;
	/** Agent node being edited */
	private AgentNode agentNode;
	/** Action */
	private ActionApplyChanges actionApplyChanges;
	/** Action */
	private ActionCancel actionCancel;
	/** Event handler */
	private EventHandler eventHandler;
	/** Setup complete flag */
	private boolean setupComplete;
	/** Context for the agent configuration panels */
	private AgentCustomConfigurationPanelContext fAgentConfPanelContext;
	/** Log replay mode flag; true if in log replay mode */
	private boolean fLogReplayMode;

// Components
	private JButton jButtonApplyChanges;
	private JButton jButtonCancel;
	private JPanel jPanelButtons;
	private AgentConfigurationPanel panelConfig;
	private AgentCustomConfigurationPanel panelCustom;

	/**
	 * Event handler.
	 */
	private final class EventHandler
	implements AgentCustomConfigurationPanel.Listener,
	ChangeListener {
		/**
		 * @see com.ixora.rms.agents.ui.AgentCustomConfigurationPanel.Listener#componentStateChanged()
		 */
		public void componentStateChanged() {
			handleComponentStateChanged();
		}
		/**
		 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
		 */
		public void stateChanged(ChangeEvent e) {
			handleComponentStateChanged();
		}
	}

	/**
	 * Apply changes action.
	 */
	private final class ActionApplyChanges extends ActionApply {
		public ActionApplyChanges() {
			super();
			// disable mnemonic
			putValue(MNEMONIC_KEY, null);
			this.enabled = false;
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			handleApplyChanges();
		}
	}

	/**
	 * Constructor called for a log replay session.
	 * @param vc
	 * @param sessionModel
	 * @param readOnlyCustomConfig
	 */
	public AgentConfigurationPanelStandAlone(
			RMSViewContainer vc,
			SessionModel sessionModel,
			boolean readOnlyCustomConfig) {
		super(new BorderLayout());
		fLogReplayMode = true;
		init(vc, null, sessionModel, readOnlyCustomConfig);
	}

	/**
	 * Constructor.
	 * @param vc
	 * @param sessionService
	 * @param sessionModel
	 * @param readOnlyCustomConfig
	 */
	public AgentConfigurationPanelStandAlone(
			RMSViewContainer vc,
			MonitoringSessionService sessionService,
			SessionModel sessionModel,
			boolean readOnlyCustomConfig) {
		super(new BorderLayout());
		if(sessionService == null) {
			throw new IllegalArgumentException("null session service");
		}
		init(vc, sessionService, sessionModel, readOnlyCustomConfig);
	}

	/**
	 * @param vc
	 * @param sessionService
	 * @param sessionModel
	 * @param readOnlyCustomConfig
	 */
	private void init(RMSViewContainer vc, MonitoringSessionService sessionService, SessionModel sessionModel, boolean readOnlyCustomConfig) {
		if(vc == null) {
			throw new IllegalArgumentException("null view container");
		}
		if(sessionModel == null) {
			throw new IllegalArgumentException("null session model");
		}
		this.viewContainer = vc;
		this.sessionService = sessionService;
		this.sessionModel = sessionModel;
		fAgentConfPanelContext = new AgentCustomConfigurationPanelContext(vc);
		panelConfig = new AgentConfigurationPanel(vc, null, readOnlyCustomConfig);
		if(fLogReplayMode) {
			panelConfig.setEnabled(false);
		}
		int strut = UIConfiguration.getPanelPadding();
		panelConfig.setBorder(BorderFactory.createEmptyBorder(
				strut, strut, strut, strut));
		this.actionApplyChanges = new ActionApplyChanges();
		this.actionCancel = new ActionCancel() {
			public void actionPerformed(ActionEvent e) {
				handleCancel();
			}
		};
		// disable mnemonic
		this.actionCancel.putValue(Action.MNEMONIC_KEY, null);
		this.actionCancel.setEnabled(false);
		this.jButtonApplyChanges = UIFactoryMgr.createButton(this.actionApplyChanges);
		this.jButtonCancel = UIFactoryMgr.createButton(this.actionCancel);
		this.jPanelButtons = new JPanel(new FlowLayout());
		this.jPanelButtons.add(this.jButtonApplyChanges);
		this.jPanelButtons.add(this.jButtonCancel);
		add(panelConfig, BorderLayout.CENTER);
		add(this.jPanelButtons, BorderLayout.SOUTH);
		this.eventHandler = new EventHandler();
		panelConfig.getSamplingIntervalSpinner()
			.addChangeListener(eventHandler);
		panelConfig.getMonitoringLevelSpinner()
			.addChangeListener(eventHandler);
		panelConfig.getGlobalCheckBox()
			.addChangeListener(eventHandler);
	}

	/**
	 * Cancels the changes to the configuration.
	 */
	private void handleCancel() {
		try {
			panelConfig.resetChanges();
			this.actionApplyChanges.setEnabled(false);
			this.actionCancel.setEnabled(false);
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Applies changes.
	 */
	private void handleApplyChanges() {
		try {
			// calculate the difference
			AgentConfiguration before =
				(AgentConfiguration)this.panelConfig.getConf().clone();
			this.panelConfig.applyChanges();
			AgentConfiguration after = this.panelConfig.getConf();
			final AgentConfiguration delta = before.getDelta(after);

			if(delta != null) {
				this.viewContainer.runJob(new UIWorkerJobDefault(
						viewContainer.getAppFrame(),
						Cursor.WAIT_CURSOR,
						MessageRepository.get(
								Msg.TEXT_APPLYINGCHANGES_AGENTCONFIGURATION)) {
					public void work() throws Exception {
						this.fResult = sessionService.configureAgent(
								agentNode.getHostNode().getHostInfo().getName(),
								agentNode.getAgentInfo().getDeploymentDtls().getAgentId(),
								delta);
					}
					public void finished(Throwable ex) {
						if(ex != null) {
							UIExceptionMgr.userException(ex);
						} else {
							sessionModel.setAgentConfigurationTuple(agentNode,
									(AgentConfigurationTuple)this.fResult);
						}
					}
				});
			}
			actionApplyChanges.setEnabled(false);
			actionCancel.setEnabled(false);
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}


	/**
	 * Handles component state changed event.
	 */
	private void handleComponentStateChanged() {
		try {
			if(!this.setupComplete) {
				return;
			}
			this.actionApplyChanges.setEnabled(true);
			this.actionCancel.setEnabled(true);
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Sets the configuration to show/edit.
	 * @param conf
	 * @param panelCustomClass
	 * @throws ClassNotFoundException
	 * @throws RMSException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 */
	public void setAgentConfiguration(AgentNode an) throws RMSException, ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		this.setupComplete = false;
		// reset custom config panel
		if(this.panelCustom != null) {
		    this.panelCustom.removeListener();
		    this.panelCustom = null;
		}
        // IMP: must be cloned as it will be written to
        AgentConfiguration agentConfig = (AgentConfiguration)an.getAgentInfo().getDeploymentDtls().getConfiguration().clone();

        String suoVersion = agentConfig.getSystemUnderObservationVersion();
        VersionableAgentInstallationData vad = an.getAgentInfo().getInstallationDtls().getVersionData(suoVersion);
		String cp = vad.getConfigPanelClass();
		this.agentNode = an;
		if(cp != null) {
			this.panelCustom = AgentCustomConfigurationPanel
				.createImplementation(cp,
					this.agentNode.getAgentInfo().getInstallationDtls().getMessageCatalog(),
					fAgentConfPanelContext);
			this.panelCustom.setListener(eventHandler);
		}
		// clone the config as the the AgentConfigurationPanel works on the passed config
		this.panelConfig.setAgentConfiguration(
			agentConfig,
			an.getAgentInfo().getInstallationDtls(),
			this.panelCustom);
		this.setupComplete = true;
	}
}
