/*
 * Created on 16-Nov-2003
 */
package com.ixora.rms.exception;

import com.ixora.common.typedproperties.exception.InvalidPropertyValue;
import com.ixora.common.typedproperties.exception.VetoException;

/**
 * InvalidConfiguration
 * @author Daniel Moraru
 */
public final class InvalidConfiguration extends RMSException {

	/**
	 * Constructor.
	 * @param msg
	 */
	public InvalidConfiguration(InvalidPropertyValue e) {
		super(e);
	}
    /**
     * Constructor.
     * @param msg
     */
    public InvalidConfiguration(VetoException e) {
        super(e);
    }
	/**
	 * Constructor.
	 * @param s
	 */
	public InvalidConfiguration(String s) {
		super(s);
	}

    /**
     * @see java.lang.Throwable#getLocalizedMessage()
     */
    public String getLocalizedMessage() {
        if(getCause() instanceof InvalidPropertyValue) {
            return ((InvalidPropertyValue)getCause()).getLocalizedMessage();
        } else if(getCause() instanceof VetoException) {
            return ((VetoException)getCause()).getLocalizedMessage();
        }
        return super.getLocalizedMessage();
    }
}
