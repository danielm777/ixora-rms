/*
 * Created on 13-Jan-2004
 */
package com.ixora.common.xml;

import java.io.Serializable;

import org.w3c.dom.Node;

import com.ixora.common.xml.exception.XMLException;

/**
 * Interface implemented by objects that know to represent
 * their state as XML.
 * @author Daniel Moraru
 */
public interface XMLExternalizable extends Serializable {
	/**
	 * Writes the representation of this object
	 * to the parent node
	 * @param parent the parent node to which to append
	 * the representation of this object
	 * @throws XMLException if the object is not in a valid state
	 * at the moment this method is called
	 */
	void toXML(Node parent) throws XMLException;
	/**
	 * Build this object from the XML represantion given
	 * in the <code>node</code>.
	 * @param node
	 * @throws XMLException if the node is invalid for this object
	 */
	void fromXML(Node node) throws XMLException;
}
