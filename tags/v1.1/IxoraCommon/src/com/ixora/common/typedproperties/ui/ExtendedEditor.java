/*
 * Created on 02-Oct-2004
 */
package com.ixora.common.typedproperties.ui;

import java.awt.Component;

import com.ixora.common.typedproperties.PropertyEntry;

/**
 * This interface must be implemented for properties that
 * require an extended editor, like Color and Date properties.
 * @author Daniel Moraru
 */
public interface ExtendedEditor<T> {
    /**
     * Editor listener.
     */
    public interface Listener {
        /**
         * @param val the editor's value
         */
        void editingStopped(Object val);
        /**
         * Invoked when the editing was cancelled.
         */
        void editingCanceled();
    }

    /**
     * @param l
     */
    void setListener(Listener l);

    /**
     * @param owner the component that ows this editor
     * @param pe
     * @return the edited value
     */
    void launch(Component owner, PropertyEntry<T> pe);
    /**
     * Hides the editor.
     */
    void close();
}
