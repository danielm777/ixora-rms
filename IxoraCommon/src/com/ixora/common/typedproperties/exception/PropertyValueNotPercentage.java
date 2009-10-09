package com.ixora.common.typedproperties.exception;

import com.ixora.common.messages.Msg;

/**
 * Thrown when the value set for a typed property
 * is of the wrong type.
 * @author Daniel Moraru
 */
public final class PropertyValueNotPercentage extends InvalidPropertyValue {
	private static final long serialVersionUID = 6298888885327229920L;

	/**
	 * Constructor.
	 * @param value
	 */
	public PropertyValueNotPercentage(String value) {
		super(Msg.COMMON_TYPEDPROPERTIES_VALUENOTPERCENTAGE, new String[] {value});
	}
}
