/*
 * Created on 16-Jun-2005
 */
package com.ixora.common.security.exception;

import com.ixora.common.exception.AppRuntimeException;

/**
 * All security exceptions must extend this class.
 */
public class SecurityException extends AppRuntimeException {
	private static final long serialVersionUID = -6709797621966683669L;

	public SecurityException() {
        super();
    }

    public SecurityException(String message) {
        super(message);
    }

    public SecurityException(Throwable cause) {
        super(cause);
    }

    public SecurityException(String message, Throwable cause) {
        super(message, cause);
    }

    public SecurityException(String msgKey, String[] msgTokens) {
        super(msgKey, msgTokens);
    }

    public SecurityException(String msgKey, boolean translate) {
        super(msgKey, translate);
    }
}
