package com.ixora.common.xml;

import java.io.Serializable;

/*
 * Created on 27-Nov-2004
 */

/**
 * XMLAttribute
 */
public abstract class XMLAttribute implements Serializable {
	private static final long serialVersionUID = -6660179320710708239L;
	private String fName;
    private boolean isMandatory = false;

    /**
     * Empty constructor, attribute is not mandatory  by default
     */
    public XMLAttribute(String name) {
        this.isMandatory = false;
        this.fName = name;
    }

    /**
     * Constructor which sets the mandatory attribute
     * @param mandatory
     */
    public XMLAttribute(String name, boolean mandatory) {
        this.isMandatory = mandatory;
        this.fName = name;
    }

    /** Initialize with a string read from XML */
    public abstract void setValue(String s);

    /** Convert the value to a string to be written in XML */
    public abstract String getValue();

    /** For display purposes */
    public String toString()
    {
        String s = getValue();
        if (s == null)
            return "";
        return s;
    }

    /**
     * @return the name to be used when writing attribute to xml. If null,
     * then the variable name (in declaring class) will be used.
     */
    public String getAttrName() {
        return fName;
    }

    /**
     * @return whether this attribute is mandatory
     */
    public boolean isMandatory() {
        return isMandatory;
    }
}
