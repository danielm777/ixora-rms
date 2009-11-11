/*
 * Created on 28-Aug-2004
 */
package com.ixora.rms.ui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTree;

import com.ixora.rms.ResourceId;
import com.ixora.rms.client.model.SessionModelTreeNode;

/**
 * @author Daniel Moraru
 */
public class EntityNodeCellRendererContextAware extends EntityNodeCellRenderer {
	private static final long serialVersionUID = 8078581977576049239L;
	/** Context */
    private ResourceId context;

    /**
     * Constructor.
     *
     */
    public EntityNodeCellRendererContextAware(ResourceId context) {
        super();
        this.context = context;
    }

    /**
     * @see com.ixora.common.ui.CheckBoxTreeCellRenderer#getTreeCellComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
     */
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean isSelected, boolean expanded, boolean leaf, int row,
            boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, isSelected, expanded,
                leaf, row, hasFocus);
        if(this.context == null) {
            return this;
        }
        // if current node is outside the context
        // change color
        SessionModelTreeNode sn = (SessionModelTreeNode)value;
        ResourceId rid = sn.getResourceId();
        if(rid == null || !rid.contains(context)) {
            // outside context
            this.setForeground(Color.GRAY);
        }
        return this;
    }
}
