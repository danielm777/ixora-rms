package com.ixora.common.ui.filter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import com.ixora.common.ui.UIExceptionMgr;

/**
 * Filtered table model. Used as an adaptor to an existing table model.
 * @author Daniel Moraru
 */
public class FilteredTableModel extends AbstractTableModel {

	/**
	 * Filter event.
	 */
	public static class FilterEvent {
		/** Event types */
		public enum Type {ADDED, REMOVED, REMOVED_ALL, ROW_FILTER_REPLACED};
		/** Filter affected */
		public Filter fFilter;
		/** Column affected */
		public int fColumn;
		/** Event type */
		public Type fType;

		public FilterEvent(Type type, Filter filter, int col) {
			fType = type;
			fFilter = filter;
			fColumn = col;
		}
		public FilterEvent(Type type) {
			fType = type;
			fColumn = -1;
		}
	}

	/**
	 * Listener.
	 */
	public interface Listener {
		/**
		 * Inoked when filters are added or removed.
		 * @param event
		 */
		void filterEvent(FilterEvent event);
		/**
		 * Invoked when this model is ready to start filtering; this is done when
		 * the filter UI classes are set; i.e when the adapted table knows the type
		 * of objects in each column.
		 */
		void readyToFilter();
	}

	private AbstractTableModel fModel;
	private Map<Integer, Integer> fIndexMap;
	private RowFilter fRowFilter;
	private Class[] fFilterUIClasses;
	private List<Listener> fListeners;

	/**
	 * @param model adapted model
	 * @param filterUIClasses the classes used for displaying filters
	 * for each of the columns in this table; there must be one entry for
	 * every column in the <code>model</code>; the null entries will mark
	 * the corresponding column unfilterable.
	 */
	public FilteredTableModel(AbstractTableModel model, Class[] filterUIClasses) {
		super();
		this.fListeners = new LinkedList<Listener>();
		this.fModel = model;
		setFilterUIClasses(filterUIClasses);
		fModel.addTableModelListener(new TableModelListener(){
			public void tableChanged(TableModelEvent e) {
				if(isFilterOn()) {
					rebuildIndexMap();
				}
				fireTableChanged(e);
			}
		});
	}

	/**
	 * @param listener
	 */
	public void addListener(Listener listener) {
		if(listener != null && !fListeners.contains(listener)) {
			fListeners.add(listener);
		}
	}

	/**
	 * @param listener
	 */
	public void removeListener(Listener listener) {
		fListeners.remove(listener);
	}

	/**
	 * @param col
	 * @param filter
	 */
	public void addFilter(int col, Filter filter) {
		if(fRowFilter == null) {
			fRowFilter = new RowFilter(col, filter);
		} else {
			fRowFilter.add(col, filter);
		}
		rebuildIndexMap();
		fireTableDataChanged();
		fireFilterEvent(new FilterEvent(FilterEvent.Type.ADDED, filter, col));
	}

	/**
	 * @param rowFilter
	 */
	public void setRowFilter(RowFilter rowFilter) {
		fRowFilter = rowFilter;
		rebuildIndexMap();
		fireTableDataChanged();
		fireFilterEvent(new FilterEvent(FilterEvent.Type.ROW_FILTER_REPLACED));
	}

	/**
	 * @param event
	 */
	private void fireFilterEvent(FilterEvent event) {
		for(Listener listener : fListeners) {
			try {
				listener.filterEvent(event);
			} catch(Exception e) {
				UIExceptionMgr.userException(e);
			}
		}
	}

	/**
	 * @param event
	 */
	private void fireReadyToFilter() {
		for(Listener listener : fListeners) {
			try {
				listener.readyToFilter();
			} catch(Exception e) {
				UIExceptionMgr.userException(e);
			}
		}
	}

	/**
	 * @param col
	 * @return
	 */
	public Filter getFilter(int col) {
		if(isFilterOn()) {
			fRowFilter.getFilter(col);
		}
		return null;
	}

	/**
	 * Removes all filters.
	 */
	public void removeFilters() {
		if(!isFilterOn()) {
			return;
		}
		fRowFilter = null;
		fIndexMap = null;
		fireTableDataChanged();
		fireFilterEvent(new FilterEvent(FilterEvent.Type.REMOVED_ALL));
	}

	/**
	 * Remove the filter on column <code>col</code>.
	 * @param col the index of the column
	 */
	public void removeFilter(int col) {
		if(!isFilterOn()) {
			return;
		}
		Filter filter = fRowFilter.remove(col);
		fireTableDataChanged();
		fireFilterEvent(new FilterEvent(FilterEvent.Type.REMOVED, filter, col));
	}

	/**
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		if(isFilterOn()) {
			return fIndexMap.size();
		}
		return fModel.getRowCount();
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return fModel.getColumnCount();
	}

	/**
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		if(isFilterOn()) {
			return fModel.getValueAt(fIndexMap.get(rowIndex), columnIndex);
		} else {
			return fModel.getValueAt(rowIndex, columnIndex);
		}
	}

	/**
	 * @return
	 */
	public boolean isFilterOn() {
		return fRowFilter != null;
	}

	/**
	 * @return true if the model is ready to support filtering; this is required
	 * to accomodate table models that do not know at construction time the type of objects for
	 * each column; they usually set the filter UI classes when data for the first row becomes available.
	 */
	public boolean readyToFilter() {
		return fFilterUIClasses != null;
	}

	/**
	 * Sets the filter UI classes.
	 * @param classes the length of this array must be the same as the number of columns
	 * in the adapted table
	 */
	public void setFilterUIClasses(Class[] classes) {
		if(classes != null) {
			if(classes.length != fModel.getColumnCount()) {
				throw new IllegalArgumentException("The numbed of filter UI classes is " +
				"different than the number of columns in the adapted table");
			}
		}
		fFilterUIClasses = classes;
		if(classes != null) {
			fireReadyToFilter();
		}
	}

	/**
	 * @return
	 */
	public RowFilter getRowFilter() {
		return fRowFilter;
	}

	/**
	 * Rebuilds the index mapping between this model and the adapted one.
	 */
	private void rebuildIndexMap() {
		fIndexMap = new HashMap<Integer, Integer>();
		int count = fModel.getRowCount();
		Set<Integer> filteredCols = fRowFilter.getFilteredColumns();
		for(int i = 0; i < count; i++) {
			Object[] objs = new Object[filteredCols.size()];
			int j = 0;
			for(Integer col : filteredCols) {
				objs[j] = fModel.getValueAt(i, col);
				++j;
			}
			if(fRowFilter.accept(objs)) {
				fIndexMap.put(fIndexMap.size(), i);
			}
		}
	}

	/**
	 * @see javax.swing.table.AbstractTableModel#findColumn(java.lang.String)
	 */
	public int findColumn(String columnName) {
		return fModel.findColumn(columnName);
	}

	/**
	 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	 */
	public Class< ? > getColumnClass(int columnIndex) {
		return fModel.getColumnClass(columnIndex);
	}

	/**
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	public String getColumnName(int column) {
		return fModel.getColumnName(column);
	}

	/**
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return fModel.isCellEditable(rowIndex, columnIndex);
	}

	/**
	 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
	 */
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		fModel.setValueAt(aValue, rowIndex, columnIndex);
	}

    /**
     * @param col
     * @return
     */
    public Class getFilterUIClassForColumn(int col) {
    	return fFilterUIClasses[col];
    }

    /**
     * @return the unfiltered number of rows
     */
    public int getRowCountUnfiltered() {
    	return fModel.getRowCount();
    }

    /**
     * @return the unfiltered number of rows; returns 0 if no filters are set
     */
    public int getRowCountFiltered() {
    	if(isFilterOn()) {
    		return getRowCount();
    	}
    	return 0;
    }

	/**
	 * @return
	 */
	public TableModel getAdaptedModel() {
		return fModel;
	}
}
