/**
 * 15-Mar-2006
 */
package com.ixora.rms.agents.url;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLExternalizable;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLAttributeMissing;
import com.ixora.common.xml.exception.XMLException;

/**
 * @author Daniel Moraru
 */
public class RequestParameters implements XMLExternalizable {
	public final static class NameValue implements Serializable {
		private String fName;
		private String fValue;
		public NameValue(String name, String value) {
			fName = name;
			fValue = value;
		}
		public String getName() {return fName;}
		public String getValue() {return fValue;}
	}
	private List<NameValue> fParams;

	/**
	 *
	 */
	public RequestParameters() {
		super();
		this.fParams = new LinkedList<NameValue>();
	}

	/**
	 * @param nv
	 */
	public RequestParameters(List<NameValue> nv) {
		this();
		fParams = nv;
	}

	/**
	 * @return
	 */
	public List<NameValue> getParameters() {
		return fParams;
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
	 */
	public void toXML(Node parent) throws XMLException {
		Document doc = parent.getOwnerDocument();
		Element el = doc.createElement("params");
		parent.appendChild(el);
		for(NameValue nv : fParams) {
			Element el1 = doc.createElement("param");
			el.appendChild(el1);
			el1.setAttribute("name", nv.getName());
			el1.setAttribute("value", nv.getValue());
		}
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
	 */
	public void fromXML(Node node) throws XMLException {
		List<Node> nodes = XMLUtils.findChildren(node, "param");
		if(!Utils.isEmptyCollection(nodes)) {
			for(Node n : nodes) {
				Attr a = XMLUtils.findAttribute(n, "name");
				if(a == null) {
					throw new XMLAttributeMissing("name");
				}
				String name = a.getValue();
				a = XMLUtils.findAttribute(n, "value");
				if(a == null) {
					throw new XMLAttributeMissing("value");
				}
				String value = a.getValue();
				fParams.add(new NameValue(name, value));
			}
		}
	}
}
