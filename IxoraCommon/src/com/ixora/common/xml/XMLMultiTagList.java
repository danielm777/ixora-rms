package com.ixora.common.xml;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ixora.common.xml.exception.XMLException;

/*
 * Created on 27-Nov-2004
 */

/**
 * XMLMultiTagList
 */
public class XMLMultiTagList extends XMLTagList {
    /** Instance of a class who can create different tags */
    private XMLTagFactory tagFactory;

    public XMLMultiTagList(XMLTagFactory tagFactory) {
        this.tagFactory = tagFactory;
    }

    /**
     * @see XMLExternalizable#fromXML(org.w3c.dom.Node)
     */
    public void fromXML(Node node) throws XMLException {
        super.fromXML(node);

        // Add all known child nodes
		NodeList nl = node.getChildNodes();
		int		len = nl.getLength();
		for(int i = 0; i < len; i++)
		{
		    Node child = nl.item(i);
		    XMLTag tag = tagFactory.createFromXML(child);

		    // Only add it if the factory recognized this tag
		    if (tag != null) {
		        tag.fromXML(child);
		        this.add(tag);
		    }
		}
    }
}
