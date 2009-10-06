/*
 * Created on 19-Nov-2003
 */
package com.ixora.rms.logging.exception;

import com.ixora.rms.exception.RMSException;

/**
 * DataLogException.
 * @author Daniel Moraru
 */
public class DataLogException extends RMSException {
	public DataLogException(String msgKey, boolean needsLocalizing) {
        super(msgKey, needsLocalizing);
    }

    public DataLogException(String msg, Object o) {
        super(msg, o);
    }

    public DataLogException(String component, String msgKey, boolean needsLocalizing) {
        super(component, msgKey, needsLocalizing);
    }

    public DataLogException(String component, String msgKey, String[] msgTokens, Throwable cause) {
        super(component, msgKey, msgTokens, cause);
    }

    public DataLogException(String component, String msgKey, String[] msgTokens) {
        super(component, msgKey, msgTokens);
    }

    public DataLogException(String component, String msgKey, Throwable cause, boolean needsLocalizing) {
        super(component, msgKey, cause, needsLocalizing);
    }

    public DataLogException(String msgKey, String[] msgTokens) {
        super(msgKey, msgTokens);
    }

    public DataLogException(String msgKey, Throwable cause, boolean needsLocalizing) {
        super(msgKey, cause, needsLocalizing);
    }

    public DataLogException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataLogException(String s) {
        super(s);
    }

    public DataLogException(Throwable cause) {
        super(cause);
    }

    public DataLogException(Exception e) {
		super(e);
	}
}
