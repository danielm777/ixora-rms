/*
 * Created on 30-Sep-2004
 */
package com.ixora.common.typedproperties.ui;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JTextField;

import com.ixora.common.typedproperties.PropertyEntry;

/**
 * Color cell.
 */
final class CellComponentFile extends CellComponent {
    /** Display control */
    private JTextField field;

	/**
	 *
	 */
	public CellComponentFile() {
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
        File f = (File)value;
        field.setText(f == null ? "" : f.getAbsolutePath());
    }
}