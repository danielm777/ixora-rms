/*
 * Created on 30-Sep-2004
 */
package com.ixora.common.typedproperties.ui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JTextField;

import com.ixora.common.typedproperties.PropertyEntry;

/**
 * Color cell.
 */
final class CellComponentColor extends CellComponent {
    /** Display control */
    private JTextField field;

	/**
	 *
	 */
	public CellComponentColor() {
	    super(new BorderLayout());
	    field = new JTextField();
	    delegate = field;
	    field.setBorder(BORDER);
	    field.setBackground(BACKGROUND);
	    field.setEditable(false);
	    add(field, BorderLayout.CENTER);
	}

	/**
     * @see com.ixora.common.typedproperties.ui.CellComponent#render(com.ixora.common.app.typedproperties.PropertyEntry, java.lang.Object)
     */
    public void render(PropertyEntry e, Object value) {
        Color c = (Color)value;
        field.setBackground(c == null ? Color.WHITE : c);
        validate();
        repaint();
    }

	/**
	 * @see javax.swing.JComponent#setBackground(java.awt.Color)
	 */
	public void setBackground(Color bg) {
		// do not propagate to delegate
	}
}