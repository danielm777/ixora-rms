package com.ixora.common.typedproperties.exception;

import com.ixora.common.messages.Msg;

/**
 * Thrown when the value set for a typed property
 * is of the wrong type.
 * @author Daniel Moraru
 */
public final class PropertyValueNotDateTime extends InvalidPropertyValue {
	private static final long serialVersionUID = 8031843459336730444L;

	/**
	 * Constructor.
	 * @param value
	 */
	public PropertyValueNotDateTime(String value) {
		super(Msg.COMMON_TYPEDPROPERTIES_VALUENOTDATE, new String[] {value});
	}
}
