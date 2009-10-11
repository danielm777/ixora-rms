/*
 * Created on 06-Jul-2004
 */
package com.ixora.rms.ui.artefacts.provider;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.ixora.rms.ResourceId;
import com.ixora.common.MessageRepository;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.ui.ShowException;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.ui.popup.PopupListener;
import com.ixora.rms.client.model.AgentNode;
import com.ixora.rms.client.model.EntityNode;
import com.ixora.rms.client.model.HostNode;
import com.ixora.rms.client.model.ProviderInstanceInfo;
import com.ixora.rms.client.model.SessionModel;
import com.ixora.rms.client.model.SessionNode;
import com.ixora.rms.services.ParserRepositoryService;
import com.ixora.rms.services.ProviderInstanceRepositoryService;
import com.ixora.rms.services.ProviderRepositoryService;
import com.ixora.rms.ui.RMSViewContainer;
import com.ixora.rms.ui.artefacts.provider.messages.Msg;

/**
 * A panel that shows the provider instances for an agent.
 * @author Daniel Moraru
 */
public final class ProviderInstancePanel extends JPanel {
	private static final long serialVersionUID = 4569368307487735263L;
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(ProviderInstancePanel.class);
	/** View container */
	private RMSViewContainer fViewContainer;
    /** Event handler */
    private EventHandler fEventHandler;
    /** Current context */
    private ResourceId fContext;
    /** Current agent node */
    private AgentNode fAgentNode;
    /** Providers table */
    private JTable fTable;
    /** Providers table scroll pane */
    private JScrollPane fTableScrollPane;
    /** Providers table model */
    private ProviderInstanceTableModel fTableModel;
    /** Session data */
    private SessionModel fSessionModel;
    /** True if in the process of updating the model */
    private boolean fUpdatingModel;

	private ActionViewProviderError fActionViewError;

    private JPopupMenu jPopupMenu;
	private JMenuItem jMenuItemViewError;

	/**
	 * Displays the error for an agent in ERROR state.
	 */
	private final class ActionViewProviderError extends AbstractAction {
		private static final long serialVersionUID = 4820643815408508075L;
		public ActionViewProviderError() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.ACTIONS_VIEW_PROVIDER_ERROR), this);
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			handleViewProviderError();
		}
	}

	/**
	 * Event handler.
	 */
	private final class EventHandler extends PopupListener
		implements SessionModel.FineListener {
		/**
		 * @see com.ixora.common.ui.popup.PopupListener#showPopup(java.awt.event.MouseEvent)
		 */
		protected void showPopup(MouseEvent e) {
			if(e.getSource() == fTable) {
				handleShowPopupOnProvidersTable(e);
				return;
			}
			if(e.getSource() == fTableScrollPane) {
				handleShowPopupOnProvidersTablePanel(e);
				return;
			}
		}
		/**
		 * @see com.ixora.rms.client.model.SessionModel.FineListener#showIdentifiersChanged(boolean)
		 */
		public void showIdentifiersChanged(boolean value) {
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
			// provider state might have changed, refresh table
			handleAgentUpdated(an);
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
     * Constructor.
     * @param viewContainer
     * @param providerInstanceRepository
     * @param providerRepository
     * @param parserRepository
     * @param sessionData
     */
    public ProviderInstancePanel(
           RMSViewContainer viewContainer,
           ProviderInstanceRepositoryService providerInstanceRepository,
		   ProviderRepositoryService providerRepository,
		   ParserRepositoryService parserRepository,
           SessionModel sessionData) {
   		super(new BorderLayout());
   		if(viewContainer == null
   				|| providerInstanceRepository == null
				|| providerRepository == null
				|| parserRepository == null) {
   			throw new IllegalArgumentException("null parameters");
   		}
   		this.fActionViewError = new ActionViewProviderError();
   		this.fViewContainer = viewContainer;
   		this.fSessionModel = sessionData;
   		this.fEventHandler = new EventHandler();
   		this.fTableModel = new ProviderInstanceTableModel();
   		this.fTable = new JTable(this.fTableModel);
   		this.fTable.getColumnModel().getColumn(0).setMaxWidth(25);
   		this.fTableScrollPane = UIFactoryMgr.createScrollPane();
   		this.fTableScrollPane.setViewportView(fTable);
   		add(this.fTableScrollPane, BorderLayout.CENTER);

   		this.fTable.addMouseListener(fEventHandler);
   		this.fTableScrollPane.addMouseListener(fEventHandler);
   		this.fSessionModel.addListener(fEventHandler);
    }

    /**
     * @param context the context of the agent in the session model
     * @param db
     */
    public void setProviderInstances(
            ResourceId context, Collection<ProviderInstanceInfo> db) {
    	if(context == null || context.getRepresentation() != ResourceId.AGENT) {
    		throw new IllegalArgumentException("invalid context: " + context);
    	}
    	this.fContext = context;
    	this.fTableModel.setProviderInstances(db);
    	// cache here the agent node
    	this.fAgentNode = (AgentNode)fSessionModel.getNodeForResourceId(context);
    }

	/**
	 * @return javax.swing.JPopupMenu
	 */
	private JPopupMenu getJPopupMenu() {
		if(jPopupMenu == null) {
			jPopupMenu = UIFactoryMgr.createPopupMenu();
			jPopupMenu.add(getJMenuItemViewError());
		}
		return jPopupMenu;
	}


	/**
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getJMenuItemViewError() {
		if(jMenuItemViewError == null) {
			jMenuItemViewError = UIFactoryMgr.createMenuItem(
					fActionViewError);
		}
		return jMenuItemViewError;
	}


    /**
     * Updates the query group table model.
     */
    private void refreshTableModel() {
    	// check first if the agent node is still in the session model
    	if(fSessionModel.getArtefactContainerForResource(fContext, true) == null) {
    		logger.error("Couldn't find agent " + fContext + " in the session model");
    		this.fTableModel.setProviderInstances(null);
    	} else {
			// reread data from model
			this.fTableModel.setProviderInstances(fAgentNode.getAgentInfo().getProviderInstances());
    	}
    }


	/**
	 * @param e
	 */
	private void handleShowPopupOnProvidersTable(MouseEvent e) {
		try {
			getJMenuItemViewError().setVisible(false);

			int sel = fTable.getSelectedRow();
			if(sel >= 0) {
				ProviderInstanceInfo pi = this.fTableModel.getProviderInstanceInfoAtRow(sel);
				if(pi.getErrorStateException() != null) {
					getJMenuItemViewError().setVisible(true);
				}
			}
			getJPopupMenu().show(e.getComponent(), e.getX(), e.getY());
		} catch(Exception ex) {
			UIExceptionMgr.userException(ex);
		}
	}

	/**
	 * @param e
	 */
	private void handleShowPopupOnProvidersTablePanel(MouseEvent e) {
		try {
			getJMenuItemViewError().setVisible(false);
			getJPopupMenu().show(e.getComponent(), e.getX(), e.getY());
		} catch(Exception ex) {
			UIExceptionMgr.userException(ex);
		}
	}

	/**
	 * @param an
	 */
	private void handleAgentUpdated(AgentNode an) {
		if(fUpdatingModel || an != this.fAgentNode) {
			return;
		}
		// refresh table
		refreshTableModel();
	}

	/**
	 * Displays the error for the selected provider (the provider must be in ERROR state).
	 */
	private void handleViewProviderError() {
        try {
    		int sel = fTable.getSelectedRow();
			if(sel < 0) {
				return;
			}
			ProviderInstanceInfo pi = fTableModel.getProviderInstanceInfoAtRow(sel);
    		Throwable t = pi.getErrorStateException();
    		if(t == null) {
    			return;
    		}
    		ShowException.show(this.fViewContainer.getAppFrame(), t);
        } catch(Exception e) {
            UIExceptionMgr.userException(e);
        }
	}
}
