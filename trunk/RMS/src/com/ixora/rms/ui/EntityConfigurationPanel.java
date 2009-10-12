/*
 * Created on 24-Dec-2003
 */
package com.ixora.rms.ui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;

import com.ixora.rms.RMSComponent;
import com.ixora.rms.RMSConfigurationConstants;
import com.ixora.rms.ResourceId;
import com.ixora.common.ComponentConfiguration;
import com.ixora.common.ConfigurationMgr;
import com.ixora.common.MessageRepository;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.ui.actions.ActionApply;
import com.ixora.common.ui.actions.ActionCancel;
import com.ixora.common.ui.jobs.UIWorkerJobDefault;
import com.ixora.common.ui.popup.PopupListener;
import com.ixora.common.utils.Utils;
import com.ixora.rms.CounterId;
import com.ixora.rms.CounterType;
import com.ixora.rms.EntityConfiguration;
import com.ixora.rms.EntityDescriptorTree;
import com.ixora.rms.MonitoringLevel;
import com.ixora.rms.client.model.AgentNode;
import com.ixora.rms.client.model.CounterInfo;
import com.ixora.rms.client.model.EntityInfo;
import com.ixora.rms.client.model.EntityNode;
import com.ixora.rms.client.model.HostNode;
import com.ixora.rms.client.model.SessionModel;
import com.ixora.rms.client.model.SessionNode;
import com.ixora.rms.dataengine.Style;
import com.ixora.rms.services.MonitoringSessionService;
import com.ixora.rms.ui.messages.Msg;

/**
 * Panel showing the selected entity's configuration.
 * @author Daniel Moraru
 */
public final class EntityConfigurationPanel extends JPanel {
	private static final long serialVersionUID = 953171368560230798L;

	/**
	 * Callback.
	 */
	public interface Callback {
		/**
		 * Invoked when a request to plot a counter was made.
		 * @param counter the counter to plot
		 * @param name the name of the data view; can be null
		 * @param desc the description of the data view; can be null
		 * @param style the style to assign to the data view
		 */
		void plot(ResourceId counter, String name, String desc, Style style);
		/**
		 * Invoked when a request to plot multiple counters was made.
		 * @param context
		 * @param counter
		 */
		void plot(ResourceId context, List<ResourceId> counter);
	}

	/** The dimension of the counter description area */
	private static final Dimension dimDesc = new Dimension(200, 70);

// controls
	private JTable fTableCounters;
	private JEditorPane fTextAreaDescription;
	private JPopupMenu fPopupMenu;

	/** Counters table model */
	private EntityCountersTableModel fTableModelCounters;
	/** Event handler */
	private EventHandler fEventHandler;
	/** Callback */
	private Callback fCallback;

// actions
	private ActionApplyChanges fActionApplyChanges;
	private ActionCancel fActionCancel;
	private ActionPlotWithCode fActionPlotWithCode;
	private ActionPlotWithStyle fActionPlotWithStyle;

	/** If true state of change events will not be processed */
	private boolean fSettingUp;
	/** View container owing this panel */
	private RMSViewContainer fViewContainer;
	/** Monitoring session service used to set the configuration */
	private MonitoringSessionService fSessionService;
	/** Monitoring session model */
	private SessionModel fSessionModel;
	/** In edit configuration */
	private EntityConfiguration fConfiguration;
	/** In edit entity node */
	private EntityNode fEntityNode;
	/** If true the panel holds a read only configuration */
	private boolean fLogReplayMode;
	/** True if in the process of updating the counters table */
	private boolean fUpdatingCounterTable;
	/** RMS Configuration reference */
	private ComponentConfiguration fRmsConf;
	/** Sampling and level panel */
	private SamplingAndLevelPanel fSamplingAndLevelPanel;

	/**
	 * Apply changes action.
	 */
	private final class ActionApplyChanges extends ActionApply {
		private static final long serialVersionUID = -1331856800985532742L;
		public ActionApplyChanges() {
			super();
			this.enabled = false;
			// disable mnemonic
			putValue(MNEMONIC_KEY, null);
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			handleApplyChanges();
		}
	}

	/**
	 * Plot action.
	 */
	private final class ActionPlot extends AbstractAction {
		private static final long serialVersionUID = 4704194006855950132L;
		public ActionPlot() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.ACTIONS_PLOT), this);
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			handlePlotCounter();
		}
	}

	/**
	 * Plot action.
	 */
	private final class ActionPlotWithCode extends AbstractAction {
		private static final long serialVersionUID = -1249431743909110523L;
		public ActionPlotWithCode() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.ACTIONS_PLOT_COUNTER_WITH_CODE), this);
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			handlePlotCounterWithCode();
		}
	}

	/**
	 * Plot action.
	 */
	private final class ActionPlotWithStyle extends AbstractAction {
		private static final long serialVersionUID = -7802977264680868952L;
		public ActionPlotWithStyle() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.ACTIONS_PLOT_COUNTER_WITH_STYLE), this);
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			handlePlotCounterWithStyle();
		}
	}

	/**
	 * Event handler.
	 */
	private final class EventHandler
				extends PopupListener
				implements
				ListSelectionListener,
					TableModelListener,
						ChangeListener,
							SessionModel.FineListener {
		/**
		 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
		 */
		public void valueChanged(ListSelectionEvent e) {
			if(e.getValueIsAdjusting()) {
				return;
			}
			ListSelectionModel lsm = (ListSelectionModel)e.getSource();
			if(lsm.isSelectionEmpty()) {
				return;
			} else {
				if(lsm == fTableCounters.getSelectionModel()) {
					// row selected
					handleCounterSelected(e);
					return;
				}
			}
		}
		/**
		 * @see javax.swing.event.TableModelListener#tableChanged(javax.swing.event.TableModelEvent)
		 */
		public void tableChanged(TableModelEvent e) {
			if(e.getSource() == fTableCounters.getModel()) {
				handleCountersTableChanged(e);
				return;
			}
		}
		/**
		 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
		 */
		public void stateChanged(ChangeEvent e) {
			handleChangedEvent(e);
		}
		/**
		 * @see com.ixora.common.ui.popup.PopupListener#showPopup(java.awt.event.MouseEvent)
		 */
		protected void showPopup(MouseEvent e) {
			if(e.getSource() == fTableCounters) {
				handleShowPopupOnCountersTable(e);
				return;
			}
		}
        /**
         * @see com.ixora.rms.client.model.SessionModel.FineListener#showIdentifiersChanged(boolean)
         */
        public void showIdentifiersChanged(boolean value) {
            handleShowIdentifiersChanged(value);
        }
		/**
		 * @see com.ixora.rms.client.model.SessionModel.FineListener#entityUpdated(com.ixora.rms.client.model.EntityNode)
		 */
		public void entityUpdated(EntityNode en) {
			handleEntityUpdated(en);
		}
		/**
		 * @see com.ixora.rms.client.model.SessionModel.FineListener#entityAdded(com.ixora.rms.client.model.EntityNode)
		 */
		public void entityAdded(EntityNode en) {
		}
		/**
		 * @see com.ixora.rms.client.model.SessionModel.FineListener#entityRemoved(com.ixora.rms.client.model.EntityNode)
		 */
		public void entityRemoved(EntityNode en) {
		}
		/**
		 * @see com.ixora.rms.client.model.SessionModel.FineListener#agentAdded(com.ixora.rms.client.model.AgentNode)
		 */
		public void agentAdded(AgentNode an) {
		}
		/**
		 * @see com.ixora.rms.client.model.SessionModel.FineListener#agentRemoved(com.ixora.rms.client.model.AgentNode)
		 */
		public void agentRemoved(AgentNode an) {
		}
		/**
		 * @see com.ixora.rms.client.model.SessionModel.FineListener#agentUpdated(com.ixora.rms.client.model.AgentNode)
		 */
		public void agentUpdated(AgentNode an) {
		}
		/**
		 * @see com.ixora.rms.client.model.SessionModel.FineListener#hostAdded(com.ixora.rms.client.model.HostNode)
		 */
		public void hostAdded(HostNode hn) {
		}
		/**
		 * @see com.ixora.rms.client.model.SessionModel.FineListener#hostRemoved(com.ixora.rms.client.model.HostNode)
		 */
		public void hostRemoved(HostNode hn) {
		}
		/**
		 * @see com.ixora.rms.client.model.SessionModel.FineListener#hostUpdated(com.ixora.rms.client.model.HostNode)
		 */
		public void hostUpdated(HostNode hn) {
		}
		/**
		 * @see com.ixora.rms.client.model.SessionModel.FineListener#sessionUpdated(com.ixora.rms.client.model.SessionNode)
		 */
		public void sessionUpdated(SessionNode sn) {
		}
	}

	/**
	 * Constructor used by the log replay view.
	 * @param viewContainer
	 * @param sessionService
	 * @param listener
	 */
	public EntityConfigurationPanel(
			RMSViewContainer viewContainer,
			SessionModel sessionModel,
			Callback listener) {
		super(new BorderLayout(2, 2));
		this.fLogReplayMode = true;
		initialize(viewContainer, null, sessionModel, listener);
	}

	/**
	 * Constructor used by the live session view.
	 * @param viewContainer
	 * @param sessionService
	 * @param listener
	 */
	public EntityConfigurationPanel(
			RMSViewContainer viewContainer,
			MonitoringSessionService sessionService,
			SessionModel sessionModel,
			Callback listener) {
		super(new BorderLayout(2, 2));
		initialize(viewContainer, sessionService, sessionModel, listener);
	}

	/**
	 * This method initializes this panel.
	 * @param viewContainer
	 * @param listener
	 */
	@SuppressWarnings("serial")
	private void initialize(
			RMSViewContainer viewContainer,
			MonitoringSessionService sessionService,
			SessionModel sessionModel,
			Callback listener) {
		this.fViewContainer = viewContainer;
		this.fCallback = listener;
		this.fSessionService = sessionService;
		this.fSessionModel = sessionModel;
		this.fEventHandler = new EventHandler();

		if(!fLogReplayMode) {
			this.fRmsConf = ConfigurationMgr.get(RMSComponent.NAME);

			this.fActionApplyChanges = new ActionApplyChanges();
			this.fActionCancel = new ActionCancel() {
				public void actionPerformed(ActionEvent e) {
					handleCancel();
				}
			};
			// disable mnemonic
			this.fActionCancel.putValue(Action.MNEMONIC_KEY, null);
			this.fActionCancel.setEnabled(false);

			// create controls
			fSamplingAndLevelPanel = new SamplingAndLevelPanel();
		}

		this.fTableModelCounters = new EntityCountersTableModel(sessionModel, fLogReplayMode);
		this.fTableModelCounters.addTableModelListener(this.fEventHandler);

		// counters table
		fTableCounters = new javax.swing.JTable(this.fTableModelCounters);
		fTableCounters.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		TableColumn c = fTableCounters.getColumnModel().getColumn(0);
		c.setPreferredWidth(25);
		c.setMaxWidth(25);
		c = fTableCounters.getColumnModel().getColumn(1);
		c.setCellRenderer(new EntityCountersTableCellRenderer());
		fTableCounters.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		// scroll pane for counters table
		JScrollPane spCounters = UIFactoryMgr.createScrollPane();
		spCounters.setViewportView(fTableCounters);

		// counter description widget
		fTextAreaDescription = UIFactoryMgr.createHtmlPane();

		// scroll pane for counter description widget
		JScrollPane spDesc = UIFactoryMgr.createScrollPane();
		spDesc.setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(
					UIConfiguration.getPanelPadding(), 0, 0, 0),
					spDesc.getBorder()));
		spDesc.setPreferredSize(dimDesc);
		spDesc.setViewportView(fTextAreaDescription);
		spDesc.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		// panel holding counters scroll pane and the description scroll pane
		JPanel panelCounters = new javax.swing.JPanel(new BorderLayout());
		panelCounters.add(spCounters, BorderLayout.CENTER);
		panelCounters.add(spDesc, BorderLayout.SOUTH);

		// panel holding the buttons
		JPanel panelButtons = new JPanel(
				new FlowLayout());
		JButton buttonApplyChanges = new JButton(this.fActionApplyChanges);
		panelButtons.add(buttonApplyChanges);
		JButton buttonCancel = new JButton(this.fActionCancel);
		panelButtons.add(buttonCancel);

		add(panelCounters, BorderLayout.CENTER);
		if(!fLogReplayMode) {
			add(panelButtons, BorderLayout.SOUTH);
		    add(fSamplingAndLevelPanel, BorderLayout.NORTH);
		}

		// allow apply and cancel buttons only for
		// modifiable configuration
		if(!fLogReplayMode) {
			fSamplingAndLevelPanel.getLevelSpinner().getModel().addChangeListener(this.fEventHandler);
			fSamplingAndLevelPanel.getRecursiveLevelCheckBox().addChangeListener(this.fEventHandler);
			fSamplingAndLevelPanel.getSamplingSpinner().getModel().addChangeListener(this.fEventHandler);
			fSamplingAndLevelPanel.getGlobalSamplingCheckBox().addChangeListener(this.fEventHandler);
		}

		// detect row selection in the counters table
		ListSelectionModel lsm = fTableCounters.getSelectionModel();
		lsm.addListSelectionListener(this.fEventHandler);
		fTableCounters.addMouseListener(this.fEventHandler);
		this.fSessionModel.addListener(fEventHandler);

		fActionPlotWithCode = new ActionPlotWithCode();
		fActionPlotWithStyle = new ActionPlotWithStyle();
	}

	/**
	 * @return javax.swing.JPopupMenu
	 */
	private JPopupMenu getJPopupMenu() {
		if(fPopupMenu == null) {
			fPopupMenu = UIFactoryMgr.createPopupMenu();
			JMenuItem menuItem = UIFactoryMgr.createMenuItem(new ActionPlot());
			fPopupMenu.add(menuItem);
			menuItem = UIFactoryMgr.createMenuItem(fActionPlotWithCode);
			fPopupMenu.add(menuItem);
			menuItem = UIFactoryMgr.createMenuItem(fActionPlotWithStyle);
			fPopupMenu.add(menuItem);
		}
		return fPopupMenu;
	}

	/**
	 * Sets the configuration details to display.
	 * @param en
	 */
	public void setConfiguration(EntityNode en) {
		try {
			this.fSettingUp = true;
			this.fEntityNode = en;
			this.fTextAreaDescription.setText(null);
			this.fTextAreaDescription.setToolTipText(null);
			EntityInfo entity = this.fEntityNode.getEntityInfo();
			this.fConfiguration = entity.getConfiguration();
			if(!fLogReplayMode) {
				// initially the agent has an empty configuration
				// so DO NOT modify the configuration at this stage
				// as the delta wan't be passed to the agent correctly
				EntityConfiguration displayValues = null;
				if(!this.fConfiguration.isSamplingIntervalSet()) {
					// set a configuration with similar data but with the global sampling time set
					// if this entity has never been
					// configured before so that the panel can display the proper values
					displayValues = new EntityConfiguration();
					displayValues.setGlobalSamplingInterval(true);
					displayValues.setSamplingInterval(this.fRmsConf.getInt(
							RMSConfigurationConstants.AGENT_CONFIGURATION_SAMPLING_INERVAL));
					displayValues.setMonitoringLevel(this.fConfiguration.getMonitoringLevel());
					Boolean recCounters = this.fConfiguration.isRecursiveEnableAllCounters();
					if(recCounters != null) {
						displayValues.setRecursiveEnableAllCounters(recCounters.booleanValue());
					}
					Boolean recLevel = this.fConfiguration.isRecursiveMonitoringLevel();
					if(recLevel != null) {
						displayValues.setRecursiveMonitoringLevel(recLevel.booleanValue());
					}
				}
				MonitoringLevel[] levels = entity.getSupportedLevels();
				fSamplingAndLevelPanel.setMonitoringConfiguration(
						displayValues != null ? displayValues : fConfiguration,
						levels, entity.supportsSamplingInterval(), true);
			}
			fUpdatingCounterTable = true;
			try {
				this.fTableModelCounters.setCounters(this.fEntityNode, null);
			} finally {
				fUpdatingCounterTable = false;
			}

			if(!fLogReplayMode) {
				calculateCommandButtonsState();
			}
		} finally {
			this.fSettingUp = false;
		}
	}

	/**
	 * Overriden this to recalculate the state of the
	 * command buttons.
	 * @see java.awt.Component#setVisible(boolean)
	 */
	public void setVisible(boolean aFlag) {
		if(aFlag) {
			if(!fLogReplayMode) {
				calculateCommandButtonsState();
			}
		}
		super.setVisible(aFlag);
	}

	/**
	 * Calculates the state of the command buttons.
	 */
	private void calculateCommandButtonsState() {
	    if(this.fEntityNode == null) {
	        return;
	    }
		// see if there are uncommited visible counters and
		// if so enable the apply and cancel buttons
		if(this.fEntityNode.getEntityInfo().uncommittedVisibleCounters()) {
			this.fActionApplyChanges.setEnabled(true);
			this.fActionCancel.setEnabled(true);
		} else {
			this.fActionApplyChanges.setEnabled(false);
			this.fActionCancel.setEnabled(false);
		}
	}

	/**
	 * Handles the counter selected event.
	 * @param e
	 */
	private void handleCounterSelected(ListSelectionEvent e) {
		try {
			int[] sel = fTableCounters.getSelectedRows();
			if(sel.length < 0 || sel.length > 1) {
                setDescriptionText(null);
				return;
			}
			CounterInfo data = (CounterInfo)fTableCounters.getValueAt(sel[0], 1);
			String desc = data.getTranslatedDescription();
			setDescriptionText(desc);
		} catch(Exception ex) {
			UIExceptionMgr.userException(ex);
		}
	}

	/**
	 * Applies changes.
	 */
	private void handleApplyChanges() {
		try {
			// build the new configuration
			final EntityConfiguration conf =
				new EntityConfiguration(this.fTableModelCounters.getEnabledCounters());

			fSamplingAndLevelPanel.apply(conf);

			// get delta configuration
			final EntityConfiguration before = this.fConfiguration;
			final EntityConfiguration delta = before.getDelta(conf);
			if(delta == null) {
				// no change in config
				return;
			}
			this.fViewContainer.runJob(new UIWorkerJobDefault(
					fViewContainer.getAppFrame(),
					Cursor.WAIT_CURSOR,
					MessageRepository.get(
						Msg.TEXT_APPLYINGCHANGES_ENTITYCONFIGURATION)) {
				public void work() throws Exception {
					EntityDescriptorTree descriptors = fSessionService.
						configureEntity(
							fEntityNode.getAgentNode().getHostNode().getHostInfo().getName(),
							fEntityNode.getAgentNode().getAgentInfo().getDeploymentDtls().getAgentId(),
							fEntityNode.getEntityInfo().getId(),
							delta);
					this.fResult = descriptors;
				}
				public void finished(Throwable ex) {
					if(ex != null) {
						// reset the config to the 'before' one
						setConfiguration(fEntityNode);
						UIExceptionMgr.userException(ex);
					} else {
						// save the config
						fSessionModel.updateEntities(
								fEntityNode.getAgentNode().getHostNode().getHostInfo().getName(),
								fEntityNode.getAgentNode().getAgentInfo().getDeploymentDtls().getAgentId(),
								(EntityDescriptorTree)this.fResult);
						fSessionModel.setDirtyEntity(fEntityNode, true);
					}
				}
				});
		} catch(Exception e) {
			handleCancel();
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Cancels changes.
	 */
	private void handleCancel() {
		try {
			// remove all uncommitted changes
			this.fTableModelCounters.rollback();
			setConfiguration(this.fEntityNode);
			this.fActionApplyChanges.setEnabled(false);
			this.fActionCancel.setEnabled(false);
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Handles counters table changed events.
	 * @param e
	 */
	private void handleCountersTableChanged(TableModelEvent e) {
	    if(!isVisible()) {
	        return;
	    }
		if(this.fSettingUp || this.fUpdatingCounterTable) {
			return;
		}
		try {
			if(e.getType() == TableModelEvent.UPDATE) {
				// update buttons state if not in log replay mode
				if(!fLogReplayMode) {
					calculateCommandButtonsState();
				}
			}
		} catch(Exception ex) {
			UIExceptionMgr.userException(ex);
		}
	}

	/**
	 * @param e
	 */
	private void handleChangedEvent(ChangeEvent e) {
	    if(this.fEntityNode == null) {
	        return;
	    }
		if(this.fSettingUp) {
			return;
		}
		try {
			Object src = e.getSource();
			if(src == fSamplingAndLevelPanel.getLevelSpinner().getModel()
					|| src == fSamplingAndLevelPanel.getRecursiveLevelCheckBox()
					|| src == fSamplingAndLevelPanel.getSamplingSpinner().getModel()
					|| src == fSamplingAndLevelPanel.getGlobalSamplingCheckBox()) {
				fActionApplyChanges.setEnabled(true);
				fActionCancel.setEnabled(true);
				if(src == fSamplingAndLevelPanel.getLevelSpinner().getModel()) {
					// update counter list for selected level
					MonitoringLevel level = (MonitoringLevel)fSamplingAndLevelPanel.getLevelSpinner().getValue();
					fUpdatingCounterTable = true;
					try {
						fTableModelCounters.setCounters(this.fEntityNode, level);
					} finally {
						fUpdatingCounterTable = false;
					}
				}
				return;
			}
			// anything selected? if so
			// update the description
			int idx = fTableCounters.getSelectedRow();
			if(idx < 0) {
				// clear
				setDescriptionText("");
			} else {
				CounterInfo cd =
					(CounterInfo)fTableModelCounters.getValueAt(idx, 1);
				String desc = cd.getTranslatedDescription();
				setDescriptionText(desc);
			}
		} catch(Exception ex) {
			UIExceptionMgr.userException(ex);
		}
	}

	/**
	 * Plots a counter.
	 */
	private void handlePlotCounter() {
		try {
		    final int[] selected = fTableCounters.getSelectedRows();
			if(selected == null || selected.length == 0) {
				return;
			}
			if(selected.length == 1) {
				this.fViewContainer.runJobSynch(
				        new UIWorkerJobDefault(
				                fViewContainer.getAppFrame(), Cursor.WAIT_CURSOR, "") {
	                public void work() throws Exception {
	                	ResourceId rid = fEntityNode.getResourceId();
	      				CounterId cid = ((CounterInfo)fTableModelCounters.getValueAt(selected[0], 1)).getId();
	                	fCallback.plot(new ResourceId(
	                			rid.getHostId(),
								rid.getAgentId(),
								rid.getEntityId(),
								cid), null, null, (Style)null);
	                }
	                public void finished(Throwable ex) {
	                    if(ex != null) {
	                        UIExceptionMgr.userException(ex);
	                    }
	                }
				});
			} else {
			    // multiple counters
			    ResourceId rid = fEntityNode.getResourceId();
			    final List<ResourceId> counters = new ArrayList<ResourceId>(selected.length);
			    for(int i = 0; i < selected.length; i++) {
      				CounterId cid = ((CounterInfo)fTableModelCounters.getValueAt(selected[i], 1)).getId();
      				counters.add(new ResourceId(
                			rid.getHostId(),
							rid.getAgentId(),
							rid.getEntityId(),
							cid));
                }
				this.fViewContainer.runJobSynch(
				        new UIWorkerJobDefault(
				                fViewContainer.getAppFrame(), Cursor.WAIT_CURSOR, "") {
	                public void work() throws Exception {
	                	// plot all of them in the context of the current entity
	                	fCallback.plot(fEntityNode.getResourceId(), counters);
	                }
	                public void finished(Throwable ex) {
	                    if(ex != null) {
	                        UIExceptionMgr.userException(ex);
	                    }
	                }
				});
			}
		} catch (Exception ex) {
			UIExceptionMgr.userException(ex);
		}
	}

	/**
	 * Plots a counter asking for a code to apply to it.
	 */
	private void handlePlotCounterWithCode() {
		try {
			// ask for code
			String id = "counter";
			CounterCodeDialog dlg = new CounterCodeDialog(fViewContainer.getAppFrame(), id);
			UIUtils.centerDialogAndShow(fViewContainer.getAppFrame(), dlg);
			String code = dlg.getCode();
			if(Utils.isEmptyString(code)) {
				return;
			}
			Style style = new Style();
			style.setCode(code);
			style.setID(id);
			plotSelectedCounterWithStyle(dlg.getCounterName(), dlg.getCounterDescription(), style);
		} catch (Exception ex) {
			UIExceptionMgr.userException(ex);
		}
	}

	/**
	 * Plots a counter asking for a style to apply to it.
	 */
	private void handlePlotCounterWithStyle() {
		try {
			// ask for style (code, min, max...)
			Style style = new Style();
			CounterStyleDialog dlg = new CounterStyleDialog(fViewContainer.getAppFrame());
			UIUtils.centerDialogAndShow(fViewContainer.getAppFrame(), dlg);
			Double max = dlg.getMax();
			if(max != null) {
				style.setMAX(max);
			}
			Double min = dlg.getMin();
			if(min != null) {
				style.setMIN(min);
			}
			if(min == null && max == null) {
				return;
			}
			plotSelectedCounterWithStyle(null, null, style);
		} catch (Exception ex) {
			UIExceptionMgr.userException(ex);
		}
	}

	/**
	 * Plots a counter using the style provided.
	 * @param style
	 * @param name
	 * @param desc
	 */
	private void plotSelectedCounterWithStyle(
			final String name, final String desc, final Style style) {
		final int[] selected = fTableCounters.getSelectedRows();
		if(selected == null || selected.length == 0) {
			return;
		}
		if(selected.length == 1) {
			this.fViewContainer.runJobSynch(
			        new UIWorkerJobDefault(
			                fViewContainer.getAppFrame(), Cursor.WAIT_CURSOR, "") {
                public void work() throws Exception {
                	ResourceId rid = fEntityNode.getResourceId();
      				CounterId cid = ((CounterInfo)fTableModelCounters.getValueAt(selected[0], 1)).getId();
                	fCallback.plot(new ResourceId(
                			rid.getHostId(),
							rid.getAgentId(),
							rid.getEntityId(),
							cid), name, desc, style);
                }
                public void finished(Throwable ex) {
                    if(ex != null) {
                        UIExceptionMgr.userException(ex);
                    }
                }
			});
		}
	}

	/**
	 * @param e
	 */
	private void handleShowPopupOnCountersTable(MouseEvent e) {
		try {
			int[] sel = fTableCounters.getSelectedRows();
			if(sel == null || sel.length == 0) {
				return;
			}
			fActionPlotWithStyle.setEnabled(false);
			fActionPlotWithCode.setEnabled(false);

			if(sel.length == 1) {
				CounterInfo ci = fTableModelCounters.getCounterAt(sel[0]);
				if(ci.getType() != CounterType.OBJECT) {
					fActionPlotWithCode.setEnabled(true);
					fActionPlotWithStyle.setEnabled(true);
				}
			}
			getJPopupMenu().show(e.getComponent(), e.getX(), e.getY());
		} catch(Exception ex) {
			UIExceptionMgr.userException(ex);
		}
	}

	/**
	 * Sets the description text in the description text area.
	 * @param desc
	 */
	private void setDescriptionText(String desc) {
		fTextAreaDescription.setText(
				desc != null ? desc : "");
		fTextAreaDescription.setToolTipText(
				desc != null ? UIUtils.getMultilineHtmlText(desc,
				    UIConfiguration.getMaximumLineLengthForToolTipText()) : "");
		fTextAreaDescription.setCaretPosition(0);
	}

    /**
     * @param value
     */
    private void handleShowIdentifiersChanged(boolean value) {
        try {
            // just refresh table
            fTableModelCounters.fireTableDataChanged();
        } catch(Exception e) {
            UIExceptionMgr.exception(e);
        }
    }

	/**
	 * @param en
	 */
	private void handleEntityUpdated(EntityNode en) {
        try {
    	    if(this.fEntityNode != en) {
    	    	return;
    	    }
    	    setConfiguration(en);
        } catch(Exception e) {
            UIExceptionMgr.exception(e);
        }
	}
}
