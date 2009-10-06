/*
 * Created on 30-Sep-2004
 */
package com.ixora.common.typedproperties.ui;

import java.awt.BorderLayout;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.JTextField;

import com.ixora.common.typedproperties.PropertyEntry;


/**
 * Color cell.
 */
final class CellComponentDate extends CellComponent {
    /** Display control */
    private JTextField field;
    /** Date formatter used to display the date */
    private DateFormat formatter;

	/**
	 *
	 */
	CellComponentDate() {
	    super(new BorderLayout());
	    field = new JTextField();
	    delegate = field;
	    field.setBorder(BORDER);
	    field.setBackground(BACKGROUND);
	    field.setEditable(false);
	    add(field, BorderLayout.CENTER);
	    formatter = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM);
	}

    /**
     * @see com.ixora.common.typedproperties.ui.CellComponent#render(com.ixora.common.app.typedproperties.PropertyEntry, java.lang.Object)
     */
    public void render(PropertyEntry e, Object value) {
    	Date d = (Date)value;
   	    field.setText(d == null ? "" : formatter.format(d));
    }
}