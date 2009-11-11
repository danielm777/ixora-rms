/*
 * Created on 30-Sep-2004
 */
package com.ixora.common.typedproperties.ui;

import java.awt.Color;
import java.awt.Component;
import java.util.EventObject;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.border.Border;
import javax.swing.event.CellEditorListener;

/**
 * This is the cell editor used by properties that do not
 * require an extended editor and therefore an in-place editor can be used.
 * @author Daniel Moraru
 */
public abstract class CellEditorInPlace<T> implements PropertyEntryCellEditor<T> {
    /** This border must be used by all display components in subclasses */
    protected static final Border BORDER = BorderFactory.createEmptyBorder(2, 2, 2, 2);
    /** Background color that must be used by all display components */
    protected static final Color BACKGROUND = Color.WHITE;

    /** Delegate cell editor */
	protected DefaultCellEditor delegate;

    /**
     * Constructor.
     */
    public CellEditorInPlace() {
        super();
    }

    /**
     * @see com.ixora.common.typedproperties.ui.PropertyEntryCellEditor#getComponent()
     */
    public Component getComponent() {
        return delegate.getComponent();
    }

    /**
     * @see javax.swing.CellEditor#cancelCellEditing()
     */
    public void cancelCellEditing() {
        delegate.cancelCellEditing();
    }

    /**
     * @see javax.swing.CellEditor#stopCellEditing()
     */
    public boolean stopCellEditing() {
        return delegate.stopCellEditing();
    }

    /**
     * @see javax.swing.CellEditor#isCellEditable(java.util.EventObject)
     */
    public boolean isCellEditable(EventObject anEvent) {
        return delegate.isCellEditable(anEvent);
    }

    /**
     * @see javax.swing.CellEditor#shouldSelectCell(java.util.EventObject)
     */
    public boolean shouldSelectCell(EventObject anEvent) {
        return delegate.shouldSelectCell(anEvent);
    }

    /**
     * @see javax.swing.CellEditor#addCellEditorListener(javax.swing.event.CellEditorListener)
     */
    public void addCellEditorListener(CellEditorListener l) {
        delegate.addCellEditorListener(l);
    }

    /**
     * @see javax.swing.CellEditor#removeCellEditorListener(javax.swing.event.CellEditorListener)
     */
    public void removeCellEditorListener(CellEditorListener l) {
        delegate.removeCellEditorListener(l);
    }
}
