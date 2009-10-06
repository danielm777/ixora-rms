/*
 * Created on 28-Dec-2004
 */
package com.ixora.rms.providers.parsers.exception;

import com.ixora.rms.exception.RMSException;
import com.ixora.rms.providers.ProvidersComponent;
import com.ixora.rms.providers.parsers.messages.Msg;

/**
 * @author Daniel Moraru
 */
public class ParserException extends RMSException {
	/**
	 * The string representation of the data that caused the parsing to fail;
	 * it could be null.
	 */
	private String fData;

	/**
	 * Constructor.
	 * @param msg
	 * @param tokens
	 * @param data the data that caused this parsing error; it can be null
	 */
	public ParserException(String msg, String[] tokens, String data) {
		super(ProvidersComponent.NAME, Msg.ERROR_INVALID_DATA, tokens);
		fData = data;
	}

	/**
	 * @param cause
	 * @param data the data that caused this parsing error; it can be null
	 */
	public ParserException(Throwable cause, String data) {
		super(cause);
		fData = data;
	}

	/**
	 * @return the string representation of the data that caused the parsing
	 * error; it could be null
	 */
	public String getData() {
		return fData;
	}
}
