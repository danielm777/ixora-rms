/**
 * 12-Jul-2005
 */
package com.ixora.common.security.license.ui;

import javax.swing.JFrame;

import com.ixora.common.security.license.LicenseProvider;

/**
 * @author Daniel Moraru
 */
public class DefaultLicenseProvider implements LicenseProvider {
	private JFrame fOwner;

	/**
	 * @param owner
	 */
	public DefaultLicenseProvider(JFrame owner) {
		super();
		fOwner = owner;
	}

	/**
	 * @see com.ixora.common.security.license.LicenseProvider#getLicense()
	 */
	public String getLicense() {
		return ShowLicense.showLicense(fOwner);
	}
}
