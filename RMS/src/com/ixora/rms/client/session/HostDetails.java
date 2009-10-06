/*
 * Created on 31-Dec-2003
 */
package com.ixora.rms.client.session;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ixora.common.xml.XMLExternalizable;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLAttributeMissing;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.common.xml.exception.XMLNodeMissing;


/**
 * Host details.
 * @author Daniel Moraru
 */
public final class HostDetails implements Serializable, XMLExternalizable {
	/** Host */
	private String host;
	/** List of agent details */
	private List<AgentDetails> agents;


	/**
	 * Constructor.
	 * @param agents
	 */
	public HostDetails() {
		super();
		agents = new LinkedList<AgentDetails>();
	}

	/**
	 * Constructor.
	 * @param host
	 * @param agents
	 */
	public HostDetails(String host) {
		super();
		this.host = host;
		agents = new LinkedList<AgentDetails>();
	}

	/**
	 * @return
	 */
	public Collection<AgentDetails> getAgents() {
		return agents;
	}

	/**
	 * @return
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param list
	 */
	public void addAgentDetails(AgentDetails ad) {
		agents.add(ad);
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
	 */
	public void toXML(Node parent) throws XMLException {
		Document doc = parent.getOwnerDocument();
		Element el = doc.createElement("host");
		parent.appendChild(el);
		el.setAttribute("name", host);
		Element el2 = doc.createElement("agents");
		el.appendChild(el2);
		AgentDetails ad;
		for(Iterator<AgentDetails> iter = agents.iterator(); iter.hasNext();) {
			ad = iter.next();
			ad.toXML(el2);
		}
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
	 */
	public void fromXML(Node node) throws XMLException {
		Attr a = XMLUtils.findAttribute(node, "name");
		if(a == null) {
			throw new XMLAttributeMissing("name");
		}
		this.host = a.getValue();
		Node n = XMLUtils.findChild(node, "agents");
		if(n == null) {
			throw new XMLNodeMissing("agents");
		}
		List l = XMLUtils.findChildren(n, "agent");
		AgentDetails ad;
		for(Iterator iter = l.iterator(); iter.hasNext();) {
			n = (Node)iter.next();
			ad = new AgentDetails();
			ad.fromXML(n);
			addAgentDetails(ad);
		}
	}
}