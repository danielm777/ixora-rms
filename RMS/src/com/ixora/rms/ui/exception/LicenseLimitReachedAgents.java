/*
 * Created on 20-Feb-2005
 */
package com.ixora.rms.ui.exception;

import com.ixora.common.security.license.exception.LicenseException;
import com.ixora.rms.ui.messages.Msg;

/**
 * @author Daniel Moraru
 */
public final class LicenseLimitReachedAgents extends LicenseException {
	private static final long serialVersionUID = 1109042289206484838L;

	/**
	 * Constructor.
	 */
	public LicenseLimitReachedAgents() {
		super(Msg.ERROR_LICENSE_LIMIT_REACHED_AGENTS, true);
        setRequiresHtmlRenderer();
	}
}
