/*
 * Created on 21-Dec-2003
 */
package com.ixora.rms.ui.views.session.activation;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import com.ixora.rms.HostId;
import com.ixora.rms.ResourceId;
import com.ixora.common.ComponentConfiguration;
import com.ixora.common.MessageRepository;
import com.ixora.common.history.HistoryMgr;
import com.ixora.common.ui.AppDialog;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.ui.actions.ActionClose;
import com.ixora.common.ui.actions.ActionHelp;
import com.ixora.common.ui.help.HelpMgr;
import com.ixora.common.ui.jobs.UIWorkerJobCancelableWithProgress;
import com.ixora.common.ui.jobs.UIWorkerJobDefault;
import com.ixora.rms.agents.AgentActivationData;
import com.ixora.rms.agents.AgentActivationTuple;
import com.ixora.rms.agents.AgentId;
import com.ixora.rms.client.AgentInstanceData;
import com.ixora.rms.client.model.AgentNode;
import com.ixora.rms.client.model.SessionModel;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.repository.AgentInstallationData;
import com.ixora.rms.services.AgentRepositoryService;
import com.ixora.rms.services.MonitoringSessionService;
import com.ixora.rms.services.ProviderInstanceRepositoryService;
import com.ixora.rms.ui.RMSViewContainer;
import com.ixora.rms.ui.messages.Msg;
/**
 * @author Daniel Moraru
 */
public final class AgentActivatorDialog extends AppDialog {
	private static final long serialVersionUID = -3801029147723468873L;

	/**
	 * Event handler.
	 */
	private final class EventHandler
				implements TreeSelectionListener {
		/**
		 * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
		 */
		public void valueChanged(TreeSelectionEvent e) {
			fViewContainer.getAppWorker().runJobSynch(
					new UIWorkerJobDefault(AgentActivatorDialog.this,
							Cursor.WAIT_CURSOR, "") {
				public void work() throws Throwable {
					handleAgentSelected();
				}
				public void finished(Throwable ex) {
				}
			});
		}
	}

	/**
	 * Activate agent action.
	 */
	private final class ActionActivateAgent extends AbstractAction {
		private static final long serialVersionUID = -6453924095831356911L;
		public ActionActivateAgent() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.ACTIONS_ACTIVATEAGENT), this);
			ImageIcon icon = UIConfiguration.getIcon("activate_agent.gif");
			if(icon != null) {
				putValue(Action.SMALL_ICON, icon);
			}
			this.enabled = false;
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			handleActivateAgent();
		}
	}


	/** MonitoringSessionService */
	private MonitoringSessionService fRmsMonitoringSession;
	/** Table */
	private AgentsTreePanel fAgentsPanel;
	/** View container */
	private RMSViewContainer fViewContainer;
	/** Hosts */
	private Collection<String> fHosts;
	/** Action activate agent */
	private Action fActionActivateAgent;
	/** Agent activation config panel */
	private AgentActivationDataPanelWithHistory fPanelAgentActivationConfig;
	/** Session model */
	private SessionModel fSessionModel;
	/** Agent help action */
	private Action fActionAgentHelp;
    /**
     * The last agent that was selected without exceptions;
     * used to restore the panel in case of an error.
     */
	private AgentActivationNode fLastGoodSelectedAgentNode;
	private JPanel fPanelContent;

    /**
	 * Constructor.
	 * @param host
	 * @param vc
	 * @param rmsConf
	 * @param rmsAr
	 * @param listener
	 * @param sm
	 * @throws RMSException
	 */
	public AgentActivatorDialog(
				Collection<String> hosts,
				RMSViewContainer vc,
				ComponentConfiguration rmsConf,
				MonitoringSessionService rmsMs,
				AgentRepositoryService rmsAr,
				ProviderInstanceRepositoryService rmsPrs,
				SessionModel sm) throws RMSException {
		super(vc.getAppFrame(), HORIZONTAL);
		initialize(hosts, vc, rmsMs, rmsAr, rmsPrs, sm);
	}

	/**
	 * @see com.ixora.common.ui.AppDialog#getDisplayPanels()
	 */
	protected Component[] getDisplayPanels() {
		return new Component[]{fPanelContent};
	}

	/**
	 * @see com.ixora.common.ui.AppDialog#getButtons()
	 */
	protected JButton[] getButtons() {
		return new JButton[]{
			UIFactoryMgr.createButton(this.fActionActivateAgent),
			UIFactoryMgr.createButton(fActionAgentHelp),
			UIFactoryMgr.createButton(new ActionClose() {
				private static final long serialVersionUID = 5453407027549250265L;
				public void actionPerformed(ActionEvent arg0) {
					dispose();
				}})
			};
	}

	/**
	 * Initializes this dialog.
	 * @param hosts
	 * @param vc
	 * @param rmsConf
	 * @param listener
	 */
	private void initialize(
			Collection<String> hosts,
			RMSViewContainer vc,
			MonitoringSessionService rmsMs,
			AgentRepositoryService rmsAr,
			ProviderInstanceRepositoryService rmsPrs,
			SessionModel sm) throws RMSException {
		if(hosts == null || hosts.size() == 0) {
			throw new IllegalArgumentException("null or invalid hosts collection");
		}
		if(vc == null) {
			throw new IllegalArgumentException("null view container");
		}
		if(rmsMs == null) {
			throw new IllegalArgumentException("null monitoring session service");
		}
		if(rmsAr == null) {
			throw new IllegalArgumentException("null agent repository service");
		}
		if(sm == null) {
			throw new IllegalArgumentException("null session model");
		}
		this.fHosts = hosts;
		this.fViewContainer = vc;
		this.fRmsMonitoringSession = rmsMs;
		this.fPanelAgentActivationConfig = new AgentActivationDataPanelWithHistory(vc, rmsPrs);
		this.fSessionModel = sm;
		this.fActionActivateAgent = new ActionActivateAgent();
		this.fActionAgentHelp = new ActionHelp() {
			private static final long serialVersionUID = 3730205822736732650L;
			public void actionPerformed(ActionEvent e) {
				handleHelpOnAgent();
			}};
		this.fActionAgentHelp.setEnabled(false);
		this.fAgentsPanel = new AgentsTreePanel(rmsAr);
		Dimension dim = new Dimension(210, 200);
		this.fAgentsPanel.setPreferredSize(dim);
		this.fAgentsPanel.setMinimumSize(dim);
		fPanelContent = new JPanel(new BorderLayout());
		fPanelContent.add(fAgentsPanel, BorderLayout.WEST);
		fPanelContent.add(fPanelAgentActivationConfig, BorderLayout.CENTER);

		setModal(false);

		// set up title
		StringBuffer buff = new StringBuffer();
		for(Iterator<String> iter = hosts.iterator(); iter.hasNext();) {
			buff.append(iter.next());
			if(iter.hasNext()) {
                buff.append(", ");
            }
		}
		setTitle(MessageRepository.get(
			Msg.TITLE_DIALOGS_AGENTACTIVATOR,
			new String[]{buff.toString()}));

		setPreferredSize(new Dimension(660, 500));

		// detect row selection in the agents table
		TreeSelectionModel lsm = fAgentsPanel.getAgentsTree().getSelectionModel();
		EventHandler ev = new EventHandler();
		lsm.addTreeSelectionListener(ev);

		fAgentsPanel.selectFirstAgentNode();

		buildContentPane();
	}

	/**
	 * Handles the activate agent event.
	 */
	private void handleActivateAgent() {
		try {
			AgentActivationNode agentNode = fAgentsPanel.getSelectedAgentNode();
			if(agentNode == null) {
				return;
			}

            // check if the license allow for the new agents to be deployed
//            final int maxAgents = LicenseMgr.getLicense().getUnits();
//            if(maxAgents > 0 && maxAgents <= sessionModel.getTotalNumberOfAgents()) {
//                throw new LicenseLimitReachedAgents();
//            }

			AgentData dtls = agentNode.getAgentData();

			final AgentInstallationData idtls = dtls.getAgentInstallationDtls();
			// save configuration details, this will update AgentDeploymentDtls;
			final AgentActivationData ddtls = (AgentActivationData)this.fPanelAgentActivationConfig.applyChanges().clone();
			// cache the last activation data
			agentNode.setLastAgentActivationData(ddtls);
			final String agentInstallationId = idtls.getAgentInstallationId();

			// check agent against license
//			List<String> modules = LicenseMgr.getLicense().getModules();
//			if(!modules.contains(agentInstallationId)) {
//				throw new LicenseNotAvailableForAgent(agentInstallationId);
//			}

			final String agentMsgCatalog = idtls.getMessageCatalog();
			final String agentName = idtls.getAgentName();
			final String translatedAgentName = MessageRepository.get(agentMsgCatalog, agentName);
			if(fHosts.size() > 1) {
				// job with progress
				fViewContainer.getAppWorker().runJob(new UIWorkerJobCancelableWithProgress(
									this,
									Cursor.WAIT_CURSOR,
									MessageRepository.get(
											Msg.TEXT_ACTIVATINGAGENT,
											new String[]{translatedAgentName}),
									fHosts.size()) {
					public void work() throws Exception {
						String host;
						for(Iterator<String> iter = fHosts.iterator(); !fCanceled && iter.hasNext();) {
                            host = iter.next();
							setProgressStep(host);
							activateOneAgent(host, agentInstallationId, idtls, ddtls);							setProgressLevel(1);
						}
						// save agent activation data to history
						HistoryMgr.add(getHistoryIdForAgent(agentInstallationId), ddtls);
						jobCompleted();
					}
					public void finished(Throwable ex) {
					}});
			} else {
				// normal job (no progress)
				fViewContainer.getAppWorker().runJob(new UIWorkerJobDefault(
									this,
									Cursor.WAIT_CURSOR,
									MessageRepository.get(
										Msg.TEXT_ACTIVATINGAGENT,
										new String[]{translatedAgentName})) {
					public void work() throws Exception {
						String host;
						for(Iterator<String> iter = fHosts.iterator(); iter.hasNext();) {
							host = iter.next();
							activateOneAgent(host, agentInstallationId, idtls, ddtls);
						}
						// save agent activation data to history
						HistoryMgr.add(getHistoryIdForAgent(agentInstallationId), ddtls);
					}
					public void finished(Throwable ex) {
					}});
			}
		} catch(Exception ex) {
			UIExceptionMgr.userException(ex);
		}
	}

	/**
	 * @param agent
	 * @return
	 */
	private String getHistoryIdForAgent(String agent) {
		return "activation_" + agent;
	}

	/**
	 * @param host
	 * @throws RMSException
	 */
	private void checkForHost(String host) throws RMSException {
        DefaultMutableTreeNode node = fSessionModel.getExistingNodeForResourceId(
        		new ResourceId(
        			new HostId(host), null, null, null));
        if(node == null) {
        	// TODO localize
        	throw new RMSException("Host " + host + " not found. Cannot add agent.");
        }
	}

	/**
	 * @param host
	 * @param agentInstallationId
	 * @param ddtls
	 * @throws RMSException
	 */
	private void checkDeploymentModeConflict(String host, String agentInstallationId, AgentActivationData ddtls)
			throws RMSException {
        DefaultMutableTreeNode node = fSessionModel.getExistingNodeForResourceId(
        		new ResourceId(
        			new HostId(host), new AgentId(agentInstallationId), null, null));
        if(node != null) {
        	AgentNode an = (AgentNode)node;
        	if(an.getAgentInfo().getDeploymentDtls().getLocation()
        			!= ddtls.getLocation()) {
        		// TODO localize
        		throw new RMSException("Cannot mix local and remote deployment modes for the same agent on the same host");
        	}
        }
	}

	/**
	 * @param host
	 * @param agentInstallationId
	 * @param idtls
	 * @param ddtls
	 * @throws RMSException
	 * @throws RemoteException
	 */
	private void activateOneAgent(String host, String agentInstallationId,
			AgentInstallationData idtls, AgentActivationData ddtls) throws RMSException, RemoteException {
		// check that host is in the model
		checkForHost(host);
		// IMP: must be cloned
		AgentActivationData activationData = (AgentActivationData)ddtls.clone();
		// prepare activation data
        activationData.setHost(host);
        activationData.setAgentInstallationId(agentInstallationId);
        // check for conflicts
        checkDeploymentModeConflict(host, agentInstallationId, activationData);
        AgentActivationTuple tuple =
            fRmsMonitoringSession.activateAgent(activationData);
        AgentInstanceData agentInstanceData = new AgentInstanceData(activationData, tuple.getDescriptor());
		fSessionModel.addAgent(
			host,
			idtls,
            agentInstanceData);
		fSessionModel.updateEntities(
		        host,
		        tuple.getAgentId(),
		        tuple.getEntities());
	}

	/**
	 * Handles the agent selected event.
	 */
	private void handleAgentSelected() {
		try {
			AgentActivationNode agentNode = fAgentsPanel.getSelectedAgentNode();
			if(agentNode == null) {
				fActionActivateAgent.setEnabled(false);
				fActionAgentHelp.setEnabled(false);
				fPanelAgentActivationConfig.setVisible(false);
				return;
			}
			fActionActivateAgent.setEnabled(true);
			fPanelAgentActivationConfig.setVisible(true);

			AgentData data = agentNode.getAgentData();
			fActionAgentHelp.setEnabled(data.getAgentInstallationDtls().getJavaHelp() != null);

            this.fPanelAgentActivationConfig.setAgentInstanceData(
                    data.getLastAgentActivationDtls(),
                    data.getAgentInstallationDtls());
            fLastGoodSelectedAgentNode = agentNode;
		} catch(Throwable ex) {
			// restore the configPanel
            if(fLastGoodSelectedAgentNode != null) {
                fAgentsPanel.selectAgentNode(fLastGoodSelectedAgentNode);
            } else {
            	fPanelAgentActivationConfig.setVisible(false);
            }
			UIExceptionMgr.userException(ex);
		}
	}

	/**
	 * Launches the java help for the selected agent.
	 */
	private void handleHelpOnAgent() {
		try {
			AgentActivationNode agentNode = fAgentsPanel.getSelectedAgentNode();
			if(agentNode == null) {
				return;
			}
			AgentData data = agentNode.getAgentData();
			String help = data.getAgentInstallationDtls().getJavaHelp();
			if(help != null) {
				HelpMgr.showHelp(this, help);
			}
		} catch(Exception ex) {
			UIExceptionMgr.userException(ex);
		}
	}
}
