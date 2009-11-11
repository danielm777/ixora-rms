/*
 * Created on 26-Mar-2005
 */
package com.ixora.rms.dataengine.definitions;

import com.ixora.common.xml.XMLTag;

/**
 * ValueFilterRuleDef
 * Used by ValueFilterDef to define a rule of filtering values
 * returned by a resource of a query.
 * Loads and saves contents into XML.
 */
public class ValueFilterRuleDef extends XMLTag {
	private static final long serialVersionUID = 5540860705346167788L;

	/**
     * Constructs an empty object, ready to be loaded from XML
     */
    public ValueFilterRuleDef() {
        super();
    }

    /**
     * Constructs an object from given values (as opposed to read it
     * from XML)
     * @param value accepted by this rule
     */
    public ValueFilterRuleDef(String value) {
        super();
    	this.fText = value;
    }

    /**
     * @return value accepted by this rule
     */
    public String getValue() {
    	return fText;
    }

    /**
     * @return the hardcoded name of this tag
     * @see com.ixora.common.xml.XMLTag#getTagName()
     */
    public String getTagName() {
    	return "value";
    }
}
