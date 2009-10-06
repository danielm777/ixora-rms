package com.ixora.rms.repository;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ixora.common.xml.XMLExternalizable;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.common.xml.exception.XMLNodeMissing;

/**
 * Manages versions of agent installation data for a particular agent.
 * @author Daniel Moraru
 */
public final class VersionableAgentInstallationDataMap
	extends VersionableAgentArtefactMap<VersionableAgentInstallationData> {

	/**
	 * @param suoVersion
	 */
	public VersionableAgentInstallationData getVersionData(String suoVersion) {
		return getForAgentVersion(VersionableAgentInstallationData.ARTEFACT_NAME, suoVersion);
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
	 */
	public void toXML(Node parent) throws XMLException {
		Document doc = parent.getOwnerDocument();
		Element qe = doc.createElement("versionItems");
		parent.appendChild(qe);
		XMLUtils.writeObjects(
			VersionableAgentInstallationData.class,
			qe,
			getAll().toArray(new VersionableAgentInstallationData[0]));
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
	 */
	public void fromXML(Node node) throws XMLException {
		try {
		    Node n = XMLUtils.findChild(node, "versionItems");
		    if(n == null) {
		        throw new XMLNodeMissing("versionItems");
		    }
			XMLExternalizable[] objs = XMLUtils.readObjects(
					VersionableAgentInstallationData.class, n, "versionItem");
			if(objs != null) {
				for(int i = 0; i < objs.length; i++) {
				    VersionableAgentInstallationData dtls = (VersionableAgentInstallationData)objs[i];
					add(dtls);
				}
			}
		} catch(Exception e) {
			throw new XMLException(e);
		}
	}
}
