/*
 * Created on 13-Dec-2003
 */
package com.ixora.common.ui.popup;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Daniel Moraru
 */
public abstract class PopupListener extends MouseAdapter {
	/**
	 * Constrctor.
	 */
	public PopupListener() {
		super();
	}
	/**
	 * @see java.awt.event.MouseAdapter#mousePressed(MouseEvent)
	 */
	public void mousePressed(MouseEvent e) {
		maybeShowPopup(e);
	}
	/**
	 * @see java.awt.event.MouseAdapter#mouseReleased(MouseEvent)
	 */
	public void mouseReleased(MouseEvent e) {
		maybeShowPopup(e);
	}
	/**
	 * Tests if the mouse event is a
	 * @param e
	 */
	private void maybeShowPopup(MouseEvent e) {
		if(e.isPopupTrigger()) {
			showPopup(e);
		}
	}

	/**
	 * Invoked when the popup menu should be displayed.
	 * @param e
	 */
	protected abstract void showPopup(MouseEvent e);
}
