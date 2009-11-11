/*
 * Created on 30-Sep-2004
 */
package com.ixora.common.typedproperties.ui;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JTextField;

import com.ixora.common.typedproperties.PropertyEntry;

/**
 * File cell.
 * @author Daniel Moraru
 */
final class CellComponentFile extends CellComponent<File> {
	private static final long serialVersionUID = 2997089022127819575L;
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
    public void render(PropertyEntry<File> e, File value) {
        File f = (File)value;
        field.setText(f == null ? "" : f.getAbsolutePath());
    }
}