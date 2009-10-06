/*
 * Created on 14-Jan-2004
 */
package com.ixora.rms.client;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ixora.common.xml.XMLExternalizable;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.common.xml.exception.XMLNodeMissing;
import com.ixora.rms.agents.AgentActivationData;
import com.ixora.rms.agents.AgentConfiguration;
import com.ixora.rms.agents.AgentDescriptor;
import com.ixora.rms.agents.AgentDescriptorImpl;
import com.ixora.rms.agents.AgentId;
import com.ixora.rms.agents.AgentLocation;

/**
 * @author Daniel Moraru
 */
public final class AgentInstanceData implements XMLExternalizable, Cloneable {
	/** Agent activation details */
	private AgentActivationData activationData;
	/** Descriptor */
	private AgentDescriptor agentDescriptor;

	/**
	 * Default constructor to support XML.
	 */
	public AgentInstanceData() {
		super();
        this.activationData = new AgentActivationData();
        this.agentDescriptor = new AgentDescriptorImpl();
	}

    /**
     * @param ad
     */
    public AgentInstanceData(AgentDescriptor ad) {
        this.activationData = new AgentActivationData();
        this.activationData.setConfiguration(ad.getAgentConfiguration());
        this.agentDescriptor = ad;
    }

	/**
	 * Constructor.
	 * @param config
	 * @param location
	 */
	public AgentInstanceData(AgentActivationData aad, AgentDescriptor ad) {
		super();
		this.activationData = aad;
		this.agentDescriptor = ad;
	}

	/**
	 * @return
	 */
	public AgentConfiguration getConfiguration() {
		return activationData.getConfiguration();
	}

	/**
	 * @return
	 */
	public AgentDescriptor getDescriptor() {
		return agentDescriptor;
	}

	/**
     * @param conf
     */
    public void setConfiguration(AgentConfiguration conf) {
        this.activationData.setConfiguration(conf);
    }

	/**
	 * @return
	 */
	public AgentLocation getLocation() {
		return activationData.getLocation();
	}

    /**
     * @return
     */
    public AgentActivationData getAgentActivationData() {
        return this.activationData;
    }

	/**
	 * @return the agentId.
	 */
	public AgentId getAgentId() {
		return this.agentDescriptor.getAgentId();
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		try {
			AgentInstanceData aid = (AgentInstanceData)super.clone();
			aid.activationData = (AgentActivationData)this.activationData.clone();
			aid.agentDescriptor = (AgentDescriptor)this.agentDescriptor.clone();
			return aid;
		} catch(CloneNotSupportedException e) {
			throw new InternalError();
		}
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
	 */
	public void toXML(Node parent) throws XMLException {
		Document doc = parent.getOwnerDocument();
		Element el = doc.createElement("instanceData");
		parent.appendChild(el);
        this.activationData.toXML(el);
        this.agentDescriptor.toXML(el);
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
	 */
	public void fromXML(Node node) throws XMLException {
		Node n = XMLUtils.findChild(node, "activation");
		if(n == null) {
			throw new XMLNodeMissing("activation");
		}
        this.activationData.fromXML(n);
		n = XMLUtils.findChild(node, "agentDescriptor");
		if(n == null) {
			throw new XMLNodeMissing("agentDescriptor");
		}
		this.agentDescriptor.fromXML(n);
	}
}
