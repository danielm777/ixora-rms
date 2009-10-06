/**
 * 15-Feb-2006
 */
package com.ixora.common.ui.filter;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLAttributeMissing;
import com.ixora.common.xml.exception.XMLException;


/**
 * @author Daniel Moraru
 */
public class FilterString implements Filter {
	protected String fFilter;
	protected boolean fNegative;

	/**
	 * XML constructor.
	 */
	public FilterString() {
		super();
	}

	/**
	 * @param filter
	 * @param negative
	 */
	public FilterString(String filter, boolean negative) {
		super();
		if(filter == null) {
			throw new IllegalArgumentException("Null filter");
		}
		fFilter = filter;
		fNegative = negative;
	}

	/**
	 * @see com.ixora.common.ui.filter.Filter#accept(java.lang.Object)
	 */
	public boolean accept(Object obj) {
		boolean ret = String.valueOf(obj).contains(fFilter);
		if(!fNegative) {
			return ret;
		}
		return !ret;
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
	 */
	public void toXML(Node parent) throws XMLException {
		Document doc = parent.getOwnerDocument();
		Element el = doc.createElement(XML_NODE);
		parent.appendChild(el);
		el.setAttribute("match", String.valueOf(fFilter));
		if(fNegative) {
			el.setAttribute("negative", String.valueOf(fNegative));
		}
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
	 */
	public void fromXML(Node node) throws XMLException {
		Attr a = XMLUtils.findAttribute(node, "match");
		if(a == null) {
			throw new XMLAttributeMissing("match");
		}
		fFilter = a.getValue();
		a = XMLUtils.findAttribute(node, "negative");
		if(a != null) {
			fNegative = Boolean.parseBoolean(a.getValue());
		}
	}
}
