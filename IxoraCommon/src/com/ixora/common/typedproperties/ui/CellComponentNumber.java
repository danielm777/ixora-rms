/*
 * Created on 30-Sep-2004
 */
package com.ixora.common.typedproperties.ui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFormattedTextField;
import javax.swing.text.NumberFormatter;

import com.ixora.common.typedproperties.PropertyEntry;
import com.ixora.common.typedproperties.PropertyEntryNumber;

/**
 * @author Daniel Moraru
 */
public abstract class CellComponentNumber<T extends Comparable<T>> extends CellComponent<T> {
	private static final long serialVersionUID = -5496254212578042476L;
	/** Compoenent */
	protected JFormattedTextField field;

    /**
     * Constructor.
     * @param lm
     */
    public CellComponentNumber() {
        super(new BorderLayout());
        field = new JFormattedTextField(createFormatter());
        delegate = field;
        field.setBorder(BORDER);
        field.setBackground(BACKGROUND);
        field.setEditable(false);
        add(field, BorderLayout.CENTER);
    }

    /**
     * @see com.ixora.common.typedproperties.ui.CellComponent#render(com.ixora.common.app.typedproperties.PropertyEntry, java.lang.Object)
     */
    @SuppressWarnings("unchecked")
	public void render(PropertyEntry<T> e, T value) {
        PropertyEntryNumber pen = (PropertyEntryNumber)e;
        NumberFormatter formatter = (NumberFormatter)field.getFormatter();
        formatter.setFormat(pen.getFormat());
        if(value != null) {
            field.setValue(value);
        } else {
            field.setText("");
        }
    }

    /**
     * @return the text field used to display the values
     */
    public NumberFormatter getFormatter() {
        return (NumberFormatter)this.field.getFormatter();
    }

    /**
     * @see java.awt.Component#setBackground(java.awt.Color)
     */
    public void setBackground(Color bg) {
        if(field != null) {
            field.setBackground(bg);
        }
    }

    /**
     * @see java.awt.Component#setForeground(java.awt.Color)
     */
    public void setForeground(Color fg) {
        if(field != null) {
            field.setForeground(fg);
        }
    }

    /**
     * Subclasses must return here the number formatter.
     * @return
     */
    protected abstract NumberFormatter createFormatter();
}
