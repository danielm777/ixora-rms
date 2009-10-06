/*
 * Created on 09-Jun-2005
 */
package com.ixora.rms.ui.dataviewboard.utils;

import java.awt.event.MouseEvent;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIUtils;

/**
 * @author Daniel Moraru
 */
public final class ToolTipHeader extends JTableHeader {
    private TableBasedControlTableModel fModel;
    /** Cached value of the max length of a line in the tooltip */
    private int maxLineLengthForToolTips;

    /**
     * @param model
     * @param colModel
     */
    public ToolTipHeader(
    		TableBasedControlTableModel model, TableColumnModel colModel) {
        super(colModel);
        fModel = model;
        this.maxLineLengthForToolTips = UIConfiguration.getMaximumLineLengthForToolTipText();
    }

    /**
     * @see javax.swing.JComponent#getToolTipText(java.awt.event.MouseEvent)
     */
    public String getToolTipText(MouseEvent e) {
        int col  = columnAtPoint(e.getPoint());
        if(col < 0) {
        	return null;
        }
        int modelCol = getTable().convertColumnIndexToModel(col);
        if(modelCol < 0) {
        	return null;
        }
        return UIUtils.getMultilineHtmlText(
        		("<b>" + fModel.getColumnName(col) + "</b><br>" + fModel.getColumnDescription(modelCol)),
        		maxLineLengthForToolTips);
    }
}
