/*
 * Created on 28-Nov-2004
 */
package com.ixora.rms.dataengine.definitions;

import org.w3c.dom.Node;

import com.ixora.rms.ResourceId;
import com.ixora.common.xml.XMLAttribute;
import com.ixora.common.xml.XMLAttributeString;
import com.ixora.common.xml.exception.XMLException;

/**
 * ResourceDef
 * Contains definition for a resource (data only, no functionality),
 * which is a way of identifying the most basic monitoring unit, the counter.
 * Resources are used as input for Functions.
 * It is also a StyledTagDef which means it allows styles to be associated with it.
 * Loads and saves contents into XML.
 */
public class ResourceDef extends StyledTagDef {
	private static final long serialVersionUID = 5469622084076948097L;
	private XMLAttribute rid = new XMLAttributeString("rid", true);

    /**
     * Constructs an empty object, ready to be loaded from XML
     */
    public ResourceDef() {
        super();
    }

    /**
     * Constructs an object from given values (as opposed to read it
     * from XML)
     * @param resource id
     */
    public ResourceDef(ResourceId rid) {
        super();
    	this.rid.setValue(rid.toString());
    	this.id.setValue(rid.toString());
    }

    /**
     * Constructs an object from given values (as opposed to read it
     * from XML)
     * @param resource id
     */
    public ResourceDef(ResourceId rid, StyleDef def) {
        super(def);
    	this.rid.setValue(rid.toString());
    	if(def != null && def.getID() != null) {
    		this.id.setValue(def.getID());
    	} else {
    		this.id.setValue(rid.toString());
    	}
    }

    /**
     * @return hardcoded name of this tag
     */
	public String getTagName() {
        return "resource";
    }

    /**
     * @return resource identifier (host/agent/entity/counter)
     */
    public ResourceId getResourceId() {
    	return new ResourceId(rid.getValue());
    }

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
	 */
	public void fromXML(Node node) throws XMLException {
		super.fromXML(node);

		// A resource's default ID is its ResourceId path
		if (getID() == null || getID().length() == 0) {
			setID(getResourceId().toString());
		}
	}
}
