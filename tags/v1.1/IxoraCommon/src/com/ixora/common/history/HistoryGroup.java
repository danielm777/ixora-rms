/**
 * 01-Feb-2006
 */
package com.ixora.common.history;

import java.util.Collections;
import java.util.List;

import com.ixora.common.xml.XMLAttributeString;
import com.ixora.common.xml.XMLExternalizable;
import com.ixora.common.xml.XMLSameTagList;
import com.ixora.common.xml.XMLTag;
import com.ixora.common.xml.XMLTagList;

/**
 * @author Daniel Moraru
 */
public final class HistoryGroup extends XMLTag {
	private static final long serialVersionUID = 1529819776972007175L;
	private static final int HISTORY_SIZE = 3;
	private XMLAttributeString fGroupId = new XMLAttributeString("id", true);
	private XMLTagList<HistoryGroupItem> fItems = new XMLSameTagList<HistoryGroupItem>(HistoryGroupItem.class);

	/**
	 * Default constructor to support XML.
	 */
	public HistoryGroup() {
		super();
	}

	/**
	 * @param id
	 */
	public HistoryGroup(String id) {
		fGroupId.setValue(id);
	}

	/**
	 * @return all items for this history
	 */
	public List<HistoryGroupItem> getItems() {
		return Collections.unmodifiableList(fItems);
	}

	/**
	 * @see com.ixora.common.xml.XMLTag#getTagName()
	 */
	public String getTagName() {
		return "historyGroup";
	}

// package
	/**
	 * @param props
	 */
	void addItem(XMLExternalizable props) {
		if(fItems.size() >= HISTORY_SIZE) {
			fItems.removeLast();
		}
		fItems.addFirst(new HistoryGroupItem(System.currentTimeMillis(), props));
	}
}
