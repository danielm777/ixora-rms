/*
 * Created on 30-Sep-2004
 */
package com.ixora.common.typedproperties.ui;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import com.ixora.common.typedproperties.PropertyEntry;

/**
 * Cell editor for properties that have a fixed set of possible values.
 * @author Daniel Moraru
 */
final class CellEditorValueSet<T> extends PropertyEntryCellEditorExtended<T> {
	private static final long serialVersionUID = 2593712236612413380L;
	/** Editor that allows the selection from the value set */
    private ExtendedEditorValueSet<T> editor;

    /**
     * Event handler.
     */
    private final class EventHandler extends MouseAdapter {
        /**
         * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
         */
        public void mouseClicked(MouseEvent e) {
            handleLaunchEditor();
        }
    }

    /**
     * Constructor.
     * @param owner
     * @param r
     */
    public CellEditorValueSet(Component owner, CellComponentValueSet<T> vs) {
        super(owner, vs);
        editor = new ExtendedEditorValueSet<T>();
        vs.getDisplay().addMouseListener(new EventHandler());
    }

	/**
	 * @see com.ixora.common.typedproperties.ui.PropertyEntryCellEditorExtended#setComponentName(java.lang.String)
	 */
	public void setComponentName(String component) {
		super.setComponentName(component);
		editor.setComponentName(component);
	}

    /**
     * @see com.ixora.common.typedproperties.ui.PropertyEntryCellEditor#setPropertyEntry(com.ixora.common.app.typedproperties.PropertyEntry)
     */
    public void setPropertyEntry(PropertyEntry<T> e) {
        super.setPropertyEntry(e);
        editor.setCellComponent(display);
    }

    /**
     * @see com.ixora.common.typedproperties.ui.PropertyEntryCellEditorExtended#createEditor()
     */
    protected ExtendedEditor<T> createEditor() throws Exception {
        return editor;
    }
}
