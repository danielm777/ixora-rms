/**
 * 10-Feb-2006
 */
package com.ixora.rms.agents.logfile.definitions;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ixora.common.xml.XMLExternalizable;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.common.xml.exception.XMLNodeMissing;
import com.ixora.rms.agents.logfile.LogParserDefinition;

/**
 * @author Daniel Moraru
 */
public class StoredLogParserDef implements XMLExternalizable {
	private static final long serialVersionUID = -286787102077565312L;
	private String fName;
	private boolean fCustom;
	private LogParserDefinition fParserDefinition;

	/**
	 *
	 */
	public StoredLogParserDef() {
		super();
	}

	/**
	 * @param name
	 * @param custom
	 * @param pd
	 */
	public StoredLogParserDef(String name, boolean custom, LogParserDefinition pd) {
		super();
		fName = name;
		fCustom= custom;
		fParserDefinition = pd;
	}

	/**
	 * @return
	 */
	public String getName() {
		return fName;
	}

	/**
	 * @return
	 */
	public boolean isCustom() {
		return fCustom;
	}

	/**
	 * @return
	 */
	public LogParserDefinition getLogParserDefinition() {
		return fParserDefinition;
	}

	/**
	 * @see com.ixora.common.xml.XMLTag#getTagName()
	 */
	public String getTagName() {
		return "logParser";
	}

	/**
	 * @see com.ixora.common.xml.XMLTag#fromXML(org.w3c.dom.Node)
	 */
	public void fromXML(Node node) throws XMLException {
		Attr a = XMLUtils.findAttribute(node, "custom");
		if(a != null) {
			fCustom = Boolean.parseBoolean(a.getValue());
		}
		a = XMLUtils.findAttribute(node, "name");
		if(a == null) {
			throw new XMLNodeMissing("name");
		}
		fName = a.getValue();
		Node n = XMLUtils.findChild(node, LogParserDefinition.XML_NODE);
		if(n == null) {
			throw new XMLNodeMissing(LogParserDefinition.XML_NODE);
		}
		fParserDefinition = new LogParserDefinition();
		fParserDefinition.fromXML(n);
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
	 */
	public void toXML(Node parent) throws XMLException {
		Document doc = parent.getOwnerDocument();
		Element el = doc.createElement("logParser");
		parent.appendChild(el);
		el.setAttribute("custom", String.valueOf(fCustom));
		el.setAttribute("name", fName);
		fParserDefinition.toXML(el);
	}
}
