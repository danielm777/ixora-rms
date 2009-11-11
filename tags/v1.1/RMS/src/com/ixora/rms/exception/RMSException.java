/*
 * Created on 19-Nov-2003
 */
package com.ixora.rms.exception;

import com.ixora.common.exception.AppException;

/**
 * @author Daniel Moraru
 */
public class RMSException extends AppException {
	private static final long serialVersionUID = 5045448860894090816L;
	/**
	 *
	 */
	public RMSException() {
		super();
	}

	/**
	 * Constructor for RMSException.
	 * @param s
	 */
	public RMSException(Object s) {
		super(s);
	}

	/**
	 * Constructor for RMSException.
	 * @param e
	 */
	public RMSException(Exception e) {
		super(e);
	}

	/**
	 * Constructor for RMSException.
	 * @param msg
	 * @param o
	 */
	public RMSException(String msg, Object o) {
		super(msg, o);
	}

	/**
	 * @param s
	 */
	public RMSException(String s) {
		super(s);
	}

	/**
	 * @param msgKey
	 * @param needsLocalizing
	 */
	public RMSException(String msgKey, boolean needsLocalizing) {
		super(msgKey, needsLocalizing);
	}

	/**
	 * @param component
	 * @param msgKey
	 * @param needsLocalizing
	 */
	public RMSException(String component, String msgKey, boolean needsLocalizing) {
		super(component, msgKey, needsLocalizing);
	}

	/**
	 * @param msgKey
	 * @param msgTokens
	 */
	public RMSException(String msgKey, String[] msgTokens) {
		super(msgKey, msgTokens);
	}

	/**
	 * @param component
	 * @param msgKey
	 * @param msgTokens
	 */
	public RMSException(String component, String msgKey, String[] msgTokens) {
		super(component, msgKey, msgTokens);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public RMSException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public RMSException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor.
	 * @param component
	 * @param msgKey
	 * @param msgTokens
	 * @param cause
	 */
	public RMSException(String component, String msgKey, String[] msgTokens,
			Throwable cause) {
		super(component, msgKey, msgTokens, cause);
	}
	/**
	 * Constructor.
	 * @param component
	 * @param msgKey
	 * @param cause
	 * @param needsLocalizing
	 */
	public RMSException(String component, String msgKey, Throwable cause,
			boolean needsLocalizing) {
		super(component, msgKey, cause, needsLocalizing);
	}
	/**
	 * Constructor.
	 * @param msgKey
	 * @param cause
	 * @param needsLocalizing
	 */
	public RMSException(String msgKey, Throwable cause,
			boolean needsLocalizing) {
		super(msgKey, cause, needsLocalizing);
	}
}
