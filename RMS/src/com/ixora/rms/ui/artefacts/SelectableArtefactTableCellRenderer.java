/*
 * Created on 29-Jun-2004
 */
package com.ixora.rms.ui.artefacts;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.ixora.rms.client.model.ArtefactInfo;

/**
 * Cell renderer for artefacts.
 * @author Daniel Moraru
 */
public final class SelectableArtefactTableCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 5840025311083053782L;
	/** Original font */
	private Font originalFont;

	/**
	 * Constructor.
	 */
	public SelectableArtefactTableCellRenderer() {
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
		if(!(value instanceof ArtefactInfo)) {
			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}
		ArtefactInfo ai = (ArtefactInfo)value;
		if(!ai.isCommitted()) {
			originalFont = getFont();
			setFont(getFont().deriveFont(Font.BOLD));
			setText("*" + getText());
		} else {
			if(originalFont != null) {
				setFont(originalFont);
			}
		}
		return ret;
	}
}
