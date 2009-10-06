/*
 * Created on 27-Sep-2004
 */
package com.ixora.common.exception;

/**
 * @author Daniel Moraru
 */
public class AppRuntimeException extends RuntimeException {
    /** Delegate */
    protected AppException appException;

    /**
     * Constructor.
     *
     */
    public AppRuntimeException() {
        super();
        appException = new AppException();
    }

    /**
     * Constructor.
     * @param message
     */
    public AppRuntimeException(String message) {
        super(message);
        appException = new AppException(message);
    }

    /**
     * Constructor.
     * @param cause
     */
    public AppRuntimeException(Throwable cause) {
        appException = new AppException(cause);
    }

    /**
     * Constructor.
     * @param message
     * @param cause
     */
    public AppRuntimeException(String message, Throwable cause) {
        appException = new AppException(message, cause);
    }

	/**
	 * AppException constructor comment. This constructor
	 * should be used by exceptions wich require localization
	 * of the message.
	 * @param msgKey the exception message key
	 * @param msgTokens the exception message tokens
	 */
	public AppRuntimeException(String msgKey, String[] msgTokens) {
		super();
		appException = new AppException(msgKey, msgTokens);
	}

	/**
	 * AppException constructor comment. This constructor
	 * should be used by exceptions wich require localization
	 * of the message.
	 * @param msgKey the exception message key
	 * @param translate
	 */
	public AppRuntimeException(String msgKey, boolean translate) {
		super();
		appException = new AppException(msgKey, translate);
	}

	/**
	 * @return the underlying app exception
	 */
	public AppException getAppException() {
		return this.appException;
	}

    /**
     * @see java.lang.Throwable#getCause()
     */
    public Throwable getCause() {
        return appException.getCause();
    }
    /**
     * @see java.lang.Throwable#getLocalizedMessage()
     */
    public String getLocalizedMessage() {
        return appException.getLocalizedMessage();
    }
    /**
     * @see java.lang.Throwable#getMessage()
     */
    public String getMessage() {
        return appException.getMessage();
    }

	/**
	 * @param b
	 */
	public void setIsInternalAppError() {
		appException.setIsInternalAppError();
	}

    /**
     * @param b
     */
    public void setRequiresHtmlRenderer() {
        appException.setRequiresHtmlRenderer();
    }

	/**
	 * @return Returns true if the exception represents
	 * an internal application error rather than just
	 * a simple user error.
	 */
	public boolean isInternalAppError() {
		return appException.isInternalAppError();
	}

    /**
     * @return
     */
    public boolean requiresHtmlRenderer() {
        return appException.requiresHtmlRenderer();
    }
}
