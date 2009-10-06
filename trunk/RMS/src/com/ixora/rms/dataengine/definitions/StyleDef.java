/*
 * Created on 18-Jan-2005
 */
package com.ixora.rms.dataengine.definitions;

/**
 * StyleDef
 * Definition of a Style, used for styles repository. Inherits from
 * StyledTagDef the same attributes as FunctionDef and ResourceDef.
 * Loads and saves contents into XML.
 */
public class StyleDef extends StyledTagDef {

    /**
     * Constructs an empty object, ready to be loaded from XML
     */
    public StyleDef() {
        super();
    }

    public String getTagName() {
    	return "style";
    }
}
