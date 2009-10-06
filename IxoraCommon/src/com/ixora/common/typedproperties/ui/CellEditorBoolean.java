/*
 * Created on 30-Sep-2004
 */
package com.ixora.common.typedproperties.ui;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;

import com.ixora.common.typedproperties.PropertyEntry;

/**
 * @author Daniel Moraru
 */
final class CellEditorBoolean extends CellEditorInPlace {
	/** Component */
	private JCheckBox field;

    /**
     * Constructor.
     */
    public CellEditorBoolean() {
        super();
        field = new JCheckBox();
        field.setBorder(BORDER);
        field.setBackground(BACKGROUND);
        delegate = new DefaultCellEditor(field);
    }

    /**
     * @see com.ixora.common.typedproperties.ui.PropertyEntryCellEditor#setPropertyEntry(com.ixora.common.app.typedproperties.PropertyEntry)
     */
    public void setPropertyEntry(PropertyEntry e) {
        Object obj = e.getValue();
        if(obj == null) {
            return;
        }
        boolean val = ((Boolean)obj).booleanValue();
        field.setSelected(val);
    }

    /**
     * @see javax.swing.CellEditor#getCellEditorValue()
     */
    public Object getCellEditorValue() {
        return Boolean.valueOf(field.isSelected());
    }
}
