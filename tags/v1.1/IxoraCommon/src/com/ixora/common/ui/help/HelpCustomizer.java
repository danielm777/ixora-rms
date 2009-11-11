/*
 * Created on Feb 21, 2005
 */
package com.ixora.common.ui.help;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.help.JHelp;
import javax.help.JHelpNavigator;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;

/**
 * Customizer for the JavaHelp frame.
 * @author Daniel Moraru
 */
public class HelpCustomizer extends WindowAdapter {
	/**
	 * Invoked to retrieve customization information.
	 */
	public interface Callback {
		// buttons
		public static final int TOOLBAR_BUTTON_PREV = 0;
		public static final int TOOLBAR_BUTTON_NEXT = 1;
		public static final int TOOLBAR_BUTTON_PRINT = 3;
		public static final int TOOLBAR_BUTTON_SETUP_PAGE = 4;

		public static final int TAB_TOC = 0;
		public static final int TAB_INDEX = 1;
		public static final int TAB_SEARCH = 2;

		void changeSplitPanes(JSplitPane sp);
		void changeFrame(JFrame f);
		void changeToolBar(JToolBar tb);
		void changeButton(JButton bt, int b);
		void changeTabbedPane(JTabbedPane tb);
		void changeNavigatorTopPanel(JPanel panel, int t);
		Icon getIconForTab(int t);
		String getTextForTab(int t);
		String getTooltipTextForTab(int t);
	}

	private boolean fDone;
	private Callback fCallback;

    /**
     * @param appFrame
     * @param cb
     */
	public HelpCustomizer(JFrame appFrame, Callback cb) {
		if(cb == null) {
			throw new IllegalArgumentException("Callback is null");
		}
		fCallback = cb;
		appFrame.addWindowListener(this);
	}

    /**
     * @param win
     */
    public void addPotentialOwner(Window win) {
        win.addWindowListener(this);
    }

	/**
	 * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent)
	 */
	public void windowDeactivated(WindowEvent e) {
		if(fDone) {
			return;
		}
		Frame[] frames = JFrame.getFrames();
		for (int i = 0; i < frames.length; i++) {
			Frame f = frames[i];
			if (!(f instanceof JFrame)) {
				continue;
			}
			JFrame jf = (JFrame)f;
			if (jf.getContentPane().getComponentCount() == 0) {
				continue;
			}
			Component c = jf.getContentPane().getComponent(0);
			if (c == null || !(c instanceof JHelp)) {
				continue;
			}
			// now we are in JavaHelp frame
			fCallback.changeFrame(jf);
			JHelp jh = (JHelp)c;
			for(int j = jh.getComponentCount() - 1; j >= 0; --j) {
				c = jh.getComponent(j);
				if(c == null) {
					continue;
				}
				if(c instanceof JToolBar) {
					// toolbar
					JToolBar jtb = (JToolBar)c;
					fCallback.changeToolBar(jtb);
					for(int k = jtb.getComponentCount() - 1; k >= 0; --k) {
						c = jtb.getComponent(k);
						if(c instanceof JButton) {
							// buttons
							fCallback.changeButton((JButton)c, k);
						}
					}
				} else if(c instanceof JSplitPane) {
					// split pane
					JSplitPane jsp = (JSplitPane)c;
					fCallback.changeSplitPanes(jsp);
					for(int k = jsp.getComponentCount() - 1; k >= 0; --k) {
						c = jsp.getComponent(k);
						if(c instanceof JTabbedPane) {
							JTabbedPane jtb = (JTabbedPane)c;
							fCallback.changeTabbedPane(jtb);
							for(int s = jtb.getTabCount() - 1; s >= 0; --s) {
								Icon icon = fCallback.getIconForTab(s);
								jtb.setIconAt(s, icon);
								String txt = fCallback.getTextForTab(s);
								if(txt != null) {
									jtb.setTitleAt(s, txt);
								}
								txt = fCallback.getTooltipTextForTab(s);
								jtb.setToolTipTextAt(s, txt);
								c = jtb.getComponentAt(s);
								if(c instanceof JHelpNavigator) {
									JHelpNavigator jin = (JHelpNavigator)c;
									for(int k1 = jin.getComponentCount() - 1; k1 >= 0; --k1) {
										c = jin.getComponent(k1);
										if(c instanceof JPanel) {
											fCallback.changeNavigatorTopPanel((JPanel)c, s);
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
}
