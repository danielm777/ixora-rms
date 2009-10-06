/*
 * Created on 03-Nov-2004
 */
package com.ixora.rms.ui.dataviewboard.utils;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.ixora.common.ui.UIFactoryMgr;

/**
 * @author Daniel Moraru
 */
public class ButtonCellRenderer extends DefaultTableCellRenderer {
	protected JButton fButton;

	/**
     * Constructor.
     */
    public ButtonCellRenderer() {
        super();
		fButton = UIFactoryMgr.createButton();
		fButton.setPreferredSize(new Dimension(16, 16));
		fButton.setText("+");
	}

    /**
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
     */
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        return fButton;
    }
}
