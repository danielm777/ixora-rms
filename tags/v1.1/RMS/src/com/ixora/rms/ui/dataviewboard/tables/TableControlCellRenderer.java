/*
 * Created on 03-Nov-2004
 */
package com.ixora.rms.ui.dataviewboard.tables;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.ixora.common.ui.UIConfiguration;

/**
 * @author Daniel Moraru
 */
public class TableControlCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1739263152235866682L;
	/**
	 * Formatter.
	 */
	private TableControlFormatter fFormatter;

	/**
     * Constructor.
     * @param stripe can be null for non row striping
     * @param up
     * @param down
     * @param deltaHistorySize
     */
    public TableControlCellRenderer(Color stripe, Color up, Color down, int deltaHistorySize) {
        super();
        fFormatter = new TableControlFormatter(
        		getBackground(), stripe, up, down, deltaHistorySize,
        		UIConfiguration.getMaximumLineLengthForToolTipText(), null);
	}

    /**
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
     */
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected,
                hasFocus, row, column);
        TableControlFormatter.FormattingData fd = fFormatter.format(value, row, column, getBackground());
        setText(fd.fText);
        setToolTipText(fd.fToolTip);
        if(isSelected) {
        	return this;
        }
        setBackground(fd.fBackgroundColor);
        return this;
    }

    /**
     * @return
     */
    public TableControlFormatter getFormatter() {
    	return fFormatter;
    }
}
