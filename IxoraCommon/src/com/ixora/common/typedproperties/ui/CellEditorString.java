/*
 * Created on 30-Sep-2004
 */
package com.ixora.common.typedproperties.ui;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;

import com.ixora.common.typedproperties.PropertyEntry;
import com.ixora.common.ui.UIFactoryMgr;

/**
 * @author Daniel Moraru
 */
final class CellEditorString extends CellEditorInPlace<String> {
	/** Compoenent */
	private JTextField field;

    /**
     * Constructor.
     */
    public CellEditorString() {
        super();
        field = UIFactoryMgr.createTextField();
        field.setBackground(BACKGROUND);
        delegate = new DefaultCellEditor(field);
    }

    /**
     * @see com.ixora.common.typedproperties.ui.PropertyEntryCellEditor#setPropertyEntry(com.ixora.common.app.typedproperties.PropertyEntry)
     */
    public void setPropertyEntry(PropertyEntry<String> e) {
        field.setText((String)e.getValue());
    }

    /**
     * @see javax.swing.CellEditor#getCellEditorValue()
     */
    public Object getCellEditorValue() {
        return field.getText();
    }
}
