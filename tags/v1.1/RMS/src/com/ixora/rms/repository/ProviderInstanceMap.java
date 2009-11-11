/*
 * Created on 30-Dec-2004
 */
package com.ixora.rms.repository;

import java.util.Collection;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ixora.common.xml.XMLExternalizable;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;

/**
 * @author Daniel Moraru
 */
public class ProviderInstanceMap extends VersionableAgentArtefactMap<ProviderInstance> {
	private static final long serialVersionUID = -6112038774585948420L;

	/**
	 * Constructor.
	 */
	public ProviderInstanceMap() {
		super();
	}


	/**
	 * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
	 */
	public void toXML(Node parent) throws XMLException {
		Document doc = parent.getOwnerDocument();
		Element qe = doc.createElement("providerInstances");
		parent.appendChild(qe);
		try {
            Collection<ProviderInstance> all = getAll();
			XMLUtils.writeObjects(
					null, qe, all == null ? null : all.toArray(new ProviderInstance[all.size()]));
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
		Node n = XMLUtils.findChild(node, "providerInstances");
		if(n != null) {
			try {
				XMLExternalizable[] objs = XMLUtils.readObjects(
						ProviderInstance.class, n, "providerInstance");
				if(objs == null) {
				    return;
				}
				for(int i = 0; i < objs.length; i++) {
					ProviderInstance pi = (ProviderInstance)objs[i];
					add(pi);
				}
			} catch(Exception e) {
				throw new XMLException(e);
			}
		}
	}
}
