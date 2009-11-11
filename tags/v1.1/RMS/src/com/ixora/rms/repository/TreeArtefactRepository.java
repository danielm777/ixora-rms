/*
 * Created on 07-Aug-2004
 */
package com.ixora.rms.repository;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ixora.common.SafeOverwrite;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLExternalizable;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLAttributeMissing;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.common.xml.exception.XMLNodeMissing;
import com.ixora.rms.EntityId;
import com.ixora.rms.repository.exception.FailedToSaveRepository;

/**
 * This class manages persistent artifacts
 * for hosts, agents and entities.<br>
 * NOTE: not synchronized
 * @author Daniel Moraru
 */
public final class TreeArtefactRepository {
	/** Logger */
	private static final AppLogger logger =
		AppLoggerFactory.getLogger(TreeArtefactRepository.class);
	/** Name of the repository file for entity resources */
	private String entityFile;
	/** Name of the repository file for agent resources */
	private String agentFile;
	/** Name of the repository file for global resources */
	private String globalFile;
	/** Repository file extension */
	private String fileExtension;
	/** Resource factory */
	private TreeArtefactFactory artefactFactory;
	/** Base folder */
	private String baseFolder;

	/**
	 * Agent queries map.
	 * Key: agentId
	 * Value: AgentData
	 */
	private Map<String, AgentData> agents;
	/**
	 * Host level data.
	 * Key: host
	 * Value: HostData
	 */
	private Map<String, HostData> hosts;
	/**
	 * Global data.
	 */
	private GlobalData global;

	/**
	 * Global resources.
	 */
	private static final class GlobalData {
		/** Global resources */
		XMLExternalizable globalResources;
		/**
		 * Whether or not an attempt has already been made to
		 * load the global resources.
		 */
		boolean globalLoaded;
		/**
		 * Whether or not the global resources have changed.
		 */
		boolean dirty;
	}

	/**
	 * Host level repository.
	 */
	private static final class HostData {
		/** Agent queries */
		XMLExternalizable hostResources;
		/**
		 * Whether or not an attempt has already been made to
		 * load the resources for this host.
		 */
		boolean hostLoaded;
		/**
		 * Whether or not the resources for this host have changed.
		 */
		boolean dirty;
	}

	/**
	 * Agent and entity level resources.
	 */
	private static final class AgentData {
		/** Resources folder */
		File resourceFolder;
		/** Key: EntityId, Value: XMLExternalizable */
		Map<EntityId, XMLExternalizable> entityResources;
		/** Agent resources */
		XMLExternalizable agentResources;
		/**
		 * Whether or not an attempt has already been made to
		 * load the entity resources for this agent.
		 */
		boolean entityLoaded;
		/**
		 * Whether or not an attempt has already been made to
		 * load the resources for this agent.
		 */
		boolean agentLoaded;
		/** Dirty flag for agent resources */
		boolean agentDirty;
		/** Dirty flag for entity resources */
		boolean entityDirty;

		AgentData(File res) {
			resourceFolder = res;
		}
	}

	/**
	 * Constructor.
	 * @param globalFile the name of the repository file for global resources
	 * @param agentFile the name of the repository file for agent resources
	 * @param entityFile the name of the repository file for entity resources
	 * @param af artefact factory able to create emtpy resource objects
	 */
	TreeArtefactRepository(
	        String globalFile,
	        String agentFile,
	        String entityFile,
	        String fileExtension,
	        TreeArtefactFactory af) {
	    this.entityFile = entityFile;
	    this.agentFile = agentFile;
	    this.globalFile = globalFile;
	    this.fileExtension = fileExtension;
	    this.artefactFactory = af;

		this.agents = new HashMap<String, AgentData>();
		this.hosts = new HashMap<String, HostData>();
		this.global = new GlobalData();
		this.baseFolder = Utils.getPath("/config/repository");
	}

	/**
	 * Loads entity resources for the given agent if necessary.
	 * @param ad
	 */
	private void loadEntityResourcesForAgent(AgentData ad) {
		if(!ad.entityLoaded) {
			// try to load it, this is the first time resources
			// have been requested for this agent
			try {
				ad.entityResources = loadEntityResources(ad);
			} catch (XMLException e) {
				logger.error(e);
			} catch (FileNotFoundException e) {
				// ignore, the entity has no metadata defined
			} finally {
				// don't retry to load it next time
				ad.entityLoaded = true;
			}
		}
	}

	/**
	 * @return the resources for the given entity
	 */
	public XMLExternalizable getEntityResources(String agentId,
	        EntityId entityId) {
		AgentData ad = getAgentData(agentId);
		loadEntityResourcesForAgent(ad);
		if(ad.entityResources != null) {
			XMLExternalizable ret = ad.entityResources.get(entityId);
			if(ret != null) {
				try {
					return (XMLExternalizable)Utils.deepClone(ret);
				} catch(Exception e) {
					logger.error(e);
				}
			}
		}
		return null;
	}

	/**
	 * @return all entity resources defined for the given agent
	 */
	@SuppressWarnings("unchecked")
	public Map<EntityId, XMLExternalizable> getEntityResources(String agentId) {
		AgentData ad = getAgentData(agentId);
		loadEntityResourcesForAgent(ad);
		if(ad.entityResources != null) {
			Map<EntityId, XMLExternalizable> ret = ad.entityResources;
			if(!Utils.isEmptyMap(ret)) {
				try {
					return (Map<EntityId, XMLExternalizable>)Utils.deepClone(new HashMap(ret));
				} catch(Exception e) {
					logger.error(e);
				}
			}
		}
		return null;
	}

	/**
	 * @see com.ixora.rms.services.QueryRepositoryService#getGlobalQueries()
	 */
	public XMLExternalizable getGlobalResources() {
		if(!global.globalLoaded) {
			// try to load it, this is the first time global
		    // resources have been requested
			try {
				global.globalResources = loadGlobalResources();
			} catch (XMLException e) {
				logger.error(e);
			} catch (FileNotFoundException e) {
				// ignore, the entity has no metadata defined
			} finally {
				// don't retry to load it next time
				global.globalLoaded = true;
			}
		}
		if(global.globalResources != null) {
			try {
				return (XMLExternalizable)Utils.deepClone(global.globalResources);
			} catch(Exception e) {
				logger.error(e);
			}
		}
		return null;
	}

	/**
	 * @return the resources for the given agent
	 */
	public XMLExternalizable getAgentResources(String agentId) {
		AgentData ad = getAgentData(agentId);
		if(!ad.agentLoaded) {
			// try to load it, this is the first time entity resources
			// have been requested for this agent
			try {
				ad.agentResources = loadAgentResources(ad);
			} catch (XMLException e) {
				logger.error(e);
			} catch (FileNotFoundException e) {
				// ignore, the entity has no metadata defined
			} finally {
				// don't retry to load it next time
				ad.agentLoaded = true;
			}
		}
		if(ad.agentResources != null) {
			try {
				return (XMLExternalizable)Utils.deepClone(ad.agentResources);
			} catch(Exception e) {
				logger.error(e);
			}
		}
		return null;
	}

	/**
	 * @return the resources for the given host
	 */
	public XMLExternalizable getHostResources(String host) {
		HostData hd = getHostData(host);
		if(!hd.hostLoaded) {
			// try to load it, this is the first time resources
			// have been requested for this host
			try {
				hd.hostResources = loadHostResources(host);
			} catch (XMLException e) {
				logger.error(e);
			} catch (FileNotFoundException e) {
				// ignore, the entity has no metadata defined
			} finally {
				// don't retry to load it next time
				hd.hostLoaded = true;
			}
		}
		if(hd.hostResources != null) {
			try {
				return (XMLExternalizable)Utils.deepClone(hd.hostResources);
			} catch(Exception e) {
				logger.error(e);
			}
		}
		return null;
	}

	/**
	 * Sets the resources for the given entity.
	 * @param agentId
	 * @param entityId
	 * @param resources
	 */
	public void setEntityResources(String agentId,
	        EntityId entityId, XMLExternalizable resources) {
	    if(agentId == null) {
	        throw new IllegalArgumentException("null agent id");
	    }
	    if(entityId == null) {
	        throw new IllegalArgumentException("null entity id");
	    }
	    if(resources == null) {
	        throw new IllegalArgumentException("null resources");
	    }
		AgentData ad = getAgentData(agentId);
		if(ad.entityResources == null) {
			ad.entityResources = new HashMap<EntityId, XMLExternalizable>();
		}
		ad.entityResources.put(entityId, resources);
		ad.entityLoaded = true;
		ad.entityDirty = true;
	}

	/**
	 * Sets the resources for the given agent.
	 * @param agentId
	 * @param resources
	 */
	public void setAgentResources(String agentId,
	        XMLExternalizable resources) {
	    if(agentId == null) {
	        throw new IllegalArgumentException("null agent id");
	    }
	    if(resources == null) {
	        throw new IllegalArgumentException("null resources");
	    }
		AgentData ad = getAgentData(agentId);
		ad.agentResources = resources;
		ad.agentLoaded = true;
		ad.agentDirty = true;
	}

	/**
	 * Sets the global resources.
	 * @param resources
	 */
	public void setGlobalResources(XMLExternalizable resources) {
	    if(resources == null) {
	        throw new IllegalArgumentException("null resources");
	    }
		global.globalResources = resources;
		global.globalLoaded = true;
		global.dirty = true;
	}

	/**
	 * Sets the host resources.
	 * @param host
	 * @param resources
	 */
	public void setHostResources(String host, XMLExternalizable resources) {
	    if(host == null) {
	        throw new IllegalArgumentException("null host");
	    }
	    if(resources == null) {
	        throw new IllegalArgumentException("null resources");
	    }
		HostData hd = getHostData(host);
		hd.hostResources = resources;
		hd.hostLoaded = true;
		hd.dirty = true;
	}

	/**
	 * Saves all changed resources.
	 * @throws FailedToSaveRepository
	 */
	public void save() throws FailedToSaveRepository {
		try {
			// global first
			if(global.dirty) {
				saveGlobalResources();
			}
			// hosts
			String host;
			HostData hd;
			for(Map.Entry<String, HostData> me : hosts.entrySet()) {
				host = me.getKey();
				hd = me.getValue();
				if(hd.dirty) {
					saveHostRepository(host);
				}
			}
			// agents and entities
			String agentId;
			AgentData ad;
			for(Map.Entry<String, AgentData> me : agents.entrySet()) {
				agentId = me.getKey();
				ad = me.getValue();
				if(ad.agentDirty) {
					saveAgentResources(agentId);
				}
				if(ad.entityDirty) {
					saveEntityResources(agentId);
				}
			}
		} catch(Exception e) {
		    logger.error(e);
			throw new FailedToSaveRepository(
					e.getMessage() != null ?
							e.getMessage() : e.toString());
		}
	}

	/**
	 * Returns the agent data for the given agent.
	 * @param agentId
	 * @return
	 */
	private AgentData getAgentData(String agentId) {
		AgentData ad = agents.get(agentId);
		if(ad == null) {
			ad = new AgentData(new File(baseFolder, agentId));
			agents.put(agentId, ad);
		}
		return ad;
	}

	/**
	 * Returns the host data for the given host.
	 * @param host
	 * @return
	 */
	private HostData getHostData(String host) {
		HostData hd = hosts.get(host);
		if(hd == null) {
			hd = new HostData();
			hosts.put(host, hd);
		}
		return hd;
	}

	/**
	 * Loads resources from file.
	 * @param f
	 * @return
	 */
	private XMLExternalizable loadResourcesFromFile(File f)
			throws XMLException, FileNotFoundException {
		if(!f.canRead() || f.length() == 0) {
			return null;
		}
		XMLExternalizable res;
		BufferedInputStream bs = null;
		try {
			bs = new BufferedInputStream(new FileInputStream(f));
			Document doc = XMLUtils.read(bs);
			res = this.artefactFactory.createArtefact();
			Node n = XMLUtils.findChild(doc, "rms");
			if(n == null) {
			    throw new XMLNodeMissing("rms");
			}
			res.fromXML(n);
		} finally {
			if(bs != null) {
				try {
					bs.close();
				} catch (IOException e1) {
				}
			}
		}
		return res;
	}

	/**
	 * @param ad
	 * @return the map key: EntityId, value: XMLExternalizable
	 */
	private Map<EntityId, XMLExternalizable> loadEntityResources(AgentData ad)
		throws XMLException,
			FileNotFoundException {
		File f = new File(ad.resourceFolder, entityFile);
		if(!f.canRead()) {
			return null;
		}
		Map<EntityId, XMLExternalizable> ret;
		BufferedInputStream bs = null;
		try {
			bs = new BufferedInputStream(new FileInputStream(f));
			Document doc = XMLUtils.read(bs);
			Node n = XMLUtils.findChild(doc.getFirstChild(), "entities");
			if(n == null) {
			    throw new XMLNodeMissing("entities");
			}
			List<Node> lst = XMLUtils.findChildren(n, "entity");
			if(lst.size() == 0) {
				return null;
			}
			ret = new HashMap<EntityId, XMLExternalizable>();
			for(Iterator<Node> iter = lst.iterator(); iter.hasNext();) {
				n = iter.next();
				Attr a = XMLUtils.findAttribute(n, "id");
				if(a == null) {
					throw new XMLAttributeMissing("id");
				}
				try {
					XMLExternalizable res = this.artefactFactory.createArtefact();
					res.fromXML(n);
					ret.put(new EntityId(a.getValue()), res);
				} catch(Exception e) {
					logger.error(e);
				}
			}
		} finally {
			if(bs != null) {
				try {
					bs.close();
				} catch (IOException e1) {
				}
			}
		}
		return ret;
	}

	/**
	 * @param ad
	 * @return XMLExternalizable
	 */
	private XMLExternalizable loadAgentResources(AgentData ad)
		throws XMLException, FileNotFoundException {
		return loadResourcesFromFile(
		       new File(ad.resourceFolder, this.agentFile));
	}

	/**
	 * @param host
	 * @return XMLExternalizable
	 */
	private XMLExternalizable loadHostResources(String host)
		throws XMLException, FileNotFoundException {
		return loadResourcesFromFile(
		       new File(baseFolder, host + "." + this.fileExtension));
	}

	/**
	 * @return XMLExternalizable
	 */
	private XMLExternalizable loadGlobalResources()
		throws XMLException, FileNotFoundException {
		return loadResourcesFromFile(
		     new File(baseFolder, this.globalFile));
	}

	/**
	 * Saves agent resources.
	 * @param agentId
	 * @throws XMLException
	 * @throws IOException
	 */
	private void saveAgentResources(String agentId) throws XMLException, IOException {
		AgentData ad = this.agents.get(agentId);
		if(ad == null) {
			return;
		}
		if(ad.agentResources == null) {
			return;
		}
		if(!ad.resourceFolder.exists()) {
			ad.resourceFolder.mkdirs();
		}
		File out = new File(ad.resourceFolder, this.agentFile);
		Document doc = XMLUtils.createEmptyDocument("rms");
		ad.agentResources.toXML(doc.getFirstChild());

		BufferedOutputStream os = null;
		SafeOverwrite so = new SafeOverwrite(out);
		try {
			so.backup();
			os = new BufferedOutputStream(new FileOutputStream(out));
			XMLUtils.write(doc, os);
			so.commit(os);
		} catch(Exception e) {
			so.rollback(os);
		}
	}

	/**
	 * Saves entities resources for the given agent.
	 * @param agentClass
	 * @throws XMLException
	 * @throws IOException
	 */
	private void saveEntityResources(String agentId)
			throws XMLException, IOException {
		AgentData ad = this.agents.get(agentId);
		if(ad == null) {
			return;
		}
		if(ad.entityResources == null) {
			return;
		}
		File out = new File(ad.resourceFolder, this.entityFile);
		Document doc = XMLUtils.createEmptyDocument("rms");
		Node parent = doc.createElement("entities");
		doc.getFirstChild().appendChild(parent);
		EntityId eid;
		XMLExternalizable artefact;
		Element en;
		for(Map.Entry<EntityId, XMLExternalizable> me : ad.entityResources.entrySet()) {
			eid = me.getKey();
			artefact = me.getValue();
			en = doc.createElement("entity");
			en.setAttribute("id", eid.toString());
			parent.appendChild(en);
			artefact.toXML(en);
		}

		BufferedOutputStream os = null;
		SafeOverwrite so = new SafeOverwrite(out);
		try {
			so.backup();
			os = new BufferedOutputStream(new FileOutputStream(out));
			XMLUtils.write(doc, os);
			so.commit(os);
		} catch(Exception e) {
			so.rollback(os);
		}
	}

	/**
	 * Saves host resources.
	 * @param host
	 * @throws XMLException
	 * @throws IOException
	 */
	private void saveHostRepository(String host) throws XMLException, IOException {
		HostData hd = this.hosts.get(host);
		if(hd == null) {
			return;
		}
		if(hd.hostResources == null) {
			return;
		}
		File out = new File(baseFolder, host + "." + this.fileExtension);
		Document doc = XMLUtils.createEmptyDocument("rms");
		hd.hostResources.toXML(doc.getFirstChild());

		BufferedOutputStream os = null;
		SafeOverwrite so = new SafeOverwrite(out);
		try {
			so.backup();
			os = new BufferedOutputStream(new FileOutputStream(out));
			XMLUtils.write(doc, os);
			so.commit(os);
		} catch(Exception e) {
			so.rollback(os);
		}
	}

	/**
	 * Saves system resources.
	 * @param host
	 * @throws XMLException
	 * @throws IOException
	 */
	private void saveGlobalResources() throws XMLException, IOException {
		if(global.globalResources == null) {
			return;
		}
		File out = new File(baseFolder, this.globalFile);
		Document doc = XMLUtils.createEmptyDocument("rms");
		global.globalResources.toXML(doc.getFirstChild());

		BufferedOutputStream os = null;
		SafeOverwrite so = new SafeOverwrite(out);
		try {
			so.backup();
			os = new BufferedOutputStream(new FileOutputStream(out));
			XMLUtils.write(doc, os);
			so.commit(os);
		} catch(Exception e) {
			so.rollback(os);
		}
	}
}
