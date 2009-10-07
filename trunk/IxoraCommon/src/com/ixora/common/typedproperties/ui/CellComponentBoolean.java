/*
 * Created on 01-Oct-2004
 */
package com.ixora.common.typedproperties.ui;

import java.awt.BorderLayout;

import javax.swing.JCheckBox;

import com.ixora.common.typedproperties.PropertyEntry;

/**
 * @author Daniel Moraru
 */
final class CellComponentBoolean extends CellComponent {
    private JCheckBox field;

    /**
     * Constructor.
     */
    public CellComponentBoolean() {
        super(new BorderLayout());
        field = new JCheckBox();
        delegate = field;
        field.setBorder(BORDER);
        field.setBackground(BACKGROUND);
        add(field, BorderLayout.CENTER);
    }

    /**
     * @see com.ixora.common.typedproperties.ui.CellComponent#render(com.ixora.common.app.typedproperties.PropertyEntry, java.lang.Object)
     */
    public void render(PropertyEntry e, Object value) {
        field.setSelected(value == null ? false : ((Boolean)value).booleanValue());
    }
}