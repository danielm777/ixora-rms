/*
 * Created on 28-Aug-2004
 */
package com.ixora.rms.ui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTree;

import com.ixora.rms.ResourceId;
import com.ixora.rms.client.model.SessionModelTreeNode;
import com.ixora.rms.client.model.SessionNode;

/**
 * @author Daniel Moraru
 */
public final class SessionModelTreeCellRendererContextAware extends
        SessionModelTreeCellRenderer {
	private static final long serialVersionUID = -1457663169557295520L;
	private ResourceId context;
    /**
     * Constructor.
     *
     */
    public SessionModelTreeCellRendererContextAware(ResourceId context) {
       super();
       this.context = context;
       this.entityRenderer = new EntityNodeCellRendererContextAware(context);
    }


    /**
     * @see javax.swing.tree.TreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
     */
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel, boolean expanded, boolean leaf, int row,
            boolean hasFocus) {
        Component  comp = super.getTreeCellRendererComponent(tree, value, sel, expanded,
                leaf, row, hasFocus);
        if(this.context == null) {
        	// exclude session node
            if(value instanceof SessionNode) {
   	            // outside context
   	            comp.setForeground(Color.GRAY);
            }
            return comp;
        }
        // if current node is outside the context
        // change color
        if(value instanceof SessionModelTreeNode) {
	        SessionModelTreeNode sn = (SessionModelTreeNode)value;
	        ResourceId rid = sn.getResourceId();
	        if(rid == null || !rid.contains(context)) {
	            // outside context
	            comp.setForeground(Color.GRAY);
	        }
        }
        return comp;
    }
}
