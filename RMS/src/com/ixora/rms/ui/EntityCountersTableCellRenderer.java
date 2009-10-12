/*
 * Created on 29-Jun-2004
 */
package com.ixora.rms.ui;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.ixora.rms.client.model.CounterInfo;

/**
 * Cell renderer for the counters table.
 * @author Daniel Moraru
 */
final class EntityCountersTableCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = -8130906000223689782L;
	/** Original font */
	private Font originalFont;

	/**
	 * Constructor.
	 */
	public EntityCountersTableCellRenderer() {
		super();
	}
	/**
	 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	public Component getTableCellRendererComponent(
			JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Component ret = super.getTableCellRendererComponent(table, value, isSelected,
				hasFocus, row, column);
		CounterInfo cd =
			(CounterInfo)table.getModel()
				.getValueAt(row, 1);
		if(!cd.isCommitted()) {
			originalFont = getFont();
			setFont(getFont().deriveFont(Font.BOLD));
		} else {
			if(originalFont != null) {
				setFont(originalFont);
			}
		}
		return ret;
	}
}
