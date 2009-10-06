/*
 * Created on 27-Sep-2004
 */
package com.ixora.common.typedproperties.exception;

import com.ixora.common.exception.AppException;

/**
 * @author Daniel Moraru
 */
public final class VetoException extends AppException {

    public VetoException(String msgKey, String[] msgTokens) {
        super(msgKey, msgTokens);
    }

    public VetoException(String s) {
        super(s);
    }

    public VetoException(String component, String msgKey, boolean needsLocalizing) {
        super(component, msgKey, needsLocalizing);
    }

    public VetoException(String component, String msgKey, String[] msgTokens) {
        super(component, msgKey, msgTokens);
    }

    public VetoException() {
        super();
    }
}
