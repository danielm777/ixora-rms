/*
 * Created on 18-Jan-2005
 */
package com.ixora.common.xml;

/**
 * XMLAttributeDouble
 */
public class XMLAttributeDouble extends XMLAttribute {

    private Double		value;

    /**
     * Empty constructor
     */
    public XMLAttributeDouble(String name) {
        super(name);
    }

    /**
     * Constructor
     * @param mandatory
     */
    public XMLAttributeDouble(String name, boolean mandatory) {
        super(name, mandatory);
    }

    /**
     * @param s
     */
    public void setValue(Double s) {
    	value = s;
    }

    /**
     * @param s
     */
    public void setValue(String s) {
        value = (s == null ? null : Double.valueOf(s));
    }

    /** @return the contents of this object, may be null */
    public Double getDouble() {
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
