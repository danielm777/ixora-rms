/*
 * Created on 18-Nov-2004
 */
package com.ixora.rms.ui.dataviewboard.utils;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ixora.common.ui.filter.RowFilter;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.common.xml.exception.XMLNodeMissing;
import com.ixora.rms.ui.dataviewboard.DataViewControlDescriptor;

/**
 * Descriptor for a table based control.
 * @author Daniel Moraru
 */
public class TableBasedControlDescriptor extends DataViewControlDescriptor {
	private static final long serialVersionUID = 943065871398968902L;
	protected int fSortedColumnIdx;
	protected boolean fSortDirectionDesc;
	protected RowFilter fRowFilter;

	/**
	 * Constructor to support XML.
	 */
	public TableBasedControlDescriptor() {
		fSortedColumnIdx = -1;
		fSortDirectionDesc = true;
	}

	/**
	 * Constructor.
	 * @param sortedColumnIdx
	 * @param sortDirectionDesc
	 * @param rowFilter
	 */
	public TableBasedControlDescriptor(int sortedColumnIdx, boolean sortDirectionDesc,
			RowFilter rowFilter) {
		super();
		this.fSortedColumnIdx = sortedColumnIdx;
		this.fSortDirectionDesc = sortDirectionDesc;
		this.fRowFilter = rowFilter;
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
	 */
	public void toXML(Node parent) throws XMLException {
		super.toXML(parent);
		Element last = XMLUtils.getLastElement(parent);
		if(last == null) {
			throw new XMLNodeMissing("control");
		}
		Document doc = parent.getOwnerDocument();
		Element el = doc.createElement("config");
		last.appendChild(el);
		el.setAttribute("sortedColumnIdx", String.valueOf(fSortedColumnIdx));
		el.setAttribute("sortDirectionDesc", String.valueOf(fSortDirectionDesc));
		if(fRowFilter != null) {
			XMLUtils.writeObject(RowFilter.class, el, fRowFilter);
		}
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
	 */
	public void fromXML(Node node) throws XMLException {
		Node n = XMLUtils.findChild(node, "config");
		if(n != null) {
			Attr a = XMLUtils.findAttribute(n, "sortedColumnIdx");
			if(a != null) {
				fSortedColumnIdx = Integer.parseInt(a.getValue());
			}
			a = XMLUtils.findAttribute(n, "sortDirectionDesc");
			if(a != null) {
				fSortDirectionDesc = Boolean.valueOf(a.getValue()).booleanValue();
			}
			a = XMLUtils.findAttribute(n, "sortDirectionDesc");
			if(a != null) {
				fSortDirectionDesc = Boolean.valueOf(a.getValue()).booleanValue();
			}
			Node nodeRowFilter = XMLUtils.findChild(n, RowFilter.XML_NODE);
			if(nodeRowFilter != null) {
				try {
					fRowFilter = (RowFilter)XMLUtils.readObject(RowFilter.class, nodeRowFilter);
				} catch(XMLException e) {
					throw e;
				} catch(Exception e) {
					throw new XMLException(e);
				}
			}
		}
		super.fromXML(node);
	}
	/**
	 * @return the sortDirectionDesc.
	 */
	public boolean isSortDirectionDesc() {
		return fSortDirectionDesc;
	}
	/**
	 * The index of the column which is sorted, -1 if no column is sorted.
	 * @return the sortedColumnIdx.
	 */
	public int getSortedColumnIdx() {
		return fSortedColumnIdx;
	}
	/**
	 * @return the row filter.
	 */
	public RowFilter getRowFilter() {
		return fRowFilter;
	}
}
