package com.ixora.common.typedproperties.exception;

import com.ixora.common.messages.Msg;

/**
 * Thrown when the value set for a typed property
 * is of the wrong type.
 * @author Daniel Moraru
 */
public final class PropertyValueNotFloat extends InvalidPropertyValue {

	/**
	 * Constructor.
	 * @param value
	 */
	public PropertyValueNotFloat(String value) {
		super(Msg.COMMON_TYPEDPROPERTIES_VALUENOTFLOAT, new String[] {value});
	}
}
