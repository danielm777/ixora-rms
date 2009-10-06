/*
 * Created on 16-Jan-2005
 */
package com.ixora.rms.exception;

/**
 * InvalidDataException
 */
public class InvalidDataException extends RMSException {

    public InvalidDataException(String s) {
        super(s);
    }

    public InvalidDataException(Throwable e) {
        super(e);
    }
}
