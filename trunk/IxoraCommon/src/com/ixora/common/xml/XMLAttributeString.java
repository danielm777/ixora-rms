package com.ixora.common.xml;
/*
 * Created on 27-Nov-2004
 */

/**
 * XMLAttributeString
 */
public class XMLAttributeString extends XMLAttribute {

    private String		value;

    /**
     * Empty constructor
     */
    public XMLAttributeString(String name) {
        super(name);
    }

    /**
     * Constructor
     * @param mandatory
     */
    public XMLAttributeString(String name, boolean mandatory) {
        super(name, mandatory);
    }

    /**
     * @param s
     */
    public void setValue(String s) {
        value = s;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String getValue() {
        return value;
    }

}
