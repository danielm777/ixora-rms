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
	private static final long serialVersionUID = 2771381001118917243L;

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

	public InvalidConfiguration(String msgKey, boolean needsLocalizing) {
		super(msgKey, needsLocalizing);
	}

	public InvalidConfiguration(String msg, Object o) {
		super(msg, o);
	}

	public InvalidConfiguration(String component, String msgKey,
			boolean needsLocalizing) {
		super(component, msgKey, needsLocalizing);
	}

	public InvalidConfiguration(String component, String msgKey,
			String[] msgTokens, Throwable cause) {
		super(component, msgKey, msgTokens, cause);
	}

	public InvalidConfiguration(String component, String msgKey,
			String[] msgTokens) {
		super(component, msgKey, msgTokens);
	}

	public InvalidConfiguration(String component, String msgKey,
			Throwable cause, boolean needsLocalizing) {
		super(component, msgKey, cause, needsLocalizing);
	}
	
	public InvalidConfiguration(String msgKey, String[] msgTokens) {
		super(msgKey, msgTokens);
	}
	
	public InvalidConfiguration(String msgKey, Throwable cause,
			boolean needsLocalizing) {
		super(msgKey, cause, needsLocalizing);
	}
	
	public InvalidConfiguration(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidConfiguration(Throwable cause) {
		super(cause);
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
