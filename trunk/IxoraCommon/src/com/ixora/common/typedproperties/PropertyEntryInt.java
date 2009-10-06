/*
 * Created on 30-Sep-2004
 */
package com.ixora.common.typedproperties;

import com.ixora.common.typedproperties.exception.InvalidPropertyValue;
import com.ixora.common.typedproperties.exception.PropertyTypeMismatch;
import com.ixora.common.typedproperties.exception.PropertyValueNotInteger;


/**
 * PropertyEntryInt.
 */
public final class PropertyEntryInt extends PropertyEntryNumber {
    /**
     * Constructor to support XML.
     */
    PropertyEntryInt() {
        this(null, false, null, false);
    }
    /**
     * Constructor.
     */
    PropertyEntryInt(String prop, boolean v, Integer[] set, boolean required) {
        super(prop, v, set, TypedProperties.TYPE_INTEGER, required);
        this.format = new AllowNullNumberFormat(AllowNullNumberFormat.NULL_INTEGER);
        format.setParseIntegerOnly(true);
    }
    /**
     * @see com.ixora.common.typedproperties.PropertyEntry#makeObject(java.lang.String)
     */
    protected Object makeObject(String value) throws InvalidPropertyValue {
		try {
			Integer ret = Integer.valueOf(value);
			validateValue(ret);
			return ret;
		} catch(NumberFormatException e) {
			throw new PropertyValueNotInteger(value);
		}
    }
    /**
     * @see com.ixora.common.typedproperties.PropertyEntry#makeString(java.lang.Object)
     */
    protected String makeString(Object obj) throws PropertyTypeMismatch {
        if(obj == null) {
            return "";
        }
        if(!(obj instanceof Integer)) {
            throw new PropertyTypeMismatch(property);
        }
        return String.valueOf(((Integer)obj).intValue());
    }
    /**
     * @see com.ixora.common.typedproperties.PropertyEntry#checkObjectType(java.lang.Object)
     */
    protected boolean checkObjectType(Object obj) {
        return obj instanceof Integer;
    }
}