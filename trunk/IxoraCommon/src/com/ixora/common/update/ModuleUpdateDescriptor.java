package com.ixora.common.update;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Attr;
import org.w3c.dom.Node;

import com.ixora.common.ComponentVersion;
import com.ixora.common.xml.XMLExternalizable;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.common.xml.exception.XMLNodeMissing;

/*
 * Created on Feb 12, 2004
 */
/**
 * @author Daniel Moraru
 */
public final class ModuleUpdateDescriptor implements Comparable<ModuleUpdateDescriptor>,
		XMLExternalizable {
	private static final long serialVersionUID = 1041151205600523081L;
	private String moduleName;
	private UpdateId updateId;
	private String updateDescription;
	private List<ComponentVersion> forModuleVersions;
	private Module newModule;
	private UpdatePartDescriptor[] components;
	private boolean installed;

	/**
	 * Constructor.
	 * @param moduleName the name of the module for which this update
	 * applies
	 */
	public ModuleUpdateDescriptor(String moduleName) {
		super();
		this.moduleName = moduleName;
	}

	/**
	 * @return the components.
	 * @param os the operation system as returned by
	 * <code>System.getProperty("os.name")</code>.
	 */
	public UpdatePartDescriptor[] getComponents(String os) {
		if(components == null || components.length == 0) {
			return null;
		}
		List<UpdatePartDescriptor> ret = new LinkedList<UpdatePartDescriptor>();
		UpdatePartDescriptor part;
		for(int i = 0; i < components.length; i++) {
			part = components[i];
			if(part.appliesToOs(os)) {
				ret.add(part);
			}
		}
		return (UpdatePartDescriptor[])ret.toArray(
			new UpdatePartDescriptor[ret.size()]);
	}

	/**
	 * @return the updateDescription.
	 */
	public String getUpdateDescription() {
		return updateDescription;
	}

	/**
	 * @return the updateId.
	 */
	public UpdateId getUpdateId() {
		return updateId;
	}

	/**
	 * @return the new module.
	 */
	public Module getNewModule() {
		return newModule;
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(ModuleUpdateDescriptor md) {
		return newModule.getVersion().compareTo(md.newModule.getVersion());
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return this.newModule.toString();
	}

	/**
	 * @return the installed.
	 */
	public boolean isInstalled() {
		return installed;
	}

	/**
	 * @param installed the installed to set.
	 */
	public void setInstalled(boolean installed) {
		this.installed = installed;
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
	 */
	public void fromXML(Node node) throws XMLException {
		Attr a = XMLUtils.findAttribute(node, "id");
		if(a == null) {
			throw new XMLNodeMissing("id");
		}
		updateId = new UpdateId(a.getValue());
		List<Node> lst = XMLUtils.findChildren(node, "forVersion");
		if(lst.size() == 0) {
			throw new XMLNodeMissing("forVersion");
		}
		Node n;
		this.forModuleVersions = new ArrayList<ComponentVersion>(lst.size());
		ComponentVersion cv;
		for(Iterator<Node> iter = lst.iterator(); iter.hasNext();) {
			n = iter.next();
			cv = new ComponentVersion(n.getFirstChild().getNodeValue());
			this.forModuleVersions.add(cv);
		}

		n = XMLUtils.findChild(node, "newVersion");
		if(n == null) {
			throw new XMLNodeMissing("newVersion");
		}
		this.newModule = new Module(
				this.moduleName,
				new ComponentVersion(n.getFirstChild().getNodeValue()));
		n = XMLUtils.findChild(node, "description");
		if(n != null) {
			this.updateDescription = n.getFirstChild().getNodeValue();
		}
		n = XMLUtils.findChild(node, "components");
		if(n == null) {
			throw new XMLNodeMissing("components");
		}
		lst = XMLUtils.findChildren(n, "component");
		if(lst.size() == 0) {
			return;
		}
		this.components = new UpdatePartDescriptor[lst.size()];
		int i = 0;
		for(Iterator<Node> iter = lst.iterator(); iter.hasNext(); ++i) {
			n = iter.next();
			UpdatePartDescriptor ucd = new UpdatePartDescriptor();
			ucd.fromXML(n);
			this.components[i] = ucd;
		}
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
	 */
	public void toXML(Node parent) throws XMLException {
		; // no needed
	}

// not visible outside this package

	/**
	 * @param m
	 * @return whether or not this update applies
	 * to the given module
	 */
	boolean isForModule(Module m) {
		if(m.getName().equals(this.newModule.getName())
				&& this.forModuleVersions.contains(m.getVersion())){
			return true;
		}
		return false;
	}

	/**
	 * @return the module versions for which this update applies.
	 */
	ComponentVersion[] getForModuleVersions() {
		return (ComponentVersion[])forModuleVersions.toArray(
				new ComponentVersion[forModuleVersions.size()]);
	}

	/**
	 * Sets the translated version of the description of this update.
	 * @param updateDescription
	 */
	void setUpdateDescription(String updateDescription) {
		this.updateDescription = updateDescription;
	}
}
