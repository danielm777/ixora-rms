/*
 * Created on 05-Dec-2004
 */
package com.ixora.common.xml;


/**
 * XMLCData
 * Pseudo-attribute: looks and feels like an attribute (having a text
 * value), but translates to a child tag with text content.
 */
public class XMLCData extends XMLAttributeString {
	private static final long serialVersionUID = -2328750562256423984L;

	/**
     * Empty constructor
     */
    public XMLCData(String name) {
        super(name);
    }

    /**
     * Constructor
     * @param mandatory
     */
    public XMLCData(String name, boolean mandatory) {
        super(name, mandatory);
    }
}
