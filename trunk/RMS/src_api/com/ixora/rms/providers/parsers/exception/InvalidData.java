/*
 * Created on 28-Dec-2004
 */
package com.ixora.rms.providers.parsers.exception;

import com.ixora.rms.providers.parsers.messages.Msg;

/**
 * @author Daniel Moraru
 */
public final class InvalidData extends ParserException {
	/**
	 * Constructor.
	 * @param msg
	 */
	public InvalidData(String msg) {
		super(Msg.ERROR_INVALID_DATA, new String[] {msg}, null);
	}

	/**
	 * Constructor.
	 * @param msg
	 * @param data that caused the parsing error
	 */
	public InvalidData(String msg, String data) {
		super(Msg.ERROR_INVALID_DATA, new String[] {msg}, data);
	}


}
