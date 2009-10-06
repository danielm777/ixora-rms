/*
 * Created on 30-Sep-2004
 */
package com.ixora.common.typedproperties;

import java.util.Date;

import com.ixora.common.typedproperties.exception.InvalidPropertyValue;
import com.ixora.common.typedproperties.exception.PropertyTypeMismatch;
import com.ixora.common.typedproperties.exception.PropertyValueNotDateTime;


/**
 * PropertyEntryDate.
 */
public final class PropertyEntryDate extends PropertyEntry {
    /**
     * Constructor to support XML.
     */
    PropertyEntryDate() {
        this(null, false, null, false);
    }
	/**
     * Constructor.
     */
    PropertyEntryDate(String prop, boolean v, Date[] set, boolean required) {
        super(prop, v, set, TypedProperties.TYPE_DATE, required);
        this.extendedEditorClass = "com.ixora.common.typedproperties.ui.ExtendedEditorDate";
    }
    /**
     * @see com.ixora.common.typedproperties.PropertyEntry#makeObject(java.lang.String)
     */
    protected Object makeObject(String value) throws InvalidPropertyValue {
	    try {
            return new Date(Long.parseLong(value));
        } catch (NumberFormatException e1) {
            throw new PropertyValueNotDateTime(value);
        }
    }
    /**
     * @see com.ixora.common.typedproperties.PropertyEntry#makeString(java.lang.Object)
     */
    protected String makeString(Object obj) throws PropertyTypeMismatch {
        if(obj == null) {
            return "";
        }
        if(!(obj instanceof Date)) {
            throw new PropertyTypeMismatch(property);
        }
        return String.valueOf(((Date)obj).getTime());
    }
    /**
     * @see com.ixora.common.typedproperties.PropertyEntry#checkObjectType(java.lang.Object)
     */
    protected boolean checkObjectType(Object obj) {
        return obj == null || obj instanceof Date;
    }
}