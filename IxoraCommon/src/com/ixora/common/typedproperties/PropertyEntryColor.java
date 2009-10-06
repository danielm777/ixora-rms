/*
 * Created on 30-Sep-2004
 */
package com.ixora.common.typedproperties;

import java.awt.Color;

import com.ixora.common.typedproperties.exception.InvalidPropertyValue;
import com.ixora.common.typedproperties.exception.PropertyTypeMismatch;


/**
 * PropertyEntryColor.
 */
public final class PropertyEntryColor extends PropertyEntry {
    /**
     * Constructor to support XML.
     */
    PropertyEntryColor() {
        this(null, false, null, false);
    }
    /**
     * Constructor.
     */
    PropertyEntryColor(String prop, boolean v, Color[] set, boolean required) {
        super(prop, v, set, TypedProperties.TYPE_COLOR, required);
        this.extendedEditorClass = "com.ixora.common.typedproperties.ui.ExtendedEditorColor";
    }
    /**
     * @see com.ixora.common.typedproperties.PropertyEntry#makeObject(java.lang.String)
     */
    protected Object makeObject(String value) throws InvalidPropertyValue {
        return new Color(Integer.parseInt(value));
    }
    /**
     * @see com.ixora.common.typedproperties.PropertyEntry#makeString(java.lang.Object)
     */
    protected String makeString(Object obj) throws PropertyTypeMismatch {
        if(obj == null) {
            return "";
        }
        if(!(obj instanceof Color)) {
            throw new PropertyTypeMismatch(property);
        }
        return String.valueOf(((Color)obj).getRGB());
    }
    /**
     * @see com.ixora.common.typedproperties.PropertyEntry#checkObjectType(java.lang.Object)
     */
    protected boolean checkObjectType(Object obj) {
        return obj == null || obj instanceof Color;
    }
}