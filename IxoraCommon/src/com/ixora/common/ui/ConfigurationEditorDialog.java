/*
 * Created on 21-Mar-2004
 */
package com.ixora.common.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.ixora.common.ComponentConfiguration;
import com.ixora.common.ConfigurationMgr;
import com.ixora.common.MessageRepository;
import com.ixora.common.messages.Msg;
import com.ixora.common.typedproperties.TypedProperties;
import com.ixora.common.typedproperties.exception.InvalidPropertyValue;
import com.ixora.common.typedproperties.ui.TypedPropertiesEditor;
import com.ixora.common.ui.actions.ActionApply;
import com.ixora.common.ui.actions.ActionCancel;
import com.ixora.common.ui.actions.ActionOk;
import com.ixora.common.update.UpdateComponent;

/**
 * @author Daniel Moraru
 */
public final class ConfigurationEditorDialog extends AppDialog {
	private static final long serialVersionUID = 4000054144103055050L;
	private JTree components;
	private TypedPropertiesEditor editor;
	private EventHandler eventHandler = new EventHandler();
	private boolean error;

	private Action actionApply = new ActionApply() {
		private static final long serialVersionUID = 1864685578167497544L;

		public void actionPerformed(ActionEvent e) {
			handleApply();
		}
	};
	private Action actionOk = new ActionOk() {
		private static final long serialVersionUID = 70886798405805782L;

		public void actionPerformed(ActionEvent e) {
			handleOk();
		}
	};
	private Action actionCancel = new ActionCancel() {
		private static final long serialVersionUID = -5466806320544832869L;

		public void actionPerformed(ActionEvent e) {
			handleCancel();
		}
	};
	private Action actionRestoreDefaults = new ActionRestoreDefaults();

	private final class ActionRestoreDefaults extends AbstractAction {
		private static final long serialVersionUID = -2487519905129945403L;
		ActionRestoreDefaults() {
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.COMMON_UI_ACTIONS_RESTOREDEFAULTS), this);
			ImageIcon icon = UIConfiguration.getIcon("defaults.gif");
			if(icon != null) {
				putValue(Action.SMALL_ICON, icon);
			}
		}
		public void actionPerformed(ActionEvent e) {
			handleRestoreDefaults();
		}

	}

	/**
	 * Event handler.
	 */
	private final class EventHandler
			implements TreeSelectionListener, Observer {
		/**
		 * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
		 */
		public void valueChanged(TreeSelectionEvent e) {
			handleComponentTreeSelectionEvent(e);
		}
		/**
		 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
		 */
		public void update(Observable o, Object arg) {
			handleInEditValueChanged();
		}
	}

	/**
	 * Component data.
	 */
	private final static class ComponentData {
		String component;
		String componentTranslated;
		TypedProperties inEdit;

		ComponentData(String c, String ct, Observer obs) {
			component = c;
			componentTranslated = ct;
			// get edit copy
			ComponentConfiguration conf = ConfigurationMgr.get(component);
			// conf can by null for dummy parent components
			if(conf != null) {
				inEdit = (TypedProperties)conf.clone();
				inEdit.addObserver(obs);
			}
		}
		public String toString() {
			return componentTranslated;
		}
	}

	/**
	 * Component node.
	 */
	private final static class ComponentNode extends DefaultMutableTreeNode {
		private static final long serialVersionUID = 5897754139795561004L;
		ComponentNode(ComponentData cd, Observer obs) {
			super(cd, true);
			// build children
			Collection<String> coll = ConfigurationMgr.getComponentsWithEditableConfigurations(cd.component);
			for(String comp : coll) {
				ComponentData cd2 = new ComponentData(comp, MessageRepository.get(comp, comp), obs);
				add(new ComponentNode(cd2, obs));
			}
		}
		ComponentData getCompoentData() {
			return (ComponentData)this.userObject;
		}
	}

	/**
	 * Renderer
	 */
	private final static class ComponentNodeRender extends DefaultTreeCellRenderer {
		private static final long serialVersionUID = -6574745261413731222L;

		/**
		 * @see javax.swing.JLabel#getIcon()
		 */
		public Icon getIcon() {
			return null;
		}
	}

	/**
	 * @param parent
	 */
	public ConfigurationEditorDialog(Dialog parent) {
		super(parent, HORIZONTAL);
		buildContentPane();
	}
	/**
	 * @param parent
	 * @param orientation
	 */
	public ConfigurationEditorDialog(Frame parent) {
		super(parent, HORIZONTAL);
		buildContentPane();
	}
	/**
	 * @see com.ixora.common.ui.AppDialog#getDisplayPanels()
	 */
	protected Component[] getDisplayPanels() {
		Component[] ret = new Component[] {
				buildLeftPanel(),
				buildRightPanel()
		};
		// all panels built, init connections
		components.addTreeSelectionListener(eventHandler);
		components.setSelectionRow(0);
		return ret;
	}
	/**
	 * @see com.ixora.common.ui.AppDialog#getButtons()
	 */
	protected JButton[] getButtons() {
		return new JButton[] {
			UIFactoryMgr.createButton(actionOk), // default button
			UIFactoryMgr.createButton(actionApply),
			UIFactoryMgr.createButton(actionRestoreDefaults),
			UIFactoryMgr.createButton(actionCancel) // escape button
		};
	}

	/**
	 * @return the left component
	 */
	@SuppressWarnings("unchecked")
	private Component buildLeftPanel() {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
		DefaultTreeModel model = new DefaultTreeModel(root);
		components = UIFactoryMgr.createTree();
		components.setEditable(false);
		components.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		components.setModel(model);
		components.setRootVisible(false);
		components.setCellRenderer(new ComponentNodeRender());
		components.setShowsRootHandles(true);
		Collection<String> coll = ConfigurationMgr.getComponentsWithEditableConfigurations();
		for(Iterator<String> iter = coll.iterator(); iter.hasNext();) {
			String comp = iter.next();
			ComponentData cd = new ComponentData(comp, MessageRepository.get(comp, comp), this.eventHandler);
			root.add(new ComponentNode(cd, this.eventHandler));
		}
		model.nodeChanged(root);
		// expand all nodes
		Enumeration<DefaultMutableTreeNode> e = root.breadthFirstEnumeration();
		DefaultMutableTreeNode n;
		while(e.hasMoreElements()) {
			n = e.nextElement();
			components.expandPath(new TreePath(n.getPath()));
		};
		JScrollPane scroll = new JScrollPane(components);
		scroll.setColumnHeaderView(UIFactoryMgr.createLabel(
			MessageRepository.get(
				UpdateComponent.NAME,
				MessageRepository.get(Msg.COMMON_UI_TEXT_COMPONENTS))));
		scroll.setPreferredSize(new Dimension(160, 100));
		this.actionApply.setEnabled(false);
		this.actionOk.setEnabled(false);
		return scroll;
	}

	/**
	 * @return the right component
	 */
	private JPanel buildRightPanel() {
		JPanel panelEditor = new JPanel(new BorderLayout());
		editor = new TypedPropertiesEditor(true);
		editor.setPreferredSize(new Dimension(350, 350));
		panelEditor.add(editor, BorderLayout.CENTER);
		return panelEditor;
	}

	/**
	 * Applies the changes.
	 */
	private void handleApply() {
		try {
			editor.applyChanges();
			applyInEditConfigurations();
			ConfigurationMgr.saveAll();
		} catch(Exception e) {
			UIExceptionMgr.exception(e);
		}
	}

	/**
	 * Applies the changes.
	 */
	private void handleOk() {
		try {
			handleApply();
			dispose();
		} catch(Exception e) {
			UIExceptionMgr.exception(e);
		}
	}

	/**
	 * Applies the changes.
	 */
	private void handleCancel() {
		try {
			dispose();
		} catch(Exception e) {
			UIExceptionMgr.exception(e);
		}
	}

	/**
	 * Applies the default values.
	 */
	private void handleRestoreDefaults() {
		try {
			this.editor.setValuesToDefaults();
			restoreDefaultsForInEditConfigurations();
		} catch(Exception e) {
			UIExceptionMgr.exception(e);
		}
	}

	/**
	 * @param e
	 */
	private void handleComponentTreeSelectionEvent(TreeSelectionEvent e) {
		try {
		    if(error) {
		        return;
		    }
			// apply changes to the current properties being edited
			// before changing properties

			editor.applyChanges();
			TreePath path = e.getNewLeadSelectionPath();
			if(path == null) {
				return;
			}
			DefaultMutableTreeNode n = (DefaultMutableTreeNode)path.getLastPathComponent();
			if(n == null) {
				return;
			}
			ComponentData cd = (ComponentData)n.getUserObject();
			if(cd.inEdit != null) {
				editor.setTypedProperties(cd.component, cd.inEdit);
			} else {
				editor.setTypedProperties(cd.component, null);
			}
		} catch(InvalidPropertyValue ex) {
		    error = true;
		    // select the original component
		    components.setSelectionPath(e.getOldLeadSelectionPath());
		    error = false;
		    UIExceptionMgr.userException(ex);

		} catch(Exception ex) {
			UIExceptionMgr.userException(ex);
		}
	}

	/**
	 * Applies the in-edit configurations.
	 */
	@SuppressWarnings("unchecked")
	private void applyInEditConfigurations() throws InvalidPropertyValue {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode)
		components.getModel().getRoot();
		// reset in edit configuration
		Enumeration<ComponentNode> e = root.breadthFirstEnumeration();
		// get rid of root
		e.nextElement();
		ComponentData cd;
		while(e.hasMoreElements()) {
			cd = ((ComponentNode)e.nextElement()).getCompoentData();
			if(cd.inEdit != null) {
				ConfigurationMgr.get(cd.component).apply(cd.inEdit);
			}
		}
	}

	/**
	 * Restore the defaults for the in-edit configurations.
	 */
	@SuppressWarnings("unchecked")
	private void restoreDefaultsForInEditConfigurations() {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode)
		components.getModel().getRoot();
		// reset in edit configuration
		Enumeration<ComponentNode> e = root.breadthFirstEnumeration();
		// get rid of root
		e.nextElement();
		ComponentData cd;
		while(e.hasMoreElements()) {
			cd = e.nextElement().getCompoentData();
			if(cd.inEdit != null) {
				cd.inEdit.setDefaults();
			}
		}
	}

	/**
	 *
	 */
	private void handleInEditValueChanged() {
		try {
			this.actionApply.setEnabled(true);
			this.actionOk.setEnabled(true);
		} catch (Exception e) {
			UIExceptionMgr.exception(e);
		}
	}
}
