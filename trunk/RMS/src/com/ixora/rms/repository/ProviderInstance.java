/*
 * Created on 27-Dec-2004
 */
package com.ixora.rms.repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.common.xml.exception.XMLNodeMissing;
import com.ixora.rms.EntityDescriptor;
import com.ixora.rms.EntityDescriptorImpl;
import com.ixora.rms.EntityId;
import com.ixora.rms.providers.ProviderConfiguration;

/**
 * The definition of the data provider.
 * @author Daniel Moraru
 */
public final class ProviderInstance extends VersionableAgentArtefactAbstract implements AuthoredArtefact {
	private static final long serialVersionUID = 4763168946154573329L;
	/** Provider name */
	private String providerName;
	/** Instance name */
	private String instanceName;
	/** Description */
	private String instanceDescription;
	/** Provider configuration */
	private ProviderConfiguration configuration;
	/** True if the provider must be deployed remotely */
	private boolean remote;
	/** True if the provider is to inherit the location from the agent */
	private boolean inheritsLocationFromAgent;
	/** Parser instance */
	private ParserInstance parserInstance;
	/** Descriptors for the entities provided by this provider instance */
	private Map<EntityId, EntityDescriptor> entityDescriptors;
	/** True if this instance is optional */
	private boolean optional;
	/** True if this instance is optional and it should be enabled by default */
	private boolean selectedByDefault;
	/** The author of this artefact */
	private String author;

	/**
	 * Constructor.
	 */
	public ProviderInstance() {
		super();
	}

	/**
	 * Constructor.
	 * @param providerName
	 * @param instanceName
	 * @param instanceDescription
	 * @param configuration
	 * @param remote
	 * @param optional
	 * @param selectedByDefault
	 * @param inheritsLocationFromAgent
	 * @param parserInstance
	 * @param entityDescriptors
	 * @param agentVersions
	 */
	public ProviderInstance(
			String providerName,
			String instanceName,
			String instanceDescription,
			ProviderConfiguration configuration,
			boolean remote,
			boolean optional,
			boolean selectedByDefault,
			boolean inheritsLocationFromAgent,
			ParserInstance parserInstance,
			Collection<EntityDescriptor> entityDescriptors,
			Collection<String> agentVersions,
			String author) {
		super(agentVersions);
		this.providerName = providerName;
		this.instanceName = instanceName;
		this.instanceDescription = instanceDescription;
		this.configuration = configuration;
		this.author = author;
		this.remote = remote;
		this.optional = optional;
		this.selectedByDefault = selectedByDefault;
		this.inheritsLocationFromAgent = inheritsLocationFromAgent;
		this.parserInstance = parserInstance;
		if(entityDescriptors != null) {
			this.entityDescriptors = new HashMap<EntityId, EntityDescriptor>();
			for(EntityDescriptor ed : entityDescriptors) {
				this.entityDescriptors.put(ed.getId(), ed);
			}
		}
	}

    /**
     * @see com.ixora.rms.repository.VersionableAgentArtefact#getArtefactName()
     */
    public String getArtefactName() {
        return instanceName;
    }

	/**
	 * @return the configuration.
	 */
	public ProviderConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * @return the name of this instance of the provider
	 */
	public String getInstanceName() {
		return this.instanceName;
	}

	/**
	 * @return the instanceDescription.
	 */
	public String getInstanceDescription() {
		return instanceDescription;
	}

	/**
	 * @return the provider name.
	 */
	public String getProviderName() {
		return providerName;
	}

	/**
	 * @return the remote.
	 */
	public boolean isRemote() {
		return remote;
	}

	/**
	 * @return the optional.
	 */
	public boolean isOptional() {
		return optional;
	}

	/**
	 * @return the inheritsLocationFromAgent.
	 */
	public boolean inheritsLocationFromAgent() {
		return inheritsLocationFromAgent;
	}

	/**
	 * @return the parserInstance.
	 */
	public ParserInstance getParserInstance() {
		return parserInstance;
	}

	/**
	 * @return the entityDescriptors.
	 */
	public Map<EntityId, EntityDescriptor> getEntityDescriptors() {
		return entityDescriptors;
	}

	/**
	 * @return
	 */
	public boolean isSelectedByDefault() {
		return selectedByDefault;
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
	 */
	public void toXML(Node parent) throws XMLException {
		Document doc = parent.getOwnerDocument();
		Element el = doc.createElement("providerInstance");
		parent.appendChild(el);
		Element el2 = doc.createElement("providerName");
		el.appendChild(el2);
		el2.appendChild(doc.createTextNode(providerName));
		el2 = doc.createElement("instanceName");
		el.appendChild(el2);
		el2.appendChild(doc.createTextNode(instanceName));
		if(instanceDescription != null) {
			el2 = doc.createElement("description");
			el.appendChild(el2);
			el2.appendChild(doc.createTextNode(instanceDescription));
		}
		// save author
		if(author != null) {
			el2 = doc.createElement("author");
			el.appendChild(el2);
			el2.appendChild(doc.createTextNode(author));
		}
		el2 = doc.createElement("remote");
		el.appendChild(el2);
		el2.appendChild(doc.createTextNode(String.valueOf(remote)));
		el2 = doc.createElement("optional");
		el.appendChild(el2);
		el2.appendChild(doc.createTextNode(String.valueOf(optional)));
		el2 = doc.createElement("selectedByDefault");
		el.appendChild(el2);
		el2.appendChild(doc.createTextNode(String.valueOf(selectedByDefault)));
		el2 = doc.createElement("inheritsLocationFromAgent");
		el.appendChild(el2);
		el2.appendChild(doc.createTextNode(String.valueOf(inheritsLocationFromAgent)));
		XMLUtils.writeObject(ProviderConfiguration.class, el, configuration);
		XMLUtils.writeObject(ParserInstance.class, el, parserInstance);
		if(entityDescriptors != null) {
			el2 = doc.createElement("descriptors");
			el.appendChild(el2);
			for(EntityDescriptor ed : entityDescriptors.values()) {
				Element desce = doc.createElement("entitydescriptor");
				el2.appendChild(desce);
				ed.toXML(desce);
			}
		}
        super.toXML(el);
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
	 */
	public void fromXML(Node node) throws XMLException {
		super.fromXML(node);
        Node n = XMLUtils.findChild(node, "providerName");
		if(n == null) {
			throw new XMLNodeMissing("providerName");
		}
		this.providerName = XMLUtils.getText(n);
		n = XMLUtils.findChild(node, "instanceName");
		if(n == null) {
			throw new XMLNodeMissing("instanceName");
		}
		this.instanceName = XMLUtils.getText(n);
		n = XMLUtils.findChild(node, "description");
		if(n != null) {
			this.instanceDescription = XMLUtils.getText(n);
		}
		n = XMLUtils.findChild(node, "author");
		if(n != null) {
			this.author = XMLUtils.getText(n);
		}
		if(Utils.isEmptyString(author)) {
			author = SYSTEM;
		}
		n = XMLUtils.findChild(node, "remote");
		if(n != null) {
			this.remote = Boolean.valueOf(XMLUtils.getText(n)).booleanValue();
		}
		n = XMLUtils.findChild(node, "optional");
		if(n != null) {
			this.optional = Boolean.valueOf(XMLUtils.getText(n)).booleanValue();
		}
		n = XMLUtils.findChild(node, "selectedByDefault");
		if(n != null) {
			this.selectedByDefault = Boolean.valueOf(XMLUtils.getText(n)).booleanValue();
		}
		n = XMLUtils.findChild(node, "inheritsLocationFromAgent");
		if(n != null) {
			this.inheritsLocationFromAgent = Boolean.valueOf(XMLUtils.getText(n)).booleanValue();
		}
		try {
			n = XMLUtils.findChild(node, "config");
			if(n == null) {
				throw new XMLNodeMissing("config");
			}
			this.configuration = (ProviderConfiguration)XMLUtils.readObject(ProviderConfiguration.class, n);
			n = XMLUtils.findChild(node, "parserInstance");
			if(n == null) {
				throw new XMLNodeMissing("parserInstance");
			}
			this.parserInstance = (ParserInstance)XMLUtils.readObject(ParserInstance.class, n);

			n = XMLUtils.findChild(node, "descriptors");
			if(n != null) {
				List<Node> lst = XMLUtils.findChildren(n, "entitydescriptor");
				this.entityDescriptors = new HashMap<EntityId, EntityDescriptor>(lst.size());
				for(Node ne : lst) {
					EntityDescriptor ed = (EntityDescriptor)XMLUtils.readObject(EntityDescriptorImpl.class, ne);
					this.entityDescriptors.put(ed.getId(), ed);
				}
			}
		} catch (XMLException e) {
			throw e;
		} catch (Exception e) {
			throw new XMLException(e);
		}
	}

	/**
	 * @see com.ixora.rms.repository.AuthoredArtefact#getAuthor()
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * @see com.ixora.rms.repository.AuthoredArtefact#setAuthor(java.lang.String)
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * @see com.ixora.rms.repository.AuthoredArtefact#isSystem()
	 */
	public boolean isSystem() {
		return Utils.isEmptyString(author) || SYSTEM.equalsIgnoreCase(author);
	}
}
