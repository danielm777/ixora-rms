/*
 * Created on 26-Feb-2005
 */
package com.ixora.common.security.license.exception;

import com.ixora.common.messages.Msg;

/**
 * @author Daniel Moraru
 */
public final class LicenseExpired extends LicenseException {
	private static final long serialVersionUID = -6602824599131838335L;

	/**
	 * Constructor.
	 */
	public LicenseExpired() {
	    super(Msg.COMMON_ERROR_LICENSE_EXPIRED, true);
	    setRequiresHtmlRenderer();
	}
}
