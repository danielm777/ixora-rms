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
public class FilterNumber implements Filter {
	private static final long serialVersionUID = 464995051966379284L;
	private Number fMin;
	private Number fMax;

	/**
	 * XML constructor.
	 */
	public FilterNumber() {
		super();
	}

	/**
	 * @param filter
	 */
	public FilterNumber(Number min, Number max) {
		super();
		fMin = (min == null ? new Long(Long.MIN_VALUE) : min);
		fMax = (max == null ? new Long(Long.MAX_VALUE) : max);
	}

	/**
	 * @see com.ixora.common.ui.filter.Filter#accept(java.lang.Object)
	 */
	public boolean accept(Object obj) {
		if(obj == null) {
			return false;
		}
		if(!(obj instanceof Number)) {
			return true;
		}
		Number num = (Number)obj;
		return num.doubleValue() >= fMin.doubleValue()
				&& num.doubleValue() <= fMax.doubleValue();
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
	 */
	public void toXML(Node parent) throws XMLException {
		Document doc = parent.getOwnerDocument();
		Element el = doc.createElement(XML_NODE);
		parent.appendChild(el);
		el.setAttribute("min", String.valueOf(fMin));
		el.setAttribute("max", String.valueOf(fMax));
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
	 */
	public void fromXML(Node node) throws XMLException {
		Attr a = XMLUtils.findAttribute(node, "min");
		if(a == null) {
			throw new XMLAttributeMissing("min");
		}
		fMin = Double.parseDouble(a.getValue());
		a = XMLUtils.findAttribute(node, "max");
		if(a == null) {
			throw new XMLAttributeMissing("max");
		}
		fMax = Double.parseDouble(a.getValue());
	}
}
