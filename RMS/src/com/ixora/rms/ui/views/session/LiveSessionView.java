package com.ixora.rms.ui.views.session;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.ixora.common.ConfigurationMgr;
import com.ixora.common.ComponentConfiguration;
import com.ixora.common.MessageRepository;
import com.ixora.common.ui.AppStatusBarDefault;
import com.ixora.common.ui.ShowException;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.ui.jobs.UIWorkerJobCancelableWithExternalProgress;
import com.ixora.common.ui.jobs.UIWorkerJobDefault;
import com.ixora.common.ui.popup.PopupListener;
import com.ixora.common.utils.Utils;
import com.ixora.rms.EntityConfiguration;
import com.ixora.rms.EntityDescriptorTree;
import com.ixora.rms.EntityId;
import com.ixora.rms.HostInformation;
import com.ixora.rms.RMS;
import com.ixora.rms.RMSComponent;
import com.ixora.rms.ResourceId;
import com.ixora.rms.agents.AgentConfiguration;
import com.ixora.rms.agents.AgentConfigurationTuple;
import com.ixora.rms.agents.AgentId;
import com.ixora.rms.agents.AgentState;
import com.ixora.rms.client.QueryRealizerImpl;
import com.ixora.rms.client.model.AgentNode;
import com.ixora.rms.client.model.DashboardInfo;
import com.ixora.rms.client.model.EntityNode;
import com.ixora.rms.client.model.HostNode;
import com.ixora.rms.client.model.SessionModel;
import com.ixora.rms.client.model.SessionModelTreeNode;
import com.ixora.rms.client.model.SessionNode;
import com.ixora.rms.client.session.MonitoringSessionDescriptor;
import com.ixora.rms.client.session.MonitoringSessionRealizer;
import com.ixora.rms.exception.InvalidAgentState;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.logging.LogRepositoryInfo;
import com.ixora.rms.providers.ProviderState;
import com.ixora.rms.reactions.ReactionsComponent;
import com.ixora.rms.reactions.ReactionsComponentConfigurationConstants;
import com.ixora.rms.services.DataLogService;
import com.ixora.rms.services.HostMonitorService;
import com.ixora.rms.services.MonitoringSessionService;
import com.ixora.rms.services.ParserRepositoryService;
import com.ixora.rms.services.ProviderInstanceRepositoryService;
import com.ixora.rms.services.ProviderRepositoryService;
import com.ixora.rms.services.ReactionLogService;
import com.ixora.rms.services.ReactionLogService.ReactionLogEvent;
import com.ixora.rms.ui.AgentOperationsPanel;
import com.ixora.rms.ui.ButtonNewViewBoardHandler;
import com.ixora.rms.ui.EntityOperationsPanel;
import com.ixora.rms.ui.HostOperationsPanel;
import com.ixora.rms.ui.RMSViewContainer;
import com.ixora.rms.ui.SessionModelSwing;
import com.ixora.rms.ui.SessionModelTreeCellRenderer;
import com.ixora.rms.ui.SessionOperationsPanel;
import com.ixora.rms.ui.SessionView;
import com.ixora.rms.ui.SessionViewLeftPanel;
import com.ixora.rms.ui.actions.ActionShowLegendWindow;
import com.ixora.rms.ui.dataviewboard.DataViewBoardHandler;
import com.ixora.rms.ui.dataviewboard.DataViewControl;
import com.ixora.rms.ui.dataviewboard.DataViewScreenDescriptor;
import com.ixora.rms.ui.dataviewboard.DataViewScreenPanel;
import com.ixora.rms.ui.exporter.HTMLGenerator;
import com.ixora.rms.ui.logchooser.LogChooser;
import com.ixora.rms.ui.logchooser.LogChooserImpl;
import com.ixora.rms.ui.messages.Msg;
import com.ixora.rms.ui.reactions.ReactionLogViewer;
import com.ixora.rms.ui.session.MonitoringSessionRepository;
import com.ixora.rms.ui.views.session.activation.AgentActivatorDialog;
import com.ixora.rms.ui.views.session.exception.LoggingStoppedDueToFatalError;

/*
 * Created on 08-Dec-03
 */
/**
 * @author Daniel Moraru
 */
public final class LiveSessionView extends SessionView {
	// gui controls
	private javax.swing.JPopupMenu jPopupMenu;
	private javax.swing.JMenuItem jMenuItemAddHosts;
	private javax.swing.JMenuItem jMenuItemRemoveHosts;
	private javax.swing.JMenuItem jMenuItemAddAgents;
	private javax.swing.JMenuItem jMenuItemRemoveAgents;
	private javax.swing.JMenuItem jMenuItemStartSession;
	private javax.swing.JMenuItem jMenuItemStopSession;
	private javax.swing.JMenuItem jMenuItemStartAgent;
	private javax.swing.JMenuItem jMenuItemViewAgentError;
	private javax.swing.JMenuItem jMenuItemRefreshNode;
	private JMenuItem jMenuItemRefreshEntityNodeWithSchedule;
	private javax.swing.JMenuItem jMenuItemViewReactionsLog;
	private javax.swing.JMenuItem jMenuItemEnableAllCounters;
	private javax.swing.JMenuItem jMenuItemEnableAllCountersRecursively;
	private javax.swing.JMenuItem jMenuItemDisableAllCounters;
	private javax.swing.JMenuItem jMenuItemDisableAllCountersRecursively;
	private javax.swing.JMenuItem jMenuItemStopAgent;
	private javax.swing.JMenuItem jMenuItemSaveSession;
	private javax.swing.JMenuItem jMenuItemSaveSessionAs;
//	private javax.swing.JMenuItem jMenuItemFilterOutUnselectedItems;
	private javax.swing.JCheckBoxMenuItem jMenuItemTurnOnOffLogging;
	private javax.swing.JCheckBoxMenuItem jMenuItemTurnOnOffReactions;
	private javax.swing.JButton jButtonStartSession;
	private javax.swing.JButton jButtonStopSession;
	private javax.swing.JButton jButtonSaveSession;
	private JButton jButtonShowReactionsLog;
	private javax.swing.JToggleButton jButtonLogging;
	private javax.swing.JToggleButton jButtonReactions;
	private JEditorPane jEditorPaneReactionsAlarm;

	/**
	 * Sampling interval panel.
	 */
	private SamplingIntervalPanel samplingIntervalPanel;
	/**
	 * Panel filling the left part of the view container
	 * hosting the session view.
	 */
	private SessionViewLeftPanel leftPanel;
	/**
	 * Panel for acting upon the selected scheme.
	 */
	private SessionOperationsPanel schemeOperationsPanel;
	/**
	 * Panel for acting upon the selected host.
	 */
	private HostOperationsPanel hostOperationsPanel;
	/**
	 * Panel for acting upon the selected entity.
	 */
	private EntityOperationsPanel entityOperationsPanel;
	/**
	 * Panel for acting upon the selected agent.
	 */
	private AgentOperationsPanel agentOperationsPanel;
	/**
	 * Event handler.
	 */
	private EventHandler eventHandler;
	/**
	 * This class knows to navigate the session tree.
	 */
	private LiveSessionTreeExplorer treeExplorer;

// object references
	/**
	 * Configuration of the RMS control component.
	 */
	private ComponentConfiguration rmsControlConfig;
	/**
	 * Host monitor service used to provide visual feedback
	 * on the state of various services on the hosts involved
	 * in this monitoring session.
	 */
	private HostMonitorService rmsHostMonitor;
	/**
	 * The control component for this monitoring session.
	 */
	private MonitoringSessionService rmsMonitoringSession;
	/**
	 * Repository used to load and save provider instances.
	 */
	private ProviderInstanceRepositoryService rmsProviderInstanceRepository;
	/**
	 * Repository used to load providers.
	 */
	private ProviderRepositoryService rmsProviderRepository;
	/**
	 * Repository used to load parsers.
	 */
	private ParserRepositoryService rmsParserRepository;
	/**
	 * Data log service used to start and stop data logging
	 * for this session.
	 */
	private DataLogService rmsDataLogService;
	/**
	 * Reaction log service.
	 */
	private ReactionLogService rmsReactionLogService;
	/**
	 * Monitoring scheme repository responsible with
	 * loading and saving monitoring schema.
	 */
	private MonitoringSessionRepository schemeRepository;
	/**
	 * The current monitoring scheme. Each monitoring session
	 * is backed up by one monitoring scheme (the object describing
	 * the current montioring environment)
	 */
	private MonitoringSessionDescriptor scheme;
	/**
	 * Query realizer.
	 */
	private QueryRealizerImpl queryRealizer;
	/**
	 * Reaction log viewer frame.
	 */
	private ReactionLogViewer reactionLogViewer;
	/** Reaction alarm */
	private ReactionAlarm reactionAlarm;

	// internal actions
	private Action actionStartSession = new ActionStartSession();
	private Action actionStopSession = new ActionStopSession();
	private Action actionSaveSession = new ActionSaveSession();
	private Action actionSaveSessionAs = new ActionSaveSessionAs();
	private Action actionStartAgents = new ActionStartAgents();
	private Action actionStopAgents = new ActionStopAgents();
	private Action actionViewAgentError = new ActionViewAgentError();
	private Action actionRefreshNode = new ActionRefreshNode();
	private Action actionRefreshEntityNodeWithSchedule = new ActionRefreshEntityNodeWithSchedule();
	private Action actionTurnOnOffLogging = new ActionTurnOnOffLogging();
	private Action actionEnableAllCounters = new ActionEnableAllCounters();
	private Action actionEnableAllCountersRecursively = new ActionEnableAllCountersRecursively();
	private Action actionDisableAllCounters = new ActionDisableAllCounters();
	private Action actionDisableAllCountersRecursively = new ActionDisableAllCountersRecursively();
	private Action actionTurnOnOffReactions = new ActionTurnOnOffReactions();
	private Action actionViewReactionsLog = new ActionViewReactionLog();

	// external actions
//	private Action actionFilterOutUnselectedItems;

	/**
	 * Start monitoring session action.
	 */
	private final class ActionStartSession extends AbstractAction {
		private static final long serialVersionUID = 6748532408287972100L;
		public ActionStartSession() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.ACTIONS_STARTSESSION), this);
			ImageIcon icon = UIConfiguration.getIcon("start.gif");
			if(icon != null) {
				putValue(Action.SMALL_ICON, icon);
			}
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			handleStartSession();
		}
	}

	/**
	 * Starts all selected agents.
	 */
	private final class ActionStartAgents extends AbstractAction {
		private static final long serialVersionUID = 6533081136534028895L;
		public ActionStartAgents() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.ACTIONS_STARTAGENTS), this);
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			handleStartAgents();
		}
	}

	/**
	 * Displays the error for an agent in ERROR state.
	 */
	private final class ActionViewAgentError extends AbstractAction {
		private static final long serialVersionUID = -1983892786913230176L;
		public ActionViewAgentError() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.ACTIONS_VIEWAGENTERROR), this);
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			handleViewAgentError();
		}
	}

	/**
	 * Displays the reaction log.
	 */
	private final class ActionViewReactionLog extends AbstractAction {
		private static final long serialVersionUID = -9211461264072764621L;
		public ActionViewReactionLog() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.ACTIONS_VIEW_REACTIONS_LOG), this);
			ImageIcon icon = UIConfiguration.getIcon("reactions.gif");
			if(icon != null) {
				putValue(Action.SMALL_ICON, icon);
			}
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			handleViewReactionLog();
		}
	}

	/**
	 * Turns on and off reactions.
	 */
	private final class ActionTurnOnOffReactions extends AbstractAction {
		private static final long serialVersionUID = -2597151277259593838L;
		public ActionTurnOnOffReactions() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.ACTIONS_DISABLE_REACTIONS), this);
			ImageIcon icon = UIConfiguration.getIcon("disable_reactions.gif");
			if(icon != null) {
				putValue(Action.SMALL_ICON, icon);
			}
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent ev) {
			handleTurnOnOffReactions(ev);
		}
	}

	/**
	 * Refreshes the selected agent or entity node.
	 */
	private final class ActionRefreshNode extends AbstractAction {
		private static final long serialVersionUID = -5931556266141179230L;
		public ActionRefreshNode() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.ACTIONS_REFRESHNODE), this);
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			handleRefreshOnTreeNode();
		}
	}

	/**
	 * Refreshes the selected agent or entity node.
	 */
	private final class ActionRefreshEntityNodeWithSchedule extends AbstractAction {
		private static final long serialVersionUID = -3762789131115078610L;
		public ActionRefreshEntityNodeWithSchedule() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.ACTIONS_REFRESH_NODE_WITH_SCHEDULE), this);
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			handleRefreshWithScheduleOnNode();
		}
	}

	/**
	 * Enables all the counters for the selected agent or entity node.<br>
	 * If the node is an agent, all it's counters will be enabled recursively; if the node
	 * is an entity only it's counters will be enabled.
	 *
	 */
	private final class ActionEnableAllCounters extends AbstractAction {
		private static final long serialVersionUID = -2803613420616570954L;
		public ActionEnableAllCounters() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.ACTIONS_ENABLE_ALL_COUNTERS), this);
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			handleSetEnabledAllCounters(true, false);
		}
	}

	/**
	 * Enables all the counters for the selected entity node recursively.
	 */
	private final class ActionEnableAllCountersRecursively extends AbstractAction {
		private static final long serialVersionUID = 5461506659742428801L;
		public ActionEnableAllCountersRecursively() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.ACTIONS_ENABLE_ALL_COUNTERS_RECURSIVELY), this);
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			handleSetEnabledAllCounters(true, true);
		}
	}

	/**
	 * Opposite of <code>ActionEnableAllCounters</code>.
	 */
	private final class ActionDisableAllCounters extends AbstractAction {
		private static final long serialVersionUID = -3826212853762845301L;
		public ActionDisableAllCounters() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.ACTIONS_DISABLE_ALL_COUNTERS), this);
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			handleSetEnabledAllCounters(false, false);
		}
	}

	/**
	 * Opposite of <code>ActionEnableAllCounters</code>..
	 */
	private final class ActionDisableAllCountersRecursively extends AbstractAction {
		private static final long serialVersionUID = 365501094919476224L;
		public ActionDisableAllCountersRecursively() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.ACTIONS_DISABLE_ALL_COUNTERS_RECURSIVELY), this);
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			handleSetEnabledAllCounters(false, true);
		}
	}

	/**
	 * Stops all selected agents.
	 */
	private final class ActionStopAgents extends AbstractAction {
		private static final long serialVersionUID = -7848594015512898142L;
		public ActionStopAgents() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.ACTIONS_STOPAGENTS), this);
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			handleStopAgents();
		}
	}

	/**
	 * Stop monitoring session action.
	 */
	private final class ActionStopSession extends AbstractAction {
		private static final long serialVersionUID = -733604822763271503L;
		public ActionStopSession() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.ACTIONS_STOPSESSION), this);
			ImageIcon icon = UIConfiguration.getIcon("stop.gif");
			if(icon != null) {
				putValue(Action.SMALL_ICON, icon);
			}
			enabled = false;
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			handleStopSession();
		}
	}
	/**
	 * Save session action.
	 */
	private final class ActionSaveSession extends AbstractAction {
		private static final long serialVersionUID = -2377427328741944441L;
		public ActionSaveSession() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.ACTIONS_SAVESESSION), this);
			ImageIcon icon = UIConfiguration.getIcon("save_session.gif");
			if(icon != null) {
				putValue(Action.SMALL_ICON, icon);
			}
		}

		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			handleSaveMonitoringSession(true, false);
		}
	}
	/**
	 * Save session as action.
	 */
	private final class ActionSaveSessionAs extends AbstractAction {
		private static final long serialVersionUID = 605243475600177868L;
		public ActionSaveSessionAs() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.ACTIONS_SAVESESSION_AS), this);
			ImageIcon icon = UIConfiguration.getIcon("save_session_as.gif");
			if(icon != null) {
				putValue(Action.SMALL_ICON, icon);
			}
		}

		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			handleSaveMonitoringSession(true, true);
		}
	}
	/**
	 * Enables/disables logging.
	 */
	private final class ActionTurnOnOffLogging extends AbstractAction {
		private static final long serialVersionUID = 6926980200091037867L;
		public ActionTurnOnOffLogging() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.ACTIONS_TURNSONOFFLOGGING), this);
			ImageIcon icon = UIConfiguration.getIcon("turn_on_off_logging.gif");
			if(icon != null) {
				putValue(Action.SMALL_ICON, icon);
			}
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			handleTurnOnOffLogging(arg0);
		}
	}

	/**
	 * Add hosts action.
	 */
	private final class ActionAddHosts extends AbstractAction {
		private static final long serialVersionUID = 3490871877736224440L;
		public ActionAddHosts() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.ACTIONS_ADD_HOSTS), this);
			//putValue(Action.SMALL_ICON, UIConfiguration.getIcon("add_hosts.gif"));
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			handleAddHosts();
		}
	}

	/**
	 * Remove hosts action.
	 */
	private final class ActionRemoveHosts extends AbstractAction {
		private static final long serialVersionUID = -3646723937204957261L;
		public ActionRemoveHosts() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.ACTIONS_REMOVE_HOSTS), this);
			//putValue(Action.SMALL_ICON, UIConfiguration.getIcon("remove_hosts.gif"));
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			handleRemoveHosts();
		}
	}

	/**
	 * Add agents action.
	 */
	private final class ActionAddAgents extends AbstractAction {
		private static final long serialVersionUID = 8756702813647226065L;
		public ActionAddAgents() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.ACTIONS_ADD_AGENTS), this);
			//putValue(Action.SMALL_ICON, UIConfiguration.getIcon("agent.gif"));
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			handleAddAgents();
		}
	}

	/**
	 * Remove agents action.
	 */
	private final class ActionRemoveAgents extends AbstractAction {
		private static final long serialVersionUID = -2694920951743964675L;
		public ActionRemoveAgents() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.ACTIONS_REMOVE_AGENTS), this);
			//putValue(Action.SMALL_ICON, UIConfiguration.getIcon("agent.gif"));
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			handleRemoveAgents();
		}
	}

	/**
	 * Event handler.
	 */
	private class EventHandler extends PopupListener
			implements
                MonitoringSessionService.Listener,
					TreeSelectionListener,
						TreeWillExpandListener,
							SessionModel.RoughListener,
								Observer,
									DataLogService.Listener,
										HTMLGenerator.Listener,
											HyperlinkListener,
												ReactionLogService.Listener {
		/**
		 * @see com.ixora.common.ui.popup.PopupListener#showPopup(java.awt.event.MouseEvent)
		 */
		protected void showPopup(MouseEvent e) {
			if(e.getSource() == LiveSessionView.this.getLeftPanel().getSessionTree()) {
				handleShowPopupOnSessionTree(e);
				return;
			}
		}
		/**
		 * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
		 */
		public void valueChanged(TreeSelectionEvent ev) {
			handleSessionTreeSelectionEvent(ev);
		}
		/**
		 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
		 */
		public void mousePressed(MouseEvent e) {
			// needed by PopupListener
			super.mousePressed(e);
			if(e.getSource() == getLeftPanel().getSessionTree()) {
				handleMousePressedOnSessionTree(e);
			}
		}
        /**
         * @see com.ixora.rms.ui.dataviewboard.DataViewBoard.Listener#controlRemoved(com.ixora.rms.ui.dataviewboard.DataViewControl)
         */
        public void controlRemoved(DataViewControl control) {
            handleControlRemoved(control);
        }
        /**
         * @see javax.swing.event.TreeWillExpandListener#treeWillCollapse(javax.swing.event.TreeExpansionEvent)
         */
        public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
            ;
        }
        /**
         * @see javax.swing.event.TreeWillExpandListener#treeWillExpand(javax.swing.event.TreeExpansionEvent)
         */
        public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
            handleTreeExpanded(event);
        }
		/**
		 * @see com.ixora.rms.client.model.SessionModel.RoughListener#modelChanged(com.ixora.rms.internal.ResourceId[], int, int)
		 */
		public void modelChanged(final ResourceId[] ridChanged, final int change, final int changeType) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					handleSessionModelChange(ridChanged, change, changeType);
				}
			});
		}
        /**
         * @see com.ixora.rms.services.MonitoringSessionService.Listener#agentNonFatalError(java.lang.String, com.ixora.rms.agents.AgentId, java.lang.Throwable)
         */
        public void agentNonFatalError(final String host, final AgentId agentId, final Throwable t) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    handleAgentNonFatalError(host, agentId, t);
                }
            });
        }
        public void agentStateChanged(String host, AgentId agentId, AgentState state, Throwable e) {
        }
        public void entitiesChanged(String host, AgentId agentId, EntityDescriptorTree entities) {
        }
        public void providerStateChanged(String host, AgentId agentId, String providerInstanceName, ProviderState state, Throwable e) {
        }
		/**
		 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
		 */
		public void update(final Observable o, final Object arg) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    handleUpdate(o, arg);
                }
            });
		}
		/**
		 * @see com.ixora.rms.services.DataLogService.Listener#error(java.lang.Throwable)
		 */
		public void error(final Throwable t) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
        			handleFatalLoggingError(t);
                }
            });
		}
		/**
		 * @see com.ixora.rms.ui.exporter.HTMLGenerator.Listener#finishedHTMLGeneration()
		 */
		public void finishedHTMLGeneration(final Exception error) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
        			handleHTMLGenerationFinished(error);
                }
            });
		}
		/**
		 * @see com.ixora.rms.ui.exporter.HTMLGenerator.Listener#cancelledHTMLGeneration()
		 */
		public void cancelledHTMLGeneration() {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                	handleHTMLGenerationCancelled();
                }
            });
		}
		/**
		 * @see com.ixora.rms.ui.exporter.HTMLGenerator.Listener#startedHTMLGeneration()
		 */
		public void startedHTMLGeneration() {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                	handleHTMLGenerationStarted();
                }
            });
		}
		/**
		 * @see javax.swing.event.HyperlinkListener#hyperlinkUpdate(javax.swing.event.HyperlinkEvent)
		 */
		public void hyperlinkUpdate(HyperlinkEvent e) {
			if(e.getSource() == getJEditorPaneReactionsAlarm()
					&& e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				handleViewReactionLog();
			}
		}
		/**
		 * @see com.ixora.rms.services.ReactionLogService.Listener#reactionLogEvent(com.ixora.rms.services.ReactionLogService.ReactionLogEvent)
		 */
		public void reactionLogEvent(final ReactionLogEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                	handleReactionLogEvent(event);
                }
            });
		}
	}

	/**
	 * Constructor.
	 * @param rls
	 * @param hms
	 * @param schemeRepository
	 * @param scheme
	 * @param vc
	 * @throws Throwable
	 */
	public LiveSessionView(
	        HostMonitorService hms,
			MonitoringSessionRepository schemeRepository,
			MonitoringSessionDescriptor scheme,
			RMSViewContainer vc) throws Throwable {
		super(vc);
		if(hms == null) {
			throw new IllegalArgumentException("null host monitor");
		}
		if(schemeRepository == null) {
			throw new IllegalArgumentException("null scheme repository");
		}
		this.eventHandler = new EventHandler();
		this.schemeRepository = schemeRepository;
		this.rmsHostMonitor = hms;
		this.rmsControlConfig = ConfigurationMgr.get(RMSComponent.NAME);
		this.rmsMonitoringSession = RMS.getMonitoringSession();
		this.rmsDataLogService = RMS.getDataLogger();
		this.rmsReactionLogService = RMS.getReactionLog();
		this.rmsProviderInstanceRepository = RMS.getProviderInstanceRepository();
		this.rmsProviderRepository = RMS.getProviderRepository();
		this.rmsParserRepository = RMS.getParserRepository();
		RMS.getDataEngine().setLogReplayMode(false);

		initializeComponents();

		this.scheme = scheme;
	}

    /**
	 * Shows the reaction log.
	 */
	private void handleViewReactionLog() {
		try {
			reactionAlarm.reset();
			if(reactionLogViewer == null) {
				reactionLogViewer = new ReactionLogViewer(rmsReactionLogService);
				reactionLogViewer.addWindowListener(new WindowAdapter() {
					public void windowClosed(WindowEvent e) {
						super.windowClosed(e);
						reactionLogViewer = null;
					}
				});
			}
			UIUtils.centerFrameAndShow(this.viewContainer.getAppFrame(), reactionLogViewer);
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 *
	 */
	private void handleTurnOnOffReactions(ActionEvent ev) {
		boolean selected = ((AbstractButton)ev.getSource()).isSelected();
		try {
			ConfigurationMgr.setBoolean(ReactionsComponent.NAME, ReactionsComponentConfigurationConstants.REACTIONS_ENABLED, !selected);
		} catch(Exception e) {
			// if an exception occured, set to the initial state
			getJButtonTurnOnOffReactions().setSelected(!selected);
			getJMenuItemTurnOnOffReactions().setSelected(!selected);
			UIExceptionMgr.userException(e);
		}
	}

	/**
     * Handles non fatal agent errors.
     * @param host
     * @param agentId
     * @param t
     */
	private void handleAgentNonFatalError(String host, AgentId agentId, Throwable t) {
		String agentName = MessageRepository.get(agentId.getInstallationId(), agentId.getInstallationId());
		if(agentId.getInstallationIdx() > 0) {
			agentName = agentName + ":" + agentId.getInstallationIdx();
		}
        this.viewContainer.getAppStatusBar().setErrorMessage(host + "/" + agentName, t);
    }

    /**
	 * @throws RMSException
	 * @see com.ixora.rms.ui.RMSView#initialize()
	 */
	public void initialize() throws RMSException {
		reactionAlarm = new ReactionAlarm(
				getJEditorPaneReactionsAlarm(),
				getJButtonShowReactionsLog());
	    viewContainer.registerMenuItemsForActionsMenu(
			new JMenuItem[] {
				getJMenuItemStartSession(),
				getJMenuItemStopSession(),
				getJMenuItemTurnOnOffLogging(),
				getJMenuItemToggleHTMLGen(),
				getJMenuItemTurnOnOffReactions()
			});

	    viewContainer.registerMenuItemsForFileMenu(
			new JMenuItem[] {
				getJMenuItemSaveSession(),
				getJMenuItemSaveSessionAs()});

		viewContainer.registerToolBarComponents(
			new JComponent[] {
				getJButtonSaveSession(),
			});

		viewContainer.registerToolBarComponents(
				new JComponent[] {
					getJButtonStartSession(),
					getJButtonStopSession(),
					getJButtonTurnOnOffLogging(),
					getJButtonTurnOnOffReactions(),
					getJButtonShowReactionsLog(),
					getJButtonToggleHTMLGeneration(),
					getSamplingIntervalPanel()
				});

		registerComponentsWithViewContainer();

		// add reactions alarm to the status bar
		viewContainer.getAppStatusBar().insertStatusBarComponent(getJEditorPaneReactionsAlarm(),
				AppStatusBarDefault.COMPONENT_PROGRESS, AppStatusBarDefault.POSITION_BEFORE);

		// now add to 'View' menu
		viewContainer.registerMenuItemsForMenu(getJMenuView(),
				new JMenuItem[]{getJMenuItemViewReactionsLog()});

		viewContainer.registerLeftComponent(getLeftPanel());
		viewContainer.registerRightComponent(
		        dataViewBoardHandler.getScreen());

		this.rmsMonitoringSession.addListener(eventHandler);
		this.rmsReactionLogService.addListener(eventHandler);

		if(this.scheme != null) {
			String sn = scheme.getName();
			this.viewContainer.appendToAppFrameTitle(sn);
			this.sessionModel.setSessionName(sn);
			final MonitoringSessionRealizer realizer =
				new MonitoringSessionRealizer(this.sessionModel);
			this.viewContainer.getAppWorker().runJob(
				new UIWorkerJobCancelableWithExternalProgress(
					this.viewContainer.getAppFrame(),
					Cursor.WAIT_CURSOR,
					MessageRepository.get(
							Msg.TEXT_REALIZING_MONITORING_SESSION,
							new String[]{scheme.getName()}),
					realizer.getProgressProvider()) {
				public void work() throws Exception {
					realizer.realize(
							rmsHostMonitor,
							rmsAgentRepository,
							rmsMonitoringSession,
							scheme);
				}
				public void finished(Throwable ex) throws Throwable {
					// continue even if an exception occured
					// realize queries
					realizer.realizeQueries(queryRealizer, scheme, rmsDataViewRepository);
					realizer.realizeDashboards(queryRealizer, scheme);
	                // now create screens
					Collection<DataViewScreenDescriptor> screens = scheme.getDataViewScreens();
					if(!Utils.isEmptyCollection(screens)) {
	                    dataViewBoardHandler.initializeFromScreenDescriptors(screens);
					}
					viewContainer.setSessionDirty(false);
				}
				public void cancel() {
					realizer.cancel();
				}
				});
		} else {
			this.scheme = new MonitoringSessionDescriptor();
			String sn = MessageRepository.get(Msg.TEXT_UNTITLED_SESSION);
			this.viewContainer.appendToAppFrameTitle(sn);
			this.sessionModel.setSessionName(sn);
			// new scheme so start by adding hosts
			handleAddHosts();
		}
	}

	/**
	 * @see com.ixora.rms.ui.RMSView#close()
	 */
	public boolean close() {
        if(this.viewContainer.isSessionDirty()) {
            // ask if scheme must be saved
            int resp = UIUtils.getYesNoCancelInput(this.viewContainer.getAppFrame(),
                    MessageRepository.get(Msg.TEXT_CLOSING_VIEW),
                    MessageRepository.get(Msg.TEXT_SAVE_MONITORING_SESSION));
            if(resp == JOptionPane.YES_OPTION) {
            	// save monitoring session synchronously
                handleSaveMonitoringSession(false, false);
            } else if(resp == JOptionPane.CANCEL_OPTION) {
                // cancel
                return true;
            }
        }

        try {
            // close logging if in progress
			if(isLoggingInProgress()) {
				finishLogging();
			}
			// stop html generator
			this.htmlGenerator.stop();
			// close all data view boards
			this.dataViewBoardHandler.close();
			// stop reaction alarm
			this.reactionAlarm.reset();
			// remove reactions alarm from status bar
			this.viewContainer.getAppStatusBar().removeStatusBarComponent(getJEditorPaneReactionsAlarm());
			// now remove from 'View' menu
			this.viewContainer.unregisterMenuItemsForMenu(getJMenuView(),
					new JMenuItem[]{getJMenuItemViewReactionsLog()});
			// unregister with the view container
			unregisterComponentsWithViewContainer();
			this.viewContainer.unregisterMenuItemsForActionsMenu(
				new JMenuItem[]{
					getJMenuItemStartSession(),
					getJMenuItemStopSession(),
					getJMenuItemTurnOnOffLogging(),
					getJMenuItemTurnOnOffReactions(),
					getJMenuItemToggleHTMLGen()});
			this.viewContainer.unregisterMenuItemsForFileMenu(
					new JMenuItem[]{
						getJMenuItemSaveSession(),
						getJMenuItemSaveSessionAs()});

			this.viewContainer.unregisterToolBarComponents(
					new JComponent[] {
						getJButtonSaveSession(),
						});

			this.viewContainer.unregisterToolBarComponents(
				new JComponent[] {
					getJButtonStartSession(),
					getJButtonStopSession(),
					getJButtonTurnOnOffLogging(),
					getJButtonTurnOnOffReactions(),
					getJButtonShowReactionsLog(),
					getJButtonToggleHTMLGeneration(),
					getSamplingIntervalPanel()
					});
			// save config
			this.rmsControlConfig.save();
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		} finally {
			// remove observers and listeners
			ConfigurationMgr.get(ReactionsComponent.NAME).deleteObserver(eventHandler);
			Collection<String> hosts = sessionModel.getAllHosts();
			this.sessionModel.close();
            this.rmsMonitoringSession.removeListener(eventHandler);
			this.rmsMonitoringSession.deactivateAllAgents();
			this.rmsHostMonitor.shutdown();
			this.rmsMonitoringSession.shutdown();
			this.rmsHostMonitor.removeHosts(hosts);
			this.rmsReactionLogService.removeListener(eventHandler);
		}
        return false;
	}

	/**
	 * @return all the agents for the given host in the tree
	 */
	private void fillMonitoringScheme(MonitoringSessionDescriptor session) {
		this.sessionModel.fillMonitoringSessionDescriptor(session);
		// now add queries registered with the QueryRealizer
		session.addQueries(queryRealizer.getRegisteredQueries());
		// add boards
		session.addDataViewScreens(dataViewBoardHandler.getScreenDescriptors());
		session.addDashboards(this.sessionModel.getDashboardHelper().getAllCommittedDashboards(DashboardInfo.ENABLED));
	}

	/**
	 * @return all the agents for the given host in the tree
	 */
	private void fillMonitoringSchemeForLog(MonitoringSessionDescriptor session) {
		this.sessionModel.fillMonitoringSessionDescriptorForLog(session);
		// now add queries registered with the QueryRealizer
		session.addQueries(queryRealizer.getRegisteredQueries());
		// add boards
		session.addDataViewScreens(dataViewBoardHandler.getScreenDescriptors());
		session.addDashboards(this.sessionModel.getDashboardHelper().getAllCommittedDashboards(DashboardInfo.ENABLED));
	}

	/**
	 * This method initializes all the components.
	 * @throws RMSException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	private void initializeComponents() throws RMSException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		sessionModel = new SessionModelSwing(
		        this.rmsMonitoringSession,
				this.rmsHostMonitor,
				this.rmsDashboardRepository,
				this.rmsDataViewRepository,
				this.rmsProviderInstanceRepository);
		sessionModel.addListener(eventHandler);
		sessionModel.setAsksAllowsChildren(true);

		this.queryRealizer = new QueryRealizerImpl(this.sessionModel,
				this.rmsMonitoringSession, this.rmsDataEngineService, this.rmsDataViewRepository);

		dataViewBoardHandler = new DataViewBoardHandler(
				viewContainer,
				queryRealizer,
				rmsDataEngineService,
				rmsReactionLogService,
				rmsDataViewBoardRepository,
				rmsDataViewRepository,
				sessionModel);
		screenPanel = new DataViewScreenPanel(dataViewBoardHandler);
		buttonNewViewBoardHandler = new ButtonNewViewBoardHandler(dataViewBoardHandler, rmsDataViewBoardRepository);
		actionShowHideLegendWindow = new ActionShowLegendWindow(viewContainer);
//		actionFilterOutUnselectedItems = new ActionShowSelectedItemsOnly(this.sessionModel);

		dataViewBoardHandler.addObserver(this);

		this.htmlGenerator = new HTMLGenerator(this.viewContainer,
				dataViewBoardHandler, this.eventHandler);

		JTree tree = getLeftPanel().getSessionTree();
		tree.setRootVisible(true);
		SessionModelTreeCellRenderer cellRenderer = new SessionModelTreeCellRenderer();
		tree.setCellRenderer(cellRenderer);
		tree.setEditable(false);
		tree.addMouseListener(this.eventHandler);
		tree.addTreeSelectionListener(this.eventHandler);
		tree.addTreeWillExpandListener(this.eventHandler);
		tree.setModel(sessionModel);
		tree.getSelectionModel().setSelectionMode(
			TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		treeExplorer = new LiveSessionTreeExplorer(this.viewContainer,
		        rmsMonitoringSession);
		entityOperationsPanel = new EntityOperationsPanel(
				this.viewContainer,
				this.rmsDataEngineService,
				this.rmsDataViewBoardRepository,
				this.rmsDataViewRepository,
				this.rmsDashboardRepository,
				this.rmsMonitoringSession,
				this.sessionModel,
				this.treeExplorer,
				this.queryRealizer,
				this.dataViewBoardHandler,
				this.dataViewBoardHandler,
				this.dataViewBoardHandler);
		agentOperationsPanel = 	new AgentOperationsPanel(
				this.viewContainer,
				this.rmsDataEngineService,
				this.rmsDataViewBoardRepository,
				this.rmsDataViewRepository,
				this.rmsDashboardRepository,
				this.rmsProviderInstanceRepository,
				this.rmsProviderRepository,
				this.rmsParserRepository,
				this.rmsMonitoringSession,
				this.sessionModel,
				this.treeExplorer,
				this.queryRealizer,
				this.dataViewBoardHandler,
				this.dataViewBoardHandler);
		hostOperationsPanel = new HostOperationsPanel(
				this.viewContainer,
				this.rmsDataEngineService,
				this.rmsDataViewBoardRepository,
				this.rmsDataViewRepository,
				this.rmsDashboardRepository,
				this.sessionModel,
				this.treeExplorer,
				this.queryRealizer,
				this.dataViewBoardHandler,
				this.dataViewBoardHandler);
		schemeOperationsPanel = new SessionOperationsPanel(
				this.viewContainer,
				this.rmsDataEngineService,
				this.rmsDataViewBoardRepository,
				this.rmsDataViewRepository,
				this.rmsDashboardRepository,
				this.sessionModel,
				this.treeExplorer,
				this.queryRealizer,
				this.dataViewBoardHandler,
				this.dataViewBoardHandler);

		ConfigurationMgr.get(ReactionsComponent.NAME).addObserver(eventHandler);
	}

    /**
	 * @return the left panel
	 */
	private SessionViewLeftPanel getLeftPanel() {
		if(leftPanel == null) {
			leftPanel = new SessionViewLeftPanel();
		}
		return leftPanel;
	}

	/**
	 * @return the sampling interval control panel
	 */
	private SamplingIntervalPanel getSamplingIntervalPanel() throws RMSException {
		if(samplingIntervalPanel == null) {
			samplingIntervalPanel = new SamplingIntervalPanel(viewContainer);
		}
		return samplingIntervalPanel;
	}

	/**
	 * @return javax.swing.JPopupMenu
	 */
	private javax.swing.JPopupMenu getJPopupMenu() {
		if(jPopupMenu == null) {
			jPopupMenu = UIFactoryMgr.createPopupMenu();
			jPopupMenu.add(getJMenuItemAddHosts());
			jPopupMenu.add(getJMenuItemAddAgents());
			jPopupMenu.add(getJMenuItemRemoveHosts());
			jPopupMenu.add(getJMenuItemStartAgent());
			jPopupMenu.add(getJMenuItemStopAgent());
			jPopupMenu.add(getJMenuItemRefreshNode());
			jPopupMenu.add(getJMenuItemRefreshEntityNodeWithSchedule());
			jPopupMenu.add(getJMenuItemViewAgentError());
			jPopupMenu.add(getJMenuItemRemoveAgents());
			jPopupMenu.add(getJMenuItemEnableAllCounters());
			jPopupMenu.add(getJMenuItemEnableAllCountersRecursively());
			jPopupMenu.add(getJMenuItemDisableAllCounters());
			jPopupMenu.add(getJMenuItemDisableAllCountersRecursively());
		}
		return jPopupMenu;
	}

	/**
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getJMenuItemAddHosts() {
		if(jMenuItemAddHosts == null) {
			jMenuItemAddHosts = UIFactoryMgr.createMenuItem(new ActionAddHosts());
		}
		return jMenuItemAddHosts;
	}

	/**
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getJMenuItemAddAgents() {
		if(jMenuItemAddAgents == null) {
			jMenuItemAddAgents = UIFactoryMgr.createMenuItem(new ActionAddAgents());
			jMenuItemAddAgents.setVisible(false);
		}
		return jMenuItemAddAgents;
	}

	/**
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getJMenuItemRemoveAgents() {
		if(jMenuItemRemoveAgents == null) {
			jMenuItemRemoveAgents = UIFactoryMgr.createMenuItem(new ActionRemoveAgents());
			jMenuItemRemoveAgents.setVisible(false);
		}
		return jMenuItemRemoveAgents;
	}

	/**
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getJMenuItemRemoveHosts() {
		if(jMenuItemRemoveHosts == null) {
			jMenuItemRemoveHosts = UIFactoryMgr.createMenuItem(new ActionRemoveHosts());
			jMenuItemRemoveHosts.setVisible(false);
		}
		return jMenuItemRemoveHosts;
	}

	/**
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getJMenuItemStartSession() {
		if(jMenuItemStartSession == null) {
			jMenuItemStartSession = UIFactoryMgr.createMenuItem(this.actionStartSession);
		}
		return jMenuItemStartSession;
	}

	/**
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getJMenuItemStopSession() {
		if(jMenuItemStopSession == null) {
			jMenuItemStopSession = UIFactoryMgr.createMenuItem(this.actionStopSession);
		}
		return jMenuItemStopSession;
	}

	/**
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getJMenuItemStartAgent() {
		if(jMenuItemStartAgent == null) {
			jMenuItemStartAgent = UIFactoryMgr.createMenuItem(this.actionStartAgents);
		}
		return jMenuItemStartAgent;
	}

	/**
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getJMenuItemViewAgentError() {
		if(jMenuItemViewAgentError == null) {
			jMenuItemViewAgentError = UIFactoryMgr.createMenuItem(this.actionViewAgentError);
		}
		return jMenuItemViewAgentError;
	}

	/**
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getJMenuItemRefreshNode() {
		if(jMenuItemRefreshNode == null) {
			jMenuItemRefreshNode = UIFactoryMgr.createMenuItem(this.actionRefreshNode);
		}
		return jMenuItemRefreshNode;
	}

	/**
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getJMenuItemRefreshEntityNodeWithSchedule() {
		if(jMenuItemRefreshEntityNodeWithSchedule == null) {
			jMenuItemRefreshEntityNodeWithSchedule = UIFactoryMgr.createMenuItem(this.actionRefreshEntityNodeWithSchedule);
		}
		return jMenuItemRefreshEntityNodeWithSchedule;
	}

	/**
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getJMenuItemViewReactionsLog() {
		if(jMenuItemViewReactionsLog == null) {
			jMenuItemViewReactionsLog = UIFactoryMgr.createMenuItem(this.actionViewReactionsLog);
		}
		return jMenuItemViewReactionsLog;
	}

	/**
	 * @return javax.swing.JEditorPane
	 */
	private javax.swing.JEditorPane getJEditorPaneReactionsAlarm() {
		if(jEditorPaneReactionsAlarm == null) {
			jEditorPaneReactionsAlarm = UIFactoryMgr.createHtmlPane();
			jEditorPaneReactionsAlarm.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
			jEditorPaneReactionsAlarm.addHyperlinkListener(eventHandler);
		}
		return jEditorPaneReactionsAlarm;
	}

	/**
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getJMenuItemEnableAllCounters() {
		if(jMenuItemEnableAllCounters == null) {
			jMenuItemEnableAllCounters = UIFactoryMgr.createMenuItem(this.actionEnableAllCounters);
		}
		return jMenuItemEnableAllCounters;
	}

	/**
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getJMenuItemEnableAllCountersRecursively() {
		if(jMenuItemEnableAllCountersRecursively == null) {
			jMenuItemEnableAllCountersRecursively = UIFactoryMgr.createMenuItem(this.actionEnableAllCountersRecursively);
		}
		return jMenuItemEnableAllCountersRecursively;
	}

	/**
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getJMenuItemDisableAllCounters() {
		if(jMenuItemDisableAllCounters == null) {
			jMenuItemDisableAllCounters = UIFactoryMgr.createMenuItem(this.actionDisableAllCounters);
		}
		return jMenuItemDisableAllCounters;
	}

	/**
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getJMenuItemDisableAllCountersRecursively() {
		if(jMenuItemDisableAllCountersRecursively == null) {
			jMenuItemDisableAllCountersRecursively = UIFactoryMgr.createMenuItem(this.actionDisableAllCountersRecursively);
		}
		return jMenuItemDisableAllCountersRecursively;
	}

	/**
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getJMenuItemStopAgent() {
		if(jMenuItemStopAgent == null) {
			jMenuItemStopAgent = UIFactoryMgr.createMenuItem(this.actionStopAgents);
		}
		return jMenuItemStopAgent;
	}

	/**
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getJMenuItemSaveSession() {
		if(jMenuItemSaveSession == null) {
			jMenuItemSaveSession = UIFactoryMgr.createMenuItem(this.actionSaveSession);
		}
		return jMenuItemSaveSession;
	}

	/**
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getJMenuItemSaveSessionAs() {
		if(jMenuItemSaveSessionAs == null) {
			jMenuItemSaveSessionAs = UIFactoryMgr.createMenuItem(this.actionSaveSessionAs);
		}
		return jMenuItemSaveSessionAs;
	}

	/**
	 * @return javax.swing.JCheckBoxMenuItem
	 */
	private javax.swing.JCheckBoxMenuItem getJMenuItemTurnOnOffLogging() {
		if(jMenuItemTurnOnOffLogging == null) {
			jMenuItemTurnOnOffLogging = UIFactoryMgr.createCheckBoxMenuItem(this.actionTurnOnOffLogging);
		}
		return jMenuItemTurnOnOffLogging;
	}

	/**
	 * @return javax.swing.JCheckBoxMenuItem
	 */
	private javax.swing.JCheckBoxMenuItem getJMenuItemTurnOnOffReactions() {
		if(jMenuItemTurnOnOffReactions == null) {
			jMenuItemTurnOnOffReactions = UIFactoryMgr.createCheckBoxMenuItem(this.actionTurnOnOffReactions);
		}
		return jMenuItemTurnOnOffReactions;
	}

	/**
     * @return
     */
//    private JMenuItem getJMenuItemFilterOutUnselectedItems() {
//        if(jMenuItemFilterOutUnselectedItems == null) {
//		    jMenuItemFilterOutUnselectedItems = UIFactoryMgr.createMenuItem(this.actionFilterOutUnselectedItems);
//		}
//		return jMenuItemFilterOutUnselectedItems;
//    }

	/**
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButtonSaveSession() {
		if(jButtonSaveSession == null) {
			jButtonSaveSession = UIFactoryMgr.createButton(this.actionSaveSession);
			jButtonSaveSession.setText(null);
			jButtonSaveSession.setMnemonic(KeyEvent.VK_UNDEFINED);
		}
		return jButtonSaveSession;
	}

	/**
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButtonStartSession() {
		if(jButtonStartSession == null) {
			jButtonStartSession = UIFactoryMgr.createButton(this.actionStartSession);
			jButtonStartSession.setText(null);
			jButtonStartSession.setMnemonic(KeyEvent.VK_UNDEFINED);
		}
		return jButtonStartSession;
	}

	/**
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButtonStopSession() {
		if(jButtonStopSession == null) {
			jButtonStopSession = UIFactoryMgr.createButton(this.actionStopSession);
			jButtonStopSession.setText(null);
			jButtonStopSession.setMnemonic(KeyEvent.VK_UNDEFINED);
		}
		return jButtonStopSession;
	}

	/**
	 * @return javax.swing.JToggleButton
	 */
	private javax.swing.JToggleButton getJButtonTurnOnOffLogging() {
		if(jButtonLogging == null) {
			jButtonLogging = UIFactoryMgr.createToggleButton(this.actionTurnOnOffLogging);
			jButtonLogging.setText(null);
			jButtonLogging.setMnemonic(KeyEvent.VK_UNDEFINED);
		}
		return jButtonLogging;
	}

	/**
	 * @return javax.swing.JToggleButton
	 */
	private javax.swing.JToggleButton getJButtonTurnOnOffReactions() {
		if(jButtonReactions == null) {
			jButtonReactions = UIFactoryMgr.createToggleButton(this.actionTurnOnOffReactions);
			jButtonReactions.setText(null);
			jButtonReactions.setMnemonic(KeyEvent.VK_UNDEFINED);
			jButtonReactions.setSelected(!ConfigurationMgr.getBoolean(ReactionsComponent.NAME, ReactionsComponentConfigurationConstants.REACTIONS_ENABLED));
		}
		return jButtonReactions;
	}

	/**
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButtonShowReactionsLog() {
		if(jButtonShowReactionsLog == null) {
			jButtonShowReactionsLog = UIFactoryMgr.createButton(this.actionViewReactionsLog);
			jButtonShowReactionsLog.setText(null);
			jButtonShowReactionsLog.setMnemonic(KeyEvent.VK_UNDEFINED);
		}
		return jButtonShowReactionsLog;
	}

	/**
	 * Handles the save session event.
	 * @param asynch
	 * @param saveAs
	 */
	private void handleSaveMonitoringSession(final boolean asynch, final boolean saveAs) {
		try {
			this.scheme = new MonitoringSessionDescriptor(
				this.scheme.getName(),
				this.scheme.getLocation());
			this.viewContainer.getAppWorker().runJobSynch(new UIWorkerJobDefault(
					viewContainer.getAppFrame(),
					Cursor.WAIT_CURSOR,
					MessageRepository.get(
						Msg.TEXT_SAVING_SESSION)) {
				public void work() throws Exception {
					// fill
				    fillMonitoringScheme(scheme);
					// and save
				    schemeRepository.saveSession(scheme, asynch, saveAs);
					String name = scheme.getName();
					if(name != null) {
						viewContainer.appendToAppFrameTitle(name);
						sessionModel.setSessionName(name);
					}
					viewContainer.setSessionDirty(false);
				}
				public void finished(Throwable ex) {
				}
			});
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Handles the add hosts event.
	 */
	private void handleAddHosts() {
		try {
			// check the number of hosts against the license
//			int max = LicenseMgr.getLicense().getUnits();
//			if(max > 0 && max <= sessionModel.getAllHosts().size()) {
//				throw new LicenseLimitReachedHosts();
//			}

			String inputValue = UIUtils.getStringInput(
					this.viewContainer.getAppFrame(),
					MessageRepository.get(Msg.TEXT_INPUTTITLE_ADDINGHOSTSTOMONITOR),
					MessageRepository.get(Msg.TEXT_SPECIFYHOSTSTOMONITOR));
			if(inputValue == null || inputValue.trim().length() == 0) {
				return;
			}
			StringTokenizer st = new StringTokenizer(inputValue);
			final Set<String> hosts = new HashSet<String>();
			String host;
			while(st.hasMoreTokens()) {
				host = st.nextToken();
				hosts.add(host);
			}

			// register first in the model so that
			// the host entries will be available for the
			// event handler (events generated by the
			// host monitor)
			for(Iterator<String> iter = hosts.iterator(); iter.hasNext();) {
				host = iter.next();
				sessionModel.addHost(host);
			}

			// this can be a little bit expensive
			this.viewContainer.getAppWorker().runJob(new UIWorkerJobDefault(
					viewContainer.getAppFrame(),
					Cursor.WAIT_CURSOR,
					MessageRepository.get(Msg.TEXT_ADDING_HOST)) {
				public void work() throws Exception {
					rmsHostMonitor.addHosts(hosts, false);
				}
				public void finished(Throwable ex) {
				}
			});

		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Handles the remove hosts event.
	 */
	@SuppressWarnings("unchecked")
	private void handleRemoveHosts() {
		try {
			Set<HostNode> nodes = getSelectedHostNodes();
			int size = nodes.size();
			if(size > 0) {
				List<String> hosts = new ArrayList<String>(size);
				for(Iterator<HostNode> iter = nodes.iterator(); iter.hasNext();) {
					HostNode node = iter.next();
					String host = node.getHostInfo().getName();
					hosts.add(host);
					// check if the host has active agents
					Enumeration<AgentNode> e = node.children();
					while(e.hasMoreElements()) {
						AgentNode an = e.nextElement();
						this.rmsMonitoringSession.deactivateAgent(
								host,
								an.getAgentInfo().getDeploymentDtls().getAgentId());
						this.sessionModel.removeAgent(an);
					}
					this.sessionModel.removeHost(node);
				}
				this.rmsHostMonitor.removeHosts(hosts);
			}
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Handles the add agents event.
	 */
	private void handleAddAgents() {
		try {
			Set<HostNode> selectedNodes = getSelectedHostNodes();
			if(selectedNodes == null || selectedNodes.size() == 0) {
				return;
			}
			final List<String> hosts = new ArrayList<String>(selectedNodes.size());
			HostNode node;
			for(Iterator<HostNode> iter = selectedNodes.iterator(); iter.hasNext();) {
				node = iter.next();
				hosts.add(node.getHostInfo().getName());
			}
			// it takes a few moments to build the activation dialog...
			this.viewContainer.getAppWorker().runJobSynch(new UIWorkerJobDefault(
					viewContainer.getAppFrame(),
					Cursor.WAIT_CURSOR,
					"") {
				public void work() throws Exception {
					fResult = new AgentActivatorDialog(
							hosts, viewContainer, rmsControlConfig,
							rmsMonitoringSession, rmsAgentRepository,
							rmsProviderInstanceRepository,
							sessionModel);
				}
				public void finished(Throwable ex) {
					if(ex == null){
						UIUtils.centerDialogAndShow(
								viewContainer.getAppFrame(), (AgentActivatorDialog)this.fResult);
					}
				}
			});
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Handles the remove agents event.
	 */
	@SuppressWarnings("unchecked")
	private void handleRemoveAgents() {
		try {
			Set<HostNode> selectedNodes = getSelectedHostNodes();
			if(selectedNodes != null && selectedNodes.size() > 0) {
				for(Iterator<HostNode> iter = selectedNodes.iterator(); iter.hasNext();) {
					HostNode hn = iter.next();
					Enumeration<AgentNode> e = hn.agents();
					while(e.hasMoreElements()) {
						final AgentNode an = e.nextElement();
                        try {
    						final String host = hn.getHostInfo().getName();
    						final AgentId agentId = an.getAgentInfo().getDeploymentDtls().getAgentId();
    						this.viewContainer.getAppWorker().runJob(new UIWorkerJobDefault(
    								viewContainer.getAppFrame(),
    								Cursor.WAIT_CURSOR,
    								MessageRepository.get(Msg.TEXT_REMOVING_AGENT, an.getAgentInfo().getTranslatedName())) {
    							public void work() throws Exception {
    								rmsMonitoringSession.deactivateAgent(host, agentId);
    							}
    							public void finished(Throwable ex) {
    								sessionModel.removeAgent(an);
    							}
    						});
                        }catch(Throwable t) {
                            UIExceptionMgr.userException(t);
                        }
					}
				}
			}
			// now check for individual selected agents
			SessionNode root = (SessionNode)sessionModel.getRoot();
			Enumeration<HostNode> e = root.children();
			while(e.hasMoreElements()) {
				HostNode hn = e.nextElement();
				Set<AgentNode> sa = getSelectedAgentsForHost(hn);
				for(Iterator<AgentNode> iter = sa.iterator(); iter.hasNext();) {
					final AgentNode an = iter.next();
					try {
						final String host = hn.getHostInfo().getName();
						final AgentId agentId = an.getAgentInfo().getDeploymentDtls().getAgentId();
						this.viewContainer.getAppWorker().runJob(new UIWorkerJobDefault(
								viewContainer.getAppFrame(),
								Cursor.WAIT_CURSOR,
								MessageRepository.get(Msg.TEXT_REMOVING_AGENT, an.getAgentInfo().getTranslatedName())) {
							public void work() throws Exception {
		                        rmsMonitoringSession.deactivateAgent(host, agentId);
							}
							public void finished(Throwable ex) {
								sessionModel.removeAgent(an);
								if(ex != null) {
									UIExceptionMgr.userException(ex);
								}
							}
						});
                    }catch(Throwable t) {
                        UIExceptionMgr.userException(t);
                    }
				}
			}
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Handles the show popup on the hosts tree.
	 * @param e
	 */
	private void handleShowPopupOnSessionTree(MouseEvent e) {
		try {
			getJMenuItemAddHosts().setVisible(false);
			getJMenuItemRemoveAgents().setVisible(false);
			getJMenuItemRemoveHosts().setVisible(false);
			getJMenuItemAddAgents().setVisible(false);
			getJMenuItemStartAgent().setVisible(false);
			getJMenuItemStopAgent().setVisible(false);
			getJMenuItemViewAgentError().setVisible(false);
			getJMenuItemRefreshNode().setVisible(false);
			getJMenuItemRefreshEntityNodeWithSchedule().setVisible(false);
			getJMenuItemEnableAllCounters().setVisible(false);
			getJMenuItemEnableAllCountersRecursively().setVisible(false);
			getJMenuItemDisableAllCounters().setVisible(false);
			getJMenuItemDisableAllCountersRecursively().setVisible(false);
			JTree tree = getLeftPanel().getSessionTree();
			int x = e.getX();
			int y = e.getY();
			TreePath path = tree.getSelectionPath();
			if(path != null) {
				DefaultMutableTreeNode sel =
					(DefaultMutableTreeNode)path.getLastPathComponent();
				if(sel != null) {
					if(sel instanceof SessionNode) {
						getJMenuItemAddHosts().setVisible(true);
					} else if(sel instanceof HostNode) {
						getJMenuItemRemoveHosts().setVisible(true);
						getJMenuItemAddAgents().setVisible(true);
						if(sel.getChildCount() > 0) {
							getJMenuItemRemoveAgents().setVisible(true);
						}
					} else if(sel instanceof AgentNode) {
						AgentNode an = (AgentNode)sel;
						if(an.getAgentInfo().getAgentState() != AgentState.STARTED) {
							getJMenuItemStartAgent().setVisible(true);
						} else {
							getJMenuItemStopAgent().setVisible(true);
						}
						getJMenuItemDisableAllCounters().setVisible(true);
						getJMenuItemRemoveAgents().setVisible(true);
						if(an.getAgentInfo().getAgentState() == AgentState.ERROR
								|| an.getAgentInfo().getErrorStateException() != null) {
							getJMenuItemViewAgentError().setVisible(true);
						}
						if(an.getAgentInfo().getDeploymentDtls().getDescriptor()
								.safeToRefreshRecursivelly()) {
	                        getJMenuItemRefreshNode().setVisible(true);
							getJMenuItemRefreshEntityNodeWithSchedule().setVisible(true);
							getJMenuItemEnableAllCounters().setVisible(true);
						}
					} else if(sel instanceof EntityNode) {
						EntityNode en = (EntityNode)sel;
						getJMenuItemDisableAllCounters().setVisible(true);
						getJMenuItemDisableAllCountersRecursively().setVisible(true);
						getJMenuItemEnableAllCounters().setVisible(true);
						if(en.getEntityInfo().safeToRefreshRecursivelly()) {
							getJMenuItemRefreshNode().setVisible(true);
							getJMenuItemRefreshEntityNodeWithSchedule().setVisible(true);
							getJMenuItemEnableAllCountersRecursively().setVisible(true);
						}
					}
				}
			} else {
				// nothing selected
				getJMenuItemAddHosts().setVisible(true);
			}
			getJPopupMenu().show(e.getComponent(), x, y);
		} catch(Exception ex) {
			UIExceptionMgr.userException(ex);
		}
	}

	/**
	 * Turns on/off logging.
	 */
	private void handleTurnOnOffLogging(ActionEvent ev) {
		AbstractButton ab = (AbstractButton)ev.getSource();
		boolean selected = ab.isSelected();
		try {
			if(selected) {
				LogChooser logChooser = new LogChooserImpl(
				        viewContainer);
				LogRepositoryInfo log = logChooser.getLogInfoForWrite();
				if(log == null) {
					// set to the initial state
					getJButtonTurnOnOffLogging().setSelected(!selected);
					getJMenuItemTurnOnOffLogging().setSelected(!selected);
				    return;
				}
				MonitoringSessionDescriptor sch = new MonitoringSessionDescriptor("log_" + new Date());
				fillMonitoringSchemeForLog(sch);
				this.rmsDataLogService.startLogging(log, sch, this.eventHandler);
				getJButtonTurnOnOffLogging().setSelected(true);
				getJMenuItemTurnOnOffLogging().setSelected(true);
				this.viewContainer.getAppStatusBar().setStateMessage(
					MessageRepository.get(Msg.TEXT_LOGGINGON, new String[]{log.getRepositoryName()}));
			} else {
				this.viewContainer.getAppWorker().runJobSynch(new UIWorkerJobDefault(
						viewContainer.getAppFrame(),
						Cursor.WAIT_CURSOR,
						MessageRepository.get(Msg.TEXT_CLOSING_LOG)) {
					public void work() throws Exception {
						rmsDataLogService.stopLogging();
					}
					public void finished(Throwable ex) {
					}
				});
				getJButtonTurnOnOffLogging().setSelected(false);
				getJMenuItemTurnOnOffLogging().setSelected(false);
				this.viewContainer.getAppStatusBar().setStateMessage(null);
			}
		} catch(Exception e) {
			// if an exception occured, set to the initial state
			getJButtonTurnOnOffLogging().setSelected(!selected);
			getJMenuItemTurnOnOffLogging().setSelected(!selected);
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Finishes the logging if logging is in progress
	 */
	private void finishLogging() {
		if(isLoggingInProgress()) {
			// logging in progress
			final MonitoringSessionDescriptor sch = new MonitoringSessionDescriptor("log_" + new Date());
			fillMonitoringSchemeForLog(sch);
			this.viewContainer.getAppWorker().runJobSynch(new UIWorkerJobDefault(
					viewContainer.getAppFrame(),
					Cursor.WAIT_CURSOR,
					MessageRepository.get(Msg.TEXT_CLOSING_LOG)) {
				public void work() throws Exception {
					rmsDataLogService.stopLogging();
				}
				public void finished(Throwable ex) {
				}
			});
			getJButtonTurnOnOffLogging().setSelected(false);
			getJMenuItemTurnOnOffLogging().setSelected(false);
			this.viewContainer.getAppStatusBar().setStateMessage(null);
		}
	}

	/**
	 * Handles the tree expanded event.
	 * @param ev
	 */
	private void handleTreeExpanded(final TreeExpansionEvent ev) {
		try {
			TreePath sp = ev.getPath();
			if(sp == null) {
				return;
			}
			Object o = sp.getLastPathComponent();
			treeExplorer.expandNode(this.viewContainer.getAppFrame(),
			        sessionModel, (SessionModelTreeNode)o);
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Handles the tree selection events.
	 * @param ev
	 */
	private void handleSessionTreeSelectionEvent(TreeSelectionEvent ev) {
		try {
			getLeftPanel().setBottomComponent(null);
			TreePath sp = ev.getPath();
			if(sp == null) {
				return;
			}
			Object o = sp.getLastPathComponent();
			if(o instanceof HostNode) {
				HostNode hn = (HostNode)o;
				ResourceId context = hn.getResourceId();
				HostInformation dtls = hn.getHostInfo().getInfo();
				this.hostOperationsPanel.getPanelDetails().setHostInfo(dtls);
				hostOperationsPanel.getPanelViews().setDataViews(
						context,
						hn.getHostInfo().getDataViewInfo());
				hostOperationsPanel.getPanelDashboards().setDashboards(
						context,
						hn.getHostInfo().getDashboardInfo());
				getLeftPanel().setBottomComponent(this.hostOperationsPanel);
			} else if(o instanceof AgentNode) {
			    AgentNode an = (AgentNode)o;
			    // when an agent node is removed a selection event is triggered
			    // the following is a workaround...
			    if(an.getParent() == null) {
			       return;
			    }
			    // ---

				this.agentOperationsPanel.getPanelConfig().setAgentConfiguration(an);
				ResourceId context = an.getResourceId();
				// set queries if needed
				agentOperationsPanel.getPanelViews().setDataViews(
						context,
						an.getAgentInfo().getDataViewInfo());
				agentOperationsPanel.getPanelDashboards().setDashboards(
						context,
						an.getAgentInfo().getDashboardInfo());
				agentOperationsPanel.getPanelProviderInstances().setProviderInstances(context,
						an.getAgentInfo().getProviderInstances());
				getLeftPanel().setBottomComponent(this.agentOperationsPanel);
			} else if(o instanceof EntityNode){
				EntityNode en = (EntityNode)o;
			    // when an entity node is removed a selection event is triggered
			    // the following is a workaround...
				if(en.getParent() == null) {
					return;
				}
				// ---
				ResourceId context = en.getResourceId();
				entityOperationsPanel.getPanelConfig().setConfiguration(en);
				entityOperationsPanel.getPanelViews().setDataViews(
						context,
						en.getEntityInfo().getDataViewInfo());
				entityOperationsPanel.getPanelDashboards().setDashboards(
						context,
						en.getEntityInfo().getDashboardInfo());
				getLeftPanel().setBottomComponent(this.entityOperationsPanel);
			} else if(o instanceof SessionNode) {
				SessionNode sn = (SessionNode)o;
				ResourceId context = sn.getResourceId();
				schemeOperationsPanel.getPanelViews().setDataViews(
						context,
						sn.getSessionInfo().getDataViewInfo());
				schemeOperationsPanel.getPanelDashboards().setDashboards(
						context,
						sn.getSessionInfo().getDashboardInfo());
				getLeftPanel().setBottomComponent(this.schemeOperationsPanel);
			}
		} catch(Exception e) {
		    // when an entity node is removed a selection event is triggered
			// that referes a node which has already been removed from the tree so
			//just log to file for the moment
			UIExceptionMgr.exception(e);
		}
	}

	/**
	 * Handles the start session event.
	 */
	private void handleStartSession() {
		try {
			this.viewContainer.getAppWorker().runJob(new UIWorkerJobDefault(
					viewContainer.getAppFrame(),
					Cursor.WAIT_CURSOR,
					MessageRepository.get(Msg.TEXT_STARTINGSESSION)) {
				public void work() throws Exception {
					rmsMonitoringSession.startAllAgents();
				}
				public void finished(Throwable ex) {
					if(ex == null) {
						actionStopSession.setEnabled(true);
						actionStartSession.setEnabled(false);
					}
				}
			});
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Handles the stop session event.
	 */
	private void handleStopSession() {
		try {
			this.viewContainer.getAppWorker().runJob(new UIWorkerJobDefault(
					viewContainer.getAppFrame(),
					Cursor.WAIT_CURSOR,
					MessageRepository.get(Msg.TEXT_STOPPINGSESSION)) {
				public void work() throws Exception {
					rmsMonitoringSession.stopAllAgents();
				}
				public void finished(Throwable ex) {
					if(ex == null) {
						actionStopSession.setEnabled(false);
						actionStartSession.setEnabled(true);
					}
				}
			});
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Handles mouse pressed event on the hosts tree.
	 */
	private void handleMousePressedOnSessionTree(MouseEvent e) {
/*		JTree tree = getLeftPanel().getJTreeHosts();
		int selRow = tree.getRowForLocation(e.getX(), e.getY());
		if(selRow == -1) {
			// if the mouse was pressed in the white space
			// of the tree deselect all tree items
			tree.getSelectionModel().clearSelection();
		}
*/	}

	/**
	 * @return the selected host nodes
	 */
	private Set<HostNode> getSelectedHostNodes() {
		JTree tree = getLeftPanel().getSessionTree();
		TreePath[] paths = tree.getSelectionPaths();
		Set<HostNode> hosts = new HashSet<HostNode>();
		TreePath path;
		HostNode hn;
		for(int i = 0; i < paths.length; i++) {
			path = paths[i];
			hn = (HostNode)path.getPathComponent(1);
			if(tree.isPathSelected(new TreePath(hn.getPath()))) {
				hosts.add(hn);
			}
		}
		return hosts;
	}

	/**
	 * @param hn
	 * @return the set of selected agent nodes for the given
	 * host node
	 */
	private Set<AgentNode> getSelectedAgentsForHost(HostNode hn) {
		JTree tree = getLeftPanel().getSessionTree();
		Set<AgentNode> agents = new HashSet<AgentNode>();
		Enumeration<AgentNode> e = hn.agents();
		AgentNode an;
		while(e.hasMoreElements()) {
			an = e.nextElement();
			if(tree.isPathSelected(new TreePath(an.getPath()))) {
				agents.add(an);
			}
		}
		return agents;
	}

	/**
	 * Invoked after a control has been removed from a view board
     * @param control
     */
    private void handleControlRemoved(DataViewControl control) {
        try {
            // TODO update the plotted flag for all elements

        } catch(Exception e) {
            UIExceptionMgr.userException(e);
        }
    }

	/**
	 * Starts the selected agents.
	 */
	@SuppressWarnings("unchecked")
	private void handleStartAgents() {
        try {
			// check for individual selected agents
			SessionNode root = (SessionNode)this.sessionModel.getRoot();
			Enumeration<HostNode> e = root.children();
			HostNode hn;
			AgentNode an;
			while(e.hasMoreElements()) {
				hn = (HostNode)e.nextElement();
				Set<AgentNode> sa = getSelectedAgentsForHost(hn);
				for(Iterator<AgentNode> iter = sa.iterator(); iter.hasNext();) {
					an = iter.next();
					this.rmsMonitoringSession.startAgent(
						hn.getHostInfo().getName(),
						an.getAgentInfo().getDeploymentDtls().getAgentId());
				}
			}
        } catch(Exception e) {
            UIExceptionMgr.userException(e);
        }
	}

	/**
	 * Stops the selected agents.
	 */
	@SuppressWarnings("unchecked")
	private void handleStopAgents() {
        try {
			// check for individual selected agents
			SessionNode root = (SessionNode)this.sessionModel.getRoot();
			Enumeration<HostNode> e = root.children();
			HostNode hn;
			AgentNode an;
			while(e.hasMoreElements()) {
				hn = (HostNode)e.nextElement();
				Set<AgentNode> sa = getSelectedAgentsForHost(hn);
				for(Iterator<AgentNode> iter = sa.iterator(); iter.hasNext();) {
					an = iter.next();
					this.rmsMonitoringSession.stopAgent(
							hn.getHostInfo().getName(),
							an.getAgentInfo().getDeploymentDtls().getAgentId());
				}
			}
        } catch(Exception e) {
            UIExceptionMgr.userException(e);
        }
	}

	/**
	 * Displays the error for the selected agent(the agent must be in ERROR state).
	 */
	private void handleViewAgentError() {
        try {
    		TreeNode node = getSelectedNode();
    		if(node == null || !(node instanceof AgentNode)) {
    			return;
    		}
    		AgentNode an = (AgentNode)node;
    		Throwable t = an.getAgentInfo().getErrorStateException();
    		if(t == null) {
    			return;
    		}
    		ShowException.show(this.viewContainer.getAppFrame(), t);
        } catch(Exception e) {
            UIExceptionMgr.userException(e);
        }
	}

	/**
	 * @param event
	 */
	private void handleReactionLogEvent(ReactionLogEvent event) {
		try {
			reactionAlarm.setReactionLogEvent(event);
		} catch(Exception e) {
			UIExceptionMgr.exception(e);
		}
	}

	/**
	 * @param t
	 */
	private void handleFatalLoggingError(Throwable t) {
        try {
        	// logging is stopped so update the UI
			getJButtonTurnOnOffLogging().setSelected(false);
			getJMenuItemTurnOnOffLogging().setSelected(false);
			this.viewContainer.getAppStatusBar().setStateMessage(null);
    		ShowException.show(this.viewContainer.getAppFrame(),
    				new LoggingStoppedDueToFatalError(t));
        } catch(Exception e) {
            UIExceptionMgr.userException(e);
        }
	}

	/**
	 * Refreshes the selected node; if the node is an agent node it will refresh it's imediate entities,
	 * if the node is an entity node it will refresh it's children and itself.
	 */
	private void handleRefreshOnTreeNode() {
        try {
			this.viewContainer.getAppWorker().runJobSynch(new UIWorkerJobDefault(
					viewContainer.getAppFrame(),
					Cursor.WAIT_CURSOR,
					MessageRepository.get(Msg.TEXT_REFRESHING_VIEW)) {
				public void work() throws Exception {
		    		TreeNode node = getSelectedNode();
		    		if(node == null) {
		    			return;
		    		}
		    		if(node instanceof EntityNode) {
		    			node = sessionModel.findParentNodeAbleToRefreshSubtree((EntityNode) node);
		    		}
		    		refreshViewRecursively(node);
				}
				public void finished(Throwable ex) {
				}
				});
        } catch(Exception e) {
            UIExceptionMgr.userException(e);
        }
	}

	/**
	 * Refreshes the selected node; if the node is an agent node it will refresh it's imediate entities,
	 * if the node is an entity node it will refresh it's children and itself.
	 */
	private void handleRefreshWithScheduleOnNode() {
        try {
    		TreeNode node = getSelectedNode();
    		if(node == null) {
    			return;
    		}
    		AgentNode agentNode = null;
    		if(node instanceof AgentNode) {
    			agentNode = (AgentNode)node;
    		}
    		EntityNode intendedEntityNode = null;
    		EntityNode entityNode = null;
			if(node instanceof EntityNode) {
	    		intendedEntityNode = (EntityNode)node;
				node = sessionModel.findParentNodeAbleToRefreshSubtree((EntityNode)node);
				if(node instanceof AgentNode) {
					agentNode = (AgentNode)node;
				} else {
					entityNode = (EntityNode)node;
				}
			}
    		Integer initialInterval = null;
    		if(agentNode != null) {
    			initialInterval = agentNode.getAgentInfo().getDeploymentDtls().getConfiguration().getRefreshInterval();
    		} else if(entityNode != null) {
    			initialInterval = entityNode.getEntityInfo().getConfiguration().getRefreshInterval();
    		}
        	// display schedule dialog and get interval
        	RefreshScheduleDialog dlg = new RefreshScheduleDialog(viewContainer.getAppFrame(), initialInterval);
        	UIUtils.centerDialogAndShow(viewContainer.getAppFrame(), dlg);
        	final Integer interval = dlg.getResult();
        	if(interval == null) {
        		return;
        	}

			if(entityNode != null) {
				final String host = entityNode.getAgentNode().getHostNode().getHostInfo().getName();
				final AgentId agentid = entityNode.getAgentNode().getAgentInfo().getDeploymentDtls().getAgentId();
				final EntityId entityid = entityNode.getEntityInfo().getId();
				final EntityNode finalEntityNode = entityNode;
				this.viewContainer.getAppWorker().runJob(new UIWorkerJobDefault(
						viewContainer.getAppFrame(),
						Cursor.WAIT_CURSOR,
						MessageRepository.get(Msg.TEXT_APPLYINGCHANGES_ENTITYCONFIGURATION)) {
					public void work() throws Exception {
			    		EntityConfiguration econf = new EntityConfiguration();
			    		econf.setRefreshInterval(interval);
			    		fResult = rmsMonitoringSession.configureEntity(
			    				host,
			    				agentid,
			    				entityid, econf);
					}
					public void finished(Throwable ex) {
						if(fResult !=null) {
							sessionModel.updateEntities(host, agentid, (EntityDescriptorTree)fResult);
							sessionModel.setDirtyEntity(finalEntityNode, true);
						}
					}
					});
			}
			if(agentNode != null) {
				final String host = agentNode.getHostNode().getHostInfo().getName();
				final AgentId agentid = agentNode.getAgentInfo().getDeploymentDtls().getAgentId();
				this.viewContainer.getAppWorker().runJob(new UIWorkerJobDefault(
						viewContainer.getAppFrame(),
						Cursor.WAIT_CURSOR,
						MessageRepository.get(Msg.TEXT_APPLYINGCHANGES_AGENTCONFIGURATION)) {
					public void work() throws Exception {
			    		AgentConfiguration aconf = new AgentConfiguration();
			    		aconf.setRefreshInterval(interval);
			    		fResult = rmsMonitoringSession.configureAgent(
			    				host,
			    				agentid,
			    				aconf);
					}
					public void finished(Throwable ex) {
						if(ex != null) {
							UIExceptionMgr.userException(ex);
						}
						if(fResult !=null) {
							sessionModel.updateEntities(host, agentid, ((AgentConfigurationTuple)fResult).getEntities());
						}
					}
					});
			}
			if(intendedEntityNode != null) {
				final String host = intendedEntityNode.getAgentNode().getHostNode().getHostInfo().getName();
				final AgentId agentid = intendedEntityNode.getAgentNode().getAgentInfo().getDeploymentDtls().getAgentId();
				final EntityId entityid = intendedEntityNode.getEntityInfo().getId();
				final EntityNode finalEntityNode = intendedEntityNode;
				this.viewContainer.getAppWorker().runJob(new UIWorkerJobDefault(
						viewContainer.getAppFrame(),
						Cursor.WAIT_CURSOR,
						MessageRepository.get(Msg.TEXT_APPLYINGCHANGES_ENTITYCONFIGURATION)) {
					public void work() throws Exception {
			    		EntityConfiguration econf = new EntityConfiguration();
			    		econf.setRefreshInterval(interval);
			    		fResult = rmsMonitoringSession.configureEntity(
			    				host,
			    				agentid,
			    				entityid, econf);
					}
					public void finished(Throwable ex) {
						if(ex != null) {
							UIExceptionMgr.userException(ex);
						}
						if(fResult !=null) {
							sessionModel.updateEntities(host, agentid, (EntityDescriptorTree)fResult);
							sessionModel.setDirtyEntity(finalEntityNode, true);
						}
					}
					});
			}
        } catch(Exception e) {
            UIExceptionMgr.userException(e);
        }
	}

	/**
	 * Refreshes agent and entity nodes recursively.
	 * @param node
	 * @throws InvalidAgentState
	 * @throws RMSException
	 * @throws RemoteException
	 */
	private void refreshViewRecursively(TreeNode node) throws InvalidAgentState, RMSException, RemoteException {
		if(node instanceof EntityNode) {
			EntityNode en = (EntityNode)node;
			AgentId agentId = en.getAgentNode().getAgentInfo().getDeploymentDtls().getAgentId();
			String host = en.getAgentNode().getHostNode().getHostInfo().getName();
			// update children
			EntityDescriptorTree children = rmsMonitoringSession.getAgentEntities(host, agentId, en.getEntityInfo().getId(), true, true);
			sessionModel.updateEntities(host, agentId, children);
		} else if(node instanceof AgentNode) {
			AgentNode an = (AgentNode)node;
			AgentId agentId = an.getAgentInfo().getDeploymentDtls().getAgentId();
			String host = an.getHostNode().getHostInfo().getName();
			// update children of root
			EntityDescriptorTree children = rmsMonitoringSession.getAgentEntities(host, agentId, null, true, true);
			sessionModel.updateEntities(host, agentId, children);
		}
	}

	/**
	 * @return the first selected node
	 */
	private TreeNode getSelectedNode() {
		JTree tree = getLeftPanel().getSessionTree();
		TreePath path = tree.getSelectionPath();
		if(path == null) {
			return null;
		}
		return (TreeNode)path.getLastPathComponent();
	}

    /**
	 * Enables/disables all counters for an agent or entity.
	 * @param enabled
	 * @param recursive
	 */
	private void handleSetEnabledAllCounters(final boolean enabled, final boolean recursive) {
		try {
    		final TreeNode node = getSelectedNode();
    		if(node == null) {
    			return;
    		}
    		String message = "";
    		if(enabled) {
    			message = MessageRepository.get(Msg.TEXT_ENABLING_COUNTERS);
    		} else {
    			message = MessageRepository.get(Msg.TEXT_DISABLING_COUNTERS);
    		}
    		if(node instanceof EntityNode) {
				this.viewContainer.getAppWorker().runJobSynch(new UIWorkerJobDefault(
						viewContainer.getAppFrame(),
						Cursor.WAIT_CURSOR,
						message) {
					public void work() throws Exception {
						EntityNode en = (EntityNode)node;
						EntityConfiguration ec = new EntityConfiguration();
						ec.setRecursiveEnableAllCounters(recursive);
						ec.setEnableAllCounters(enabled);
						EntityDescriptorTree descriptors = rmsMonitoringSession.configureEntity(
								en.getAgentNode().getHostNode().getHostInfo().getName(),
								en.getAgentNode().getAgentInfo().getDeploymentDtls().getAgentId(),
								en.getEntityInfo().getId(), ec);
						// update the model
						sessionModel.updateEntities(
								en.getAgentNode().getHostNode().getHostInfo().getName(),
								en.getAgentNode().getAgentInfo().getDeploymentDtls().getAgentId(),
								descriptors);
						sessionModel.setDirtyEntity(en, true);
					}
					public void finished(Throwable ex) {
					}
					});
    		} else if(node instanceof AgentNode) {
				this.viewContainer.getAppWorker().runJobSynch(new UIWorkerJobDefault(
						viewContainer.getAppFrame(),
						Cursor.WAIT_CURSOR,
						message) {
					public void work() throws Exception {
						AgentNode an = (AgentNode)node;
						AgentConfiguration ac = new AgentConfiguration();
						ac.setEnableAllCounters(enabled);
						AgentConfigurationTuple tuple = rmsMonitoringSession.configureAgent(
								an.getHostNode().getHostInfo().getName(),
								an.getAgentInfo().getDeploymentDtls().getAgentId(),
								ac);
						sessionModel.setAgentConfigurationTuple(an, tuple);
					}
					public void finished(Throwable ex) {
					}
					});
    		}
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * @param ridChanged
	 * @param change
	 * @param changeType
	 */
	private void handleSessionModelChange(ResourceId[] ridChanged, int change, int changeType) {
		try {
			this.viewContainer.setSessionDirty(true);
			if(change == SessionModel.RoughListener.CHANGE_AGENTS
					&& changeType == SessionModel.RoughListener.CHANGE_TYPE_ADDED) {
				// make agent visible in the tree
				ResourceId rid = ridChanged[0];
				AgentNode an = (AgentNode)sessionModel.getNodeForResourceId(rid);
				JTree tree = getLeftPanel().getSessionTree();
				tree.scrollPathToVisible(new TreePath(an.getPath()));
				if(this.actionStopSession.isEnabled()) {
					// agent added, if the monitoring session is started
					// start this agent as well
					this.rmsMonitoringSession.startAgent(
							an.getHostNode().getHostInfo().getName(),
							an.getAgentInfo().getDeploymentDtls().getAgentId());
				}
			}
		} catch(Exception e) {
			UIExceptionMgr.exception(e);
		}
	}

	/**
	 * @return true if logging is in progress
	 */
	private boolean isLoggingInProgress() {
		return getJButtonTurnOnOffLogging().isSelected();
	}

	/**
	 * @param o
	 * @param arg
	 */
	private void handleUpdate(Observable o, Object arg) {
		try {
			ComponentConfiguration conf = ConfigurationMgr.get(ReactionsComponent.NAME);
			if(conf == o) {
				if(ReactionsComponentConfigurationConstants.REACTIONS_ENABLED.equals(arg)) {
					boolean disabled = !conf.getBoolean(ReactionsComponentConfigurationConstants.REACTIONS_ENABLED);
					getJButtonTurnOnOffReactions().setSelected(disabled);
					getJMenuItemTurnOnOffReactions().setSelected(disabled);
				}
			}
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

}
