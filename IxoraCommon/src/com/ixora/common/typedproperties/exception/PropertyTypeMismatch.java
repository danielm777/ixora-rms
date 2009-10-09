package com.ixora.common.typedproperties.exception;

import com.ixora.common.exception.AppRuntimeException;
import com.ixora.common.messages.Msg;

/**
 * Thrown when the value set for a typed property
 * is of the wrong type.
 * @author Daniel Moraru
 */
public final class PropertyTypeMismatch extends AppRuntimeException {
	private static final long serialVersionUID = 8300357277170117825L;

	/**
	 * Constructor.
	 * @param property
	 */
	public PropertyTypeMismatch(String property) {
		super(Msg.COMMON_TYPEDPROPERTIES_TYPEMISMATCH, new String[] {property});
	}
}
