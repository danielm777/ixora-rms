/*
 * Created on 30-Sep-2004
 */
package com.ixora.common.typedproperties.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;

import javax.swing.AbstractCellEditor;

import com.ixora.common.typedproperties.PropertyEntry;
import com.ixora.common.ui.UIExceptionMgr;

/**
 * Property value editor that requires a more advanced
 * editor to be opened.
 * @author Daniel Moraru
 */
public class PropertyEntryCellEditorExtended extends AbstractCellEditor
	 implements PropertyEntryCellEditor {
    /** Extended editors events */
    public interface ExtendedEditorListener {
    	/**
    	 * Invoked just before launching the editor.
    	 * @param editor
    	 * @param pe
    	 * @throws Exception
    	 */
    	void aboutToLaunch(ExtendedEditor editor, PropertyEntry pe) throws Exception;
    }

    /**
     * This is the component used to display the value
     * while the extended editor is shown.
     */
    protected CellComponentExtended display;
    /** Data returned by the extended editor */
    protected Object data;
    /** The entry being editted */
    protected PropertyEntry entry;
    /**
     * The owner component used to trace down the
     * window which will be the parent of the extended
     * editor.
     */
    protected Component owner;
    /** Extended editor */
    protected ExtendedEditor editor;
    /** Component name */
    protected String componentName;
    /** Listener for extended editor events */
    protected ExtendedEditorListener editorListener;
    /** Event handler */
    private EventHandler eventHandler;

    /**
     * Event handler.
     */
    private final class EventHandler extends MouseAdapter
    	implements ActionListener, ExtendedEditor.Listener {
        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            handleLaunchEditor();
        }
        /**
         * @see com.ixora.common.typedproperties.ui.ExtendedEditor.Listener#editingStopped(java.lang.Object)
         */
        public void editingStopped(Object val) {
            handleEditingStopped(val);
        }
        /**
         * @see com.ixora.common.typedproperties.ui.ExtendedEditor.Listener#editingCanceled()
         */
        public void editingCanceled() {
            handleEditingCanceled();
        }
    }

    /**
     * Constructor.
     * @param owner the component that will own the extended editor
     * @param display component that will display the value
     */
    protected PropertyEntryCellEditorExtended(Component owner,
            CellComponentExtended display) {
        super();
        this.owner = owner;
        this.display = display;
        this.eventHandler = new EventHandler();
        this.display.getButton().addActionListener(this.eventHandler);
        // add a mouse listener to the display component as well
        // if clicked the extended editor will be toggled
        this.display.getDisplay().addMouseListener(this.eventHandler);
    }

    /**
     * Sets the current entry.
     * @param e
     */
    public void setPropertyEntry(PropertyEntry e) {
        entry = e;
        data = e.getValue();
        display.render(e);
    }

	/**
	 * Sets the component.
	 * @param component
	 */
	public void setComponentName(String component) {
	    this.componentName = component;
	    display.setComponentName(component);
	}

    /**
	 * @see javax.swing.CellEditor#getCellEditorValue()
	 */
	public Object getCellEditorValue() {
		return data;
	}

	/**
	 * @see javax.swing.CellEditor#cancelCellEditing()
	 */
	public void cancelCellEditing() {
		fireEditingCanceled();
		this.editor = null;
	}

    /**
     * @see com.ixora.common.typedproperties.ui.PropertyEntryCellEditor#getComponent()
     */
    public Component getComponent() {
        return display;
    }

	/**
	 * @see javax.swing.CellEditor#stopCellEditing()
	 */
	public boolean stopCellEditing() {
		fireEditingStopped();
		this.editor = null;
		return true;
	}

// ExtendedEditor.Listener

	/**
     * @see com.ixora.common.typedproperties.ui.ExtendedEditor.Listener#editingStopped(java.lang.Object)
     */
    protected void handleEditingStopped(Object val) {
        this.data = val;
		fireEditingStopped();
		this.editor = null;
    }

    /**
     * @see com.ixora.common.typedproperties.ui.ExtendedEditor.Listener#editingCanceled()
     */
    protected void handleEditingCanceled() {
        fireEditingCanceled();
        this.editor = null;
    }

	/**
	 * Allows subclasses to intervene in the creation of the
	 * extended editor for the current property type.<br>
	 * By default it creates the extended editor provided by the
	 * property.
	 * @param l
	 * @return
	 * @throws Exception
	 */
	protected ExtendedEditor createEditor() throws Exception {
	    return (ExtendedEditor)Class.forName(
	            entry.getExtendedEditorClass()).newInstance();
	}

	/**
	 * Launches the editor.
	 */
	protected void handleLaunchEditor() {
		try {
			if(editor != null) {
				editor.close();
				editor = null;
				return;
			}
		    editor = createEditor();
		    editor.setListener(this.eventHandler);
		    if(editorListener != null) {
		    	editorListener.aboutToLaunch(editor, entry);
		    }
		    editor.launch(owner, entry);
		} catch (Exception ex) {
			fireEditingCanceled();
			UIExceptionMgr.userException(ex);
		}
	}

	/**
	 * @param editorListener
	 */
	public void setExtendedEditorListener(ExtendedEditorListener editorListener) {
		this.editorListener = editorListener;
	}
}