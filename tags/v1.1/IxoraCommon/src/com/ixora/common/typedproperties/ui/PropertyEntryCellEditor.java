/*
 * Created on 30-Sep-2004
 */
package com.ixora.common.typedproperties.ui;

import java.awt.Component;

import javax.swing.CellEditor;

import com.ixora.common.typedproperties.PropertyEntry;

/**
 * Cell editor for property values.
 * @author Daniel Moraru
 */
public interface PropertyEntryCellEditor<T> extends CellEditor {
    /**
     * Sets the current entry.
     * @param e
     */
    void setPropertyEntry(PropertyEntry<T> e);
    /**
     * @return the component that implements the editor
     */
    Component getComponent();
}