/*
 * Created on 17-Jun-2004
 */
package com.ixora.common.update.multinode.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import com.ixora.common.MessageRepository;
import com.ixora.common.remote.ServiceState;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.ui.jobs.UIWorker;
import com.ixora.common.ui.jobs.UIWorkerJobDefault;
import com.ixora.common.update.UpdateComponent;
import com.ixora.common.update.multinode.UpdateManagerMultiNode;
import com.ixora.common.update.multinode.UpdateManagerMultiNodeDefault;
import com.ixora.common.update.multinode.UpdateableFactory;
import com.ixora.common.update.multinode.ui.messages.Msg;
import com.ixora.common.update.ui.UpdateDialog;

/**
 * @author Daniel Moraru
 */
public final class UpdateMultinodeDialog extends UpdateDialog {
	private static final long serialVersionUID = 3217073474875148244L;
	/** Cache here to avoid casting all the time */
	private UpdateManagerMultiNode updateManagerMulti;
	/** Nodes panel */
	private JPanel jPanelNodes;
	/** Nodes list */
	private JList jListNodes;
	/** Model for the list of nodes */
	private DefaultListModel modelNodes;
	/** Action add node */
	private Action actionAddNode;
	/** Event handler */
	private EventHandler eventHandler;
	/** RMI updateable object name */
	private String rmiObjectName;
	/** RMI regitry port */
	private int rmiRegistryPort;
	/** UI worker */
	private UIWorker worker;

	/**
	 * Add node action.
	 */
	@SuppressWarnings("serial")
	private final class ActionAddNode extends AbstractAction {
		public ActionAddNode() {
			UIUtils.setUsabilityDtls(
					MessageRepository.get(
					UpdateComponent.NAME, Msg.UPDATE_UI_ACTION_ADDNODE), this);
			putValue(Action.NAME, null);
			ImageIcon icon = UIConfiguration.getIcon("add_node.gif");
			if(icon != null) {
				putValue(Action.SMALL_ICON, icon);
			}
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			handleAddNode();
		}
	}

	/**
	 * Remove node action.
	 */
	@SuppressWarnings("serial")
	private final class ActionRemoveNode extends AbstractAction {
		public ActionRemoveNode() {
			UIUtils.setUsabilityDtls(MessageRepository.get(
					UpdateComponent.NAME, Msg.UPDATE_UI_ACTION_REMOVENODE), this);
			putValue(Action.NAME, null);
			ImageIcon icon = UIConfiguration.getIcon("remove_node.gif");
			if(icon != null) {
				putValue(Action.SMALL_ICON, icon);
			}
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			handleRemoveNode();
		}
	}

	/**
	 * Element in the node jList.
	 */
	private static final class NodeData {
		ServiceState state;
		String host;
		String updateStatus;
		NodeData(String host) {
			this.host = host;
			this.state = ServiceState.UNKNOWN;
			this.updateStatus = MessageRepository.get(UpdateComponent.NAME,
					Msg.UPDATE_UI_TEXT_STATUS_NODE_NOT_UPDATED);
		}
		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object obj) {
			if(obj == this) {
				return true;
			}
			if(!(obj instanceof NodeData)) {
				return false;
			}
			NodeData that = (NodeData)obj;
			return host.equals(that.host);
		}
		/**
		 * @see java.lang.Object#hashCode()
		 */
		public int hashCode() {
			return host.hashCode();
		}
		/**
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return host + "[" + updateStatus + "]";
		}
	}

	/**
	 * Cell renderer for the list of nodes.
	 */
	@SuppressWarnings("serial")
	private static final class NodeListCellRenderer extends DefaultListCellRenderer {
		private final static ImageIcon iconOffline = UIConfiguration.getIcon("node_offline.gif");
		private final static ImageIcon iconOnline = UIConfiguration.getIcon("node_online.gif");

		public Component getListCellRendererComponent(
		  JList list,
		  Object value,            // value to display
		  int index,               // cell index
		  boolean isSelected,      // is the cell selected
		  boolean cellHasFocus)    // the list and the cell have the focus
		{
			JLabel ret = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			NodeData nd = (NodeData)value;
			ret.setIcon(nd.state == ServiceState.ONLINE ? iconOnline : iconOffline);
			return this;
		}
	}

	/**
	 * Event handler.
	 */
	private final class EventHandler implements UpdateManagerMultiNode.Listener {
		/**
		 * @see com.ixora.common.update.multinode.UpdateManagerMultiNode.Listener#nodeUpdateStatus(java.lang.String, boolean)
		 */
		public void nodeUpdateStatus(final String node, final boolean ok) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					handleNodeUpdateStatus(node, ok);
				}
			});
		}

		/**
		 * @see com.ixora.common.update.multinode.UpdateManagerMultiNode.Listener#nodeStateChanged(java.lang.String, com.ixora.common.remote.ServiceState)
		 */
		public void nodeStateChanged(final String node, final ServiceState state) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					handleNodeStateChanged(node, state);
				}
			});

		}
	}

	/**
	 * @param updateableRMIName
	 * @param rmiRegistryPort
	 * @param parent
	 * @param worker
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public UpdateMultinodeDialog(
			String updateableRMIName,
			int rmiRegistryPort,
			Dialog parent,
			UIWorker worker) throws Exception {
		super(parent);
		this.rmiObjectName = updateableRMIName;
		this.rmiRegistryPort = rmiRegistryPort;
		init(worker);
	}

	/**
	 * @param updateableRMIName
	 * @param rmiRegistryPort
	 * @param parent
	 * @param worker
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public UpdateMultinodeDialog(
			String updateableRMIName,
			int rmiRegistryPort,
			Frame parent,
			UIWorker worker) throws Exception {
		super(parent);
		this.rmiObjectName = updateableRMIName;
		this.rmiRegistryPort = rmiRegistryPort;
		init(worker);
	}

	/**
	 * @see com.ixora.common.ui.AppDialog#getDisplayPanels()
	 */
	protected Component[] getDisplayPanels() {
		return new Component[] {
				this.jPanel
		};
	}

	/**
	 * @see com.ixora.common.update.ui.UpdateDialog#createUpdateManager()
	 */
	protected void createUpdateManager() throws URISyntaxException, IOException {
		this.updateManagerMulti = new UpdateManagerMultiNodeDefault(
				new UpdateableFactory(rmiObjectName, rmiRegistryPort),
				this.eventHandler);
		this.updateManager = this.updateManagerMulti;
	}

	/**
	 * @see com.ixora.common.update.ui.UpdateDialog#init(com.ixora.common.ui.jobs.UIWorker)
	 */
	protected void init(UIWorker worker) throws Exception {
		this.worker = worker;
		this.eventHandler = new EventHandler();
		modelNodes = new DefaultListModel();
		jListNodes = new JList(modelNodes);
		jListNodes.setCellRenderer(new NodeListCellRenderer());
		jPanelNodes = new JPanel(new BorderLayout());
		JScrollPane scroll = new JScrollPane(jListNodes);
		scroll.setColumnHeaderView(UIFactoryMgr.createLabel(
			MessageRepository.get(
				UpdateComponent.NAME,
				Msg.UPDATE_UI_TEXT_NODES)));
		scroll.setPreferredSize(new Dimension(150, 100));
		jPanelNodes.add(scroll, BorderLayout.CENTER);
		JPanel jButtons = new JPanel();
		jButtons.setLayout(new FlowLayout());
		JButton addNode = new JButton(
				this.actionAddNode = new ActionAddNode());
		jButtons.add(addNode);
		JButton removeNode = new JButton(
				new ActionRemoveNode());
		jButtons.add(removeNode);
		jPanelNodes.add(jButtons, BorderLayout.SOUTH);
		// call last the super version as this
		// will build the components
		super.init(worker);
		// add the nodes panel to the central panel
		// of the superclass
		jPanelCenter.add(Box.createHorizontalStrut(UIConfiguration.getPanelPadding()), 0);
		jPanelCenter.add(jPanelNodes, 0);
		// resize
		setSize(590, 350);
	}

	/**
	 * @param node
	 * @param ok
	 */
	@SuppressWarnings("unchecked")
	private void handleNodeUpdateStatus(String node, boolean ok) {
		try {
			NodeData nd;
			Enumeration e = modelNodes.elements();
			int i = 0;
			while(e.hasMoreElements()) {
				nd = (NodeData)e.nextElement();
				if(nd.host.equals(node)) {
					if(ok) {
						nd.updateStatus =
							MessageRepository.get(UpdateComponent.NAME,
							Msg.UPDATE_UI_TEXT_STATUS_NODE_UPDATED);
					} else {
						nd.updateStatus =
							MessageRepository.get(UpdateComponent.NAME,
							Msg.UPDATE_UI_TEXT_STATUS_NODE_UPDATED);
					}
					this.modelNodes.set(i, nd);
				}
				++i;
			}
		} catch(Exception e) {
			UIExceptionMgr.exception(e);
		}
	}

	/**
	 * Adds a node.
	 */
	private void handleAddNode() {
		try {
			String tmp = (String)this.actionAddNode.getValue(Action.SHORT_DESCRIPTION);
			tmp = UIUtils.getStringInput(this, tmp, tmp);
			if(tmp == null || tmp.trim().length() == 0) {
				return;
			}
			StringTokenizer tok = new StringTokenizer(tmp);
			Set<String> nodes = new HashSet<String>(tok.countTokens());
			while(tok.hasMoreTokens()) {
				nodes.add(tok.nextToken());
			}
			final String[] na = (String[])nodes.toArray(new String[nodes.size()]);
			// add first to list...
			for(int i = 0; i < na.length; i++) {
				this.modelNodes.addElement(new NodeData(na[i]));
			}
			// then add into the update manager
			this.worker.runJob(new UIWorkerJobDefault(
					this,
					Cursor.WAIT_CURSOR,
					"") {
				public void work() throws Exception {
					updateManagerMulti.addNodes(na);
				}
				public void finished(Throwable ex) {
					if(ex != null) {
						UIExceptionMgr.userException(ex);
					}
				}
			});
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Removes a node.
	 */
	private void handleRemoveNode() {
		try {
			int[] sel = jListNodes.getSelectedIndices();
			if(sel == null || sel.length == 0) {
				return;
			}
			String[] nodes = new String[sel.length];
			NodeData[] nds = new NodeData[sel.length];
			NodeData nd;
			for(int i = 0; i < sel.length; i++) {
				nd = (NodeData)modelNodes.get(i);
				nodes[i] = nd.host;
				nds[i] = nd;
			}
			for(int i = 0; i < nds.length; i++) {
				modelNodes.removeElement(nds[i]);
			}
			this.updateManagerMulti.removeNodes(nodes);
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Handles node state changed event.
	 * @param node
	 * @param state
	 */
	@SuppressWarnings("unchecked")
	private void handleNodeStateChanged(String node, ServiceState state) {
		try {
			NodeData nd;
			Enumeration e = modelNodes.elements();
			int i = 0;
			while(e.hasMoreElements()) {
				nd = (NodeData)e.nextElement();
				if(nd.host.equals(node)) {
					nd.state = state;
					this.modelNodes.set(i, nd);
				}
				++i;
			}
		} catch(Exception e) {
			UIExceptionMgr.exception(e);
		}
	}
}
