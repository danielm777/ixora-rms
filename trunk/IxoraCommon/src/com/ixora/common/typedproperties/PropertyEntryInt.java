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
public final class PropertyEntryInt extends PropertyEntryNumber<Integer> {
	private static final long serialVersionUID = 592892853952896663L;
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
    protected Integer makeObject(String value) throws InvalidPropertyValue {
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
    protected String makeString(Integer obj) throws PropertyTypeMismatch {
        if(obj == null) {
            return "";
        }
        return String.valueOf(obj.intValue());
    }
}