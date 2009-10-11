/*
 * Created on 06-Jul-2004
 */
package com.ixora.rms.ui.artefacts;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Set;

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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;

import com.ixora.rms.ResourceId;
import com.ixora.common.MessageRepository;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.ui.actions.ActionApply;
import com.ixora.common.ui.actions.ActionCancel;
import com.ixora.common.ui.popup.PopupListener;
import com.ixora.rms.client.QueryRealizer;
import com.ixora.rms.client.model.AgentNode;
import com.ixora.rms.client.model.ArtefactInfo;
import com.ixora.rms.client.model.ArtefactInfoContainer;
import com.ixora.rms.client.model.EntityNode;
import com.ixora.rms.client.model.HostNode;
import com.ixora.rms.client.model.SessionModel;
import com.ixora.rms.client.model.SessionModelTreeNode;
import com.ixora.rms.client.model.SessionNode;
import com.ixora.rms.ui.RMSViewContainer;
import com.ixora.rms.ui.actions.ActionViewXML;

/**
 * A panel that allows to manage a set of artefacts.
 * @author Daniel Moraru
 */
public abstract class ArtefactSelectorPanel<T extends ArtefactInfo> extends JPanel {
	private static final long serialVersionUID = -7313639653232410884L;

	/** Logger */
    private static final AppLogger logger = AppLoggerFactory.getLogger(ArtefactSelectorPanel.class);
	/**
	 * Apply changes action.
	 */
	protected final class ActionApplyChanges extends ActionApply {
		private static final long serialVersionUID = 341302431514865457L;
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
	 * Edit artefact action.
	 */
	protected final class ActionEditArtefact extends AbstractAction {
		private static final long serialVersionUID = -9061148508393214487L;
		public ActionEditArtefact(String message) {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(message), this);
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			handleEditArtefact();
		}
	}

	/**
	 * Edit artefact action.
	 */
	protected final class ActionAddArtefact extends AbstractAction {
		private static final long serialVersionUID = -3800683582099331490L;
		public ActionAddArtefact(String message) {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(
			        message), this);
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			handleAddArtefact();
		}
	}

	/**
	 * Edit artefact action.
	 */
	protected final class ActionRemoveArtefact extends AbstractAction {
		private static final long serialVersionUID = 7783191370924346739L;
		public ActionRemoveArtefact(String message) {
			super();
			UIUtils.setUsabilityDtls(
			       MessageRepository.get(message), this);
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			handleRemoveArtefact();
		}
	}

	/**
	 * Plot action.
	 */
	protected final class ActionPlotArtefact extends AbstractAction {
		private static final long serialVersionUID = 1108394577813040676L;
		public ActionPlotArtefact(String message) {
			super();
			UIUtils.setUsabilityDtls(
			        MessageRepository.get(message), this);
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			handlePlotArtefact();
		}
	}

	/**
	 * Event handler.
	 */
	private final class EventHandler
				extends PopupListener
				implements ListSelectionListener,
					TableModelListener,
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
				if(lsm == getJTableArtefacts().getSelectionModel()) {
					// row selected
					handleArtefactSelected(e);
					return;
				}
			}
		}

		/**
		 * @see com.ixora.common.ui.popup.PopupListener#showPopup(java.awt.event.MouseEvent)
		 */
		protected void showPopup(MouseEvent e) {
			if(e.getSource() == getJTableArtefacts()) {
				handleShowPopupOnArtefactsTable(e);
				return;
			}
			if(e.getSource() == getJScrollPaneArtefacts()) {
				handleShowPopupOnArtefactsTablePanel(e);
				return;
			}
		}
		/**
		 * @see javax.swing.event.TableModelListener#tableChanged(javax.swing.event.TableModelEvent)
		 */
		public void tableChanged(TableModelEvent e) {
			if(e.getSource() == getJTableArtefacts().getModel()) {
				handleArtefactsTableChanged(e);
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

	/** The dimension of the counter description area */
	private static final Dimension dimDesc = new Dimension(200, 70);

// controls
	protected JScrollPane jScrollPaneArtefacts;
	protected JScrollPane jScrollPaneDescription;
	protected JTable jTableArtefacts;
	protected JEditorPane jTextDescription;
	protected JButton jButtonApplyChanges;
	protected JButton jButtonCancel;
	protected JMenuItem jMenuItemPlot;
	protected JMenuItem jMenuItemEdit;
	protected JMenuItem jMenuItemAdd;
	protected JMenuItem jMenuItemRemove;
	private JMenuItem jMenuItemViewXML;
	protected JPanel jPanelArtefacts;
	protected JPanel jPanelButtons;
	protected JPopupMenu jPopupMenu;

// actions
	protected ActionApplyChanges fActionApply;
	protected ActionCancel fActionCancel;
	protected ActionPlotArtefact fActionPlot;
	protected ActionEditArtefact fActionEdit;
	protected ActionAddArtefact fActionAdd;
	protected ActionRemoveArtefact fActionRemove;
	protected Action fActionViewXML;

	/** If true state of change events will not be processed */
	protected boolean fSettingUp;
	/** Monitoring session data tree */
	protected SessionModel fSessionData;
	/** Query realizer */
	protected QueryRealizer fQueryRealizer;

// values for the current context
    /** Context represented by a ResourceId */
	protected ResourceId fContext;
	/**
	 * Cached reference to the artefact container for
	 * the current context.
	 */
	protected ArtefactInfoContainer fArtefactContainer;
    /**
     * Cached reference to the session node for the
     * current context.
     */
    protected SessionModelTreeNode fSessionNode;
    /**
     * Cached reference to the agent version for the current
     * context.
     */
    protected String fSUOVersion;
    /**
     * Cached reference to all agent versions available for the current context;
     * used for artefact that could be assigned different agent versions.
     */
    protected Set<String> fAllSUOVersions;
	/** ViewContainer */
	protected RMSViewContainer fViewContainer;
	/** Log replay mode flag */
	protected boolean fLogReplayMode;

	/** Event handler */
	private EventHandler fEventHandler;
	/** Artefact table model */
	protected SelectableArtefactTableModel<T> fTableModelArtefacts;

    /**
	 * Constructor used by a log replay session.
	 * @param viewContainer
	 * @param sessionData
	 * @param tableModel
	 * @param actionAddArtefact
	 * @param actionRemoveArtefact
	 * @param actionEditArtefact
	 * @param actionPlotArtefact
	 */
	protected ArtefactSelectorPanel(
			RMSViewContainer viewContainer,
			SessionModel sessionData,
			SelectableArtefactTableModel<T> tableModel,
			String actionAddArtefact,
			String actionRemoveArtefact,
			String actionEditArtefact,
			String actionPlotArtefact) {
	    this(viewContainer, sessionData,
	            null, tableModel,
	            actionAddArtefact, actionRemoveArtefact,
	            actionEditArtefact, actionPlotArtefact);
	}

	/**
	 * Constructor used by live session.
	 * @param viewContainer
	 * @param sessionData
	 * @param queryRealizer
	 * @param tableModel
	 * @param actionAddArtefact
	 * @param actionRemoveArtefact
	 * @param actionEditArtefact
	 * @param actionPlotArtefact
	 */
	protected ArtefactSelectorPanel(
			RMSViewContainer viewContainer,
			SessionModel sessionData,
			QueryRealizer queryRealizer,
			SelectableArtefactTableModel<T> tableModel,
			String actionAddArtefact,
			String actionRemoveArtefact,
			String actionEditArtefact,
			String actionPlotArtefact) {
		super(new BorderLayout());
		initialize(viewContainer,
				sessionData, queryRealizer, tableModel,
				actionAddArtefact,
				actionRemoveArtefact,
				actionEditArtefact,
				actionPlotArtefact);
	}

	/**
     * Instrycts the session model to recalculate the
     * status of the artefacts.
     */
    protected abstract void refreshArtefactStatus();

    /**
	 * Sets the artefacts to display.
	 * @param context the resource id describing the context
	 * of the current artefacts.
	 * @param artefact the artefact to set (collection of ArtefactInfo)
	 */
	protected void setArtefacts(
			ResourceId context,
			Collection<T> artefacts) {
		this.fSettingUp = true;
		this.fContext = context;
		try {
            fSessionNode = fSessionData.getSessionModelTreeNodeForResourceId(fContext);
            if(fSessionNode == null) {
                // this happen when a node is removed from the tree sometimes...
                logger.error("Node " + fContext + " not found in the session model");
                return;
            }
            fSUOVersion = fSessionData.getAgentVersionInContext(fSessionNode);
            fAllSUOVersions = fSessionData.getAllAgentVersionsInContext(fSessionNode);
		    fArtefactContainer = fSessionNode.getArtefactInfoContainer();
		    refreshArtefactStatus();
			calculateCommandButtonsState();
            this.fTableModelArtefacts.setArtefacts(
				context, artefacts);
			setDescriptionText("");
		} finally {
			this.fSettingUp = false;
		}
	}

    /**
     * @see java.awt.Component#setVisible(boolean)
     */
    public void setVisible(boolean aFlag) {
        if(aFlag) {
            refreshArtefactStatus();
            calculateCommandButtonsState();
        }
        super.setVisible(aFlag);
    }

	/**
	 * Calculates the command button states.
	 */
	private void calculateCommandButtonsState() {
		if(uncommittedArtifacts()) {
			this.fActionApply.setEnabled(true);
			this.fActionCancel.setEnabled(true);
		} else {
			this.fActionApply.setEnabled(false);
			this.fActionCancel.setEnabled(false);
		}
	}

	/**
	 * @return true if there are uncommitted artifacts
	 */
	protected abstract boolean uncommittedArtifacts();

	/**
	 * @return javax.swing.JTable
	 */
	protected JTable getJTableArtefacts() {
		if(jTableArtefacts == null) {
			jTableArtefacts = new javax.swing.JTable(
			        this.fTableModelArtefacts);
			jTableArtefacts.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
			TableColumn c = jTableArtefacts.getColumnModel().getColumn(0);
			c.setPreferredWidth(25);
			c.setMaxWidth(25);
			c = jTableArtefacts.getColumnModel().getColumn(1);
			c.setCellRenderer(new SelectableArtefactTableCellRenderer());
			jTableArtefacts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		}
		return jTableArtefacts;
	}

	/**
	 * @return javax.swing.JTextArea
	 */
	protected JEditorPane getJTextAreaDescription() {
		if(jTextDescription == null) {
			jTextDescription = UIFactoryMgr.createHtmlPane();
		}
		return jTextDescription;
	}

	/**
	 * @return javax.swing.JButton
	 */
	protected javax.swing.JButton getJButtonApplyChanges() {
		if(jButtonApplyChanges == null) {
			jButtonApplyChanges = new javax.swing.JButton(this.fActionApply);
		}
		return jButtonApplyChanges;
	}

	/**
	 * @return javax.swing.JButton
	 */
	protected javax.swing.JButton getJButtonCancel() {
		if(jButtonCancel == null) {
			jButtonCancel = new javax.swing.JButton(this.fActionCancel);
		}
		return jButtonCancel;
	}

	/**
	 * @return javax.swing.JPopupMenu
	 */
	protected JPopupMenu getJPopupMenu() {
		if(jPopupMenu == null) {
			jPopupMenu = UIFactoryMgr.createPopupMenu();
			jPopupMenu.add(getJMenuItemPlot());
			jPopupMenu.add(getJMenuItemEdit());
			if(fActionAdd != null) {
				jPopupMenu.add(getJMenuItemAdd());
			}
			jPopupMenu.add(getJMenuItemRemove());
			jPopupMenu.add(getJMenuItemViewXML());
		}
		return jPopupMenu;
	}

	/**
	 * @return javax.swing.JMenuItem
	 */
	protected JMenuItem getJMenuItemPlot() {
		if(jMenuItemPlot == null) {
			jMenuItemPlot = UIFactoryMgr.createMenuItem(
				fActionPlot);
		}
		return jMenuItemPlot;
	}

	/**
	 * @return javax.swing.JMenuItem
	 */
	protected JMenuItem getJMenuItemEdit() {
		if(jMenuItemEdit == null) {
			jMenuItemEdit = UIFactoryMgr.createMenuItem(
					fActionEdit);
		}
		return jMenuItemEdit;
	}

	/**
	 * @return javax.swing.JMenuItem
	 */
	protected JMenuItem getJMenuItemAdd() {
		if(fActionAdd == null) {
			return null;
		}
		if(jMenuItemAdd == null) {
			jMenuItemAdd = UIFactoryMgr.createMenuItem(
					fActionAdd);
		}
		return jMenuItemAdd;
	}

	/**
	 * @return javax.swing.JMenuItem
	 */
	protected JMenuItem getJMenuItemRemove() {
		if(jMenuItemRemove == null) {
			jMenuItemRemove = UIFactoryMgr.createMenuItem(
					fActionRemove);
		}
		return jMenuItemRemove;
	}

	/**
	 * @return javax.swing.JMenuItem
	 */
	protected JMenuItem getJMenuItemViewXML() {
		if(jMenuItemViewXML == null) {
			jMenuItemViewXML = UIFactoryMgr.createMenuItem(
					fActionViewXML);
		}
		return jMenuItemViewXML;
	}

	/**
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJPanelArtefacts() {
		if(jPanelArtefacts == null) {
		    jPanelArtefacts = new JPanel(new BorderLayout());
		    jPanelArtefacts.add(getJScrollPaneArtefacts(), BorderLayout.CENTER);
		    jPanelArtefacts.add(getJScrollPaneDescription(), BorderLayout.SOUTH);
		}
		return jPanelArtefacts;
	}

	/**
	 * @return javax.swing.JScrollPane
	 */
	private javax.swing.JScrollPane getJScrollPaneArtefacts() {
		if(jScrollPaneArtefacts == null) {
			jScrollPaneArtefacts = UIFactoryMgr.createScrollPane();
			jScrollPaneArtefacts.setViewportView(getJTableArtefacts());
		}
		return jScrollPaneArtefacts;
	}

	/**
	 * @return javax.swing.JScrollPane
	 */
	private javax.swing.JScrollPane getJScrollPaneDescription() {
		if(jScrollPaneDescription == null) {
			jScrollPaneDescription = UIFactoryMgr.createScrollPane();
			jScrollPaneDescription.setBorder(
				BorderFactory.createCompoundBorder(
					BorderFactory.createEmptyBorder(
						UIConfiguration.getPanelPadding(), 0, 0, 0),
					jScrollPaneDescription.getBorder()));
			jScrollPaneDescription.setPreferredSize(dimDesc);
			jScrollPaneDescription.setViewportView(getJTextAreaDescription());
			jScrollPaneDescription.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		}
		return jScrollPaneDescription;
	}
	/**
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJPanelButtons() {
		if(jPanelButtons == null) {
			jPanelButtons = new javax.swing.JPanel(
				new FlowLayout());
			jPanelButtons.add(getJButtonApplyChanges());
			jPanelButtons.add(getJButtonCancel());
		}
		return jPanelButtons;
	}

	/**
	 * Handles the query selected event.
	 * @param e
	 */
	private void handleArtefactSelected(ListSelectionEvent e) {
		try {
			int sel = getJTableArtefacts().getSelectedRow();
			if(sel < 0) {
				return;
			}
			ArtefactInfo data = getArtefactInfoAtRow(sel);
			setDescriptionText(data.getTranslatedDescription());
		} catch(Exception ex) {
			UIExceptionMgr.userException(ex);
		}
	}

	/**
	 * Applies changes.
	 */
	protected abstract void handleApplyChanges();

	/**
	 * Cancels changes.
	 */
	protected abstract void handleCancel();

	/**
	 * Plots an artefact.
	 */
	protected abstract void handlePlotArtefact();

	/**
	 * Edits an artefact.
	 */
	protected abstract void handleEditArtefact();

	/**
	 * Adds an artefact.
	 */
	protected void handleAddArtefact() {
		;
	}

	/**
	 * Removes an artefact.
	 */
	protected abstract void handleRemoveArtefact();

	/**
	 * @param e
	 */
	protected void handleShowPopupOnArtefactsTable(MouseEvent e) {
		try {
			if(fActionAdd != null) {
				getJMenuItemAdd().setVisible(true);
			}
			getJMenuItemPlot().setVisible(false);
			getJMenuItemEdit().setVisible(false);
			getJMenuItemRemove().setVisible(false);
			getJMenuItemViewXML().setVisible(false);
			JTable table = getJTableArtefacts();
			int sel = table.getSelectedRow();
			if(sel >= 0) {
				ArtefactInfo ai = getArtefactInfoAtRow(sel);
				if(showPlotMenuItemForArtefact(ai)) {
					getJMenuItemPlot().setVisible(true);
				}
				getJMenuItemEdit().setVisible(true);
				getJMenuItemRemove().setVisible(true);
				getJMenuItemViewXML().setVisible(true);
			}
			getJPopupMenu().show(e.getComponent(), e.getX(), e.getY());
		} catch(Exception ex) {
			UIExceptionMgr.userException(ex);
		}
	}

	/**
	 * @param ai
	 * @return whether or not to show the Plot menu item for the given artefact; the default
	 * implementation will return true if the artefact is enabled and committed
	 */
	protected boolean showPlotMenuItemForArtefact(ArtefactInfo ai) {
		return ai.getFlag(ArtefactInfo.ENABLED) && ai.isCommitted();
	}

    /**
     * @param rowIndex
     * @return
     */
    protected ArtefactInfo getArtefactInfoAtRow(int rowIndex) {
        return (ArtefactInfo)getJTableArtefacts().getValueAt(rowIndex, 1);
    }

	/**
	 * @param e
	 */
	protected void handleShowPopupOnArtefactsTablePanel(MouseEvent e) {
		try {
			getJMenuItemPlot().setVisible(false);
			getJMenuItemEdit().setVisible(false);
			getJMenuItemRemove().setVisible(false);
			getJMenuItemViewXML().setVisible(false);
			if(fActionAdd != null) {
				getJMenuItemAdd().setVisible(true);
			}
			getJPopupMenu().show(e.getComponent(), e.getX(), e.getY());
		} catch(Exception ex) {
			UIExceptionMgr.userException(ex);
		}
	}

	/**
	 * Handles artefacts table changed events.
	 * @param e
	 */
	protected void handleArtefactsTableChanged(TableModelEvent e) {
		if(this.fSettingUp) {
			return;
		}
		try {
			if(e.getType() == TableModelEvent.UPDATE) {
				// update buttons state
				calculateCommandButtonsState();
			}
		} catch(Exception ex) {
			UIExceptionMgr.userException(ex);
		}
	}

	/**
	 * Sets the description text in the description text area.
	 * @param desc can be null and in this case the area will be cleared
	 */
	protected void setDescriptionText(String desc) {
		getJTextAreaDescription().setText(
				desc != null ? desc : "");
		getJTextAreaDescription().setToolTipText(
				desc != null ? UIUtils.getMultilineHtmlText(desc,
                        UIConfiguration.getMaximumLineLengthForToolTipText()) : "");
		getJTextAreaDescription().setCaretPosition(0);
	}

	/**
	 * Initializes this component.
	 * @param viewContainer
	 * @param sessionData
	 * @param queryRealizer
	 * @param tableModel
	 */
	private void initialize(
			RMSViewContainer viewContainer,
			SessionModel sessionData,
			QueryRealizer queryRealizer,
			SelectableArtefactTableModel<T> tableModel,
			String actionAddArtefact,
			String actionRemoveArtefact,
			String actionEditArtefact,
			String actionPlotArtefact) {
		if(sessionData == null) {
			throw new IllegalArgumentException("null session data");
		}
		if(queryRealizer == null) {
		    fLogReplayMode = true;
		}
		this.fSessionData = sessionData;
		this.fQueryRealizer = queryRealizer;
		this.fViewContainer = viewContainer;
		this.fEventHandler = new EventHandler();
		if(actionAddArtefact != null) {
			this.fActionAdd =  new ActionAddArtefact(actionAddArtefact);
		}
		this.fActionRemove =  new ActionRemoveArtefact(actionRemoveArtefact);
		this.fActionEdit =  new ActionEditArtefact(actionEditArtefact);
		this.fActionPlot =  new ActionPlotArtefact(actionPlotArtefact);
		this.fActionApply = new ActionApplyChanges();
   		this.fActionViewXML = new ActionViewXML(viewContainer) {
			private static final long serialVersionUID = 5007748037706445191L;

			protected String getXML() throws Exception {
				return getXMLDefinitionForSelectedArtefact();
			}
   		};

		this.fActionCancel = new ActionCancel() {
			private static final long serialVersionUID = 8025417229493631676L;

				public void actionPerformed(ActionEvent e) {
					handleCancel();
				}
			};
		// disable mnemonic
		this.fActionCancel.putValue(Action.MNEMONIC_KEY, null);
		this.fActionCancel.setEnabled(false);
		this.fTableModelArtefacts = tableModel;
		this.fTableModelArtefacts.addTableModelListener(fEventHandler);
		// detect row selection in the counters table
		ListSelectionModel lsm = getJTableArtefacts().getSelectionModel();
		lsm.addListSelectionListener(fEventHandler);
		getJTableArtefacts().addMouseListener(fEventHandler);
		getJScrollPaneArtefacts().addMouseListener(fEventHandler);
		add(getJPanelArtefacts(), BorderLayout.CENTER);
		// hide buttons if in log replay mode
		if(!fLogReplayMode) {
		    add(getJPanelButtons(), BorderLayout.SOUTH);
		}
	}

    /**
	 * @return
	 */
	protected abstract String getXMLDefinitionForSelectedArtefact() throws Exception;

	/**
     * @param value
     */
    private void handleShowIdentifiersChanged(boolean value) {
        try {
            // just refresh table
            fTableModelArtefacts.fireTableDataChanged();
        } catch(Exception e) {
            UIExceptionMgr.exception(e);
        }
    }
}