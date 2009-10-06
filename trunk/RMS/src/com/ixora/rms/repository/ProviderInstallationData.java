/*
 * Created on 30-Dec-2004
 */
package com.ixora.rms.repository;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Node;

import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.common.xml.exception.XMLNodeMissing;

/**
 * @author Daniel Moraru
 */
public final class ProviderInstallationData implements InstallationArtefact {
	/** Provider class */
	private String clazz;
	/** Provider name */
	private String name;
	/** Provider confguration panel class */
	private String confPanelClass;
	/**
	 * The jars containing agent's code.
	 * It must be relative to the <code>application.home</code> system property. It must be not null
	 * if the agent requires it's own classloader.
	 */
	private String[] jars;
	/** Jar with the UI code */
	private String uiJar;

	/**
	 * Constructor.
	 */
	public ProviderInstallationData() {
		super();
	}

	/**
	 * @return the providerClass.
	 */
	public String getProviderClass() {
		return clazz;
	}
	/**
	 * @return the providerConfPanelClass.
	 */
	public String getProviderConfPanelClass() {
		return confPanelClass;
	}
	/**
	 * @return the providerName.
	 */
	public String getProviderName() {
		return name;
	}

	/**
	 * @see com.ixora.rms.repository.InstallationArtefact#getInstallationIdentifier()
	 */
	public String getInstallationIdentifier() {
		return getProviderName();
	}

	/**
	 * @return the jars.
	 */
	public String[] getJars() {
		return jars;
	}

	/**
	 * @return the ui jar.
	 */
	public String getUIJar() {
		return uiJar;
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
	 */
	public void toXML(Node parent) throws XMLException {
		; // no need to implement yet
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
	 */
	public void fromXML(Node node) throws XMLException {
		Node n = XMLUtils.findChild(node, "class");
		if(n == null) {
			throw new XMLNodeMissing("class");
		}
		this.clazz = XMLUtils.getText(n);
		n = XMLUtils.findChild(node, "name");
		if(n == null) {
			throw new XMLNodeMissing("name");
		}
		this.name = XMLUtils.getText(n);
		n = XMLUtils.findChild(node, "confPanelClass");
		if(n == null) {
			throw new XMLNodeMissing("confPanelClass");
		}
		this.confPanelClass = XMLUtils.getText(n);
		// optional
		n = XMLUtils.findChild(node, "jars");
		if(n != null) {
			List m = XMLUtils.findChildren(n, "jar");
			if(m.size() == 0) {
				throw new XMLNodeMissing("jar");
			}
			List<String> jlist = new LinkedList<String>();
			String j;
			for(Iterator iter = m.iterator(); iter.hasNext();) {
				 n = (Node)iter.next();
				 j = XMLUtils.getText(n);
				 if(j != null && j.length() > 0) {
				 	jlist.add(j);
				 }
			}
			int size = jlist.size();
			if(size > 0) {
				jars = jlist.toArray(new String[size]);
			}
		}
		// optional
		n = XMLUtils.findChild(node, "uiJar");
		if(n != null) {
			this.uiJar = XMLUtils.getText(n);
		}
	}
}
