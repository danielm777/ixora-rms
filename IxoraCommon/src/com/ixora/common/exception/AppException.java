package com.ixora.common.exception;

import com.ixora.common.MessageRepository;



/**
 * Application exception.
 * @author: Daniel Moraru
 */
public class AppException extends Exception {
	private static final long serialVersionUID = 2409585820005581434L;
	/**
	 * If this flag it true then the exception represents a
	 * critical/internal application error.
	 * The exceptions that have this field set to true should
	 * be generating an application error report.
	 */
	protected boolean internalAppError;
	/** Message key if message needs localizing */
	protected String messageKey;
	/** Message tokens */
	protected String[] messageTokens;
	/** Component */
	protected String component;
    /** If true an html renderer will be used to display this exception */
    protected boolean requiresHtmlRenderer;

	/**
	 * BkException constructor comment.
	 */
	public AppException() {
		super();
	}

	/**
	 * BkException constructor comment.
	 * @param s java.lang.String
	 */
	public AppException(String s) {
		super(s);
	}

	/**
	 * BkException constructor comment.
	 * @param s java.lang.Object
	 */
	public AppException(Object s) {
		super(s.toString());
	}

   /**
	 * BkException constructor comment.
	 * @param s java.lang.Exception
	 */
	public AppException(Exception e) {
		this((Throwable)e);
	}

	/**
	 * AppException constructor comment.
	 * @param msg String
	 * @param o Object
	 */
	public AppException(String msg, Object o) {
		super(msg + ": " + o);
	}

	/**
	 * AppException constructor comment. This constructor
	 * should be used by exceptions wich require localization
	 * of the message.
	 * @param msgKey the exception message key
	 * @param msgTokens the exception message tokens
	 */
	public AppException(String msgKey, String[] msgTokens) {
		super();
		this.messageKey = msgKey;
		this.messageTokens = msgTokens;
	}

	/**
	 * AppException constructor comment. This constructor
	 * should be used by exceptions wich require localization
	 * of the message and the message has no tokens.
	 * @param msgKey the exception message key
	 * @param needsLocalizing needed just to identify this
	 * as being the constructor of an exception with a message
	 * that needs localization
	 */
	public AppException(String msgKey, boolean needsLocalizing) {
		super();
		this.messageKey = msgKey;
	}

	/**
	 * AppException constructor comment. This constructor
	 * should be used by exceptions wich require localization
	 * of the message and the message has no tokens.
	 * @param component the component for which the localization
	 * applies
	 * @param msgKey the exception message key
	 * @param needsLocalizing needed just to identify this
	 * as being the constructor of an exception with a message
	 * that needs localization
	 */
	public AppException(String component, String msgKey, boolean needsLocalizing) {
		super();
		this.component = component;
		this.messageKey = msgKey;
	}

	/**
	 * AppException constructor comment. This constructor
	 * should be used by exceptions wich require localization
	 * of the message.
	 * @param component the component for which the localization
	 * applies
	 * @param msgKey the exception message key
	 * @param msgTokens the exception message tokens
	 */
	public AppException(String component, String msgKey, String[] msgTokens) {
		super();
		this.component = component;
		this.messageKey = msgKey;
		this.messageTokens = msgTokens;
	}

	/**
	 * @param b
	 */
	public void setIsInternalAppError() {
		internalAppError = true;
	}

    /**
     * @param b
     */
    public void setRequiresHtmlRenderer() {
        requiresHtmlRenderer = true;
    }

	/**
	 * @param message
	 * @param cause
	 */
	public AppException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param msgKey
	 * @param cause
	 */
	public AppException(String msgKey, Throwable cause, boolean needsLocalizing) {
		super(cause);
		this.messageKey = msgKey;
	}

	/**
	 * @param component
	 * @param msgKey
	 * @param cause
	 */
	public AppException(String component, String msgKey, Throwable cause, boolean needsLocalizing) {
		super(cause);
		this.component = component;
		this.messageKey = msgKey;
	}

	/**
	 * AppException constructor comment. This constructor
	 * should be used by exceptions wich require localization
	 * of the message.
	 * @param component the component for which the localization
	 * applies
	 * @param msgKey the exception message key
	 * @param msgTokens the exception message tokens
	 * @param cause
	 */
	public AppException(String component, String msgKey, String[] msgTokens, Throwable cause) {
		super(cause);
		this.component = component;
		this.messageKey = msgKey;
		this.messageTokens = msgTokens;
	}

	/**
	 * @param cause
	 */
	public AppException(Throwable cause) {
		super(cause);
		AppException app = null;
		if(cause instanceof AppException) {
			app = (AppException)cause;
		} else if(cause instanceof AppRuntimeException) {
			app = ((AppRuntimeException)cause).getAppException();
		}
		if(app != null) {
			this.component = app.component;
			this.internalAppError = app.internalAppError;
			this.messageKey = app.messageKey;
			this.messageTokens = app.messageTokens;
			this.requiresHtmlRenderer = app.requiresHtmlRenderer;
		}
	}

	/**
	 * @see java.lang.Throwable#getLocalizedMessage()
	 */
	public String getLocalizedMessage() {
		// TODO localize
		String caused = getCause() != null && this != getCause()
				? " Caused by: " + getCause().getLocalizedMessage() : "";
		String returned = null;
		if(this.messageKey == null) {
			returned = getMessage();
		} else {
			if(this.messageTokens == null) {
				if(this.component == null) {
					returned = MessageRepository.get(this.messageKey);
				} else {
					returned = MessageRepository.get(
							this.component,
							this.messageKey);
				}
			} else {
				if(this.component == null) {
					returned = MessageRepository.get(
						this.messageKey,
						this.messageTokens);
				} else {
					returned = MessageRepository.get(
							this.component,
							this.messageKey,
							this.messageTokens);
				}
			}
		}
		return returned + caused;
	}

	/**
	 * @return Returns true if the exception represents
	 * an internal application error rather than just
	 * a simple user error.
	 */
	public boolean isInternalAppError() {
		return internalAppError;
	}

    /**
     * @return
     */
    public boolean requiresHtmlRenderer() {
        return requiresHtmlRenderer;
    }
}