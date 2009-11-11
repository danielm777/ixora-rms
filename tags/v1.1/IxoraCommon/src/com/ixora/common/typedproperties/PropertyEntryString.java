/*
 * Created on 30-Sep-2004
 */
package com.ixora.common.typedproperties;

import com.ixora.common.typedproperties.exception.InvalidPropertyValue;
import com.ixora.common.typedproperties.exception.PropertyTypeMismatch;


/**
 * PropertyEntryString.
 */
public class PropertyEntryString extends PropertyEntry<String> {
	private static final long serialVersionUID = 4067303909953093390L;
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
	protected PropertyEntryString(String prop, boolean v, String[] set, int type, boolean required) {
	    super(prop, v, set, type, required, null);
	}

    /**
     * @see com.ixora.common.typedproperties.PropertyEntry#makeObject(java.lang.String)
     */
    protected String makeObject(String value) throws InvalidPropertyValue {
        if(value != null && value.length() == 0) {
            return null;
        }
		return value;
    }
    /**
     * @see com.ixora.common.typedproperties.PropertyEntry#makeString(java.lang.Object)
     */
    protected String makeString(String obj) throws PropertyTypeMismatch {
        if(obj == null) {
            return "";
        }
        return obj;
    }
    /**
     * @see com.ixora.common.typedproperties.PropertyEntry#setValue(java.lang.Object)
     */
    public boolean setValue(String obj) {
        // for strings an empty string is equivalent to
        // a null value
        if("".equals(obj)) {
            obj = null;
        }
        return super.setValue(obj);
    }
}