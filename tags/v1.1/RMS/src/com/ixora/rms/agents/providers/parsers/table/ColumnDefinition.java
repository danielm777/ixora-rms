/*
 * Created on 03-Jan-2005
 */
package com.ixora.rms.agents.providers.parsers.table;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ixora.common.xml.XMLExternalizable;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLAttributeMissing;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.rms.CounterId;
import com.ixora.rms.EntityId;

/**
 * @author Daniel Moraru
 */
public final class ColumnDefinition implements XMLExternalizable {
	private static final long serialVersionUID = -3988776310970443512L;
	private int columnIndex;
	private EntityId entityId;
	private CounterId counterId;

	/**
	 * Constructor.
	 */
	public ColumnDefinition() {
		super();
	}

	/**
	 * Constructor.
	 * @param columnIndex
	 * @param entityId
	 * @param counterId
	 */
	public ColumnDefinition(int columnIndex, EntityId entityId,
			CounterId counterId) {
		super();
		this.columnIndex = columnIndex;
		this.entityId = entityId;
		this.counterId = counterId;
	}

	/**
	 * @return the columnIndex.
	 */
	public int getColumnIndex() {
		return columnIndex;
	}

	/**
	 * @return the counterId.
	 */
	public CounterId getCounterId() {
		return counterId;
	}

	/**
	 * @return the entityId.
	 */
	public EntityId getEntityId() {
		return entityId;
	}

	/**
	 * @return
	 */
	public boolean isTextOnly() {
		return entityId == null && counterId == null;
	}

	/**
	 * @return
	 */
	public boolean isEntity() {
		return counterId == null && this.entityId != null;
	}

	/**
	 * @return
	 */
	public boolean isCounter() {
		return counterId != null;
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
	 */
	public void toXML(Node parent) throws XMLException {
		Document doc = parent.getOwnerDocument();
		Element el = doc.createElement("column");
		parent.appendChild(el);
		el.setAttribute("idx", String.valueOf(columnIndex));
		if(entityId != null) {
			el.setAttribute("eid", entityId.toString());
		}
		if(counterId != null) {
			el.setAttribute("cid", counterId.toString());
		}
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
	 */
	public void fromXML(Node node) throws XMLException {
		Attr a = XMLUtils.findAttribute(node, "idx");
		if(a == null) {
			throw new XMLAttributeMissing("idx");
		}
		this.columnIndex = Integer.parseInt(a.getNodeValue());
		a = XMLUtils.findAttribute(node, "eid");
		if(a != null) {
			entityId = new EntityId(a.getNodeValue());
		}
		a = XMLUtils.findAttribute(node, "cid");
		if(a != null) {
			counterId = new CounterId(a.getNodeValue());
		}
	}
}
