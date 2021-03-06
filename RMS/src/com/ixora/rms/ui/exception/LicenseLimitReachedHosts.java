/*
 * Created on 20-Feb-2005
 */
package com.ixora.rms.ui.exception;

import com.ixora.common.security.license.exception.LicenseException;
import com.ixora.rms.ui.messages.Msg;

/**
 * @author Daniel Moraru
 */
public final class LicenseLimitReachedHosts extends LicenseException {
	private static final long serialVersionUID = 5203387276221229717L;

	/**
	 * Constructor.
	 */
	public LicenseLimitReachedHosts() {
		super(Msg.ERROR_LICENSE_LIMIT_REACHED_HOSTS, true);
        setRequiresHtmlRenderer();
	}
}
