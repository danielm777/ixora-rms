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
public final class PropertyEntryColor extends PropertyEntry<Color> {
	private static final long serialVersionUID = -4800218568848935255L;
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
    protected Color makeObject(String value) throws InvalidPropertyValue {
        return new Color(Integer.parseInt(value));
    }
    /**
     * @see com.ixora.common.typedproperties.PropertyEntry#makeString(java.lang.Object)
     */
    protected String makeString(Color obj) throws PropertyTypeMismatch {
        if(obj == null) {
            return "";
        }
        return String.valueOf(((Color)obj).getRGB());
    }
}