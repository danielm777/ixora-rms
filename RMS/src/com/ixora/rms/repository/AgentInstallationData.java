package com.ixora.rms.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ixora.common.ComponentVersion;
import com.ixora.common.MessageRepository;
import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.common.xml.exception.XMLNodeMissing;
import com.ixora.rms.exception.AgentVersionIsNotInstalled;

/**
 * This class describes an installed monitoring agent.
 * @author Daniel Moraru
 */
public final class AgentInstallationData implements InstallationArtefact {
	private static final long serialVersionUID = -812024275951557532L;
	/** Agent implementation class name */
	private String implClass;
	/** Agent description */
	private String description;
	/** Agent alternate name (user friendly name/description) */
	private String name;
	/** Supported monitored system versions */
	private String[] systemVersions;
	/** Agent version */
	private ComponentVersion agentVersion;
	/** Custom agent */
	private boolean customAgent;
	/**
	 * The message catalog for this agent; if not set the agent installation
	 * id will be used to look up localized messages.
	 */
	private String messageCatalog;
	/** Help url for java help */
	private String javaHelp;
	/** Help url for web help */
	private String webHelp;
	/** The categoty this agents belongs to */
	private String category;
	/**
     * Per version data.
	 */
    private VersionableAgentInstallationDataMap versionData;

	/**
	 * Default constructor to support XML.
	 */
	public AgentInstallationData(){
		super();
        this.versionData = new VersionableAgentInstallationDataMap();
	}

	/**
	 * Constructor.
	 * @param implClass agent implementation class
	 * @param customAgent true if this is a custom agent
	 * @param version the version of this agent
	 * @param configPanelClass agent custom configuration panel class
	 * @param agentName agent name
	 * @param description agent description
	 * @param locations supported locations
	 * @param levels supported monitoring levels
	 * @param versions supported monitored system versions
	 * @param defaultLevelIdx
	 * @param jars
	 * @param uiJar
	 * @param msgCatlog
	 * @param javaHelp
	 * @param webHelp
	 * @param versData
	 * @param category the category this agent belongs to
	 */
	public AgentInstallationData(
		String implClass,
		boolean customAgent,
		ComponentVersion version,
		String agentName,
		String description,
		String[] versions,
		String msgCatalog,
		String javaHelp,
		String webHelp,
        VersionableAgentInstallationDataMap versData,
        String category) {
		super();
		init(implClass, customAgent, version, agentName, description, versions, msgCatalog, javaHelp, webHelp, versData, category);
        // check if we have at least one version data
        Collection<VersionableAgentInstallationData> coll = this.versionData.getAll();
        if(Utils.isEmptyCollection(coll)) {
        	throw new IllegalArgumentException("No version data");
        }
	}

	/**
	 * @param implClass
	 * @param customAgent
	 * @param version
	 * @param agentName
	 * @param description
	 * @param versions
	 * @param msgCatalog
	 * @param javaHelp
	 * @param webHelp
	 * @param versData
	 * @param category
	 */
	private void init(String implClass,
			boolean customAgent,
			ComponentVersion version,
			String agentName,
			String description,
			String[] versions,
			String msgCatalog,
			String javaHelp,
			String webHelp,
	        VersionableAgentInstallationDataMap versData, String category) {
		this.implClass = implClass;
		this.customAgent = customAgent;
		this.agentVersion = version;
		this.name = agentName;
		this.description = description;
		this.systemVersions = versions;
		this.messageCatalog = msgCatalog;
		this.javaHelp = javaHelp;
		this.webHelp = webHelp;
        this.versionData = versData;
        this.category = category;
	}

    /**
     * Constructor.
     * @param implClass agent implementation class
     * @param customAgent true if this is a custom agent
     * @param version the version of this agent
     * @param configPanelClass agent custom configuration panel class
     * @param agentName agent name
     * @param description agent description
     * @param locations supported locations
     * @param levels supported monitoring levels
     * @param versions supported monitored system versions
     * @param defaultLevelIdx
     * @param jars
     * @param uiJar
     * @param msgCatlog
     * @param versData
     * @param category
     */
    public AgentInstallationData(
        String implClass,
        boolean customAgent,
        ComponentVersion version,
        String agentName,
        String description,
        String[] versions,
        String msgCatalog,
        String javaHelp,
        String webHelp,
        VersionableAgentInstallationData versData,
        String category) {
    	super();
        init(implClass, customAgent, version, agentName, description, versions, msgCatalog, javaHelp, webHelp,
        		new VersionableAgentInstallationDataMap(), category);
        this.versionData.add(versData);
    }

	/**
	 * @return the name of the agent
	 */
	public String getAgentName() {
		return name;
	}

	/**
	 * @return the agent class
	 */
	public String getAgentImplClass() {
		return implClass;
	}

    /**
     * @param suoVersion can be null
     * @return agent installation data for the given system under observation version; will not be null
     * @throws AgentVersionIsNotInstalled if the given version of the agent is not installed
     */
    public VersionableAgentInstallationData getVersionData(String suoVersion) throws AgentVersionIsNotInstalled {
    	VersionableAgentInstallationData ret = this.versionData.getVersionData(suoVersion);
    	if(ret == null) {
    		// get the one that aplies to all
    		ret = this.versionData.getVersionData(null);
    	}
        if(ret == null) {
        	throw new AgentVersionIsNotInstalled(
                   MessageRepository.get(getAgentInstallationId(), getAgentInstallationId()), suoVersion);
       }
       return ret;
    }

    /**
     * @return
     */
    public VersionableAgentInstallationDataMap getVersionData() {
        return this.versionData;
    }

	/**
	 * @return the agent installation id
	 */
	public String getAgentInstallationId() {
		// use the name as id
		return name;
	}

	/**
	 * @return the agent description
	 */
	public String getAgentDescription() {
		return description;
	}

	/**
	 * @return the category this agent belongs to
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @return the supported monitored system versions
	 */
	public String[] getSystemUnderObservationVersions() {
		return systemVersions;
	}


	/**
	 * @return the java help url.
	 */
	public String getJavaHelp() {
		return javaHelp;
	}

	/**
	 * @return the web help url.
	 */
	public String getWebHelp() {
		return webHelp;
	}
	
	/**
	 * @return the agent version.
	 */
	public ComponentVersion getAgentVersion() {
		return agentVersion;
	}

	/**
	 * @return true if this agent is a custom one (not supplied with the tool)
	 */
	public boolean isCustomAgent() {
		return customAgent;
	}

	/**
	 * @see com.ixora.rms.repository.InstallationArtefact#getInstallationIdentifier()
	 */
	public String getInstallationIdentifier() {
		return getAgentInstallationId();
	}

	/**
	 * @return the messageCatalog.
	 */
	public String getMessageCatalog() {
		return messageCatalog != null ? messageCatalog : getAgentInstallationId();
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
	 */
	public void toXML(Node parent) throws XMLException {
		Document doc = parent.getOwnerDocument();
		Element an = (Element)parent;
		Element el = doc.createElement("class");
		an.appendChild(el);
		el.appendChild(doc.createTextNode(implClass));
		el = doc.createElement("version");
		an.appendChild(el);
		el.appendChild(doc.createTextNode(agentVersion.toString()));
		el = doc.createElement("name");
		an.appendChild(el);
		el.appendChild(doc.createTextNode(name));
		el = doc.createElement("description");
		an.appendChild(el);
		el.appendChild(doc.createTextNode(description));
		el = doc.createElement("custom");
		an.appendChild(el);
		el.appendChild(doc.createTextNode(String.valueOf(customAgent)));
		if(category != null) {
			el = doc.createElement("category");
			an.appendChild(el);
			el.appendChild(doc.createTextNode(category));
		}
		Element el2;
        if(systemVersions != null) {
			el = doc.createElement("versions");
			an.appendChild(el);
			for(int i = 0; i < systemVersions.length; i++) {
				el2 = doc.createElement("version");
				el.appendChild(el2);
				el2.appendChild(doc.createTextNode(systemVersions[i]));
			}
		}
		if(messageCatalog != null) {
			el = doc.createElement("messageCatalog");
			an.appendChild(el);
			el.appendChild(doc.createTextNode(messageCatalog));
		}
		if(javaHelp != null) {
			el = doc.createElement("javaHelp");
			an.appendChild(el);
			el.appendChild(doc.createTextNode(javaHelp));
		}
		if(webHelp != null) {
			el = doc.createElement("webHelp");
			an.appendChild(el);
			el.appendChild(doc.createTextNode(webHelp));
		}
		this.versionData.toXML(parent);
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
	 */
	public void fromXML(Node node) throws XMLException {
		Node n = XMLUtils.findChild(node, "class");
		if(n == null) {
			throw new XMLNodeMissing("class");
		}
		this.implClass = XMLUtils.getText(n);
		n = XMLUtils.findChild(node, "name");
		if(n == null) {
			throw new XMLNodeMissing("name");
		}
		this.name = XMLUtils.getText(n);
		n = XMLUtils.findChild(node, "version");
		if(n == null) {
			throw new XMLNodeMissing("version");
		}
		this.agentVersion = new ComponentVersion(
				XMLUtils.getText(n));
		n = XMLUtils.findChild(node, "description");
		if(n == null) {
			throw new XMLNodeMissing("description");
		}
		this.description = XMLUtils.getText(n);

		n = XMLUtils.findChild(node, "custom");
		if(n != null) {
			this.customAgent = Utils.parseBoolean(XMLUtils.getText(n));
		}

		n = XMLUtils.findChild(node, "category");
		if(n != null) {
			this.category = XMLUtils.getText(n);
		}

		List<Node> m;
		// versions are optional
		n = XMLUtils.findChild(node, "versions");
        if(n != null) {
			m = XMLUtils.findChildren(n, "version");
			if(m.size() == 0) {
				throw new XMLNodeMissing("version");
			}
			List<String> vers = new ArrayList<String>(m.size());
			String v;
			for(Iterator<Node> iter = m.iterator(); iter.hasNext();) {
				 n = iter.next();
				 v = XMLUtils.getText(n);
				 if(v != null) {
				 	vers.add(v);
				 }
			}
			this.systemVersions = (String[])vers.toArray(new String[vers.size()]);
		}
		n = XMLUtils.findChild(node, "messageCatalog");
		if(n != null) {
			this.messageCatalog = XMLUtils.getText(n);
		}
		n = XMLUtils.findChild(node, "javaHelp");
		if(n != null) {
			this.javaHelp = XMLUtils.getText(n);
		}
		n = XMLUtils.findChild(node, "webHelp");
		if(n != null) {
			this.webHelp = XMLUtils.getText(n);
		}

		this.versionData = new VersionableAgentInstallationDataMap();
		this.versionData.fromXML(node);
	}
}
