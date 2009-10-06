/*
 * Created on 28-Dec-2003
 */
package com.ixora.common.ui;

import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * A tree node to use with the CheckBoxTreeCellRenderer.
 * @author Daniel Moraru
 */
public class DefaultCheckTreeNode extends DefaultMutableTreeNode {
	/** Checked flag */
	protected boolean checked;
	/** Enabled flag */
	protected boolean enabled;

	/**
	 * Constructor
	 * @param userObject
	 * @param checked whether the node should be in a checked state
	 * @param enabled whether the node should be enabled or not
	 * @param allowsChildren whether the node allows children or not
	 */
	public DefaultCheckTreeNode(Object userObject, boolean checked, boolean enabled, boolean allowsChildren) {
		super(userObject, allowsChildren);
		this.checked = checked;
		this.enabled = enabled;
	}

	/**
	 * Set whether the check node is checked or not. When a node
	 * is checked, the parent all subnodes are considered checked
	 */
	public void setChecked(boolean checked) {
		this.checked = checked;
		Enumeration children = children();
		while(children.hasMoreElements()) {
			DefaultCheckTreeNode node = (DefaultCheckTreeNode)children.nextElement();
			node.setChecked(this.checked);
		}
		// if it is checked and the parent is another
		// DefaultCheckTreeNode and it is not checked then
		// check it
		if(checked) {
			TreeNode parent = getParent();
			if(parent instanceof DefaultCheckTreeNode) {
				DefaultCheckTreeNode cn = (DefaultCheckTreeNode)parent;
				if(!cn.checked) {
					cn.checked = true;
				}
			}
		}
	}

	/**
	 * @return whether the check node is checked or not
	 */
	public boolean isChecked() {
		return this.checked ;
	}

	/**
	 * Sets whether the check node is enabled or not
	 */
	public void setEnabled(boolean enabled)	{
		this.enabled = enabled;
	}

	/**
	 * @return whether the check node is enabled or not
	 */
	public boolean isEnabled() {
		return this.enabled;
	}
}
