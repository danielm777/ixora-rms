/*
 * Created on 30-Sep-2004
 */
package com.ixora.common.typedproperties;

import com.ixora.common.typedproperties.exception.InvalidPropertyValue;
import com.ixora.common.typedproperties.exception.PropertyTypeMismatch;
import com.ixora.common.typedproperties.exception.PropertyValueNotFloat;


/**
 * PropertyEntryFloat.
 */
public final class PropertyEntryFloat extends PropertyEntryNumber {
    /**
     * Constructor to support XML.
     */
    PropertyEntryFloat() {
        this(null, false, null, false);
    }
    /**
     * Constructor.
     */
    PropertyEntryFloat(String prop, boolean v, Float[] set, boolean required) {
        super(prop, v, set, TypedProperties.TYPE_FLOAT, required);
        this.format = new AllowNullNumberFormat(AllowNullNumberFormat.NULL_FLOAT);//NumberFormat.getNumberInstance();
    }
    /**
     * @see com.ixora.common.typedproperties.PropertyEntry#makeObject(java.lang.String)
     */
    protected Object makeObject(String value) throws InvalidPropertyValue {
		try {
			Float ret = Float.valueOf(value);
			validateValue(ret);
			return ret;
		} catch(NumberFormatException e) {
			throw new PropertyValueNotFloat(value);
		}
    }
    /**
     * @see com.ixora.common.typedproperties.PropertyEntry#makeString(java.lang.Object)
     */
    protected String makeString(Object obj) throws PropertyTypeMismatch {
        if(obj == null) {
            return "";
        }
        if(!(obj instanceof Float)) {
            throw new PropertyTypeMismatch(property);
        }
        return String.valueOf(((Float)obj).floatValue());
    }
    /**
     * @see com.ixora.common.typedproperties.PropertyEntry#checkObjectType(java.lang.Object)
     */
    protected boolean checkObjectType(Object obj) {
        return obj == null || obj instanceof Float;
    }
}