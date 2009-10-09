package com.ixora.common.xml;
/*
 * Created on 27-Nov-2004
 */

/**
 * XMLAttributeInt
 */
public class XMLAttributeInt extends XMLAttribute {
	private static final long serialVersionUID = 6606088986991800960L;
	private Integer		value;

    /**
     * Empty constructor
     */
    public XMLAttributeInt(String name) {
        super(name);
    }

    /**
     * Constructor
     * @param mandatory
     */
    public XMLAttributeInt(String name, boolean mandatory) {
        super(name, mandatory);
    }

    /**
     * @param s
     */
    public void setValue(Integer s) {
        value = s;
    }

    /**
     * @param s
     */
    public void setValue(String s) {
        value = (s == null ? null : Integer.decode(s));
    }

    /** @return the contents of this object, may be null */
    public Integer getInteger() {
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
