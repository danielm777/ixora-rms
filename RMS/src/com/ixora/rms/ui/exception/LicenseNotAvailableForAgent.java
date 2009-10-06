/*
 * Created on 20-Feb-2005
 */
package com.ixora.rms.ui.exception;

import com.ixora.common.security.license.exception.LicenseException;
import com.ixora.rms.ui.messages.Msg;

/**
 * @author Daniel Moraru
 */
public final class LicenseNotAvailableForAgent extends LicenseException {
	/**
	 * Constructor.
	 */
	public LicenseNotAvailableForAgent(String agent) {
		super(Msg.ERROR_LICENSE_NOT_AVAILABLE_FOR_AGENT, new String[]{agent});
        setRequiresHtmlRenderer();
	}
}
