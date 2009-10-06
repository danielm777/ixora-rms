/**
 * 15-Feb-2006
 */
package com.ixora.common.ui.filter;

import java.util.Date;

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
public class FilterDate implements Filter {
	private long fDateMin;
	private long fDateMax;

	/**
	 * XML constructor.
	 */
	public FilterDate() {
		super();
	}

	/**
	 * @param filter
	 */
	public FilterDate(long min, long max) {
		super();
		fDateMin = (min == 0 ? Long.MIN_VALUE : min);
		fDateMax = (max == 0 ? Long.MAX_VALUE : max);
	}

	/**
	 * @see com.ixora.common.ui.filter.Filter#accept(java.lang.Object)
	 */
	public boolean accept(Object obj) {
		long date = 0;
		if(obj instanceof Long) {
			date = ((Long)obj).longValue();
		} else if(obj instanceof Date) {
			date = ((Date)obj).getTime();
		}
		if(date == 0) {
			return true;
		}
		return date >= fDateMin && date <= fDateMax;
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
	 */
	public void toXML(Node parent) throws XMLException {
		Document doc = parent.getOwnerDocument();
		Element el = doc.createElement(XML_NODE);
		parent.appendChild(el);
		el.setAttribute("min", String.valueOf(fDateMin));
		el.setAttribute("max", String.valueOf(fDateMax));
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
	 */
	public void fromXML(Node node) throws XMLException {
		Attr a = XMLUtils.findAttribute(node, "min");
		if(a == null) {
			throw new XMLAttributeMissing("min");
		}
		fDateMin = Long.parseLong(a.getValue());
		a = XMLUtils.findAttribute(node, "max");
		if(a == null) {
			throw new XMLAttributeMissing("max");
		}
		fDateMax = Long.parseLong(a.getValue());
	}
}
