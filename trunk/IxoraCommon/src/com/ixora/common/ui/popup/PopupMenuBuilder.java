/*
 * Created on 01-Jan-2004
 */
package com.ixora.common.ui.popup;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * Builds a popup menu using a list of objects.
 * @author Daniel Moraru
 */
public class PopupMenuBuilder {
	/**
	 * Listener.
	 */
	public interface Listener  {
		/**
		 * Invoked when the element in the menu corresponding
		 * to object <code>o</code> from the input list is clicked.
		 * @param o the object from the list that generated
		 * the event
		 */
		void actionPerformed(Object o);
	}

	/** JPopupMenu */
	protected JPopupMenu menu;
	/** List */
	protected Object[] list;
	/** Listener */
	protected Listener listener;

	/**
	 * Constructor.
	 * @param menu the menu for wich to build the list
	 * @param l the listener
	 */
	public PopupMenuBuilder(JPopupMenu menu, Listener l) {
		super();
		if(menu == null) {
			throw new IllegalArgumentException("null menu");
		}
		if(l == null) {
			throw new IllegalArgumentException("null listener");
		}
		this.menu = menu;
		this.listener = l;
	}

	/**
	 * Sets the list.
	 * @param mru
	 */
	@SuppressWarnings("serial")
	public void setList(Object[] lst) {
		if(lst == null) {
			throw new IllegalArgumentException("null list");
		}
		this.list = lst;
		this.menu.removeAll();
		for(int i = 0; i < lst.length; ++i) {
			final Object object = lst[i];
			JMenuItem mi = new JMenuItem(new AbstractAction(object.toString()) {
				public void actionPerformed(ActionEvent e) {
					listener.actionPerformed(object);
				}
			});
			this.menu.add(mi);
		}
	}
}
