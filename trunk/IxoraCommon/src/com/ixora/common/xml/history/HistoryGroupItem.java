package com.ixora.common.xml.history;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ixora.common.xml.XMLAttributeDouble;
import com.ixora.common.xml.XMLExternalizable;
import com.ixora.common.xml.XMLTag;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;

/**
 * An item in a history group.
 * @author Daniel Moraru
 */
public class HistoryGroupItem extends XMLTag implements Comparable<HistoryGroupItem> {
	private XMLAttributeDouble fDate = new XMLAttributeDouble("date", true);
	private XMLExternalizable fItem;

	/**
	 * Default constructor to support XML.
	 */
	public HistoryGroupItem() {
		super();
	}

	/**
	 * @param date
	 * @param item
	 */
	public HistoryGroupItem(long date, XMLExternalizable item) {
		fDate.setValue(new Double(date));
		fItem = item;
	}

	/**
	 * @return
	 */
	public long getDate() {
		return fDate.getDouble().longValue();
	}

	/**
	 * @return
	 */
	public XMLExternalizable getItem() {
		return fItem;
	}

	/**
	 * @see java.lang.Comparable#compareTo(T)
	 */
	public int compareTo(HistoryGroupItem o) {
		if(fDate.getDouble().equals(o.fDate.getDouble())) {
			return 0;
		}
		return fDate.getDouble().longValue() > o.fDate.getDouble().longValue() ? 1 : -1;
	}

	/**
	 * @see com.ixora.common.xml.XMLTag#getTagName()
	 */
	public String getTagName() {
		return "historyGroupItem";
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
	 */
	public void fromXML(Node node) throws XMLException {
		super.fromXML(node);
		try {
			Element el = XMLUtils.getFirstElement(node);
			if(el != null) {
				this.fItem = XMLUtils.readObject(null, el);
			}
		} catch (XMLException e) {
			throw e;
		} catch (Exception e) {
			throw new XMLException(e);
		}
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
	 */
	public void toXML(Node parent) throws XMLException {
		super.toXML(parent);
		// append class to the fItem entry
		XMLUtils.writeObject(null, XMLUtils.getLastElement(parent), fItem);
	}
}