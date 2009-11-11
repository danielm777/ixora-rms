/*
 * Created on 22-Mar-2005
 */
package com.ixora.rms.ui;

import javax.swing.Icon;
import javax.swing.JLabel;

import com.ixora.rms.client.model.SessionModelTreeNode;

/**
 * @author Daniel Moraru
 */
public interface SessionModelTreeNodeDecorator {
	/**
	 * Decorates the label associated with the given node.
	 * @param node
	 * @param icon
	 * @param showCheckbox
	 * @param label
	 */
	void decorate(SessionModelTreeNode node, Icon itemIcon, boolean showCheckbox, JLabel label);
}