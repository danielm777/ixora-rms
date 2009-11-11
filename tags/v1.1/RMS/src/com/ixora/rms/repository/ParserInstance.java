/*
 * Created on 27-Dec-2004
 */

package com.ixora.rms.repository;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ixora.common.xml.XMLExternalizable;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.common.xml.exception.XMLNodeMissing;
import com.ixora.rms.providers.parsers.ParsingRulesDefinition;

/**
 * The definition of the data provider.
 * @author Daniel Moraru
 */
public final class ParserInstance implements XMLExternalizable {
	private static final long serialVersionUID = 8473411372203229608L;
	/** Parser name */
	private String parserName;
	/** Parsing rules configuration */
	private ParsingRulesDefinition rules;

	/**
	 * Constructor.
	 */
	public ParserInstance() {
		super();
	}

	/**
	 * Constructor.
	 * @param parserName
	 * @param rules
	 */
	public ParserInstance(String parserName, ParsingRulesDefinition rules) {
		super();
		if(parserName == null || rules == null) {
			throw new IllegalArgumentException("null params");
		}
		this.parserName = parserName;
		this.rules = rules;
	}

	/**
	 * @return the parser name.
	 */
	public String getParserName() {
		return parserName;
	}

	/**
	 * @return the rules.
	 */
	public ParsingRulesDefinition getRules() {
		return rules;
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
	 */
	public void toXML(Node parent) throws XMLException {
		Document doc = parent.getOwnerDocument();
		Element el = doc.createElement("parserInstance");
		parent.appendChild(el);
		Element el2 = doc.createElement("parser");
		el.appendChild(el2);
		el2.appendChild(doc.createTextNode(parserName));
		XMLUtils.writeObject(null, el, rules);
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
	 */
	public void fromXML(Node node) throws XMLException {
		Node n = XMLUtils.findChild(node, "parser");
		if(n == null) {
			throw new XMLNodeMissing("parser");
		}
		this.parserName = XMLUtils.getText(n);
		n = XMLUtils.findChild(node, "rules");
		if(n == null) {
			throw new XMLNodeMissing("rules");
		}
		try {
			this.rules = (ParsingRulesDefinition)XMLUtils.readObject(null, n);
		} catch (XMLException e) {
			throw e;
		} catch (Exception e) {
			throw new XMLException(e);
		}
	}
}
