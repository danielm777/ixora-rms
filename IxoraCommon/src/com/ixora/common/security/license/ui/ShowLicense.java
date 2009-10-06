/*
 * Created on 27-Jun-2004
 */
package com.ixora.common.security.license.ui;

import java.awt.Window;

/**
 * Shows license information.
 * @author Daniel Moraru
 */
public final class ShowLicense {
	/**
	 * Constructor.
	 */
	private ShowLicense() {
		super();
	}

	/**
	 * Shows license information.
	 * @param parent
	 */
	public static String showLicense(Window parent) {
		return ShowLicenseDialog.showDialog(parent);
	}
}
