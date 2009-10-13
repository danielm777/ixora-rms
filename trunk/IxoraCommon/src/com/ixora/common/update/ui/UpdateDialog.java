/*
 * Created on Feb 23, 2004
 */
package com.ixora.common.update.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.ixora.common.MessageRepository;
import com.ixora.common.os.OSUtils;
import com.ixora.common.ui.AppDialog;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.ui.actions.ActionClose;
import com.ixora.common.ui.jobs.UIWorker;
import com.ixora.common.ui.jobs.UIWorkerJobDefault;
import com.ixora.common.update.Module;
import com.ixora.common.update.ModuleUpdateDescriptor;
import com.ixora.common.update.UpdateComponent;
import com.ixora.common.update.UpdateManager;
import com.ixora.common.update.UpdateManagerDefault;
import com.ixora.common.update.UpdatePartDescriptor;
import com.ixora.common.update.ui.messages.Msg;
import com.ixora.common.xml.exception.XMLException;

/**
 * @author Daniel Moraru
 */
public class UpdateDialog extends AppDialog {
	private static final long serialVersionUID = 2484348260172208703L;
	private JButton jButtonCheckUpdates;
	private JButton jButtonGetAllUpdates;
	private JButton jButtonGetSelectedUpdate;
	private Action actionCheckUpdates;
	private Action actionGetAllUpdates;
	private Action actionGetSelectedUpdate;
	private JList jListModules;
	private JTree jTreeUpdates;
	private JTextArea jTextAreaDescription;
	private EventHandler eventHandler;
	private boolean checkedForUpdates;

// accessible to subclasses
	/** Central panel */
	protected JPanel jPanelCenter;
	/** Operation system for which to show updates */
	protected String os;
	/** UIWorker */
	protected UIWorker worker;
	/** Main panel */
	protected JPanel jPanel;
	/** Update manager */
	protected UpdateManager updateManager;

	/**
	 * Event handler.
	 */
	private final class EventHandler
		implements TreeSelectionListener,
			ListSelectionListener {
		/**
		 * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
		 */
		public void valueChanged(TreeSelectionEvent e) {
			handleUpdatesTreeSelectionEvent(e);
		}
		/**
		 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
		 */
		public void valueChanged(ListSelectionEvent e) {
			handleModuleListSelectionEvent(e);
		}
	}

	/**
	 * Update info
	 */
	private final static class ModuleUpdateInfo {
		ModuleUpdateDescriptor descriptor;

		ModuleUpdateInfo(ModuleUpdateDescriptor d) {
			descriptor = d;
		}

		public String toString() {
			return descriptor.toString()
			+ " ["
			+ (descriptor.isInstalled() ? "installed" : "not installed")
			+ "]";
		}
	}

	/**
	 * Renderer
	 */
	@SuppressWarnings("serial")
	private final static class ModuleUpdateNodeRender extends DefaultTreeCellRenderer {
		/**
		 * @see javax.swing.JLabel#getIcon()
		 */
		public Icon getIcon() {
			return null;
		}
	}

	/**
	 * Check updates action.
	 */
	@SuppressWarnings("serial")
	private final class CheckUpdatesAction extends AbstractAction {
		public CheckUpdatesAction(){
			UIUtils.setUsabilityDtls(MessageRepository.get(
				UpdateComponent.NAME,
				Msg.UPDATE_UI_ACTION_CHECKFORUPDATES), this);
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			handleCheckUpdates();
		}
	}

	/**
	 * Get updates action.
	 */
	@SuppressWarnings("serial")
	private final class GetAllUpdatesAction extends AbstractAction {
		public GetAllUpdatesAction(){
			UIUtils.setUsabilityDtls(MessageRepository.get(
					UpdateComponent.NAME,
					Msg.UPDATE_UI_ACTION_UPDATEALL), this);
			this.enabled = false;
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			handleGetUpdatesAll();
		}
	}

	/**
	 * Get selected update action.
	 */
	@SuppressWarnings("serial")
	private final class GetSelectedUpdateAction extends AbstractAction {
		public GetSelectedUpdateAction(){
			UIUtils.setUsabilityDtls(MessageRepository.get(
					UpdateComponent.NAME,
					Msg.UPDATE_UI_ACTION_UPDATE), this);
			this.enabled = false;
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			handleGetUpdateSelected();
		}
	}

	/**
	 * Constructor.
	 * @param parent
	 */
	protected UpdateDialog(Dialog parent) {
		super(parent, HORIZONTAL);
	}

	/**
	 * Constructor.
	 * @param parent
	 */
	protected UpdateDialog(Frame parent) {
		super(parent, HORIZONTAL);
	}

	/**
	 * @param parent
	 * @param worker
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public UpdateDialog(
				Dialog parent,
				UIWorker worker) throws Exception {
		super(parent, HORIZONTAL);
		init(worker);
	}

	/**
	 * @param parent
	 * @param worker
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public UpdateDialog(
				Frame parent,
				UIWorker worker) throws Exception {
		super(parent, HORIZONTAL);
		init(worker);
	}
	/**
	 * @see com.ixora.common.ui.AppDialog#getDisplayPanels()
	 */
	protected Component[] getDisplayPanels() {
		return new Component[] {jPanel};
	}
	/**
	 * @see com.ixora.common.ui.AppDialog#getButtons()
	 */
	@SuppressWarnings("serial")
	protected JButton[] getButtons() {
		JButton close = UIFactoryMgr.createButton(new ActionClose() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		return new JButton[] {
			jButtonCheckUpdates, // default
			jButtonGetAllUpdates,
			jButtonGetSelectedUpdate,
			close}; // 'escape' button
	}
	/**
	 * Allows subclasses to set the modules to show.
	 * @param modules
	 */
	protected void setModules(Module[] modules) {
		jListModules.setListData(modules);
		if(modules != null && modules.length > 0) {
			jListModules.setSelectedIndex(0);
		}
	}
	/**
	 * Allows subclasses to set the operating system for which
	 * to show updates.
	 * @param os
	 */
	protected void setOs(String os) {
		this.os = os;
		int sel = jListModules.getSelectedIndex();
		jListModules.clearSelection();
		jListModules.setSelectedIndex(sel);
	}
	/**
	 * @param updateManager
	 * @param worker
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	protected void init(UIWorker worker) throws Exception {
		createUpdateManager();
		this.worker = worker;
		this.os = OSUtils.getOsName();
		eventHandler = new EventHandler();
		jListModules = new JList(updateManager.getRegisteredModules());
		jTreeUpdates = UIFactoryMgr.createTree();
		jTreeUpdates.setModel(null);
		jTreeUpdates.setCellRenderer(new ModuleUpdateNodeRender());
		actionCheckUpdates = new CheckUpdatesAction();
		actionGetAllUpdates = new GetAllUpdatesAction();
		actionGetSelectedUpdate = new GetSelectedUpdateAction();
		jButtonCheckUpdates = new JButton(actionCheckUpdates);
		jButtonGetSelectedUpdate = new JButton(actionGetSelectedUpdate);
		jButtonGetAllUpdates = new JButton(actionGetAllUpdates);
		jTextAreaDescription = new JTextArea();
		jTextAreaDescription.setEditable(false);
		jTextAreaDescription.setBackground(this.getBackground());
		// modules panel
		JPanel jPanelModules = new JPanel(new BorderLayout());
		JScrollPane scroll = new JScrollPane(jListModules);
		scroll.setColumnHeaderView(UIFactoryMgr.createLabel(
			MessageRepository.get(
				UpdateComponent.NAME,
				Msg.UPDATE_UI_TEXT_MODULES)));
		scroll.setPreferredSize(new Dimension(200, 100));
		jPanelModules.add(scroll, BorderLayout.CENTER);
		// components panel
		JPanel jPanelUpdates = new JPanel(new BorderLayout());
		scroll = new JScrollPane(jTreeUpdates);
		scroll.setColumnHeaderView(UIFactoryMgr.createLabel(
			MessageRepository.get(
				UpdateComponent.NAME,
				Msg.UPDATE_UI_TEXT_UPDATES)));
		scroll.setPreferredSize(new Dimension(300, 100));
		jPanelUpdates.add(scroll, BorderLayout.CENTER);

		jPanelCenter = new JPanel();
		jPanelCenter.setLayout(new BoxLayout(jPanelCenter, BoxLayout.X_AXIS));
		jPanelCenter.add(jPanelModules);
		jPanelCenter.add(Box.createHorizontalStrut(UIConfiguration.getPanelPadding()));
		jPanelCenter.add(jPanelUpdates);

		JPanel jPanelSouth = new JPanel();
		jPanelSouth.setLayout(new BoxLayout(jPanelSouth, BoxLayout.Y_AXIS));
		scroll = new JScrollPane(jTextAreaDescription);
		scroll.setPreferredSize(new Dimension(200, 50));
		jPanelSouth.add(Box.createVerticalStrut(UIConfiguration.getPanelPadding()));
		jPanelSouth.add(scroll);

		this.jPanel = new JPanel(new BorderLayout());
		this.jPanel.add(jPanelCenter, BorderLayout.CENTER);
		this.jPanel.add(jPanelSouth, BorderLayout.SOUTH);
		jTreeUpdates.getSelectionModel().addTreeSelectionListener(eventHandler);
		jListModules.getSelectionModel().addListSelectionListener(eventHandler);

		buildContentPane();
		setPreferredSize(new Dimension(500, 350));
	}

	/**
	 * Creates the update manager to use.
	 * @throws IOException
	 * @throws URISyntaxException
	 * @throws StartableError
	 */
	protected void createUpdateManager()
		throws URISyntaxException, IOException, Exception {
		this.updateManager = new UpdateManagerDefault();
	}

	/**
	 * Checks updates.
	 */
	private void handleCheckUpdates() {
		try {
			worker.runJob(new UIWorkerJobDefault(
					(Window)SwingUtilities.getRoot(this),
					Cursor.WAIT_CURSOR,
					MessageRepository.get(
							UpdateComponent.NAME,
							Msg.UPDATE_UI_TEXT_CHECKING_FOR_UPDATES)) {
				public void work() throws Exception {
					updateManager.refresh();
				}
				public void finished(Throwable ex) {
					checkedForUpdates = true;
					// select first component if none selected
					if(jListModules.getModel().getSize() > 0) {
						jListModules.clearSelection();
						jListModules.setSelectedIndex(0);
						actionGetAllUpdates.setEnabled(true);
					}
				}
				});
		} catch (Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Gets all updates.
	 */
	private void handleGetUpdatesAll() {
		try {
			worker.runJob(new UIWorkerJobDefault(
					(Window)SwingUtilities.getRoot(this),
					Cursor.WAIT_CURSOR,
					MessageRepository.get(
							UpdateComponent.NAME,
							Msg.UPDATE_UI_TEXT_INSTALLING_UPDATES)) {
				public void work() throws Exception {
					updateManager.installAllUpdates();
				}
				public void finished(Throwable ex) {
					if(ex == null) {
						DefaultTreeModel model = (DefaultTreeModel)jTreeUpdates.getModel();
						if(model == null) {
							return;
						}
						model.nodeChanged((TreeNode)model.getRoot());
						actionGetAllUpdates.setEnabled(false);
						actionGetSelectedUpdate.setEnabled(false);
						if(updateManager.needToRestartApplication()) {
							jTextAreaDescription.setText(MessageRepository.get(
									UpdateComponent.NAME,
									Msg.UPDATE_UI_TEXT_NEED_APPLICATION_RESTART));
						}
					}
				}
				});
		} catch (Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Updates the selected components.
	 */
	private void handleGetUpdateSelected() {
		try {
			Object o = jListModules.getSelectedValue();
			if(o == null) {
				return;
			}
			final Module m = (Module)o;
			TreePath path = jTreeUpdates.getSelectionPath();
			if(path == null) {
				return;
			}
			final DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getPathComponent(0);
			ModuleUpdateInfo mui = (ModuleUpdateInfo)node.getUserObject();
			if(mui.descriptor.isInstalled()) {
				return;
			}
			final ModuleUpdateDescriptor desc = mui.descriptor;
			worker.runJob(new UIWorkerJobDefault(
					(Window)SwingUtilities.getRoot(this),
					Cursor.WAIT_CURSOR,
					MessageRepository.get(
							UpdateComponent.NAME,
							Msg.UPDATE_UI_TEXT_INSTALLING_UPDATES)) {
				public void work() throws Exception {
					updateManager.installUpdate(m, desc.getUpdateId());
				}
				public void finished(Throwable ex) {
					if(ex == null) {
						DefaultTreeModel model = (DefaultTreeModel)jTreeUpdates.getModel();
						if(model == null) {
							return;
						}
						model.nodeChanged(node);
						actionGetSelectedUpdate.setEnabled(false);
					}
				}
				});
		} catch (Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * @param e
	 */
	private void handleUpdatesTreeSelectionEvent(TreeSelectionEvent e) {
		try {
			TreePath path = e.getPath();
			if(path == null) {
				return;
			}
			Object o = ((DefaultMutableTreeNode)path.getPathComponent(0)).getUserObject();
			ModuleUpdateInfo mui = ((ModuleUpdateInfo)o);
			jTextAreaDescription.setText(mui.descriptor.getUpdateDescription());
			if(!mui.descriptor.isInstalled()) {
				actionGetSelectedUpdate.setEnabled(true);
			} else {
				actionGetSelectedUpdate.setEnabled(false);
			}
		} catch(Exception e1) {
			UIExceptionMgr.userException(e1);
		}
	}

	/**
	 * @param e
	 */
	private void handleModuleListSelectionEvent(ListSelectionEvent e) {
		try {
			jTextAreaDescription.setText(null);
			showUpdatesForSelectedModule();
		} catch(Exception e1) {
			UIExceptionMgr.userException(e1);
		}
	}

	/**
	 * Shows updates for the selected module.
	 * @throws IOException
	 * @throws XMLException
	 */
	private void showUpdatesForSelectedModule() throws IOException, XMLException {
		if(!checkedForUpdates) {
			return;
		}
		Module module = (Module)jListModules.getSelectedValue();
		if(module == null) {
			jTreeUpdates.setModel(null);
			return;
		}
		ModuleUpdateDescriptor update =
			updateManager.getLatestUpdate(module);
		if(update == null) {
			jTreeUpdates.setModel(null);
			return;
		}
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(new ModuleUpdateInfo(update));
		jTreeUpdates.setModel(new DefaultTreeModel(root));
		UpdatePartDescriptor[] components = update.getComponents(this.os);
		if(components != null) {
			UpdatePartDescriptor descriptor;
			for(int i = 0; i < components.length; i++) {
				descriptor = components[i];
				root.add(new DefaultMutableTreeNode(descriptor));
			}
		}
		jTreeUpdates.expandPath(new TreePath(root.getPath()));
	}
}
