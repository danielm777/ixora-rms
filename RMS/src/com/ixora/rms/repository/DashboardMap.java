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
import com.ixora.common.xml.exception.XMLNodeMissing;

/**
 * This is a container for a set of dashboards that knows to
 * serialize itself as XML.
 * @author Daniel Moraru
 */
public class DashboardMap extends VersionableAgentArtefactMap<Dashboard> {
	private static final long serialVersionUID = -1689751366833510569L;
	/**
	 * Constructor.
	 */
	public DashboardMap() {
		super();
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
	 */
	public void toXML(Node parent) throws XMLException {
		Document doc = parent.getOwnerDocument();
		Element qe = doc.createElement("dashboards");
		parent.appendChild(qe);
		XMLUtils.writeObjects(
			Dashboard.class,
			qe,
			getAll().toArray(new Dashboard[0]));
	}
	/**
	 * @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
	 */
	public void fromXML(Node node) throws XMLException {
		try {
		    Node n = XMLUtils.findChild(node, "dashboards");
		    if(n == null) {
		        throw new XMLNodeMissing("dashboards");
		    }
			XMLExternalizable[] objs = XMLUtils.readObjects(
			        Dashboard.class, n, "dashboard");
			if(objs != null) {
				Dashboard dtls;
				for(int i = 0; i < objs.length; i++) {
				    dtls = (Dashboard)objs[i];
					add(dtls);
				}
			}
		} catch(Exception e) {
			throw new XMLException(e);
		}
	}
}
