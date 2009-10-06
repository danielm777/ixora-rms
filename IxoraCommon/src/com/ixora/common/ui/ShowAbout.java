/*
 * Created on 27-Jun-2004
 */
package com.ixora.common.ui;

import java.awt.Window;

/**
 * Shows product information.
 * @author Daniel Moraru
 */
public final class ShowAbout {
	/**
	 * Constructor.
	 */
	private ShowAbout() {
		super();
	}

	/**
	 * Shows product information.
	 * @param parent
	 */
	public static void showAbout(Window parent) {
		 ShowAboutDialog.showDialog(parent);
	}
}
