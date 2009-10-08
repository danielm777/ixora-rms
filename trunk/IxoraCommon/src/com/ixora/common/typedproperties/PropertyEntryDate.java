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
public final class PropertyEntryDate extends PropertyEntry<Date> {
	private static final long serialVersionUID = 1540392993343334545L;
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
    protected Date makeObject(String value) throws InvalidPropertyValue {
	    try {
            return new Date(Long.parseLong(value));
        } catch (NumberFormatException e1) {
            throw new PropertyValueNotDateTime(value);
        }
    }
    /**
     * @see com.ixora.common.typedproperties.PropertyEntry#makeString(java.lang.Object)
     */
    protected String makeString(Date obj) throws PropertyTypeMismatch {
        if(obj == null) {
            return "";
        }
        return String.valueOf(obj.getTime());
    }
}