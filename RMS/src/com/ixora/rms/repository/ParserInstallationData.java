/*
 * Created on 30-Dec-2004
 */
package com.ixora.rms.repository;

import java.util.List;

import org.w3c.dom.Node;

import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.common.xml.exception.XMLNodeMissing;

/**
 * @author Daniel Moraru
 */
public final class ParserInstallationData implements InstallationArtefact {
	/** Parser class */
	private String clazz;
	/** Parser name */
	private String name;
	/** Parser rules panel class */
	private String rulesPanelClass;
	/** Supported providers */
	private String[] providers;

	/**
	 * Constructor.
	 */
	public ParserInstallationData() {
		super();
	}

	/**
	 * @return the class of the parser.
	 */
	public String getParserClass() {
		return clazz;
	}
	/**
	 * @return the class of the rules panel.
	 */
	public String getParsingRulesPanelClass() {
		return rulesPanelClass;
	}
	/**
	 * @return the parserName.
	 */
	public String getParserName() {
		return name;
	}

	/**
	 * @see com.ixora.rms.repository.InstallationArtefact#getInstallationIdentifier()
	 */
	public String getInstallationIdentifier() {
		return getParserName();
	}

	/**
	 * @return the supported providers.
	 */
	public String[] getSupportedProviders() {
		return providers;
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
		n = XMLUtils.findChild(node, "rulesPanelClass");
		if(n == null) {
			throw new XMLNodeMissing("rulesPanelClass");
		}
		this.rulesPanelClass = XMLUtils.getText(n);
		n = XMLUtils.findChild(node, "providers");
		if(n == null) {
			throw new XMLNodeMissing("providers");
		}
		List<Node> nl = XMLUtils.findChildren(n, "provider");
		this.providers = new String[nl.size()];
		int i = 0;
		for(Node n2 : nl) {
			this.providers[i] = XMLUtils.getText(n2);
			++i;
		}
	}
}

