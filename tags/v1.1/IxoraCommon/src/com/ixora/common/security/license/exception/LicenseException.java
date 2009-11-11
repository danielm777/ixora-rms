/*
 * Created on 16-Jun-2005
 */
package com.ixora.common.security.license.exception;

import com.ixora.common.exception.AppRuntimeException;

/**
 * All licensing exceptions must extend this class.
 * LicenseException
 */
public class LicenseException extends AppRuntimeException {
	private static final long serialVersionUID = -8218767349722668624L;

	public LicenseException() {
        super();
    }

    public LicenseException(String message) {
        super(message);
    }

    public LicenseException(Throwable cause) {
        super(cause);
    }

    public LicenseException(String message, Throwable cause) {
        super(message, cause);
    }

    public LicenseException(String msgKey, String[] msgTokens) {
        super(msgKey, msgTokens);
    }

    public LicenseException(String msgKey, boolean translate) {
        super(msgKey, translate);
    }
}
