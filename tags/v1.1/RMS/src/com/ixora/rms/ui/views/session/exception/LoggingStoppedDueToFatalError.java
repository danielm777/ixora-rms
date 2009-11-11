/*
 * Created on 20-Feb-2005
 */
package com.ixora.rms.ui.views.session.exception;

import com.ixora.common.security.license.exception.LicenseException;
import com.ixora.rms.ui.messages.Msg;

/**
 * @author Daniel Moraru
 */
public final class LoggingStoppedDueToFatalError extends LicenseException {
	private static final long serialVersionUID = 2296480395226890860L;

	/**
	 * Constructor.
	 */
	public LoggingStoppedDueToFatalError(Throwable t) {
		super(Msg.ERROR_LOGGING_STOPPED_DUE_TO_FATAL_ERROR,
				new String[]{t.getMessage() != null ? t.getMessage() : t.toString()});
		setIsInternalAppError();
	}
}
