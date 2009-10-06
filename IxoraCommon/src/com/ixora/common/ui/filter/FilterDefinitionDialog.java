/*
 * Created on 14-Dec-2004
 */
package com.ixora.common.ui.filter;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import com.ixora.common.ui.AppDialog;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.ui.actions.ActionCancel;
import com.ixora.common.ui.actions.ActionOk;

/**
 * Dialog used to set filters on any number of columns in a table whose model
 * is a <code>FilteredTableModel</code>.
 * @author Daniel Moraru
 */
public class FilterDefinitionDialog extends AppDialog {
	/** Table with the columns names */
	private JTable fColumnsTable;
	/** Scroll pane for the columns table */
	private JScrollPane fScrollPane;
	/** Header panel */
	private JEditorPane fHeaderPanel;
	/** Filtered table model */
	private FilteredTableModel fTableModel;
	/** Event handler */
	private EventHandler fEventHandler;

	/**
	 * Event handler.
	 */
	private class EventHandler implements TableModel.Callback {
		/**
		 * @see com.ixora.common.ui.filter.FilterDefinitionDialog.TableModel.Callback#createFilter(int)
		 */
		public Filter createFilter(int columnIdx) {
			return handleCreateFilter(columnIdx);
		}
	}

	/**
	 * Column data used in the TableModel.
	 */
	private static class ColumnData {
		Filter fFilter;
		String fName;
		int fIdx;

		ColumnData(Filter filter, String columnName, int columnIdx) {
			fFilter = filter;
			fName = columnName;
			fIdx = columnIdx;
		}
	}

	/**
	 * Table model holding filter iformation for columns.
	 */
	private static class TableModel extends AbstractTableModel {
		interface Callback {
			/**
			 * Invoked when a filter is about to be enabled.
			 * @param columnIdx the column on which the filter is about to be set
			 * @return
			 */
			Filter createFilter(int columnIdx);
		}

		List<ColumnData> fData;
		Callback fCallback;

		TableModel(List<ColumnData> data, Callback callback) {
			fData = data;
			fCallback = callback;
		}

		/**
		 * @see javax.swing.table.TableModel#getRowCount()
		 */
		public int getRowCount() {
			return fData.size();
		}

		/**
		 * @see javax.swing.table.TableModel#getColumnCount()
		 */
		public int getColumnCount() {
			return 2;
		}

		/**
		 * @see javax.swing.table.TableModel#getColumnName(int)
		 */
		public String getColumnName(int column) {
			return column == 0 ? "" : "Column"; // TODO localize
		}

		/**
		 * @see javax.swing.table.TableModel#getColumnClass(int)
		 */
		public Class<?> getColumnClass(int columnIndex) {
			return columnIndex == 0 ? Boolean.class : String.class;
		}

		/**
		 * @see javax.swing.table.TableModel#isCellEditable(int, int)
		 */
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex == 0 ? true : false;
		}

		/**
		 * @see javax.swing.table.TableModel#getValueAt(int, int)
		 */
		public Object getValueAt(int rowIndex, int columnIndex) {
			ColumnData data = fData.get(rowIndex);
			if(columnIndex == 0) {
				return data.fFilter == null ? Boolean.FALSE : Boolean.TRUE;
			} else if(columnIndex == 1) {
				return data.fName;
			}
			return null;
		}

		/**
		 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
		 */
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			if(((Boolean)aValue)) {
				Filter filter = fCallback.createFilter(fData.get(rowIndex).fIdx);
				if(filter != null) {
					fData.get(rowIndex).fFilter = filter;
				}
			} else {
				fData.get(rowIndex).fFilter = null;
			}
			fireTableRowsUpdated(rowIndex, rowIndex);
		}
	}

	/**
	 * Constructor.
	 * @param parent
	 * @param columns
	 */
	public FilterDefinitionDialog(Frame parent, FilteredTableModel model) {
		super(parent, VERTICAL);
		init(model);
	}

	/**
	 * Constructor.
	 * @param parent
	 * @param columns
	 */
	public FilterDefinitionDialog(Dialog parent, FilteredTableModel model) {
		super(parent, VERTICAL);
		init(model);
	}

	/**
	 * @param model
	 */
	private void init(FilteredTableModel model) {
		setModal(true);
		setTitle("Define Filter");
		setPreferredSize(new Dimension(350, 360));

		fEventHandler = new EventHandler();
		fTableModel = model;
		fColumnsTable = UIFactoryMgr.createTable();

		int cols = model.getColumnCount();
		List<ColumnData> colData = new LinkedList<ColumnData>();
		for(int i = 0; i < cols; i++) {
			Class<?> clazz = fTableModel.getFilterUIClassForColumn(i);
			if(clazz != null) {
				colData.add(new ColumnData(model.getFilter(i), model.getColumnName(i), i));
			}
		}
		fColumnsTable.setModel(new TableModel(colData, fEventHandler));
		TableColumn col = fColumnsTable.getTableHeader().getColumnModel().getColumn(0);
		col.setMaxWidth(25);

		fScrollPane = UIFactoryMgr.createScrollPane();
		fScrollPane.setViewportView(fColumnsTable);

		fHeaderPanel = UIFactoryMgr.createHtmlPane();
		// TODO localize
		fHeaderPanel.setText("Select columns on which to apply filters; " +
				"filters are applied in the order in which they are defined.");
		fHeaderPanel.setPreferredSize(new Dimension(300, 50));
		buildContentPane();
	}

	/**
	 * @see com.ixora.common.ui.AppDialog#getDisplayPanels()
	 */
	protected Component[] getDisplayPanels() {
		return new Component[] {fHeaderPanel, fScrollPane};
	}

	/**
	 * @see com.ixora.common.ui.AppDialog#getButtons()
	 */
	protected JButton[] getButtons() {
		return new JButton[] {
			new JButton(new ActionOk() {
				public void actionPerformed(ActionEvent e) {
					handleOk();
				}
			}),
			new JButton(new ActionCancel() {
				public void actionPerformed(ActionEvent e) {
					fTableModel.removeFilters();
					dispose();
				}
			})
		};
	}

	/**
	 * Handles Ok button pressed.
	 */
	private void handleOk() {
		// apply all defined filters
		List<ColumnData> data = ((TableModel)fColumnsTable.getModel()).fData;
		boolean filterSet = false;
		for(ColumnData col : data) {
			if(col.fFilter != null) {
				fTableModel.addFilter(col.fIdx, col.fFilter);
				filterSet = true;
			}
		}
		if(!filterSet) {
			fTableModel.removeFilters();
		}
		dispose();
	}

	/**
	 * @param col
	 * @return
	 */
	private Filter handleCreateFilter(int col) {
		try {
			// launch the filter editor
			Class<?> clazz = fTableModel.getFilterUIClassForColumn(col);
			if(clazz == null) {
				return null;
			}
			Constructor cons = clazz.getConstructor(new Class[]{Dialog.class});
			FilterEditorDialog fdlg = (FilterEditorDialog)cons.newInstance(
				new Object[]{this});
			UIUtils.centerDialogAndShow(this, fdlg);
			return fdlg.getFilter();
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
		return null;
	}
}
