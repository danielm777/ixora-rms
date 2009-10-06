/*
 * Created on 30-Sep-2004
 */
package com.ixora.common.typedproperties;

import com.ixora.common.typedproperties.exception.InvalidPropertyValue;
import com.ixora.common.typedproperties.exception.PropertyTypeMismatch;


/**
 * PropertyEntryBoolean.
 */
public final class PropertyEntryBoolean extends PropertyEntry {
    /**
     * Constructor to support XML.
     */
    PropertyEntryBoolean() {
        super();
        this.type = TypedProperties.TYPE_BOOLEAN;
    }
    /**
     * Constructor.
     */
    PropertyEntryBoolean(String prop, boolean v, Boolean[] set, boolean required) {
        super(prop, v, set, TypedProperties.TYPE_BOOLEAN, required);
    }
    /**
     * @see com.ixora.common.typedproperties.PropertyEntry#makeObject(java.lang.String)
     */
    protected Object makeObject(String value) throws InvalidPropertyValue {
		return Boolean.valueOf(value);
    }
    /**
     * @see com.ixora.common.typedproperties.PropertyEntry#makeValueFromObject(java.lang.Object)
     */
    protected String makeString(Object obj) throws PropertyTypeMismatch {
        if(obj == null) {
            return "";
        }
        if(!(obj instanceof Boolean)) {
            throw new PropertyTypeMismatch(property);
        }
        return String.valueOf(((Boolean)obj).booleanValue());
    }
    /**
     * @see com.ixora.common.typedproperties.PropertyEntry#checkObjectType(java.lang.Object)
     */
    protected boolean checkObjectType(Object obj) {
        return obj == null || obj instanceof Boolean;
    }
}