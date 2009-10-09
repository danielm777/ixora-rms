package com.ixora.common.typedproperties.exception;

import com.ixora.common.messages.Msg;

/**
 * Thrown when the value set for a typed property
 * is of the wrong type.
 * @author Daniel Moraru
 */
public final class PropertyValueNotInteger extends InvalidPropertyValue {
	private static final long serialVersionUID = -4465499395924003010L;

	/**
	 * Constructor.
	 * @param value
	 */
	public PropertyValueNotInteger(String value) {
		super(Msg.COMMON_TYPEDPROPERTIES_VALUENOTINTEGER, new String[] {value});
	}
}
