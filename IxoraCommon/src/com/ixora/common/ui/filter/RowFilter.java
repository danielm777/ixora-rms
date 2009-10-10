package com.ixora.common.ui.filter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLAttributeMissing;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.common.xml.exception.XMLNodeMissing;

/**
 * Filter for the set of filtered columns.
 * @author Daniel Moraru
 */
public class RowFilter implements Filter {
	private static final long serialVersionUID = -4212574883093757545L;
	/** The xml node name for this class */
	public static final String XML_NODE = "rowFilter";
	/** Filters keyed by column index */
	private Map<Integer, Filter> fFilters;

	/**
	 * XML constructor.
	 */
	public RowFilter() {
		fFilters = new LinkedHashMap<Integer, Filter>();
	}

	/**
	 * @param col
	 * @param filter
	 */
	public RowFilter(int col, Filter filter) {
		this();
		add(col, filter);
	}

	/**
	 * @param objs
	 * @return
	 */
	public boolean accept(Object obj) {
		Object[] objs = (Object[])obj;
		int i = 0;
		for(Filter filter : fFilters.values()) {
			if(!filter.accept(objs[i])) {
				return false;
			}
			++i;
		}
		return true;
	}

	/**
	 * Adds a filter on the column with index <code>col</code>.
	 * @param col
	 * @param filter
	 */
	public void add(int col, Filter filter) {
		fFilters.put(col, filter);
	}

	/**
	 * Removes the filter on column <code>col</code>.
	 * @param col
	 */
	public Filter remove(int col) {
		return fFilters.remove(col);
	}

	/**
	 * @return the indices of all filtered columns
	 */
	public Set<Integer> getFilteredColumns() {
		return fFilters.keySet();
	}

	/**
	 * @param col
	 */
	public Filter getFilter(int col) {
		return fFilters.get(col);
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
	 */
	public void toXML(Node parent) throws XMLException {
		Document doc = parent.getOwnerDocument();
		Element el = doc.createElement("rowFilter");
		parent.appendChild(el);
		for(Map.Entry<Integer, Filter> entry : fFilters.entrySet()) {
			Element col = doc.createElement("column");
			el.appendChild(col);
			col.setAttribute("idx", entry.getKey().toString());
			XMLUtils.writeObject(null, col, entry.getValue());
		}
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
	 */
	public void fromXML(Node node) throws XMLException {
		List<Node> columnNodes = XMLUtils.findChildren(node, "column");
		if(!Utils.isEmptyCollection(columnNodes)) {
			for(Node col : columnNodes) {
				Attr a = XMLUtils.findAttribute(col, "idx");
				if(a == null) {
					throw new XMLAttributeMissing("idx");
				}
				int idx = Integer.parseInt(a.getValue());
				Node filterNode = XMLUtils.findChild(col, Filter.XML_NODE);
				if(filterNode == null) {
					throw new XMLNodeMissing(Filter.XML_NODE);
				}
				try {
					Filter filter = (Filter)XMLUtils.readObject(null, filterNode);
					add(idx, filter);
				} catch(XMLException e) {
					throw e;
				} catch(Exception e) {
					throw new XMLException(e);
				}
			}
		}
	}
}