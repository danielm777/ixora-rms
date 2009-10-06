/*
 * Created on 14-Nov-2004
 */
package com.ixora.rms.ui.dataviewboard;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ixora.rms.ResourceId;
import com.ixora.common.xml.XMLExternalizable;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLAttributeMissing;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.common.xml.exception.XMLNodeMissing;
import com.ixora.rms.repository.DataView;
import com.ixora.rms.repository.DataViewId;

/**
 * Descriptor used by data controls to store and restore their state.
 * @author Daniel Moraru
 */
public abstract class DataViewControlDescriptor implements XMLExternalizable {
	/** Not null if the control displays a data view from repository */
	protected DataViewId dataViewId;
	/** Not null if the control displays a on-the-fly defined view */
	protected DataView dataView;
	/** If <code>dataView</code> is not null this must be not null as well */
	protected ResourceId dataViewContext;

	/**
	 * Constructor.
	 */
	protected DataViewControlDescriptor() {
		super();
	}
	/**
	 * @return the dataViewId.
	 */
	public DataViewId getDataViewId() {
		return dataViewId;
	}
	/**
	 * @return the dataViewInfo.
	 */
	public DataView getDataView() {
		return dataView;
	}
	/**
	 * @return the dataViewContext.
	 */
	public ResourceId getDataViewContext() {
		return dataViewContext;
	}
	/**
	 * @param dataViewId the dataViewId to set.
	 */
	public void setDataViewId(DataViewId dataViewId) {
		this.dataViewId = dataViewId;
	}
	/**
	 * @param context the context of the hosted data view
	 * @param dataView the dataView to set.
	 */
	public void setDataView(ResourceId context, DataView dataView) {
		this.dataView = dataView;
		this.dataViewContext = context;
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
	 */
	public void fromXML(Node node) throws XMLException {
		Node n = XMLUtils.findChild(node, "view");
		if(n == null) {
			throw new XMLNodeMissing("view");
		}
		Attr a = XMLUtils.findAttribute(n, "id");
		if(a != null) {
			dataViewId = new DataViewId(a.getValue());
		} else {
			try {
				dataView = (DataView)XMLUtils.readObject(null, n);
			} catch(Exception e) {
				throw new XMLException(e);
			}
			// now read the context
			n = XMLUtils.findChild(node, "context");
			if(n == null) {
				throw new XMLNodeMissing("context");
			}
			a = XMLUtils.findAttribute(n, "id");
			if(a == null) {
				throw new XMLAttributeMissing("id");
			}
			String cid = a.getValue();
			if(cid == null || cid.trim().length() == 0) {
				this.dataViewContext = null;
			} else {
				this.dataViewContext = new ResourceId(cid.trim());
			}
			return;
		}
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
	 */
	public void toXML(Node parent) throws XMLException {
		Document doc = parent.getOwnerDocument();
		Element el = doc.createElement("control");
		parent.appendChild(el);
		if(dataViewId != null) {
			Element el2 = doc.createElement("view");
			el2.setAttribute("id", dataViewId.toString());
			el.appendChild(el2);
			return;
		}
		if(dataView != null) {
			XMLUtils.writeObject(null, el, dataView);
			Element el2 = doc.createElement("context");
			el.appendChild(el2);
			if(this.dataViewContext == null) {
				el2.setAttribute("id", "");
			} else {
				el2.setAttribute("id", this.dataViewContext.toString());
			}
			return;
		}
	}
}
