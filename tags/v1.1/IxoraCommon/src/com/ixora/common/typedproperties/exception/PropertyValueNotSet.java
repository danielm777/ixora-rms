/*
 * Created on 27-Sep-2004
 */
package com.ixora.common.typedproperties.exception;

import com.ixora.common.exception.AppRuntimeException;
import com.ixora.common.messages.Msg;

/**
 * Thrown when a property is required to have a value but none was set.
 * @author Daniel Moraru
 */
public final class PropertyValueNotSet extends AppRuntimeException {
	private static final long serialVersionUID = -7071289883757011537L;

	/**
     * Constructor.
     */
    public PropertyValueNotSet(String property) {
        super(Msg.COMMON_TYPEDPROPERTIES_VALUE_NOT_SET, new String[] {property});
    }
}
