/*
 * Created on 30-Sep-2004
 */
package com.ixora.common.typedproperties;

import com.ixora.common.typedproperties.exception.InvalidPropertyValue;
import com.ixora.common.typedproperties.exception.PropertyTypeMismatch;
import com.ixora.common.typedproperties.exception.PropertyValueNotInteger;
import com.ixora.common.typedproperties.exception.PropertyValueNotPercentage;


/**
 * PropertyEntryFloat.
 */
public final class PropertyEntryPercentage extends PropertyEntryNumber {
    /**
     * Constructor to support XML.
     */
    PropertyEntryPercentage() {
        this(null, false, null, false);
    }
    /**
     * Constructor.
     */
    PropertyEntryPercentage(String prop, boolean v, Float[] set, boolean required) {
        super(prop, v, set, TypedProperties.TYPE_PERCENTAGE, required);
        this.format = new AllowNullNumberFormat(AllowNullNumberFormat.NULL_FLOAT);//NumberFormat.getPercentInstance();
    }
    /**
     * @see com.ixora.common.typedproperties.PropertyEntry#makeObject(java.lang.String)
     */
    protected Object makeObject(String value) throws InvalidPropertyValue {
		try {
		    Float obj = Float.valueOf(value);
		    float fl = obj.floatValue();
		    if(fl < 0 || fl > 1) {
		        throw new PropertyValueNotPercentage(value);
		    }
		    validateValue(obj);
			return obj;
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