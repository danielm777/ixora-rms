/*
 * Created on 01-Jul-2005
 */
package com.ixora.rms.agents;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.common.xml.exception.XMLNodeMissing;
import com.ixora.rms.agents.AgentId;

/**
 * AgentDescriptorImpl
 */
public class AgentDescriptorImpl implements AgentDescriptor {
	private static final long serialVersionUID = -8992882807821367968L;
	private AgentConfiguration fAgentConfig;
    private AgentId fAgentId;
    private boolean fSafeForRefresh;

    /**
     * Default constructor to support XML.
     */
    public AgentDescriptorImpl() {
    }

    /**
     * @param config
     * @param id
     */
    public AgentDescriptorImpl(AgentConfiguration config, AgentId id) {
    	this(config, id, true);
    }

    /**
     * @param config
     * @param id
     * @param safeForRefresh
     */
    public AgentDescriptorImpl(AgentConfiguration config, AgentId id, boolean safeForRefresh) {
        super();
        fAgentConfig = config;
        fAgentId = id;
        fSafeForRefresh = safeForRefresh;
    }

    /**
     * @see com.ixora.rms.agents.AgentDescriptor#getAgentConfiguration()
     */
    public AgentConfiguration getAgentConfiguration() {
        return this.fAgentConfig;
    }

    /**
     * @see com.ixora.rms.agents.AgentDescriptor#getAgentId()
     */
    public AgentId getAgentId() {
        return fAgentId;
    }

	/**
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		try {
			AgentDescriptorImpl ad = (AgentDescriptorImpl)super.clone();
			ad.fAgentConfig = (AgentConfiguration)this.fAgentConfig.clone();
			return ad;
		} catch(CloneNotSupportedException e) {
			throw new InternalError();
		}
	}

    /**
     * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
     */
    public void toXML(Node parent) throws XMLException {
        Document doc = parent.getOwnerDocument();
        Element el = doc.createElement("agentDescriptor");
        parent.appendChild(el);
        Element el2 = doc.createElement("agentId");
        el.appendChild(el2);
        el2.appendChild(doc.createTextNode(fAgentId.toString()));
        fAgentConfig.toXML(el);
    }

    /**
     * @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
     */
    public void fromXML(Node node) throws XMLException {
        Node n = XMLUtils.findChild(node, "agentId");
        if(n == null) {
            throw new XMLNodeMissing("agentId");
        }
        fAgentId = new AgentId(XMLUtils.getText(n));
        n = XMLUtils.findChild(node, "config");
        if(n == null) {
            throw new XMLNodeMissing("config");
        }
        fAgentConfig = new AgentConfiguration();
        fAgentConfig.fromXML(n);
    }

	/**
	 * @see com.ixora.rms.agents.AgentDescriptor#safeToRefreshRecursivelly()
	 */
	public boolean safeToRefreshRecursivelly() {
		return fSafeForRefresh;
	}
}
