/**
 * 03-Feb-2006
 */
package com.ixora.rms.ui.views.session.activation;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.utils.Utils;
import com.ixora.rms.repository.AgentCategory;
import com.ixora.rms.repository.AgentInstallationData;
import com.ixora.rms.services.AgentRepositoryService;

/**
 * @author Daniel Moraru
 */
public final class AgentsTreePanel extends JPanel {
	private static final long serialVersionUID = 3633944220682193063L;
	private JTree fTree;
	private DefaultTreeModel fTreeModel;

	private static class AgentInstallationDataComparator implements Comparator<AgentInstallationData> {
		/**
		 * @see java.util.Comparator#compare(T, T)
		 */
		public int compare(AgentInstallationData o1, AgentInstallationData o2) {
			return o1.getAgentName().compareToIgnoreCase(o2.getAgentName());
		}
	}

	private static class TreeCellRenderer extends DefaultTreeCellRenderer {
		private static final long serialVersionUID = -3087777307081468658L;
		private static final ImageIcon iconAgent = UIConfiguration.getIcon("install_agent_node.gif");
		private static final ImageIcon iconCategory = UIConfiguration.getIcon("install_cat_node.gif");

		public Component getTreeCellRendererComponent(
				JTree tree, Object value, boolean sel,
				boolean expanded, boolean leaf, int row, boolean hasFocus) {
			JLabel label = (JLabel)super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
					row, hasFocus);
			if(value instanceof AgentActivationNode) {
				if(iconAgent != null) {
					label.setIcon(iconAgent);
				}
				label.setFont(getFont().deriveFont(Font.PLAIN));
				AgentData agentData = ((AgentActivationNode)value).getAgentData();
				label.setToolTipText(agentData.getAgentDescription());
				label.setText("<html>" + agentData.getAgentName()
						+ "<font color='#888888'> ("
						+ agentData.getAgentDescription()
						+ ")</font>"
						+ "</html>");
			} else {
				if(iconCategory != null) {
					label.setIcon(iconCategory);
				}
				label.setFont(getFont().deriveFont(Font.BOLD));
			}
			return label;
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("unchecked")
	public AgentsTreePanel(AgentRepositoryService ars) {
		super(new BorderLayout());
		fTree = UIFactoryMgr.createTree();
		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		fTreeModel = new DefaultTreeModel(root);
		fTree.setModel(fTreeModel);
		fTree.setEditable(false);
		fTree.setRootVisible(false);
		fTree.setShowsRootHandles(true);
		fTree.setCellRenderer(new TreeCellRenderer());
		JScrollPane scrollPane = UIFactoryMgr.createScrollPane();
		scrollPane.setViewportView(fTree);
		fTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		ToolTipManager.sharedInstance().registerComponent(fTree);

		Map<String, AgentInstallationData> agents = ars.getInstalledAgents();
		if(!Utils.isEmptyMap(agents)) {
			// build the tree with categories and their assigned agents
			Map<String, List<AgentInstallationData>> perCategory = new HashMap<String, List<AgentInstallationData>>();
			for(AgentInstallationData aid : agents.values()) {
				String category = aid.getCategory();
				if(category == null) {
					category = AgentCategory.MISCELLANEOUS.getStringID();
				}
				List<AgentInstallationData> catList = perCategory.get(category);
				if(catList == null) {
					catList = new LinkedList<AgentInstallationData>();
					perCategory.put(category, catList);
				}
				catList.add(aid);
			}
			List<String> sortedCategories = new ArrayList<String>(perCategory.keySet());
			Collections.sort(sortedCategories);
			sortedCategories.remove(AgentCategory.MISCELLANEOUS.getStringID());
			// add the misc category at the end
			sortedCategories.add(AgentCategory.MISCELLANEOUS.getStringID());
			for(String category : sortedCategories) {
				CategoryNode catNode = new CategoryNode(category);
				root.add(catNode);
				// add agents for category
				List<AgentInstallationData> catAgents = perCategory.get(category);
				// sort list
				Collections.sort(catAgents, new AgentInstallationDataComparator());
				if(!Utils.isEmptyCollection(catAgents)) {
					for(AgentInstallationData aid : catAgents) {
						catNode.add(new AgentActivationNode(aid));
					}
				}
			}
		}
		// expand all nodes
		Enumeration<DefaultMutableTreeNode> e = root.breadthFirstEnumeration();
		while(e.hasMoreElements()) {
			DefaultMutableTreeNode n = e.nextElement();
			fTree.expandPath(new TreePath(n.getPath()));
		};
		add(scrollPane, BorderLayout.CENTER);
	}

	/**
	 * @return
	 */
	public JTree getAgentsTree() {
		return fTree;
	}

	/**
	 * @return
	 */
	public AgentActivationNode getSelectedAgentNode() {
		TreePath path = fTree.getSelectionPath();
		if(path == null) {
			return null;
		}
		Object sel = path.getLastPathComponent();
		if(sel instanceof AgentActivationNode) {
			return (AgentActivationNode)sel;
		}
		return null;
	}

	/**
	 *
	 */
	public void selectFirstAgentNode() {
		fTree.setSelectionRow(1);
	}

	/**
	 * @param node
	 */
	public void selectAgentNode(AgentActivationNode node) {
		fTree.setSelectionPath(new TreePath(node.getPath()));
	}
}
