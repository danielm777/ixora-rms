/*
 * Created on 16-Jun-2005
 */
package com.ixora.common.security.exception;

import com.ixora.common.exception.AppRuntimeException;

/**
 * All security exceptions must extend this class.
 */
public class SecException extends AppRuntimeException {

    public SecException() {
        super();
    }

    public SecException(String message) {
        super(message);
    }

    public SecException(Throwable cause) {
        super(cause);
    }

    public SecException(String message, Throwable cause) {
        super(message, cause);
    }

    public SecException(String msgKey, String[] msgTokens) {
        super(msgKey, msgTokens);
    }

    public SecException(String msgKey, boolean translate) {
        super(msgKey, translate);
    }
}
