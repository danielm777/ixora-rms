/*
 * Created on 31-Dec-2003
 */
package com.ixora.rms.client.session;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ixora.common.xml.XMLExternalizable;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.common.xml.exception.XMLNodeMissing;
import com.ixora.rms.client.AgentInstanceData;


/**
 * Agent details.
 * @author Daniel Moraru
 */
public final class AgentDetails implements XMLExternalizable {
	private static final long serialVersionUID = 4781035249608187502L;
	/** Agent deployment details */
	private AgentInstanceData deploymentDtls;
	/** Entity details */
	private List<EntityDetails> entities;

	/**
	 * Constructor.
	 */
	public AgentDetails() {
		this.entities = new LinkedList<EntityDetails>();
	}

	/**
	 * Constructor.
	 * @param ddtls
	 */
	public AgentDetails(AgentInstanceData ddtls) {
		this.deploymentDtls = ddtls;
		this.entities = new LinkedList<EntityDetails>();
	}

	/**
	 * @return
	 */
	public AgentInstanceData getAgentDeploymentDtls() {
		return deploymentDtls;
	}

	/**
	 * @return
	 */
	public Collection<EntityDetails> getEntities() {
		return Collections.unmodifiableCollection(entities);
	}

	/**
	 * @param ed
	 */
	public void addEntityDetails(EntityDetails ed) {
		entities.add(ed);
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
	 */
	public void toXML(Node parent) throws XMLException {
		Document doc = parent.getOwnerDocument();
		Element el = doc.createElement("agent");
		parent.appendChild(el);
		deploymentDtls.toXML(el);
		Element el2 = doc.createElement("entities");
		el.appendChild(el2);
		for(EntityDetails ed : entities) {
			ed.toXML(el2);
		}
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
	 */
	public void fromXML(Node node) throws XMLException {
		Node n = XMLUtils.findChild(node, "instanceData");
		if(n == null) {
			throw new XMLNodeMissing("instanceData");
		}
		this.deploymentDtls = new AgentInstanceData();
		this.deploymentDtls.fromXML(n);
		n = XMLUtils.findChild(node, "entities");
		if(n == null) {
			throw new XMLNodeMissing("entities");
		}
		List<Node> l = XMLUtils.findChildren(n, "entity");
		if(l.size() == 0) {
			return;
		}
		this.entities = new ArrayList<EntityDetails>(l.size());
		EntityDetails ed;
		for(Iterator<Node> iter = l.iterator(); iter.hasNext();) {
			n = iter.next();
			ed = new EntityDetails();
			ed.fromXML(n);
			this.entities.add(ed);
		}
	}

}