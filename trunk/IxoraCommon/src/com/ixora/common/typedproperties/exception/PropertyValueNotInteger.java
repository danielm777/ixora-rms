package com.ixora.common.typedproperties.exception;

import com.ixora.common.messages.Msg;

/**
 * Thrown when the value set for a typed property
 * is of the wrong type.
 * @author Daniel Moraru
 */
public final class PropertyValueNotInteger extends InvalidPropertyValue {

	/**
	 * Constructor.
	 * @param value
	 */
	public PropertyValueNotInteger(String value) {
		super(Msg.COMMON_TYPEDPROPERTIES_VALUENOTINTEGER, new String[] {value});
	}
}
