/*
 * Created on 03-Nov-2004
 */
package com.ixora.rms.ui.reactions;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIUtils;

/**
 * @author Daniel Moraru
 */
final class ReactionTableCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = -7862118223467456068L;
	/** Original background color */
    private Color fOriginalBackColor;
    /** Color to use for row stripping */
    private Color fStripeColor;
    /** Cached value of the max length of a line in the tooltip */
    private int fMaxLineLengthForToolTips;
    /** Text color */
    private Color fTextColor;

    /**
     * Constructor.
     */
    public ReactionTableCellRenderer() {
        super();
        this.fStripeColor = new Color(240, 240, 240);
        this.fMaxLineLengthForToolTips = UIConfiguration.getMaximumLineLengthForToolTipText();
	}

    /**
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
     */
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected,
                hasFocus, row, column);
        String toolTipText = String.valueOf(value);
        if(toolTipText.length() < fMaxLineLengthForToolTips) {
        	setToolTipText(toolTipText);
        } else {
        	setToolTipText(UIUtils.getMultilineHtmlText(toolTipText, fMaxLineLengthForToolTips));
        }
        if(isSelected) {
        	return this;
        }
        if(fOriginalBackColor == null) {
        	// save original background color
        	fOriginalBackColor = getBackground();
        }
        if(fStripeColor == null) {
        	fStripeColor = fOriginalBackColor;
        }
        setBackground(row % 2 == 0 ? fOriginalBackColor : fStripeColor);
        if(column == 0) {
        	fTextColor = getTextColor(value);
        }
        if(fTextColor != null) {
        	setForeground(fTextColor);
        }
        return this;
    }

	/**
	 * @param value
	 * @return
	 */
	private Color getTextColor(Object value) {
		if(value != null) {
			if(value.equals("CRITICAL")) {
				return Color.RED.darker();
			} else if(value.equals("HIGH")) {
				return Color.ORANGE.darker();
			} else if(value.equals("LOW")) {
				return Color.GREEN.darker();
			}
		}
		return null;
	}
}
