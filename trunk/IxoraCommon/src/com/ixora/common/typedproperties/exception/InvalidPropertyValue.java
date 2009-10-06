package com.ixora.common.typedproperties.exception;

import com.ixora.common.exception.AppRuntimeException;
import com.ixora.common.messages.Msg;

/**
 * Thrown when the value of a typed property is set to the wrong type
 * is invalid.
 * @author Daniel Moraru
 */
public class InvalidPropertyValue extends AppRuntimeException {

    /**
     * Constructor.
     * @param prop
     * @param val
     */
    public InvalidPropertyValue(String prop, String val) {
        super(Msg.COMMON_TYPEDPROPERTIES_INVALIDVALUE,
                new String[]{val, prop});
    }
	/**
	 * Constructor.
	 * @param m
	 */
	protected InvalidPropertyValue(String m, String[] tokens) {
		super(m, tokens);
	}
}
