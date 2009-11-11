/*
 * Created on 04-Dec-2004
 */
package com.ixora.common.typedproperties.ui.list;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import com.ixora.common.typedproperties.AllowNullNumberFormat;
import com.ixora.common.typedproperties.PropertyEntry;
import com.ixora.common.typedproperties.TypedProperties;
import com.ixora.common.utils.Utils;

/**
 * @author Daniel Moraru
 */
public class PropertyListTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 7082625322945566623L;

	/**
	 * Listener.
	 */
	public interface Listener {
		/**
		 * Invoked just before a new property will be added.
		 * @param model
		 * @param prop
		 * @throws Exception if an instanceof VetoExeption the property is silently discarded
		 */
		void aboutToAddNewProperties(PropertyListTableModel model, TypedProperties prop) throws Exception;
		/**
		 * Invoked just before an entity is deleted.
		 * @param model
		 * @param prop
		 */
		void deletedProperties(PropertyListTableModel model, TypedProperties prop);
	}

	/** Prototype */
	protected TypedProperties fPrototype;
	/** Component */
	protected String fComponent;
	/** Column names */
	protected String[] fColumnNames;
	/** Properties */
	protected List<TypedProperties> fProperties;
	/** True to fire table update events */
	protected boolean fFireTableEvents;
	/** Listeners */
	protected List<Listener> fListeners;

	/**
	 * Constructor.
	 * @param prototype the prototype, requiered if properties is null, if null the first element
	 * in the list will become the prototype
	 * @param component owner component
	 * @param properties list of initial values
	 */
	public PropertyListTableModel(TypedProperties prototype,
			String component, List<? extends TypedProperties> properties) {
		this(prototype, component, properties, true);
	}

	/**
	 * Constructor.
	 * @param prototype the prototype, requiered if properties is null, if null the first element
	 * in the list will become the prototype
	 * @param component owner component
	 * @param properties list of initial values
	 * @param fireTableEvents
	 */
	protected PropertyListTableModel(TypedProperties prototype,
			String component, List<? extends TypedProperties> properties, boolean fireTableEvents) {
		super();
		if(prototype == null) {
			if(properties == null || properties.size() == 0) {
				throw new IllegalArgumentException("the prototype is missing");
			} else {
				prototype = properties.get(0);
			}
		} else {
			this.fPrototype = prototype;
		}
		this.fListeners = new LinkedList<Listener>();
		this.fFireTableEvents = fireTableEvents;
		this.fComponent = component;
		Map<String, PropertyEntry<?>> entries = prototype.getEntries();
		fColumnNames = new String[entries.size()];
		int i = 0;
		for(Iterator<String> iter = entries.keySet().iterator(); iter.hasNext(); ++i) {
			fColumnNames[i] = Utils.getTranslatedMessage(component, iter.next());
		}
		if(properties == null) {
			this.fProperties = new LinkedList<TypedProperties>();
		} else {
			this.fProperties = new LinkedList<TypedProperties>(properties);
		}
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return fColumnNames.length;
	}

	/**
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return this.fProperties.size();
	}

	/**
	 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
	 */
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if(AllowNullNumberFormat.isNull(aValue)) {
			aValue = null;
		}
	    TypedProperties props = this.fProperties.get(rowIndex);
	    PropertyEntry<?> entry = props.getEntryAt(columnIndex);
	    props.setObject(entry.getProperty(), aValue);
		fireTableCellUpdated(rowIndex, columnIndex);
	}

	/**
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		TypedProperties prop = this.fProperties.get(rowIndex);
		return prop.getEntryAt(columnIndex);
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	public String getColumnName(int column) {
		return fColumnNames[column];
	}

	/**
	 * @see javax.swing.table.TableModel#isCellEditable(int, int)
	 */
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		// all cells are editable
		return true;
	}

	/**
	 * Adds a new property by cloning the prototype.
	 * @throws Exception if instanceof VetoException the operation is cancelled silently
	 */
	public void addNewProperty() throws Exception {
		TypedProperties newProp = (TypedProperties)this.fPrototype.clone();
		fireAboutToAddNewProperties(newProp);
		int size = this.fProperties.size();
		this.fProperties.add(newProp);
		if(fFireTableEvents) {
			fireTableRowsInserted(size, size);
		}
	}

	/**
	 * Adds a new property by cloning the prototype.
	 * @param idx
	 * @throws Exception if instanceof VetoException the operation is cancelled silently
	 */
	public void addNewProperty(int idx) throws Exception {
		TypedProperties newProp = (TypedProperties)this.fPrototype.clone();
		fireAboutToAddNewProperties(newProp);
		this.fProperties.add(idx, newProp);
		if(fFireTableEvents) {
			fireTableRowsInserted(idx, idx);
		}
	}

	/**
	 * Removes the property at the given row
	 * @param row
	 */
	public void removeProperty(int row) {
		TypedProperties props = this.fProperties.remove(row);
		if(props != null) {
			fireAboutToDeleteProperties(props);
		}
		if(fFireTableEvents) {
			fireTableRowsDeleted(row, row);
		}
	}

	/**
	 * Moves property up in the list.
	 * @param row
	 */
	public void movePropertyUp(int row) {
		if(row == 0) {
			return;
		}
		Collections.swap(fProperties, row - 1, row);
		if(fFireTableEvents) {
			fireTableRowsUpdated(row - 1, row);
		}
	}

	/**
	 * Moves property up in the list.
	 * @param row
	 */
	public void movePropertyDown(int row) {
		if(row == this.fProperties.size() - 1) {
			return;
		}
		Collections.swap(fProperties, row, row + 1);
		if(fFireTableEvents) {
			fireTableRowsUpdated(row, row + 1);
		}
	}

	/**
	 * @return
	 */
	public List<TypedProperties> getAllProperties() {
		return this.fProperties;
	}

	/**
	 * Clears all data.
	 */
	public void clear() {
		fProperties.clear();
		if(fFireTableEvents) {
			fireTableDataChanged();
		}
	}

	/**
	 * Sets the properties.
	 * @param props
	 */
	public void setProperties(List<? extends TypedProperties> props) {
		fProperties.addAll(props);
		if(fFireTableEvents) {
			fireTableDataChanged();
		}
	}

	/**
	 * @param listener
	 */
	public void addListener(Listener listener) {
		if(listener != null && !this.fListeners.contains(listener)) {
			this.fListeners.add(listener);
		}
	}

	/**
	 * @param prop
	 * @throws Exception
	 */
	protected void fireAboutToAddNewProperties(TypedProperties prop) throws Exception {
		for(Listener list : fListeners) {
			list.aboutToAddNewProperties(this, prop);
		}
	}

	/**
	 * @param prop
	 */
	protected void fireAboutToDeleteProperties(TypedProperties prop) {
		for(Listener list : fListeners) {
			list.deletedProperties(this, prop);
		}
	}

	/**
	 * @param listener
	 */
	public void removeListener(Listener listener) {
		this.fListeners.remove(listener);
	}
}
