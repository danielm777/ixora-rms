/*
 * Created on 30-Sep-2004
 */
package com.ixora.common.typedproperties.ui;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import com.ixora.common.MessageRepository;
import com.ixora.common.messages.Msg;
import com.ixora.common.typedproperties.AllowNullNumberFormat;
import com.ixora.common.typedproperties.PropertyEntry;
import com.ixora.common.typedproperties.TypedProperties;


/**
 * Table model for properties.
 */
final class PropertyTableModel extends AbstractTableModel {
    private PropertyEntry[] fData;
	private TypedPropertiesEditor fEditor;
	private boolean fEditable;

	/**
	 * @param editor
	 * @param props
	 */
	public PropertyTableModel(
	        TypedPropertiesEditor editor, TypedProperties props) {
	    this.fEditor = editor;
	    Set keys = props.keys();
	    List lst = new LinkedList();
	    String key;
	    PropertyEntry entry;
	    int i = 0;
	    for(Iterator iter = keys.iterator(); iter.hasNext(); ++i) {
            key = (String)iter.next();
            entry = props.getEntry(key);
            if(entry.isVisible()) {
                lst.add(entry);
            }
        }
	    this.fData = (PropertyEntry[])lst.toArray(
	            new PropertyEntry[lst.size()]);
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return 2;
	}

	/**
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return fData.length;
	}

	/**
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
	    PropertyEntry pe = fData[rowIndex];
		if(columnIndex == 0) {
			// property name
			return fEditor.getTranslatedMessage(pe.getProperty());
		} else {
		    // return the entry and let the renderes provide
		    // the displayed text
			return pe;
		}
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	public String getColumnName(int column) {
		if(column == 0) {
			return MessageRepository.get(Msg.COMMON_UI_TEXT_PROPERTY);
		} else {
			return MessageRepository.get(Msg.COMMON_UI_TEXT_VALUE);
		}
	}

	/**
	 * @see javax.swing.table.TableModel#isCellEditable(int, int)
	 */
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if(!fEditable) {
			return false;
		}
		if(columnIndex == 1) {
		    PropertyEntry entry =
		        this.fData[rowIndex];
			return !entry.isReadOnly();
		}
		return false;
	}

	/**
	 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
	 */
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if(AllowNullNumberFormat.isNull(aValue)) {
			aValue = null;
		}
		if(columnIndex == 1) {
		    PropertyEntry entry =
		        this.fData[rowIndex];
		    fEditor.getProperties().setObject(entry.getProperty(), aValue);
			fireTableCellUpdated(rowIndex, columnIndex);
		}
	}

	/**
	 * Enables/disables editing.
	 * @param e
	 */
	public void setEditable(boolean e) {
		this.fEditable = e;
	}

	/**
	 * @param row
	 * @return
	 */
	public PropertyEntry getEntryAt(int row) {
	    return fData[row];
	}

	/**
	 * @return
	 */
	public PropertyEntry[] getEntries() {
	    return this.fData;
	}
}