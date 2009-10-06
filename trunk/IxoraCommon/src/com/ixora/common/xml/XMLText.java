/*
 * Created on 05-Dec-2004
 */
package com.ixora.common.xml;


/**
 * XMLText
 * Pseudo-attribute: looks and feels like an attribute (having a text
 * value), but translates to a child tag with text content.
 */
public class XMLText extends XMLAttributeString {

    /**
     * Empty constructor
     */
    public XMLText(String name) {
        super(name);
    }

    /**
     * Constructor
     * @param mandatory
     */
    public XMLText(String name, boolean mandatory) {
        super(name, mandatory);
    }
}
