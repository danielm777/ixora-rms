package com.ixora.common.typedproperties.exception;

import com.ixora.common.messages.Msg;

/**
 * Thrown when the value set for a typed property
 * is of the wrong type.
 * @author Daniel Moraru
 */
public final class PropertyValueOutOfRange extends InvalidPropertyValue {
	private static final long serialVersionUID = -8615827115681448082L;

	/**
	 * Constructor.
	 * @param value
	 */
	public PropertyValueOutOfRange(String value) {
		super(Msg.COMMON_TYPEDPROPERTIES_VALUEOUTOFRANGE, new String[] {value});
	}
}
