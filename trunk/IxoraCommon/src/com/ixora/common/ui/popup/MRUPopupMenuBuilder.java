/*
 * Created on 01-Jan-2004
 */
package com.ixora.common.ui.popup;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * Builds a popup menu using a list of most recently used objects.
 * @author Daniel Moraru
 */
public class MRUPopupMenuBuilder extends PopupMenuBuilder {
	/**
	 * Constructor.
	 * @param menu the menu for wich to build the MRU list
	 * @param l the listener
	 */
	public MRUPopupMenuBuilder(JPopupMenu menu, Listener l) {
		super(menu, l);
	}

	/**
	 * Sets the MRU list.
	 * @param mru
	 */
	public void setList(Object[] mru) {
		if(mru == null) {
			throw new IllegalArgumentException("null mru list");
		}
		this.list = mru;
		this.menu.removeAll();
		for(int i = 0; i < mru.length; ++i) {
			final Object object = mru[i];
			String temp = (i + 1) + " " + object.toString();
			JMenuItem mi = new JMenuItem(new AbstractAction(temp) {
				public void actionPerformed(ActionEvent e) {
					listener.actionPerformed(object);
				}
			});
			mi.setMnemonic(temp.charAt(0));
			this.menu.add(mi);
		}
	}
}
