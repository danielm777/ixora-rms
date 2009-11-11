/*
 * Created on Jun 1, 2004
 */
package com.ixora.common.security.license.exception;

import com.ixora.common.messages.Msg;

/**
 * @author Daniel Moraru
 */
public final class InvalidLicense extends LicenseException {
	private static final long serialVersionUID = -283533448705140098L;

	/**
	 * Constructor.
	 */
	public InvalidLicense() {
		super(Msg.COMMON_ERROR_INVALID_LICENSE, true);
	}
}
