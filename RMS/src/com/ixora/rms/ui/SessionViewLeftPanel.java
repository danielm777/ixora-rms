package com.ixora.rms.ui;
import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.ToolTipManager;

import com.ixora.common.ui.UIFactoryMgr;

/*
 * Created on 13-Dec-2003
 */
/**
 * @author Daniel Moraru
 */
public final class SessionViewLeftPanel extends JPanel {
	private static final long serialVersionUID = -4075826936771972820L;
	private javax.swing.JScrollPane jScrollPaneSessionTree;
	private javax.swing.JTree jTreeSession;
	private javax.swing.JSplitPane jSplitPane;
	private javax.swing.JPanel jPanelBottom;

	/**
	 * This is the default constructor
	 */
	public SessionViewLeftPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 */
	private void initialize() {
		this.setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));
		this.add(getJSplitPane(), null);
	}

	/**
	 * @return javax.swing.JScrollPane
	 */
	private javax.swing.JScrollPane getJScrollPaneSessionTree() {
		if(jScrollPaneSessionTree == null) {
			jScrollPaneSessionTree = UIFactoryMgr.createScrollPane();
			jScrollPaneSessionTree.setViewportView(getSessionTree());
		}
		return jScrollPaneSessionTree;
	}

	/**
	 * @return javax.swing.JSplitPane
	 */
	private javax.swing.JSplitPane getJSplitPane() {
		if(jSplitPane == null) {
			jSplitPane = UIFactoryMgr.createSplitPane("vertical1");
			jSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			jSplitPane.setTopComponent(getJScrollPaneSessionTree());
			jSplitPane.setBottomComponent(getJPanelBottom());
			jSplitPane.setResizeWeight(0.7);
			jSplitPane.setBorder(null);
			jSplitPane.setOneTouchExpandable(true);
		}
		return jSplitPane;
	}

	/**
	 * @return javax.swing.JTree
	 */
	public javax.swing.JTree getSessionTree() {
		if(jTreeSession == null) {
			jTreeSession = UIFactoryMgr.createTree();
			ToolTipManager.sharedInstance().registerComponent(jTreeSession);
		}
		return jTreeSession;
	}

	/**
	 * @return javax.swing.JScrollPane
	 */
	private javax.swing.JPanel getJPanelBottom() {
		if(jPanelBottom == null) {
			jPanelBottom = new JPanel(new BorderLayout());
		}
		return jPanelBottom;
	}

	/**
	 * Sets the component to be displayed at the bottom of the
	 * left panel.
	 * @param c
	 */
	public void setBottomComponent(Component c) {
		if(c == null) {
			this.jPanelBottom.removeAll();
		} else {
			this.jPanelBottom.add(c, BorderLayout.CENTER);
		}
		validate();
		repaint();
	}
}
