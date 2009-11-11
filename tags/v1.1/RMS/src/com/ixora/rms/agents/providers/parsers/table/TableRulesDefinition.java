/*
 * Created on 03-Jan-2005
 */
package com.ixora.rms.agents.providers.parsers.table;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.common.xml.exception.XMLNodeMissing;
import com.ixora.rms.CounterId;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.providers.parsers.MonitoringDataParsingRulesDefinition;

/**
 * @author Daniel Moraru
 */
public final class TableRulesDefinition implements MonitoringDataParsingRulesDefinition {
	private static final long serialVersionUID = 3506435358154264825L;
	/** Columns */
	private ColumnDefinition[] fColumns;
	/** Column separator */
	private String fColumnSeparator;
	/** Ignore lines that matching this regex */
	private String fIgnoreLines;
	/** @see accumulateVolatileEntities() */
	private boolean fAccumulateVolatileEntities;
	/**
	 * When there are more columns in the table data and the number of columns
	 * specified in <code>fColumns</code> the columns at this indexes will be discarded
	 * and the data sample used.<br>
	 * If null any such samples will be discarded.
	 */
	private Integer[] fColumnsToIgnoreWhenMoreThanExpected;
	/**
	 * If true, the single column data will be converted to a row.
	 */
	private boolean fConvertColumnToRow;

	/**
	 * Constructor.
	 */
	public TableRulesDefinition() {
		super();
		this.fColumnSeparator = " \t";
	}

	/**
	 * Constructor.
	 * @param columns
	 * @param columnSeparator
	 * @param ignoreLines
	 * @param columnsToIgnore
	 * @param accumulateVolatileEntities
	 */
	public TableRulesDefinition(ColumnDefinition[] columns,
			String columnSeparator,
			String ignoreLines,
			String columnsToIgnore,
			boolean accumulateVolatileEntities,
			boolean convertColToRow) {
		super();
		if(columns == null || columnSeparator == null) {
			throw new IllegalArgumentException("null params");
		}
        if(columnSeparator.length() != 0) {
            this.fColumnSeparator = columnSeparator;
        }
        if(!Utils.isEmptyString(columnsToIgnore)) {
        	fColumnsToIgnoreWhenMoreThanExpected = parseColumnsToIgnore(columnsToIgnore);
        }
		this.fColumns = columns;
		this.fIgnoreLines = ignoreLines;
		this.fAccumulateVolatileEntities = accumulateVolatileEntities;
		this.fConvertColumnToRow = convertColToRow;
	}

	/**
	 * @return the columns.
	 */
	public ColumnDefinition[] getColumns() {
		return fColumns;
	}

	/**
	 * @return the columnSeparator.
	 */
	public String getColumnSeparator() {
		return fColumnSeparator;
	}

	/**
	 * @see com.ixora.rms.agents.providers.parsers.MonitoringDataParsingRulesDefinition#accumulateVolatileEntities()
	 */
	public boolean accumulateVolatileEntities() {
		return fAccumulateVolatileEntities;
	}

	/**
	 * @return whether or not to convert the single data column to a row before
	 * processing it
	 */
	public boolean convertColumnToRow() {
		return fConvertColumnToRow;
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
	 */
	public void toXML(Node parent) throws XMLException {
		Document doc = parent.getOwnerDocument();
		Element el = doc.createElement("rules");
		parent.appendChild(el);
		Element el2;
		if(fColumnSeparator != null) {
			el2 = doc.createElement("columnSeparator");
			el.appendChild(el2);
			el2.appendChild(doc.createTextNode(fColumnSeparator));
		}
		el2 = doc.createElement("columns");
		el.appendChild(el2);
		for(ColumnDefinition cd : fColumns) {
			cd.toXML(el2);
		}
		if(!Utils.isEmptyString(fIgnoreLines)) {
			el2 = doc.createElement("ignoreLines");
            el.appendChild(el2);
			el2.appendChild(doc.createTextNode(fIgnoreLines));
		}
		if(!Utils.isEmptyArray(fColumnsToIgnoreWhenMoreThanExpected)) {
			el2 = doc.createElement("columnsToIgnoreWhenMoreThanExpected");
            el.appendChild(el2);
			el2.appendChild(doc.createTextNode(columnsToIgnoreToString(fColumnsToIgnoreWhenMoreThanExpected)));
		}

		el2 = doc.createElement("accumulateVolatileEntities");
		el.appendChild(el2);
		el2.appendChild(doc.createTextNode(String.valueOf(fAccumulateVolatileEntities)));
		el2 = doc.createElement("convertColumnToRow");
		el.appendChild(el2);
		el2.appendChild(doc.createTextNode(String.valueOf(fConvertColumnToRow)));
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
	 */
	public void fromXML(Node node) throws XMLException {
		Node n = XMLUtils.findChild(node, "columns");
		if(n == null) {
			throw new XMLNodeMissing("columns");
		}
		List<Node> nl = XMLUtils.findChildren(n, "column");
		if(nl.size() == 0) {
			throw new XMLNodeMissing("column");
		}
		List<ColumnDefinition> lst = new ArrayList<ColumnDefinition>(nl.size());
		for(Node n2 : nl) {
			ColumnDefinition cd = new ColumnDefinition();
			cd.fromXML(n2);
			lst.add(cd);
		}
		this.fColumns = lst.toArray(new ColumnDefinition[lst.size()]);

		n = XMLUtils.findChild(node, "columnSeparator");
		if(n != null) {
			String cs = XMLUtils.getText(n);
			if(cs != null && cs.length() > 0) {
				this.fColumnSeparator = cs;
			}
		}

		n = XMLUtils.findChild(node, "ignoreLines");
		if(n != null) {
			fIgnoreLines = XMLUtils.getText(n);
		}

		n = XMLUtils.findChild(node, "accumulateVolatileEntities");
		if(n != null) {
			String acc = XMLUtils.getText(n);
			if(!Utils.isEmptyString(acc)) {
				this.fAccumulateVolatileEntities = Utils.parseBoolean(acc);
			}
		}

		n = XMLUtils.findChild(node, "convertColumnToRow");
		if(n != null) {
			String ccr = XMLUtils.getText(n);
			if(!Utils.isEmptyString(ccr)) {
				this.fConvertColumnToRow = Utils.parseBoolean(ccr);
			}
		}

		n = XMLUtils.findChild(node, "columnsToIgnoreWhenMoreThanExpected");
		if(n != null) {
			String cols = XMLUtils.getText(n);
			if(!Utils.isEmptyString(cols)) {
				this.fColumnsToIgnoreWhenMoreThanExpected = parseColumnsToIgnore(cols);
			}
		}

	}

	/**
	 * @see com.ixora.rms.agents.providers.parsers.MonitoringDataParsingRulesDefinition#getEntities()
	 */
	public Set<EntityId> getEntities() {
		Set<EntityId> ret = new LinkedHashSet<EntityId>();
		for(ColumnDefinition cd : fColumns) {
			EntityId eid = cd.getEntityId();
			if(eid != null) {
				ret.add(eid);
			}
		}
		return ret;
	}

	/**
	 * @see com.ixora.rms.agents.providers.parsers.MonitoringDataParsingRulesDefinition#getCounters(com.ixora.rms.EntityId)
	 */
	public Set<CounterId> getCounters(EntityId e) {
		Set<CounterId> ret = new LinkedHashSet<CounterId>();
		for(ColumnDefinition cd : fColumns) {
			EntityId eid = cd.getEntityId();
			if(eid != null) {
				if(cd.getEntityId().equals(e)) {
					CounterId cid = cd.getCounterId();
					if(cid != null) {
						ret.add(cid);
					}
				}
			}
		}
		return ret;
	}

	/**
	 * @return
	 */
	public String getIgnoreLines() {
		return fIgnoreLines;
	}

	/**
	 * When there are more columns in the table data than the number of columns
	 * specified in column definitions the columns at this indexes will be discarded
	 * and the data sample used.
	 * @return null if you want to discard samples with more data columns
	 */
	public Integer[] getColumnsToIgnoreWhenMoreThanExpected() {
		return fColumnsToIgnoreWhenMoreThanExpected;
	}

	/**
	 * @param line a comma separated list of column indexes
	 * @return
	 */
	private static Integer[] parseColumnsToIgnore(String line) {
		if(Utils.isEmptyString(line)) {
			return null;
		}
		String[] idxs = line.split(",");
		if(Utils.isEmptyArray(idxs)) {
			return null;
		}
		List<Integer> lst = new LinkedList<Integer>();
		for(String idx : idxs) {
			lst.add(Integer.parseInt(idx.trim()));
		}
		if(Utils.isEmptyCollection(lst)) {
			return null;
		}
		return lst.toArray(new Integer[lst.size()]);
	}

	/**
	 * @return
	 */
	private static String columnsToIgnoreToString(Integer[] cols) {
		if(Utils.isEmptyArray(cols)) {
			return "";
		}
		StringBuffer buff = new StringBuffer();
		for(int i = 0; i < cols.length; i++) {
			buff.append(cols[i]);
			if(i < cols.length - 1) {
				buff.append(",");
			}
		}
		return buff.toString();
	}

	/**
	 * @return
	 */
	public String getColumnsToIgnoreWhenMoreThanExpectedAsString() {
		if(Utils.isEmptyArray(fColumnsToIgnoreWhenMoreThanExpected)) {
			return null;
		}
		return columnsToIgnoreToString(fColumnsToIgnoreWhenMoreThanExpected);
	}
}
