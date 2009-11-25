/*
 * Created on 18-Jan-2005
 */
package com.ixora.common.xml;

/**
 * XMLAttributeBoolean
 */
public class XMLAttributeBoolean extends XMLAttribute {
	private static final long serialVersionUID = 9084448034889888064L;
	private Boolean	value;

    /**
     * Empty constructor
     */
    public XMLAttributeBoolean(String name) {
        super(name);
    }

    /**
     * Constructor
     * @param mandatory
     */
    public XMLAttributeBoolean(String name, boolean mandatory) {
        super(name, mandatory);
    }

    /**
     * @param s
     */
    public void setValue(String s) {
        value = Boolean.valueOf(s);
    }

    /**
     * @param s
     */
    public void setValue(Boolean s) {
        value = s;
    }

    /** @return the contents of this object, may be null */
    public Boolean getBoolean() {
        return value;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String getValue() {
        if (value != null)
            return value.toString();
        return null;
    }
}
