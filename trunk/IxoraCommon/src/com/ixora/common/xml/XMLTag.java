package com.ixora.common.xml;
import java.lang.reflect.Field;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import com.ixora.common.utils.Utils;
import com.ixora.common.xml.exception.XMLAttributeMissing;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.common.xml.exception.XMLNodeMissing;

/*
 * Created on 27-Nov-2004
 */

/**
 * XMLTag.
 * The way it works: <br>
 * Subclass this class, initialize all XMLExternalizable fields when declared (to
 * make sure that they are initialized before the call to fromXML())<br>
 * Limitation: If a field is declared as being an XMLExternalizable
 * (not a implementation), it's externalization must
 * be handled by the subclass as this type of fields are ignored by XMLTag.
 */
public class XMLTag implements XMLExternalizable {
	private static final long serialVersionUID = 5546219649423427878L;

	/** Text contents of this node */
	protected String fText = "";

	/** Whether this tag is mandatory */
    private boolean isMandatory = false;

    /**
     * Empty constructor, tag is not mandatory  by default
     */
    public XMLTag() {
        this.isMandatory = false;
    }

    /**
     * Constructor which sets the mandatory attribute
     * @param mandatory
     */
    public XMLTag(boolean mandatory) {
        this.isMandatory = mandatory;
    }

    /**
     * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
     */
    public void toXML(Node parent) throws XMLException {
        // Create the tag
		Document doc = parent.getOwnerDocument();
		Element el = doc.createElement(getTagName());

		try {
			// Write out text contents of this node
        	Text tnode = doc.createTextNode(this.fText);
        	el.appendChild(tnode);

			// Write out attributes and child tags, if any
			Field[] fields = Utils.getAllFields(getClass());
			for (int i = 0; i < fields.length; i++) {
			    Field f = fields[i];
			    f.setAccessible(true);
			    Object fieldValue = f.get(this);

			    // attributes, if they have values
	            if (fieldValue instanceof XMLAttribute) {
	                XMLAttribute a = (XMLAttribute)fieldValue;

	                // Use attribute's name, if any, or variable name otherwise
	                String attrName = a.getAttrName();
	                if (attrName == null) {
	                    attrName = f.getName();
	                }

	                // Write attribute's value and name
	                String attrVal = a.getValue();
	                if (attrVal != null) {
	                	if (fieldValue instanceof XMLText) {
	                		// pseudo-attribute, actually a text node
	    	        		Element elText = doc.createElement(attrName);
	    	            	tnode = doc.createTextNode(attrVal);
	    	            	elText.appendChild(tnode);
	    	            	el.appendChild(elText);
	                	} else if (fieldValue instanceof XMLCData) {
	                		// pseudo-attribute, actually a CDATA text node
	    	        		Element elText = doc.createElement(attrName);
	    	            	tnode = doc.createCDATASection(attrVal);
	    	            	elText.appendChild(tnode);
	    	            	el.appendChild(elText);
	                	} else {
	                		// regular attribute
	                		el.setAttribute(attrName, attrVal.toString());
	                	}
	                }
	            }
	            // other child tags
	            else if (fieldValue instanceof XMLExternalizable) {
	            	// write only fields which are not declared
	            	// as of the interface type as we cannot read
	            	// them with fromXML()
	            	if(f.getType() != XMLExternalizable.class) {
	            		XMLExternalizable t = (XMLExternalizable)fieldValue;
	            		t.toXML(el);
	            	}
	            }
	        }

			// Add the new tag to this parent
			parent.appendChild(el);

		} catch (IllegalAccessException e) {
            throw new XMLException(e);
        }
    }

    /**
     * @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
     */
    public void fromXML(Node node) throws XMLException {
        try {
    		NamedNodeMap attrs = node.getAttributes();
    		Node n;

    		// Read text contents of this node
    		this.fText = XMLUtils.getText(node);

			// Read attributes and child tags, if any
			Field[] fields = Utils.getAllFields(getClass());
			for (int i = 0; i < fields.length; i++) {
			    Field f = fields[i];
			    f.setAccessible(true);
			    Object fieldValue = f.get(this);
			    // set values for attributes
	            if (fieldValue instanceof XMLAttribute){
	                XMLAttribute a = (XMLAttribute)fieldValue;

	                // Use attribute's name, if any, or variable name otherwise
	                String attrName = a.getAttrName();
	                if (attrName == null) {
	                    attrName = f.getName();
	                }

	                String attrVal = null;
                	if (fieldValue instanceof XMLText) {
                		// pseudo-attribute, actually a text node
    	                Node child = XMLUtils.findChild(node, attrName);
    	                if (child != null) {
    	                	attrVal = XMLUtils.getText(child);
    	                } else if (a.isMandatory()) {
    	                    throw new XMLNodeMissing(attrName);
    	                }
                    } else if (fieldValue instanceof XMLCData) {
                    		// pseudo-attribute, actually a CDATA node
        	                Node child = XMLUtils.findChild(node, attrName);
        	                if (child != null) {
        	                	attrVal = XMLUtils.getText(child);
        	                } else if (a.isMandatory()) {
        	                    throw new XMLNodeMissing(attrName);
        	                }
                	} else {
		                // regular attribute: read its value
						n = attrs.getNamedItem(attrName);
						if (n != null) {
							attrVal = n.getNodeValue();
    	                } else if (a.isMandatory()) {
    	                    throw new XMLAttributeMissing(attrName);
    	                }
                	}

					if (attrVal != null) {
					    a.setValue(attrVal);
					}
	            }
	            // child single tags
	            else if (fieldValue instanceof XMLTag)
	            {
	                XMLTag t = (XMLTag)fieldValue;
	                Node child = XMLUtils.findChild(node, t.getTagName());
	                if (child != null) {
	                    t.fromXML(child);
	                } else if (t.isMandatory()) {
	                    throw new XMLNodeMissing(t.getTagName());
	                }
	            }
	            // child multi tags get initialized from the same tag
	            else if (fieldValue instanceof XMLExternalizable)
	            {
	                XMLExternalizable t = (XMLExternalizable)fieldValue;
	                t.fromXML(node);
	            }
	        }
		} catch (IllegalAccessException e) {
            throw new XMLException(e);
        }
    }

    /**
     * Override to change the name of the XML tag. By default
     * uses the low-caps version of the name of the class.
     */
    public String getTagName() {
        return getClass().getSimpleName().toLowerCase();
    }

    /**
     * @return whether this attribute is mandatory
     */
    public boolean isMandatory() {
        return isMandatory;
    }
}
