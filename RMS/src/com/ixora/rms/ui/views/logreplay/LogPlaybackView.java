/*
 * Created on 23-Jun-2004
 */
package com.ixora.rms.ui.views.logreplay;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.ixora.common.MessageRepository;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.ui.jobs.UIWorkerJobDefault;
import com.ixora.common.ui.popup.PopupListener;
import com.ixora.common.utils.Utils;
import com.ixora.rms.EntityDescriptor;
import com.ixora.rms.HostId;
import com.ixora.rms.RMS;
import com.ixora.rms.ResourceId;
import com.ixora.rms.agents.AgentDescriptor;
import com.ixora.rms.agents.AgentId;
import com.ixora.rms.client.AgentInstanceData;
import com.ixora.rms.client.model.AgentNode;
import com.ixora.rms.client.model.EntityNode;
import com.ixora.rms.client.model.HostNode;
import com.ixora.rms.client.model.SessionNode;
import com.ixora.rms.client.session.MonitoringSessionDescriptor;
import com.ixora.rms.client.session.MonitoringSessionRealizer;
import com.ixora.rms.exception.AgentIsNotInstalled;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.logging.DataLogCompareAndReplayConfiguration;
import com.ixora.rms.logging.LogRepositoryInfo;
import com.ixora.rms.logging.TimeInterval;
import com.ixora.rms.repository.AgentInstallationData;
import com.ixora.rms.services.DataLogReplayService;
import com.ixora.rms.services.DataLogScanningService;
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
import com.ixora.rms.ui.dataviewboard.DataViewScreenDescriptor;
import com.ixora.rms.ui.dataviewboard.DataViewScreenPanel;
import com.ixora.rms.ui.exporter.HTMLGenerator;
import com.ixora.rms.ui.exporter.HTMLGenerator.Listener;
import com.ixora.rms.ui.messages.Msg;

/**
 * Log playback view.
 * @author Daniel Moraru
 */
public final class LogPlaybackView extends SessionView {
// internal actions
	private Action actionPlayLog;
	private Action actionPauseLog;
	private Action actionRewindLog;

// controls
	private javax.swing.JMenuItem jMenuItemPlayLog;
	private javax.swing.JButton jButtonPlayLog;
	private javax.swing.JMenuItem jMenuItemPauseLog;
	private javax.swing.JButton jButtonPauseLog;
	private javax.swing.JMenuItem jMenuItemRewindLog;
	private javax.swing.JButton jButtonRewindLog;
	private ReplaySpeedPanel replaySpeedPanel;
	/**
	 * Data log replayer.
	 */
	private DataLogReplayService rmsLogReplay;
	/**
	 * Panel for acting upon the selected host.
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
	 * Panel filling the left part of the view container
	 * hosting the session view.
	 */
	private SessionViewLeftPanel leftPanel;
	/**
	 * Event handler.
	 */
	private EventHandler eventHandler;
	/**
	 * Manages state/progress for log replay.
	 */
	private LogReplayProgressHandler logStateHandler;
	/** True if the log replay has finished or it was rewinded */
	private boolean canResetScreens;
	/**
	 * The replay configuration; it can be null.
	 */
	private DataLogCompareAndReplayConfiguration fReplayConfiguration;

	/**
	 * Event handler.
	 */
	private final class EventHandler extends PopupListener
				implements TreeSelectionListener,
					DataLogReplayService.ReadListener,
						DataLogScanningService.ScanListener, Listener {
		/**
		 * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
		 */
		public void valueChanged(TreeSelectionEvent ev) {
			handleSessionTreeSelectionEvent(ev);
		}
	    /**
		 * @see com.ixora.common.ui.popup.PopupListener#showPopup(java.awt.event.MouseEvent)
		 */
		protected void showPopup(MouseEvent e) {
			if(e.getSource() == LogPlaybackView.this.getLeftPanel().getSessionTree()) {
				handleShowPopupOnSessionTree(e);
				return;
			}
		}
        /**
         * @see com.ixora.rms.services.DataLogReplayService.ReadListener#finishedReadingLog(com.ixora.rms.logging.LogRepositoryInfo)
         */
        public void finishedReadingLog(LogRepositoryInfo rep) {
            SwingUtilities.invokeLater(new Runnable(){
                public void run() {
                    handleReachedEndOfLogForRead();
                }
            });
        }
		/**
		 * @see com.ixora.rms.services.DataLogScanningService.ScanListener#newEntity(com.ixora.rms.logging.LogRepositoryInfo, com.ixora.rms.HostId, com.ixora.rms.agents.AgentId, com.ixora.rms.EntityDescriptor)
		 */
		public void newEntity(LogRepositoryInfo rep, final HostId host, final AgentId aid, final EntityDescriptor ed) {
            SwingUtilities.invokeLater(new Runnable(){
                public void run() {
                	handleNewEntity(host, aid, ed);
                }
            });
		}
        /**
         * @see com.ixora.rms.services.DataLogScanningService.ScanListener#newAgent(com.ixora.rms.logging.LogRepositoryInfo, com.ixora.rms.HostId, com.ixora.rms.agents.AgentDescriptor)
         */
        public void newAgent(LogRepositoryInfo rep, final HostId host, final AgentDescriptor ad) {
            SwingUtilities.invokeLater(new Runnable(){
                public void run() {
                    handleNewAgent(host, ad);
                }
            });
        }
        /**
         * @see com.ixora.rms.services.DataLogReplayService.ReadListener#fatalReadError(com.ixora.rms.logging.LogRepositoryInfo, java.lang.Exception)
         */
        public void fatalReadError(LogRepositoryInfo rep, final Exception e) {
            SwingUtilities.invokeLater(new Runnable(){
                public void run() {
                    handleReplayFatalError(e);
                }
            });
        }
		/**
		 * @see com.ixora.rms.services.DataLogScanningService.ScanListener#fatalScanError(com.ixora.rms.logging.LogRepositoryInfo, java.lang.Exception)
		 */
		public void fatalScanError(LogRepositoryInfo rep, final Exception e) {
            SwingUtilities.invokeLater(new Runnable(){
                public void run() {
                    handleReplayFatalError(e);
                }
            });
		}
		/**
		 * @see com.ixora.rms.services.DataLogReplayService.ReadListener#readProgress(com.ixora.rms.logging.LogRepositoryInfo, long)
		 */
		public void readProgress(LogRepositoryInfo rep, final long time) {
            SwingUtilities.invokeLater(new Runnable(){
                public void run() {
                    handleReadProgress(time);
                }
            });
		}
		/**
		 * @see com.ixora.rms.services.DataLogScanningService.ScanListener#finishedScanningLog(com.ixora.rms.logging.LogRepositoryInfo, com.ixora.rms.logging.TimeInterval)
		 */
		public void finishedScanningLog(LogRepositoryInfo rep, final TimeInterval ti) {
            SwingUtilities.invokeLater(new Runnable(){
                public void run() {
                    handleReachedEndOfLogForScan(ti);
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
 	}

	/**
	 * Play log.
	 */
	private final class ActionPlayLog extends AbstractAction {
		private static final long serialVersionUID = -4371549752739304250L;
		public ActionPlayLog() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(
					Msg.ACTIONS_PLAY_LOG), this);
			ImageIcon icon = UIConfiguration.getIcon("play_log.gif");
			if(icon != null) {
				putValue(Action.SMALL_ICON, icon);
			}
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			handlePlayLog();
		}
	}

	/**
	 * Pause log.
	 */
	private final class ActionPauseLog extends AbstractAction {
		private static final long serialVersionUID = 5746018901819513216L;
		public ActionPauseLog() {
			super();
			enabled = false;
			UIUtils.setUsabilityDtls(MessageRepository.get(
					Msg.ACTIONS_PAUSE_LOG), this);
			ImageIcon icon = UIConfiguration.getIcon("pause_log.gif");
			if(icon != null) {
				putValue(Action.SMALL_ICON, icon);
			}
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			handlePauseLog();
		}
	}

	/*
	 * Pause log.
	 */
	private final class ActionRewindLog extends AbstractAction {
		private static final long serialVersionUID = 1258866305025332239L;
		public ActionRewindLog() {
			super();
			enabled = false;
			UIUtils.setUsabilityDtls(MessageRepository.get(
					Msg.ACTIONS_REWIND_LOG), this);
			ImageIcon icon = UIConfiguration.getIcon("rewind_log.gif");
			if(icon != null) {
				putValue(Action.SMALL_ICON, icon);
			}
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			handleRewindLog();
		}
	}

	/**
	 * @param vc
	 * @param replayService
	 * @param config
	 * @throws Throwable
	 */
	public LogPlaybackView(RMSViewContainer vc, DataLogReplayService replayService, 
			DataLogScanningService scanningService, 
			DataLogCompareAndReplayConfiguration config) throws Throwable {
		super(vc);
		this.fReplayConfiguration = config;
		this.actionPlayLog = new ActionPlayLog();
		this.actionPauseLog = new ActionPauseLog();
		this.actionRewindLog = new ActionRewindLog();
		this.rmsLogReplay = replayService;
		RMS.getDataEngine().setLogReplayMode(true);
		this.eventHandler = new EventHandler();
		this.rmsLogReplay.addReadListener(eventHandler);
		scanningService.addScanListener(eventHandler);

		initializeComponents();
	}

	/**
	 * @see com.ixora.rms.ui.RMSView#initialize()
	 */
	public void initialize() {
	    viewContainer.registerMenuItemsForActionsMenu(
				new JMenuItem[] {
						getJMenuItemPlayLog(),
						getJMenuItemPauseLog(),
						getJMenuItemRewindLog(),
						getJMenuItemToggleHTMLGen()
					});
	    viewContainer.registerToolBarComponents(
				new JComponent[] {
						getJButtonPlayLog(),
						getJButtonPauseLog(),
						getJButtonRewindLog(),
						getJButtonToggleHTMLGeneration(),
						replaySpeedPanel
					});

		registerComponentsWithViewContainer();

		viewContainer.registerLeftComponent(getLeftPanel());
		viewContainer.registerRightComponent(dataViewBoardHandler
		        .getScreen());

		if(fReplayConfiguration.getLogOne() == null) {
            this.actionPlayLog.setEnabled(false);
			return;
		}
		try {
		    MonitoringSessionDescriptor scheme = rmsLogReplay.getScheme();
	        MonitoringSessionRealizer schemeRealizer =
	            new MonitoringSessionRealizer(sessionModel);
            schemeRealizer.realize(rmsAgentRepository, scheme);
        	// load artefacts
        	sessionModel.loadArtefacts();
            // now create screens
			Collection<DataViewScreenDescriptor> screens = scheme.getDataViewScreens();
			if(!Utils.isEmptyCollection(screens)) {
                dataViewBoardHandler.initializeFromScreenDescriptors(screens);
			}
			viewContainer.appendToAppFrameTitle(scheme.getName());
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * @see com.ixora.rms.ui.RMSView#close()
	 */
	public boolean close() {
		try {
			// clear state message
			viewContainer.getAppStatusBar().setStateMessage(null);
			// stop html generator
			this.htmlGenerator.stop();
			// reset daata view boards
			this.dataViewBoardHandler.close();
			// unregister controls
			unregisterComponentsWithViewContainer();
			viewContainer.unregisterMenuItemsForActionsMenu(
				new JMenuItem[] {
					getJMenuItemPlayLog(),
					getJMenuItemPauseLog(),
					getJMenuItemRewindLog(),
					getJMenuItemToggleHTMLGen()
				});
			viewContainer.unregisterToolBarComponents(
				new JComponent[] {
					getJButtonPlayLog(),
					getJButtonPauseLog(),
					getJButtonRewindLog(),
					getJButtonToggleHTMLGeneration(),
					replaySpeedPanel
				});
			if(logStateHandler != null) {
				logStateHandler.cleanup();
			}
		} finally {
			// unregister listeners
			this.rmsLogReplay.removeReadListener(eventHandler);
			// shutdown services
			this.rmsLogReplay.shutdown();
		}
        return false;
	}

	/**
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButtonPlayLog() {
		if(jButtonPlayLog == null) {
			jButtonPlayLog = UIFactoryMgr.createButton(this.actionPlayLog);
			jButtonPlayLog.setText(null);
			jButtonPlayLog.setMnemonic(KeyEvent.VK_UNDEFINED);
		}
		return jButtonPlayLog;
	}

	/**
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getJMenuItemPlayLog() {
		if(jMenuItemPlayLog == null) {
			jMenuItemPlayLog = UIFactoryMgr.createMenuItem(this.actionPlayLog);
		}
		return jMenuItemPlayLog;
	}

	/**
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButtonPauseLog() {
		if(jButtonPauseLog == null) {
			jButtonPauseLog = UIFactoryMgr.createButton(this.actionPauseLog);
			jButtonPauseLog.setText(null);
			jButtonPauseLog.setMnemonic(KeyEvent.VK_UNDEFINED);
		}
		return jButtonPauseLog;
	}

	/**
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getJMenuItemPauseLog() {
		if(jMenuItemPauseLog == null) {
			jMenuItemPauseLog = UIFactoryMgr.createMenuItem(this.actionPauseLog);
		}
		return jMenuItemPauseLog;
	}

	/**
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButtonRewindLog() {
		if(jButtonRewindLog == null) {
			jButtonRewindLog = UIFactoryMgr.createButton(this.actionRewindLog);
			jButtonRewindLog.setText(null);
			jButtonRewindLog.setMnemonic(KeyEvent.VK_UNDEFINED);
		}
		return jButtonRewindLog;
	}

	/**
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getJMenuItemRewindLog() {
		if(jMenuItemRewindLog == null) {
			jMenuItemRewindLog = UIFactoryMgr.createMenuItem(this.actionRewindLog);
		}
		return jMenuItemRewindLog;
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
	 * This method initializes all the components.
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	private void initializeComponents() throws RMSException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		sessionModel = new SessionModelSwing(
		        rmsAgentRepository,
		        rmsDashboardRepository,
		        rmsDataViewRepository);
		sessionModel.setAsksAllowsChildren(false);
		dataViewBoardHandler = new DataViewBoardHandler(
				viewContainer,
				null,
				rmsDataEngineService,
				null,
				rmsDataViewBoardRepository,
				rmsDataViewRepository,
				sessionModel);
		screenPanel = new DataViewScreenPanel(dataViewBoardHandler);
		buttonNewViewBoardHandler = new ButtonNewViewBoardHandler(dataViewBoardHandler, rmsDataViewBoardRepository);
		actionShowHideLegendWindow = new ActionShowLegendWindow(viewContainer);

		replaySpeedPanel = new ReplaySpeedPanel(rmsLogReplay);

		dataViewBoardHandler.addObserver(this);

		this.htmlGenerator = new HTMLGenerator(this.viewContainer,
				dataViewBoardHandler, this.eventHandler);

		JTree tree = getLeftPanel().getSessionTree();
		tree.setModel(this.sessionModel);
		tree.setRootVisible(true);
		SessionModelTreeCellRenderer cellRenderer = new SessionModelTreeCellRenderer();
		tree.setCellRenderer(cellRenderer);
		tree.setEditable(false);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		tree.addMouseListener(this.eventHandler);
		tree.addTreeSelectionListener(this.eventHandler);

		LogReplayTreeExplorer treeExplorer = new LogReplayTreeExplorer();
		entityOperationsPanel = new EntityOperationsPanel(
				this.viewContainer,
				this.rmsDataEngineService,
				this.rmsDataViewBoardRepository,
				this.rmsDataViewRepository,
				this.rmsDashboardRepository,
				this.sessionModel,
				treeExplorer,
				this.dataViewBoardHandler,
				this.dataViewBoardHandler,
				this.dataViewBoardHandler);
		agentOperationsPanel = 	new AgentOperationsPanel(
				this.viewContainer,
				this.rmsDataEngineService,
				this.rmsDataViewBoardRepository,
				this.rmsDataViewRepository,
				this.rmsDashboardRepository,
				this.sessionModel,
				treeExplorer,
				this.dataViewBoardHandler,
				this.dataViewBoardHandler);
		hostOperationsPanel = new HostOperationsPanel(
				this.viewContainer,
				this.rmsDataEngineService,
				this.rmsDataViewBoardRepository,
				this.rmsDataViewRepository,
				this.rmsDashboardRepository,
				this.sessionModel,
				treeExplorer,
				this.dataViewBoardHandler,
				this.dataViewBoardHandler);
		schemeOperationsPanel = new SessionOperationsPanel(
				this.viewContainer,
				this.rmsDataEngineService,
				this.rmsDataViewBoardRepository,
				this.rmsDataViewRepository,
				this.rmsDashboardRepository,
				this.sessionModel,
				treeExplorer,
				this.dataViewBoardHandler,
				this.dataViewBoardHandler);
	}

	/**
	 * Plays the current log.
	 */
	private void handlePlayLog() {
		try {
			// reset controls first if not paused
			if(canResetScreens) {
				dataViewBoardHandler.reset();
			}
			viewContainer.getAppWorker().runJob(new UIWorkerJobDefault(
					viewContainer.getAppFrame(),
					Cursor.WAIT_CURSOR,
					MessageRepository.get(
						Msg.TEXT_STARTINGREPLAY)) {
				public void work() throws Exception {
				    rmsLogReplay.startReplay(fReplayConfiguration);
				}
				public void finished(Throwable ex) {
					if(ex == null) {
					    actionPlayLog.setEnabled(false);
						actionPauseLog.setEnabled(true);
						actionRewindLog.setEnabled(true);
					}
				}
			});
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Pause the current log.
	 */
	private void handlePauseLog() {
		try {
			canResetScreens = false;
			viewContainer.getAppWorker().runJob(new UIWorkerJobDefault(
					viewContainer.getAppFrame(),
					Cursor.WAIT_CURSOR,
					MessageRepository.get(
						Msg.TEXT_PAUSINGREPLAY)) {
				public void work() throws Exception {
				    rmsLogReplay.pauseReplay();
				}
				public void finished(Throwable ex) {
					if(ex == null) {
						actionPlayLog.setEnabled(true);
						actionPauseLog.setEnabled(false);
						actionRewindLog.setEnabled(true);
					}
				}
			});
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Rewinds the current log.
	 */
	private void handleRewindLog() {
		try {
			canResetScreens = true;
			logStateHandler.reset();
			viewContainer.getAppWorker().runJob(new UIWorkerJobDefault(
					viewContainer.getAppFrame(),
					Cursor.WAIT_CURSOR,
					MessageRepository.get(
						Msg.TEXT_REWINDINGLOG)) {
				public void work() throws Exception {
				    rmsLogReplay.stopReplay();
				}
				public void finished(Throwable ex) {
					if(ex == null) {
						actionPlayLog.setEnabled(true);
						actionPauseLog.setEnabled(false);
						actionRewindLog.setEnabled(false);
					}
				}
			});
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
				hostOperationsPanel.getPanelViews().setDataViews(
						context,
						hn.getHostInfo().getDataViewInfo());
				hostOperationsPanel.getPanelDashboards().setDashboards(
						context,
						hn.getHostInfo().getDashboardInfo());
				getLeftPanel().setBottomComponent(this.hostOperationsPanel);
			} else if(o instanceof AgentNode) {
				AgentNode an = (AgentNode)o;
				agentOperationsPanel.getPanelConfig().setAgentConfiguration(an);
				ResourceId context = an.getResourceId();
				agentOperationsPanel.getPanelViews().setDataViews(
						context,
						an.getAgentInfo().getDataViewInfo());
				agentOperationsPanel.getPanelDashboards().setDashboards(
						context,
						an.getAgentInfo().getDashboardInfo());
				getLeftPanel().setBottomComponent(this.agentOperationsPanel);
			} else if(o instanceof EntityNode){
				EntityNode en = (EntityNode)o;
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
			UIExceptionMgr.userException(e);
		}
	}

    /**
     * Handles end of log event.
     */
    private void handleReachedEndOfLogForRead() {
        try {
        	this.canResetScreens = true;
        	this.logStateHandler.finshed();
            this.actionPlayLog.setEnabled(false);
            this.actionPauseLog.setEnabled(false);
            this.actionRewindLog.setEnabled(true);
		} catch(Exception e) {
			UIExceptionMgr.exception(e);
		}
    }

    /**
     * Handles end of log event.
     * @param ti
     */
    private void handleReachedEndOfLogForScan(TimeInterval ti) {
        try {
			this.logStateHandler = new LogReplayProgressHandler(
					 viewContainer,
					 MessageRepository.get(
					           Msg.TEXT_LOADED_LOG,
					           new String[]{fReplayConfiguration.getLogOne().getLogRepository().getRepositoryName()}),
					fReplayConfiguration == null ? ti.getStart() : fReplayConfiguration.getLogOne().getTimeInterval().getStart(),
					fReplayConfiguration == null ? ti.getEnd() : fReplayConfiguration.getLogOne().getTimeInterval().getEnd());
			if(fReplayConfiguration != null) {
				handlePlayLog();
			}
		} catch(Exception e) {
			UIExceptionMgr.exception(e);
		}
    }

    /**
     * Handles log read progress.
     * @param time
     */
	private void handleReadProgress(long time) {
        try {
        	this.logStateHandler.setProgress(time);
        } catch(Exception e) {
			UIExceptionMgr.exception(e);
		}
	}

    /**
     * @param e
     */
    private void handleReplayFatalError(Exception e) {
        try {
        	this.canResetScreens = true;
            this.actionPlayLog.setEnabled(false);
            this.actionPauseLog.setEnabled(false);
            this.actionRewindLog.setEnabled(true);
            UIExceptionMgr.userException(e);
		} catch(Exception ex) {
			UIExceptionMgr.exception(ex);
		}
    }

	/**
	 * Handles the show popup on the hosts tree.
	 * @param e
	 */
	private void handleShowPopupOnSessionTree(MouseEvent e) {
/*		try {
			JTree tree = getLeftPanel().getJTreeHosts();
			int x = e.getX();
			int y = e.getY();
			TreePath path = tree.getSelectionPath();
			if(path != null) {
				DefaultMutableTreeNode sel =
					(DefaultMutableTreeNode)path.getLastPathComponent();
				if(sel != null) {
					if(sel instanceof SchemeNode) {
						;
					} else if(sel instanceof HostNode) {
					    ;
					} else if(sel instanceof AgentNode) {
					    ;
					}
				}
			} else {
				// nothing selected
				;
			}
			//getJPopupMenu().show(e.getComponent(), x, y);
		} catch(Exception ex) {
			UIExceptionMgr.userException(ex);
		}
*/	}

	/**
	 * Adds the new entity to the session model.
	 * @param host
	 * @param aid
	 * @param ed
	 */
	private void handleNewEntity(HostId host, AgentId aid, EntityDescriptor ed) {
		try {
			ResourceId ridHost = new ResourceId(host, null, null, null);
			ResourceId ridAgent = new ResourceId(host, aid, null, null);
			ResourceId ridEntity = new ResourceId(host, aid, ed.getId(), null);
			if(sessionModel.getNodeForResourceId(ridHost) == null) {
				sessionModel.addHost(host.toString());
			}
			if(sessionModel.getNodeForResourceId(ridAgent) == null) {
                throw new RMSException("Agent node missing: " + ridAgent);
			}
			EntityNode node = (EntityNode)sessionModel.getNodeForResourceId(ridEntity);
			if(node == null) {
				sessionModel.addEntity(host.toString(), aid, ed);
			} else {
				sessionModel.updateEntity(node, ed);
            }
		} catch(Exception e) {
			UIExceptionMgr.exception(e);
		}
	}

    /**
     * @param host
     * @param ad
     */
    private void handleNewAgent(HostId host, AgentDescriptor ad) {
        try {
            AgentId aid = ad.getAgentId();
            ResourceId ridHost = new ResourceId(host, null, null, null);
            ResourceId ridAgent = new ResourceId(host, aid, null, null);
            if(sessionModel.getNodeForResourceId(ridHost) == null) {
                sessionModel.addHost(host.toString());
            }
            if(sessionModel.getNodeForResourceId(ridAgent) == null) {
                // create an AgentInstanceData
                AgentInstallationData agentInstallationData = rmsAgentRepository
                    .getInstalledAgents().get(aid.getInstallationId());
                if(agentInstallationData == null) {
                    throw new AgentIsNotInstalled(aid.getInstallationId());
                }
                AgentInstanceData agentInstanceData = new AgentInstanceData(ad);
                sessionModel.addAgent(host.toString(), agentInstallationData, agentInstanceData);
            } else {
                // update agent data

            }
        } catch(Exception e) {
            UIExceptionMgr.exception(e);
        }
    }
}
