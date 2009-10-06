/*
 * Created on Feb 21, 2005
 */
package com.ixora.common.ui.help;

import java.awt.Component;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;

import com.ixora.common.ui.UIUtils;
import com.ixora.common.ui.help.HelpCustomizer.Callback;

/**
 * @author Daniel Moraru
 */
public class HelpCustomizerDefaultCallback implements Callback {
	protected ImageIcon fIconPrev;
	protected ImageIcon fIconNext;
	protected ImageIcon fIconPrint;
	protected ImageIcon fIconSetupPage;

	protected ImageIcon fIconTabTOC;
	protected ImageIcon fIconTabIndex;
	protected ImageIcon fIconTabSearch;

	protected ImageIcon fIconApp;

	protected String fTitleTabTOC;
	protected String fTitleTabIndex;
	protected String fTitleTabSearch;

	protected String fTipTabTOC;
	protected String fTipTabIndex;
	protected String fTipTabSearch;
	protected int fPanelPadding;
	private int fSplitPaneDividerSize;

	/**
	 * Constructor.
	 * @param iconApp
	 * @param iconPrev
	 * @param iconNext
	 * @param iconPrint
	 * @param iconSetupPage
	 * @param iconTabTOC
	 * @param iconTabIndex
	 * @param iconTabSearch
	 * @param titleTabTOC
	 * @param titleTabIndex
	 * @param titleTabSearch
	 * @param tipTabTOC
	 * @param tipTabIndex
	 * @param tipTabSearch
	 * @param panelPadding
	 * @param splitPaneDividerSize
	 */
	public HelpCustomizerDefaultCallback(ImageIcon iconApp, ImageIcon iconPrev,
			ImageIcon iconNext, ImageIcon iconPrint, ImageIcon iconSetupPage,
			ImageIcon iconTabTOC, ImageIcon iconTabIndex, ImageIcon iconTabSearch,
			String titleTabTOC, String titleTabIndex, String titleTabSearch,
			String tipTabTOC, String tipTabIndex, String tipTabSearch,
			int panelPadding,
			int splitPaneDividerSize) {
		super();
		fIconApp = iconApp;
		fIconPrev = iconPrev;
		fIconNext = iconNext;
		fIconPrint = iconPrint;
		fIconSetupPage = iconSetupPage;

		fIconTabTOC = iconTabTOC;
		fIconTabIndex = iconTabIndex;
		fIconTabSearch = iconTabSearch;

		fTitleTabTOC = titleTabTOC;
		fTitleTabIndex = titleTabIndex;
		fTitleTabSearch = titleTabSearch;

		fTipTabTOC = tipTabTOC;
		fTipTabIndex = tipTabIndex;
		fTipTabSearch = tipTabSearch;

		fPanelPadding = panelPadding;
		fSplitPaneDividerSize = splitPaneDividerSize;
	}

	/**
	 * @see help.HelpCustomizer.Callback#changeSplitPanes(javax.swing.JSplitPane)
	 */
	public void changeSplitPanes(JSplitPane sp) {
		sp.setDividerSize(fSplitPaneDividerSize);
		sp.setDividerLocation(250);
	}

	/**
	 * @see help.HelpCustomizer.Callback#changeToolBar(javax.swing.JToolBar)
	 */
	public void changeToolBar(JToolBar jtb) {
		// no insets
		jtb.setMargin(new Insets(0, 0, 0, 0));
		jtb.setRollover(false);
	}

	/**
	 * @see help.HelpCustomizer.Callback#changeButton(javax.swing.JButton, int)
	 */
	public void changeButton(JButton bt, int b) {
		bt.setMargin(new Insets(1, 1, 1, 1));
		switch(b) {
			case TOOLBAR_BUTTON_PREV:
				if(fIconPrev != null) {
					bt.setIcon(fIconPrev);
				}
				break;
			case TOOLBAR_BUTTON_NEXT:
				if(fIconNext != null) {
					bt.setIcon(fIconNext);
				}
				break;
			case TOOLBAR_BUTTON_PRINT:
				if(fIconPrint != null) {
					bt.setIcon(fIconPrint);
				}
				break;
			case TOOLBAR_BUTTON_SETUP_PAGE:
				if(fIconSetupPage != null) {
					bt.setIcon(fIconSetupPage);
				}
				break;
		}
	}

	/**
	 * @see help.HelpCustomizer.Callback#changeTabbedPane(javax.swing.JTabbedPane)
	 */
	public void changeTabbedPane(JTabbedPane tb) {
		tb.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
	}

	/**
	 * @see help.HelpCustomizer.Callback#getIconForTab(int)
	 */
	public Icon getIconForTab(int t) {
		switch(t) {
			case TAB_TOC:
				return fIconTabTOC;
			case TAB_INDEX:
				return fIconTabIndex;
			case TAB_SEARCH:
				return fIconTabSearch;
		}
		return null;
	}

	/**
	 * @see help.HelpCustomizer.Callback#getTextForTab(int)
	 */
	public String getTextForTab(int t) {
		switch(t) {
			case TAB_TOC:
				return fTitleTabTOC;
			case TAB_INDEX:
				return fTitleTabIndex;
			case TAB_SEARCH:
				return fTitleTabSearch;
		}
		return null;
	}

	/**
	 * @see help.HelpCustomizer.Callback#getTooltipTextForTab(int)
	 */
	public String getTooltipTextForTab(int t) {
		switch(t) {
			case TAB_TOC:
				return fTipTabTOC;
			case TAB_INDEX:
				return fTipTabIndex;
			case TAB_SEARCH:
				return fTipTabSearch;
		}
		return null;
	}

	/**
	 * @see com.ixora.common.ui.help.HelpCustomizer.Callback#changeNavigatorTopPanel(javax.swing.JPanel)
	 */
	public void changeNavigatorTopPanel(JPanel panel, int tab) {
		JLabel labelToRemove = null;
		for(int i = panel.getComponentCount() - 1; i >= 0; --i) {
			Component c = panel.getComponent(i);
			if(c instanceof JLabel) {
				labelToRemove = (JLabel)c;
			}
		}
		if(labelToRemove != null) {
			panel.setBorder(BorderFactory.createEmptyBorder(fPanelPadding, fPanelPadding, fPanelPadding, fPanelPadding));
		}
	}

	/**
	 * @see com.ixora.common.ui.help.HelpCustomizer.Callback#changeFrame(javax.swing.JFrame)
	 */
	public void changeFrame(JFrame f) {
		f.setIconImage(fIconApp.getImage());
		f.setSize(UIUtils.getScreenSize());
		f.validate();
	}
}
