/*
 * Created on 06-Feb-2005
 */
package com.ixora.rms.dataengine.definitions;

import com.ixora.common.xml.XMLAttribute;
import com.ixora.common.xml.XMLAttributeString;
import com.ixora.common.xml.XMLTag;

/**
 * ParamDef
 * Contains definition for a function parameter (data only, no functionality),
 * which identifies a Resource to be passed as input to the function.
 * Loads and saves contents into XML.
 */
public class ParamDef extends XMLTag {
    private XMLAttribute	id = new XMLAttributeString("id", true);
    private XMLAttribute	value = new XMLAttributeString("value");
    private XMLAttribute	type = new XMLAttributeString("type");

    /**
     * Constructs an empty object, ready to be loaded from XML
     */
    public ParamDef() {
        super();
    }

    /**
     * Constructs an object from given values (as opposed to read it
     * from XML)
     * @param id ID of resource to use as input parameter
     */
    public ParamDef(String id) {
        super();
    	this.id.setValue(id);
    }


    /**
     * @return id of the resource parameter
     */
    public String getID() {
    	return id.getValue();
    }

    /**
     * @return type of the resource parameter
     */
    public String getType() {
    	return type.getValue();
    }

    /**
     * @return hardcoded value, rather than a resource
     */
    public String getValue() {
    	return value.getValue();
    }

    /**
     * @return the hardcoded name of this tag
     * @see com.ixora.common.xml.XMLTag#getTagName()
     */
    public String getTagName() {
    	return "param";
    }
}
