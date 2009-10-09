package com.ixora.common.xml;
import java.util.Iterator;
import java.util.LinkedList;

import org.w3c.dom.Node;

import com.ixora.common.xml.exception.XMLException;

/*
 * Created on 27-Nov-2004
 */

/**
 * XMLTagList
 * @author Cristian Costache
 * @author Daniel Moraru
 */
public abstract class XMLTagList<T extends XMLTag> extends LinkedList<T> implements XMLExternalizable {
	private static final long serialVersionUID = 6287597392968099993L;

	public XMLTagList() {
    }

    /**
     * @see XMLExternalizable#toXML(org.w3c.dom.Node)
     */
    public void toXML(Node parent) throws XMLException {
        for (Iterator<T> it = iterator(); it.hasNext();) {
            XMLTag tag = it.next();
            tag.toXML(parent);
        }
    }

    /**
     * @see XMLExternalizable#fromXML(org.w3c.dom.Node)
     */
    public void fromXML(Node node) throws XMLException {
	    // Clear contents first
	    this.clear();
    }

}
