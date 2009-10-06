/*
 * Created on 30-Sep-2004
 */
package com.ixora.common.typedproperties.ui;


/**
 * @author Daniel Moraru
 */
final class CellEditorFloat extends CellEditorNumber {
    /**
     * Constructor.
     */
    public CellEditorFloat(CellComponentFloat comp) {
        super(comp);
   }

    /**
     * @see javax.swing.CellEditor#getCellEditorValue()
     */
    public Object getCellEditorValue() {
        Object obj = field.getValue();
        if(obj == null) {
            return null;
        }
        return new Float(((Number)field.getValue()).floatValue());
    }
}
