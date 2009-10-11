/*
 * Created on 14-Jan-2004
 */
package com.ixora.rms.agents;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ixora.common.xml.XMLExternalizable;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.common.xml.exception.XMLInvalidFieldValue;
import com.ixora.common.xml.exception.XMLNodeMissing;

/**
 * @author Daniel Moraru
 */
public final class AgentActivationData implements XMLExternalizable, Cloneable {
	private static final long serialVersionUID = -7939795643007611080L;
	/** Agent configuration details */
    private AgentConfiguration config;
    /** Agent location */
    private AgentLocation location;
    /** Host to monitor */
    private String host;
    /** Agent installation id */
    private String agentInstallationId;


    /**
     * Default constructor to support XML.
     */
    public AgentActivationData() {
        super();
    }

    /**
     * Constructor.
     * @param host
     * @param agentInstallationId
     * @param config
     * @param location
     */
    public AgentActivationData(String host,
            String agentInstallationId,
            AgentConfiguration config,
            AgentLocation location) {
        super();
        this.host = host;
        this.agentInstallationId = agentInstallationId;
        this.config = config;
        this.location = location;
    }

    /**
     * @return
     */
    public AgentConfiguration getConfiguration() {
        return config;
    }

    /**
     * @return
     */
    public AgentLocation getLocation() {
        return location;
    }

    /**
     * @param location
     */
    public void setLocation(AgentLocation location) {
        this.location = location;
    }

    /**
     * @param dtls
     */
    public void setConfiguration(AgentConfiguration dtls) {
        config = dtls;
    }

    /**
     * @return Returns the agentInstallationId.
     */
    public String getAgentInstallationId() {
        return agentInstallationId;
    }

    /**
     * @param agentInstallationId The agentInstallationId to set.
     */
    public void setAgentInstallationId(String agentInstallationId) {
        this.agentInstallationId = agentInstallationId;
    }

    /**
     * @return Returns the host.
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host The host to set.
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        try {
            AgentActivationData aid = (AgentActivationData)super.clone();
            aid.config = (AgentConfiguration)this.config.clone();
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
        Element el = doc.createElement("activation");
        parent.appendChild(el);
        Element el2 = doc.createElement("location");
        el.appendChild(el2);
        el2.appendChild(doc.createTextNode(String.valueOf(location.getKey())));
        el2 = doc.createElement("host");
        el.appendChild(el2);
        el2.appendChild(doc.createTextNode(this.host));
        el2 = doc.createElement("agentInstallationId");
        el.appendChild(el2);
        el2.appendChild(doc.createTextNode(this.agentInstallationId));
        if(config == null) {
            XMLException e = new XMLException("config is null");
            e.setIsInternalAppError();
            throw e;
        }
        config.toXML(el);
    }

    /**
     * @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
     */
    public void fromXML(Node node) throws XMLException {
        Node n = XMLUtils.findChild(node, "location");
        if(n == null) {
            throw new XMLNodeMissing("location");
        }
        this.location = AgentLocation.resolve(Integer.parseInt(n.getFirstChild().getNodeValue()));
        if(this.location == null) {
            throw new XMLInvalidFieldValue(node.getFirstChild().getNodeValue());
        }
        n = XMLUtils.findChild(node, "host");
        if(n == null) {
            throw new XMLNodeMissing("host");
        }
        this.host = XMLUtils.getText(n);
        n = XMLUtils.findChild(node, "agentInstallationId");
        if(n == null) {
            throw new XMLNodeMissing("agentInstallationId");
        }
        this.agentInstallationId = XMLUtils.getText(n);
        n = XMLUtils.findChild(node, "config");
        if(n == null) {
            throw new XMLNodeMissing("config");
        }
        this.config = new AgentConfiguration();
        this.config.fromXML(n);
    }
}
