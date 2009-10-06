/*
 * Created on 03-Nov-2004
 */
package com.ixora.rms.ui.dataviewboard.logs;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.ixora.common.ui.UIConfiguration;

/**
 * @author Daniel Moraru
 */
public class LogControlTableCellRenderer extends DefaultTableCellRenderer {
	/**
	 * Formatter.
	 */
	private LogControlFormatter fFormatter;

	/**
     * Constructor.
     * @param stripe can be null for non row striping
     */
    public LogControlTableCellRenderer(Color stripe) {
        super();
        fFormatter = new LogControlFormatter(
        		getBackground(), stripe,
        		UIConfiguration.getMaximumLineLengthForToolTipText(),
        		null);
	}

    /**
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
     */
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected,
                hasFocus, row, column);
        LogControlFormatter.FormattingData fd = fFormatter.format(value, row, column, getBackground());
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
    public LogControlFormatter getFormatter() {
    	return fFormatter;
    }
}
