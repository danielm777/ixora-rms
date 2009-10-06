/*
 * Created on 31-Dec-2003
 */
package com.ixora.rms.client.session;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ixora.common.xml.XMLExternalizable;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.common.xml.exception.XMLNodeMissing;
import com.ixora.rms.EntityConfiguration;
import com.ixora.rms.EntityDescriptor;
import com.ixora.rms.EntityDescriptorImpl;
import com.ixora.rms.EntityId;


/**
 * Entity details.
 * @author Daniel Moraru
 */
public final class EntityDetails implements XMLExternalizable  {
	/** Entity configuration */
	private EntityConfiguration configuration;
	/**
	 * Entity descriptor. Used only for playback only as
	 * for a live session this info is got from the agent
	 */
	private EntityDescriptor descriptor;

	/**
	 * Constructor.
	 */
	public EntityDetails() {
	}

	/**
	 * Constructor.
	 * @param desc
	 * @param config
	 */
	public EntityDetails(EntityDescriptor desc, EntityConfiguration config) {
		descriptor = desc;
		configuration = config;
	}

	/**
	 * @return
	 */
	public EntityConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * @return desc
	 */
	public EntityDescriptor getDescriptor() {
	    return descriptor;
	}

    /**
     * @return
     */
    public EntityId getEntityId() {
        return descriptor.getId();
    }

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
	 */
	public void toXML(Node parent) throws XMLException {
		Document doc = parent.getOwnerDocument();
		Element el = doc.createElement("entity");
		parent.appendChild(el);
		configuration.toXML(el);
		Element desce = doc.createElement("entitydescriptor");
		el.appendChild(desce);
		descriptor.toXML(desce, configuration.getMonitoredCountersIds());
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
	 */
	public void fromXML(Node node) throws XMLException {
		Node n = XMLUtils.findChild(node, "config");
		if(n == null) {
			throw new XMLNodeMissing("config");
		}
		this.configuration = new EntityConfiguration();
		this.configuration.fromXML(n);
		this.descriptor = new EntityDescriptorImpl();
		n = XMLUtils.findChild(node, "entitydescriptor");
		if(n == null) {
		    throw new XMLNodeMissing("entitydescriptor");
		}
		this.descriptor.fromXML(n);
	}
}