/*
 * Created on 03-Nov-2004
 */
package com.ixora.rms.ui.dataviewboard.properties;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.ixora.common.ui.UIConfiguration;

/**
 * @author Daniel Moraru
 */
public class PropertiesControlTableCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 4414835994458659953L;
	/**
	 * Formatter.
	 */
	private PropertiesControlFormatter fFormatter;

	/**
     * Constructor.
     * @param stripe can be null for non row striping
	 * @param defaultNumberFormat
     */
    public PropertiesControlTableCellRenderer(Color stripe, String defaultNumberFormat) {
        super();
        fFormatter = new PropertiesControlFormatter(
        		getBackground(), stripe,
        		UIConfiguration.getMaximumLineLengthForToolTipText(),
        		defaultNumberFormat);
	}

    /**
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
     */
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected,
                hasFocus, row, column);
        PropertiesControlFormatter.FormattingData fd = fFormatter.format(value, row, column, getBackground());
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
    public PropertiesControlFormatter getFormatter() {
    	return fFormatter;
    }
}
