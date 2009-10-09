/*
 * Created on 30-Sep-2004
 */
package com.ixora.common.typedproperties.ui;

import java.awt.Component;
import java.util.EventObject;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

import com.ixora.common.typedproperties.PropertyEntry;

/**
 * Cell editor.
 */
public final class PropertyTableCellEditor implements TableCellEditor {
	/** All editors */
    private CellEditors editors;
    /** Current editor */
	private PropertyEntryCellEditor<?> editor;
    /** Component name */
    private String componentName;
    /** Listeners */
    private List<ExtendedEditorListener> listeners;
    private EventHandler eventHandler;

    /**
     * Listener for extended editor events.
     */
    public interface ExtendedEditorListener extends PropertyEntryCellEditorExtended.ExtendedEditorListener {
    }

    private class EventHandler implements PropertyEntryCellEditorExtended.ExtendedEditorListener {
		/**
		 * @throws Exception
		 * @see com.ixora.common.typedproperties.ui.PropertyEntryCellEditorExtended.ExtendedEditorListener#aboutToLaunch(com.ixora.common.typedproperties.ui.ExtendedEditor, com.ixora.common.typedproperties.PropertyEntry)
		 */
		public void aboutToLaunch(ExtendedEditor<?> editor, PropertyEntry<?> pe) throws Exception {
			fireAboutToLaunch(editor, pe);
		}
    }

	/**
	 * @param owner owner of all extended editors
	 * @param renderer
	 */
	public PropertyTableCellEditor(
	        Component owner) {
	    CellRenderers renderers = new CellRenderers();
	    this.editors = new CellEditors(owner, renderers);
	    this.eventHandler = new EventHandler();
	    this.listeners = new LinkedList<ExtendedEditorListener>();
	}

	/**
	 * Sets the component.
	 * @param component
	 */
	public void setComponentName(String component) {
	    this.componentName = component;
	}

	/**
	 * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
	 */
	@SuppressWarnings("unchecked")
	public Component getTableCellEditorComponent(
			JTable table, Object value,
			boolean isSelected, int row, int column) {
	   if(value instanceof PropertyEntry) {
			PropertyEntry pe = (PropertyEntry)value;
		    int type = pe.getType();
		    PropertyEntryCellEditor<?> e = null;
		    // set uses a specialized editor
			if(pe.getValueSet() != null) {
			    e = (PropertyEntryCellEditor<?>)editors.getEditorValueSet(type);
			} else {
			    e = (PropertyEntryCellEditor<?>)editors.getEditor(type);
			}
			if(e != null) {
				if(e instanceof PropertyEntryCellEditorExtended) {
					PropertyEntryCellEditorExtended ee = (PropertyEntryCellEditorExtended)e;
					ee.setComponentName(componentName);
					ee.setExtendedEditorListener(this.eventHandler);
				}
			    e.setPropertyEntry(pe);
			    editor = e;
		    	return editor.getComponent();
			}
	   }
	   return null;
	}

	/**
	 * @see javax.swing.CellEditor#cancelCellEditing()
	 */
	public void cancelCellEditing() {
		if(editor != null) {
			editor.cancelCellEditing();
		}
	}

	/**
	 * @see javax.swing.CellEditor#stopCellEditing()
	 */
	public boolean stopCellEditing() {
		if(editor != null) {
			return editor.stopCellEditing();
		}
		return true;
	}

	/**
	 * @see javax.swing.CellEditor#getCellEditorValue()
	 */
	public Object getCellEditorValue() {
		return editor.getCellEditorValue();
	}

	/**
	 * @see javax.swing.CellEditor#isCellEditable(java.util.EventObject)
	 */
	public boolean isCellEditable(EventObject anEvent) {
		return true;
	}

	/**
	 * @see javax.swing.CellEditor#shouldSelectCell(java.util.EventObject)
	 */
	public boolean shouldSelectCell(EventObject anEvent) {
		return editor.shouldSelectCell(anEvent);
	}

	/**
	 * @see javax.swing.CellEditor#addCellEditorListener(javax.swing.event.CellEditorListener)
	 */
	public void addCellEditorListener(CellEditorListener l) {
	    editors.addCellEditorListener(l);
	}

	/**
	 * @see javax.swing.CellEditor#removeCellEditorListener(javax.swing.event.CellEditorListener)
	 */
	public void removeCellEditorListener(CellEditorListener l) {
		editors.removeCellEditorListener(l);
	}

	/**
	 * @param list
	 */
	public void addExtendedEditorListener(ExtendedEditorListener list) {
		if(list != null && !listeners.contains(list)) {
			listeners.add(list);
		}
	}

	/**
	 * @param list
	 */
	public void removeExtendedEditorListener(ExtendedEditorListener list) {
		listeners.remove(list);
	}

	/**
	 * @param editor
	 * @param pe
	 * @throws Exception
	 */
	private void fireAboutToLaunch(ExtendedEditor<?> editor, PropertyEntry<?> pe) throws Exception {
		for(ExtendedEditorListener list : listeners) {
			list.aboutToLaunch(editor, pe);
		}
	}
}