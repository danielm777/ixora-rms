/*
 * Created on 30-Sep-2004
 */
package com.ixora.common.typedproperties;

import com.ixora.common.typedproperties.exception.InvalidPropertyValue;
import com.ixora.common.typedproperties.exception.PropertyTypeMismatch;


/**
 * PropertyEntryString.
 */
public class PropertyEntryString extends PropertyEntry {
    /**
     * Constructor to support XML.
     */
    PropertyEntryString() {
        this(null, false, null, false);
    }
    /**
     * Constructor.
     */
    PropertyEntryString(String prop, boolean v, String[] set, boolean required) {
        super(prop, v, set, TypedProperties.TYPE_STRING, required);
    }

	/**
	 * Constructor.
	 * @param prop
	 * @param v
	 * @param set
	 * @param type
	 * @param required
	 */
	protected PropertyEntryString(String prop, boolean v, Object[] set, int type, boolean required) {
	    super(prop, v, set, type, required, null);
	}

    /**
     * @see com.ixora.common.typedproperties.PropertyEntry#makeObject(java.lang.String)
     */
    protected Object makeObject(String value) throws InvalidPropertyValue {
        if(value != null && value.length() == 0) {
            return null;
        }
		return value;
    }
    /**
     * @see com.ixora.common.typedproperties.PropertyEntry#makeString(java.lang.Object)
     */
    protected String makeString(Object obj) throws PropertyTypeMismatch {
        if(obj == null) {
            return "";
        }
        if(!(obj instanceof String)) {
            throw new PropertyTypeMismatch(property);
        }
        return (String)obj;
    }
    /**
     * @see com.ixora.common.typedproperties.PropertyEntry#checkObjectType(java.lang.Object)
     */
    protected boolean checkObjectType(Object obj) {
        return obj == null || obj instanceof String;
    }
    /**
     * @see com.ixora.common.typedproperties.PropertyEntry#setValue(java.lang.Object)
     */
    public boolean setValue(Object obj) {
        // for strings an empty string is equivalent to
        // a null value
        if("".equals(obj)) {
            obj = null;
        }
        return super.setValue(obj);
    }
}