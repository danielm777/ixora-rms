/*
 * Created on 16-Jan-2005
 */
package com.ixora.rms.exception;

/**
 * InvalidDataException
 * @author Daniel Moraru
 */
public class InvalidDataException extends RMSException {
	private static final long serialVersionUID = -964131927482046756L;

	public InvalidDataException(String s) {
        super(s);
    }

    public InvalidDataException(Throwable e) {
        super(e);
    }
}
