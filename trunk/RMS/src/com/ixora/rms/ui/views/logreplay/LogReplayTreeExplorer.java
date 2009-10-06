/*
 * Created on 13-Sep-2004
 */
package com.ixora.rms.ui.views.logreplay;

import java.awt.Window;

import com.ixora.rms.client.model.SessionModel;
import com.ixora.rms.client.model.SessionModelTreeNode;
import com.ixora.rms.ui.SessionTreeExplorer;

/**
 * @author Daniel Moraru
 */
public final class LogReplayTreeExplorer implements SessionTreeExplorer {
    /**
     * Constructor.
     */
    public LogReplayTreeExplorer() {
        super();
    }

    /**
     * @see com.ixora.rms.ui.SessionTreeExplorer#expandNode(java.awt.Window, com.ixora.rms.client.model.SessionModel, com.ixora.rms.client.model.SessionModelTreeNode)
     */
    public void expandNode(Window owner, SessionModel model,
            SessionModelTreeNode node) {
        ; // nothing
    }
}
