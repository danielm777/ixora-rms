/*
 * Created on 19-Aug-2004
 */
package com.ixora.rms.ui;

import java.awt.Window;

import com.ixora.rms.client.model.SessionModel;
import com.ixora.rms.client.model.SessionModelTreeNode;

/**
 * @author Daniel Moraru
 */
public interface SessionTreeExplorer {
    /**
     * Expands the session tree node.
     * @param owner the owner of the session tree
     * @param model session model
     * @param node node to be expanded
     */
    void expandNode(Window owner, SessionModel model, SessionModelTreeNode node);
}
