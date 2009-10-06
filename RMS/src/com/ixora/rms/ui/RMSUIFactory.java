/*
 * Created on 10-Jan-2004
 */
package com.ixora.rms.ui;

import java.awt.Component;
import java.awt.Insets;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SpinnerModel;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import com.ixora.common.ui.TextComponentHandler;
import com.ixora.common.ui.ToolBarComponent;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIFactoryDefault;

/**
 * @author Daniel Moraru
 */
public final class RMSUIFactory extends UIFactoryDefault {
	/** Rolloever enabled flag */
	private static final boolean ROLLOVER_ENABLED = false;

	static {
		UIManager.put("Tree.expandedIcon", UIConfiguration.getIcon("minus.gif"));
		UIManager.put("Tree.collapsedIcon", UIConfiguration.getIcon("plus.gif"));
	}

	/**
	 * Toolbar for RMS application. It intercepts calls to
	 * <code>add(Component)</code> and adjust the margins
	 * for some of them.
	 */
	private final static class RMSToolBar extends JToolBar {
		/**
		 * @see java.awt.Container#add(java.awt.Component, int)
		 */
		public Component add(Component comp, int index) {
			return super.add(processComponent(comp), index);
		}
		/**
		 * @see java.awt.Container#add(java.awt.Component, java.lang.Object, int)
		 */
		public void add(Component comp, Object constraints, int index) {
			super.add(processComponent(comp), constraints, index);
		}
		/**
		 * @see java.awt.Container#add(java.awt.Component, java.lang.Object)
		 */
		public void add(Component comp, Object constraints) {
			super.add(processComponent(comp), constraints);
		}
		/**
		 * @see java.awt.Container#add(java.awt.Component)
		 */
		public Component add(Component comp) {
			return super.add(processComponent(comp));
		}

		/**
		 * Processes a component before being added to the toolbar.
		 * @param comp
		 */
		private Component processComponent(Component comp) {
			if(comp instanceof AbstractButton) {
				AbstractButton ab = (AbstractButton)comp;
				ab.setRolloverEnabled(ROLLOVER_ENABLED);
				ab.setMargin(new Insets(1, 1, 1, 1));
			} else if(comp instanceof ToolBarComponent) {
				ToolBarComponent button = (ToolBarComponent)comp;
				button.setRolloverEnabled(ROLLOVER_ENABLED);
				button.setMargin(new Insets(1, 1, 1, 1));
			}
			return comp;
		}
	}

	/**
	 * Constructor.
	 */
	public RMSUIFactory() {
		super();
	}

	/**
	 * @return JToolBar
	 */
	public JToolBar createToolBar() {
		JToolBar ret = new RMSToolBar();
		//ret.setUI(uiToolBar);
		ret.setMargin(new Insets(0, 0, 0, 0));
		ret.setRollover(ROLLOVER_ENABLED);
		return ret;
	}

	/**
	 * @see com.ixora.common.ui.UIFactory#createButton()
	 */
	public JButton createButton() {
		JButton ret = super.createButton();
		ret.setRolloverEnabled(ROLLOVER_ENABLED);
		return ret;
	}

	/**
	 * @see com.ixora.common.ui.UIFactory#createButton(javax.swing.Action)
	 */
	public JButton createButton(Action action) {
		JButton ret = super.createButton(action);
		ret.setRolloverEnabled(ROLLOVER_ENABLED);
		return ret;
	}

	/**
	 * @param action
	 * @return JMenu
	 */
	public JMenu createMenu(Action action) {
		JMenu ret = new JMenu(action);
		ret.setToolTipText(null);
		return ret;
	}

	/**
	 * @param action
	 * @return JMenuItem
	 */
	public JMenuItem createMenuItem(Action action) {
		JMenuItem ret = new JMenuItem(action);
		ret.setToolTipText(null);
		return ret;
	}

	/**
	 * @param action
	 * @return JCheckBoxMenuItem
	 */
	public JCheckBoxMenuItem createCheckBoxMenuItem(Action action) {
		JCheckBoxMenuItem ret = new JCheckBoxMenuItem(action);
		ret.setToolTipText(null);
		return ret;
	}

	/**
	 * @return JSplitPane
	 */
	public JSplitPane createSplitPane() {
		JSplitPane ret = new JSplitPane();
		ret.setBorder(null);
		ret.setDividerSize(UIConfiguration.getSplitPaneDividerSize());
		ret.setOneTouchExpandable(true);
		return ret;
	}

	/**
	 * @return JSpinner
	 */
	public JSpinner createSpinner() {
		JSpinner ret = new JSpinner();
		//ret.setBorder(null);
		return ret;
	}

	/**
	 * @return JSpinner
	 * @param model
	 */
	public JSpinner createSpinner(SpinnerModel model) {
		JSpinner ret = new JSpinner(model);
		//ret.setBorder(null);
		return ret;
	}


	/**
	 * @return JTextArea
	 */
	public JTextArea createTextArea() {
		JTextArea ret = new javax.swing.JTextArea();
		new TextComponentHandler(ret);
		ret.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		return ret;
	}

	/**
	 * @param title
	 * @return Border
	 */
	public TitledBorder createTitledBorder(String title)	{
		return BorderFactory.createTitledBorder(" " + title + " ");
	}

	/**
	 * @see com.ixora.common.ui.UIFactory#createScrollPane()
	 */
	public JScrollPane createScrollPane() {
		JScrollPane ret = new JScrollPane();
		return ret;
	}

	/**
	 * @see com.ixora.common.ui.UIFactory#createTree()
	 */
	public JTree createTree() {
		JTree tree = new javax.swing.JTree();
		tree.putClientProperty("JTree.lineStyle", "Angled");
		tree.setShowsRootHandles(true);
		return tree;
	}

	/**
	 * @see com.ixora.common.ui.UIFactory#createTabbedPane()
	 */
	public JTabbedPane createTabbedPane() {
		JTabbedPane tabpane = new JTabbedPane();
		tabpane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		return tabpane;
	}
}
