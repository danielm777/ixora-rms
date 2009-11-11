package com.ixora.common.typedproperties.exception;

import com.ixora.common.messages.Msg;

/**
 * Thrown when the value set for a typed property
 * is of the wrong type.
 * @author Daniel Moraru
 */
public final class PropertyValueNotNumber extends InvalidPropertyValue {
	private static final long serialVersionUID = 3100581162516611236L;

	/**
	 * Constructor.
	 * @param value
	 */
	public PropertyValueNotNumber(String value) {
		super(Msg.COMMON_TYPEDPROPERTIES_VALUENOTNUMBER, new String[] {value});
	}
}
