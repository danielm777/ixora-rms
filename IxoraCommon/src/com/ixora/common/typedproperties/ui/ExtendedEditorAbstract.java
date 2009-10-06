/*
 * Created on 02-Oct-2004
 */
package com.ixora.common.typedproperties.ui;



/**
 * @author Daniel Moraru
 */
public abstract class ExtendedEditorAbstract
			implements ExtendedEditor {
    /** Listener */
    private Listener listener;

    /**
     * Constructor.
     */
    protected ExtendedEditorAbstract() {
        super();
    }

    /**
     * @see com.ixora.common.typedproperties.ui.ExtendedEditor#setListener(com.ixora.common.ui.typedproperties.ExtendedEditor.Listener)
     */
    public void setListener(Listener l) {
        this.listener = l;
    }

    /**
     * Fires editing stopped event.
     * @param val
     */
    protected void fireEditingStopped(Object val) {
        listener.editingStopped(val);
    }

    /**
     * Fires editing canceled event.
     * @param val
     */
    protected void fireEditingCanceled() {
        listener.editingCanceled();
    }
}
