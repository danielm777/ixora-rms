/*
 * Created on 28-Nov-2004
 */
package com.ixora.rms.dataengine.definitions;

import java.util.List;

import org.w3c.dom.Node;

import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLAttribute;
import com.ixora.common.xml.XMLAttributeString;
import com.ixora.common.xml.XMLSameTagList;
import com.ixora.common.xml.XMLTagList;
import com.ixora.common.xml.exception.XMLException;

/**
 * FunctionDef
 * Contains definition for a function (data only, no functionality),
 * which has a name (operation being performed) and a list of resources
 * as parameters. It is also a StyledTagDef which means it allows styles
 * to be associated.
 * Loads and saves contents into XML.
 */
public class FunctionDef extends StyledTagDef {
	private static final long serialVersionUID = -4206136821667283770L;
	protected XMLAttribute	op = new XMLAttributeString("op", true);
    protected XMLTagList<ParamDef> params = new XMLSameTagList<ParamDef>(ParamDef.class);

    /**
     * Constructs an empty object, ready to be loaded from XML
     */
    public FunctionDef() {
        super();
    }

    /**
     * Constructs an object from given values (as opposed to read it
     * from XML)
     * @param op operation performed (actual function)
     * @param listParamDefs list of input parameters
     */
    public FunctionDef(String op, List<ParamDef> listParamDefs) {
        super();
    	this.op.setValue(op);
    	for (ParamDef pd : listParamDefs) {
    		params.add(pd);
    	}
    }

    /**
     * Constructs an object from given values (as opposed to read it
     * from XML)
     * @param op operation performed (actual function)
     * @param pd a single input parameter
     */
    public FunctionDef(String op, ParamDef pd) {
        super();
    	this.op.setValue(op);
    	params.add(pd);
    }

    /**
     * @return the operation performed by this function
     */
    public String getOp() {
    	return op.getValue();
    }

    /**
     * @return the hardcoded name of this tag
     * @see com.ixora.common.xml.XMLTag#getTagName()
     */
    public String getTagName() {
    	return "function";
    }

    /**
     * @return list of input parameters
     */
    public List<ParamDef> getParameters() {
    	return params;
    }

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
	 */
	public void fromXML(Node node) throws XMLException {
		super.fromXML(node);
		if(Utils.isEmptyCollection(params)) {
			throw new XMLException("The function must have at least one parameter");
		}
		// A function's default ID is it's name
		String name = getName();
		String id = getID();
		if (id == null || id.length() == 0) {
			id = name;
		}
		if (name == null || name.length() == 0) {
			name = id;
		}
		// If ID is still empty then both of them are null, so give them
		// a random value.
		if (id == null || id.length() == 0) {
			id = String.valueOf(Utils.getRandomInt(0, 999999999));
			name = id;
		}
		setID(id);
		setName(name);
	}
}
