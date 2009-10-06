/*
 * Created on 10-Jul-2004
 */
package com.ixora.rms.repository;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ixora.common.xml.XMLExternalizable;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;

/**
 * This is a container for a set of queries that knows to
 * serialize itself as XML.
 * @author Daniel Moraru
 */
public class DataViewMap extends VersionableAgentArtefactMap<DataView> {

	/**
	 * Constructor.
	 */
	public DataViewMap() {
		super();
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
	 */
	public void toXML(Node parent) throws XMLException {
		Document doc = parent.getOwnerDocument();
		Element qe = doc.createElement("views");
		parent.appendChild(qe);
		try {
			XMLUtils.writeObjects(
					null, qe, getAll().toArray(new DataView[0]));
		} catch(XMLException e) {
			throw e;
		} catch(Exception e) {
		    throw new XMLException(e);
		}
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
	 */
	public void fromXML(Node node) throws XMLException {
		Node n = XMLUtils.findChild(node, "views");
		if(n != null) {
			try {
				XMLExternalizable[] objs = XMLUtils.readObjects(
						null, n, "view");
				if(objs == null) {
				    return;
				}
				DataView v;
				for(int i = 0; i < objs.length; i++) {
					v = (DataView)objs[i];
					add(v);
				}
			} catch(Exception e) {
				throw new XMLException(e);
			}
		}
	}
}
