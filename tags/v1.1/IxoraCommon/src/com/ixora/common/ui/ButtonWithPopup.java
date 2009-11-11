/*
 * Created on 10-Jan-2004
 */
package com.ixora.common.ui;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 * Simulates a button with a popup menu activator.
 * @author Daniel Moraru
 */
public class ButtonWithPopup
		extends JPanel implements PopupMenuListener, ToolBarComponent {
	private static final long serialVersionUID = 7157072865630462655L;
	/** Main button */
	protected JButton button1;
	/** Secondary, popup activator button */
	protected JToggleButton button2;
	/** Popup menu to show the activator is clicked */
	protected JPopupMenu popup;

	private final class EventHandler implements ActionListener, ChangeListener {
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			showPopupMenu();
		}

		/**
		 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
		 */
		public void stateChanged(ChangeEvent e) {
			if(e.getSource() == button1) {
				button2.getModel().setRollover(button1.getModel().isRollover());
				return;
			} else if(e.getSource() == button2) {
				button1.getModel().setRollover(button2.getModel().isRollover());
			}
		}
	}

	/**
	 * Constructor.
	 * @param action
	 */
	public ButtonWithPopup(Action action) {
		super();
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		button1 = UIFactoryMgr.createButton(action);
		button2 = UIFactoryMgr.createToggleButton();
		button2.setIcon(UIConfiguration.getIcon("arrowd.gif"));
		EventHandler eh = new EventHandler();
		button2.addActionListener(eh);
		button2.addChangeListener(eh);
		button1.addChangeListener(eh);
		Insets orig = button2.getMargin();
		button2.setMargin(new Insets(orig.top, 0, orig.bottom, 0));
		button2.setPreferredSize(
			new Dimension(
				(int)button2.getPreferredSize().getWidth(),
				(int)button1.getPreferredSize().getHeight()));
		add(button1);
		add(button2);
		popup = UIFactoryMgr.createPopupMenu();
		popup.addPopupMenuListener(this);
	}

	/**
	 * Adds the given menu item to the popup menu of this button.
	 * @param menuItem
	 */
	public void add(JMenuItem menuItem) {
		popup.add(menuItem);
	}

	/**
	 * Adds the given menu to the popup menu of this button.
	 * @param menuItem
	 */
	public void add(JMenu menu) {
		popup.add(menu);
	}

	/**
	 * Adds a separator to the popup menu of this button.
	 */
	public void addSeparator() {
		popup.addSeparator();
	}

	/**
	 * Removes the given menu item from the popup menu of this button.
	 * @param menuItem
	 */
	public void remove(JMenuItem menuItem) {
		popup.remove(menuItem);
	}

	/**
	 * Removes the given menu from the popup menu of this button.
	 * @param menuItem
	 */
	public void remove(JMenu menu) {
		popup.remove(menu);
	}

	/**
	 * Subclasses could override this method to build the popup menu
	 * on the fly.
	 * @see javax.swing.event.PopupMenuListener#popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent)
	 */
	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		button2.setSelected(true);
	}

	/**
	 * @see javax.swing.event.PopupMenuListener#popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent)
	 */
	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		button2.setSelected(false);
	}

	/**
	 * @see javax.swing.event.PopupMenuListener#popupMenuCanceled(javax.swing.event.PopupMenuEvent)
	 */
	public void popupMenuCanceled(PopupMenuEvent e) {
		button2.setSelected(false);
	}

	/**
	 * @return the popup menu associated with this button
	 */
	public JPopupMenu getPopupMenu() {
		return popup;
	}

	/**
	 * Shows the popup menu.
	 */
	protected void showPopupMenu() {
		popup.show(button1, 0, button2.getHeight());
	}

// the following methods try to mimic the interface to an abstract button
	 /**
	  * @see com.ixora.common.ui.ToolBarComponent#setText(String)
	  */
	 public void setText(String text) {
		 button1.setText(text);
	 }

	 /**
	  * @see com.ixora.common.ui.ToolBarComponent#setIcon(Icon)
	  */
	 public void setIcon(Icon icon) {
		 button1.setIcon(icon);
	 }

	 /**
	  * @see com.ixora.common.ui.ToolBarComponent#setMnemonic(int)
	  */
	 public void setMnemonic(int m) {
		 button1.setMnemonic(m);
	 }

	 /**
	  * @see com.ixora.common.ui.ToolBarComponent#setMargin(Insets)
	  */
	 public void setMargin(Insets insets) {
		 button1.setMargin(insets);
		 // don't let the scondary button modify its
		 // width
		 Insets orig = button2.getMargin();
		 button2.setMargin(new Insets(insets.top, orig.left, insets.bottom, orig.right));
	 }

	 /**
	  * @see com.ixora.common.ui.ToolBarComponent#setRolloverEnabled(boolean)
	  */
	 public void setRolloverEnabled(boolean rollover) {
	 	button1.setRolloverEnabled(rollover);
		button2.setRolloverEnabled(rollover);
	 }

	/**
	 * @see com.ixora.common.ui.ToolBarComponent#setBorder(Border)
	 */
	public void setBorder(Border border) {
		if(button1 != null && button2 != null) {
			button1.setBorder(border);
			button2.setBorder(border);
		}
	}
}
