/**
 * 20-Feb-2006
 */
package com.ixora.rms.ui.dataviewboard.properties;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.ixora.rms.ResourceId;
import com.ixora.common.ui.filter.FilterEditorDialogString;
import com.ixora.rms.CounterDescriptor;
import com.ixora.rms.CounterType;
import com.ixora.rms.data.CounterValue;
import com.ixora.rms.dataengine.QueryResultData;
import com.ixora.rms.dataengine.Style;
import com.ixora.rms.dataengine.external.QueryData;
import com.ixora.rms.dataengine.external.QuerySeries;
import com.ixora.rms.ui.dataviewboard.utils.NumericValueDeltaHandler;
import com.ixora.rms.ui.dataviewboard.utils.NumericValueDeltaHandlerContext;
import com.ixora.rms.ui.dataviewboard.utils.TableBasedControlTableModel;

/**
 * @author Daniel Moraru
 */
public class PropertiesControlTableModel extends TableBasedControlTableModel implements NumericValueDeltaHandlerContext {
	private static final String[] columnNames = new String[]{
		"Property", // TODO localize
		"Value",
		""
	};
	private static final String[] columnDescriptions = new String[]{
		"Property", // TODO localize
		"Value",
		""
	};
	private static final Class[] columnFilterUIClasses = new Class[]{
		FilterEditorDialogString.class,
		FilterEditorDialogString.class,
		null // no filtering on this column
	};
	/**
	 * Data; Object can be a date or a NumericValueDeltaHandler.
	 */
	private Map<String, Object> fEntries;
	/**
	 * Cell formatter.
	 */
	private PropertiesControlFormatter fFormatter;

	/**
	 * @param formatter
	 *
	 */
	public PropertiesControlTableModel(PropertiesControlFormatter formatter) {
		super();
		fFormatter = formatter;
		fEntries = new LinkedHashMap<String, Object>();
        fFormatter.addListener(new PropertiesControlFormatter.Listener(){
			public void formattingChanged() {
				fireTableDataChanged();
			}});
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.utils.TableBasedControlTableModel#reset()
	 */
	public void reset() {
		fEntries.clear();
		fireTableDataChanged();
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.utils.TableBasedControlTableModel#inspectData(com.ixora.rms.dataengine.external.QueryData)
	 */
	public boolean inspectData(QueryData data) {
		boolean added = false;
		for(Iterator itS = data.iterator(); itS.hasNext();) {
			QuerySeries series = (QuerySeries)itS.next();
			for(Iterator iter = series.iterator(); iter.hasNext();) {
				QueryResultData qrd = (QueryResultData)iter.next();
				ResourceId id = qrd.getMatchedResourceId();
				if(id.getRepresentation() == ResourceId.COUNTER
						&& id.getCounterId().equals(CounterDescriptor.TIMESTAMP_ID)) {
					continue;
				}
				CounterValue value = qrd.getValue();
				CounterType type = qrd.getType();
				Style style = qrd.getQueryResult().getStyle();
				// update values
				// check if resource not already in and if so
				// add it
                String iname = qrd.getQueryResult().getStyle().getIName();
                Object obj = fEntries.get(iname);
				if(obj != null) {
					if(obj instanceof NumericValueDeltaHandler) {
						((NumericValueDeltaHandler)obj).setValue(value);
						continue;
					}
				} else {
					added = true;
				}
				if(type == CounterType.DATE
						|| Style.TYPE_DATE.equals(style.getType())) {
					obj = new Date((long)value.getDouble());
				} else if(type == CounterType.DOUBLE || type == CounterType.LONG) {
					obj = new NumericValueDeltaHandler(this, value);
				} else {
					obj = value.toString();
				}
				fEntries.put(iname, obj);
			}
		}
		fireTableDataChanged();
		return added;
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.utils.TableBasedControlTableModel#getColumnDescription(int)
	 */
	public String getColumnDescription(int column) {
		return columnDescriptions[column];
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	public String getColumnName(int column) {
		return columnNames[column];
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.utils.TableBasedControlTableModel#getFilterUIClasses()
	 */
	public Class[] getFilterUIClasses() {
		return columnFilterUIClasses;
	}

	/**
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return fEntries.size();
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return columnNames.length;
	}

	/**
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		Set<Map.Entry<String, Object>> set = fEntries.entrySet();
		int i = 0;
		for(Map.Entry<String, Object> entry : set) {
			if(i == rowIndex) {
				if(columnIndex == 0) {
					return entry.getKey();
				} else {
					return entry.getValue();
				}
			}
			++i;
		}
		return null;
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.utils.NumericValueDeltaHandlerContext#getDeltaHistorySize()
	 */
	public int getDeltaHistorySize() {
		return 0;
	}
}
