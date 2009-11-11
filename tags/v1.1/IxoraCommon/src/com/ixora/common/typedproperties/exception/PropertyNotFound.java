/*
 * Created on 27-Sep-2004
 */
package com.ixora.common.typedproperties.exception;

import com.ixora.common.exception.AppRuntimeException;
import com.ixora.common.messages.Msg;

/**
 * @author Daniel Moraru
 */
public final class PropertyNotFound extends AppRuntimeException {
	private static final long serialVersionUID = -5322805302580621600L;

	/**
     * Constructor.
     */
    public PropertyNotFound(String property) {
        super(Msg.COMMON_TYPEDPROPERTIES_PROPERTY_NOT_FOUND, new String[] {property});
    }
}
