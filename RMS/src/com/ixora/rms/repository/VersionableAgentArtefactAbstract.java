/*
 * Created on 26-Jun-2005
 */
package com.ixora.rms.repository;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLExternalizable;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;

/**
 * VersionableAgentArtefact
 */
public abstract class VersionableAgentArtefactAbstract implements XMLExternalizable, VersionableAgentArtefact {
    /** Agent versions to which this provider instance applies */
    protected HashSet<String> agentVersions;

    public VersionableAgentArtefactAbstract() {
        super();
        this.agentVersions = new HashSet<String>();
    }

    public VersionableAgentArtefactAbstract(Collection<String> agentVersions) {
        super();
        setUpFromCollection(agentVersions);
    }

    /**
     * @see com.ixora.rms.repository.VersionableAgentArtefact#getAgentVersions()
     */
    public Set<String> getAgentVersions() {
        if(agentVersions == null) {
            return null;
        }
        return (Set<String>)agentVersions.clone();
    }

    /**
     * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
     */
    public void toXML(Node parent) throws XMLException {
        Document doc = parent.getOwnerDocument();
        Element el = doc.createElement("agentVersions");
        parent.appendChild(el);
        if(this.agentVersions != null) {
            for(String av : this.agentVersions) {
                Element el2 = doc.createElement("agentVersion");
                el.appendChild(el2);
                el2.appendChild(doc.createTextNode(av));
            }
        }
    }

    /**
     * @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
     */
    public void fromXML(Node node) throws XMLException {
        Node n = XMLUtils.findChild(node, "agentVersions");
        if(n != null) {
            List lst = XMLUtils.findChildren(n, "agentVersion");
            if(!Utils.isEmptyCollection(lst)) {
                for(Iterator iter = lst.iterator(); iter.hasNext();) {
                     String av = XMLUtils.getText((Node)iter.next());
                     if(av != null) {
                        this.agentVersions.add(av);
                     }
                }
            }
        }
    }

    /**
     * @see com.ixora.rms.repository.VersionableAgentArtefact#addAgentVersions(java.util.Collection)
     */
    public void addAgentVersions(Collection<String> versions) {
        this.agentVersions.addAll(versions);
    }

    /**
     * @see com.ixora.rms.repository.VersionableAgentArtefact#removeAgentVersions(java.util.Collection)
     */
    public void removeAgentVersions(Collection<String> versions) {
        this.agentVersions.removeAll(versions);
    }

    /**
     * @see com.ixora.rms.repository.VersionableAgentArtefact#setAgentVersions(java.util.Collection)
     */
    public void setAgentVersions(Collection<String> versions) {
        setUpFromCollection(versions);
    }

    /**
     * @param versions
     */
    private void setUpFromCollection(Collection<String> versions) {
        if(!Utils.isEmptyCollection(versions)) {
            this.agentVersions = new HashSet<String>(versions);
        } else {
            this.agentVersions = new HashSet<String>();
        }
    }

    /**
     * @see com.ixora.rms.repository.VersionableAgentArtefact#appliesToAgentVersion(java.lang.String)
     */
    public boolean appliesToAgentVersion(String agentVersion) {
        // in no agent versions specified then it applies to all versions
        return agentVersion == null || this.agentVersions.size() == 0 || this.agentVersions.contains(agentVersion);
    }
}
