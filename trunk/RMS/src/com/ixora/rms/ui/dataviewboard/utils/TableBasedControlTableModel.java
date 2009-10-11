/**
 * 08-Feb-2006
 */
package com.ixora.rms.ui.dataviewboard.utils;

import javax.swing.table.AbstractTableModel;

import com.ixora.common.ui.filter.FilterEditorDialog;
import com.ixora.rms.dataengine.external.QueryData;

/**
 * @author Daniel Moraru
 */
public abstract class TableBasedControlTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 6479473669366165314L;

	/**
	 *
	 */
	protected TableBasedControlTableModel() {
		super();
	}

	/**
	 * @see javax.swing.table.TableModel#isCellEditable(int, int)
	 */
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	/**
	 *
	 */
	public abstract void reset();

	/**
	 * @param data
	 * @return
	 */
	public abstract boolean inspectData(QueryData data);

	/**
	 * @param modelCol
	 * @return
	 */
	public abstract String getColumnDescription(int modelCol);

	/**
	 * @return
	 */
	public abstract Class<? extends FilterEditorDialog>[] getFilterUIClasses();
}
