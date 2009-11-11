/*
 * Created on 16-Oct-2004
 */
package com.ixora.rms.ui.dataviewboard.exception;

import com.ixora.rms.exception.RMSException;


/**
 * @author Daniel Moraru
 */
public final class FailedToCreateControl extends RMSException {
	private static final long serialVersionUID = -2362282605223930737L;
	/**
     * Constructor.
     * @param e
     */
    public FailedToCreateControl(Exception e) {
        super(e);
    }
    /**
     * Constructor.
     * @param msg
     * @param needsTranslation
     */
    public FailedToCreateControl(String msg, boolean needsTranslation) {
        super(msg, needsTranslation);
    }
    /**
     * @see java.lang.Throwable#getLocalizedMessage()
     */
    public String getLocalizedMessage() {
        return getCause() != null ? getCause().getLocalizedMessage() : super.getLocalizedMessage();
    }
    /**
     * @see java.lang.Throwable#getMessage()
     */
    public String getMessage() {
        return getCause() != null ? getCause().getMessage() : super.getMessage();
    }
}
