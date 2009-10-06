/*
 * Created on 17-Jan-2004
 */
package com.ixora.common.typedproperties.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import com.ixora.common.MessageRepository;
import com.ixora.common.messages.Msg;
import com.ixora.common.typedproperties.PropertyEntry;
import com.ixora.common.typedproperties.PropertyEntryNumber;
import com.ixora.common.typedproperties.TypedProperties;
import com.ixora.common.typedproperties.exception.InvalidPropertyValue;
import com.ixora.common.typedproperties.exception.PropertyValueNotSet;
import com.ixora.common.typedproperties.exception.VetoException;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.utils.Utils;

/**
 * Typed properties editor.
 * @author Daniel Moraru
 */
public class TypedPropertiesEditor extends JPanel {
	/** Listener */
	protected Listener fListener;
	/**
	 * Component name. It is used to find the localized
	 * messages for properties.
	 */
	protected String fComponent;
	/** Current configuration */
	protected TypedProperties fProperties;
	/** Event handler */
	private EventHandler fEventHandler;
	/** Table */
	private JTable fTableProps;
	/** Property description area */
	private JTextArea fTextAreaDesc;
	/** Property value cell editor */
	private PropertyTableCellEditor fTableCellEditor;
	/** Property value cell renderer */
	private PropertyTableCellRenderer fTableCellRenderer;
	/** Editable flag */
	private boolean fEditable;

	/**
	 * Listener.
	 */
	public interface Listener {
		/**
		 * Invokde when the original state of any component
		 * has changed.
		 */
		void componentStateChanged();
	}

	/**
	 * Event handler
	 */
	private final class EventHandler implements TableModelListener,
		ListSelectionListener {
		/**
		 * @see javax.swing.event.TableModelListener#tableChanged(javax.swing.event.TableModelEvent)
		 */
		public void tableChanged(TableModelEvent e) {
			if(e.getType() == TableModelEvent.UPDATE) {
				fireComponentStateChanged();
			}
		}
		/**
		 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
		 */
		public void valueChanged(ListSelectionEvent e) {
			handleTableSelectionChanged();
		}
	}

	/**
	 * Constructor.
	 * @param showDescriptionPane whether or not to show
	 * the property description pane
	 */
	public TypedPropertiesEditor(boolean showDescriptionPane) {
		super(new BorderLayout());
		init(showDescriptionPane);
	}

	/**
	 * Initializes this component.
	 * @param showDescriptionPane
	 */
	private void init(boolean showDescriptionPane) {
		JScrollPane scrollPaneProps = UIFactoryMgr.createScrollPane();
		scrollPaneProps.setVerticalScrollBarPolicy(
				javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPaneProps.setHorizontalScrollBarPolicy(
				javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		fEditable = true;
		fTableProps = new JTable();
		fTableProps.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fTableProps.setColumnSelectionAllowed(false);
		fTableProps.getTableHeader().setReorderingAllowed(false);
		scrollPaneProps.setViewportView(fTableProps);
		add(scrollPaneProps, BorderLayout.CENTER);
		fEventHandler = new EventHandler();
		if(showDescriptionPane) {
			JScrollPane scrollPaneDesc = UIFactoryMgr.createScrollPane();
			fTextAreaDesc = UIFactoryMgr.createTextArea();
			fTextAreaDesc.setBackground(getBackground());
			fTextAreaDesc.setEditable(false);
			fTextAreaDesc.setWrapStyleWord(true);
			fTextAreaDesc.setLineWrap(true);
			scrollPaneDesc.setViewportView(fTextAreaDesc);
			scrollPaneDesc.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(
						UIConfiguration.getPanelPadding(), 0, 0, 0),
						scrollPaneDesc.getBorder()));
			scrollPaneDesc.setPreferredSize(new Dimension(200, 50));
			add(scrollPaneDesc, BorderLayout.SOUTH);
			this.fTableProps.getSelectionModel().addListSelectionListener(fEventHandler);
		}
		this.fTableCellRenderer = new PropertyTableCellRenderer();
		this.fTableCellEditor = new PropertyTableCellEditor(this);
	}

	/**
	 * Set the listener.
	 * @param listener
	 */
	public void setListener(Listener listener) {
		if(listener == null) {
			throw new IllegalArgumentException("null listener");
		}
		this.fListener = listener;
	}

	/**
	 * Remove the listener.
	 */
	public void removeListener() {
		this.fListener = null;
	}

	/**
	 * Sets the new properties to edit.
	 * @param component the component name, used to get the
	 * localized names of properties
	 * @param props the properties to work on
	 */
	public void setTypedProperties(String component, TypedProperties props) {
		if(props == null) {
			props = new TypedProperties();
		}
		if(fTextAreaDesc != null) {
			setDescriptionText("", "");
		}
		this.fComponent = component;
		this.fProperties = props;
		Set keys = props.keys();
		int size = keys.size();
		TableModel model = this.fTableProps.getModel();
		if(model != null) {
			model.removeTableModelListener(fEventHandler);
		}
		PropertyTableModel pmodel = new PropertyTableModel(this, fProperties);
		pmodel.setEditable(fEditable);
		pmodel.addTableModelListener(fEventHandler);
		this.fTableCellEditor.setComponentName(component);
		this.fTableCellRenderer.setComponentName(component);
		this.fTableProps.setModel(pmodel);
		this.fTableProps.getColumnModel().getColumn(1).setCellEditor(fTableCellEditor);
		this.fTableProps.getColumnModel().getColumn(1).setCellRenderer(fTableCellRenderer);
	}

	/**
	 * Stop editing.
	 */
	public void stopEditing() {
		if(this.fProperties == null) {
			return;
		}
		fTableCellEditor.stopCellEditing();
	}

	/**
	 * Cancels editing.
	 */
	public void cancelEditing() {
		if(this.fProperties == null) {
			return;
		}
		fTableCellEditor.cancelCellEditing();
	}

	/**
	 * @return the first property that is required ot have a value but it doesn't
	 */
	public PropertyEntry hasMissingValues() {
	    PropertyTableModel model = (PropertyTableModel)fTableProps.getModel();
	    PropertyEntry[] data = model.getEntries();
	    PropertyEntry entry;
	    for(int i = 0; i < data.length; i++) {
            entry = data[i];
            if(entry.isRequired() && entry.getValue() == null) {
                return entry;
            }
        }
	    return null;
	}

	/**
	 * Applies changes to the edited TypedProperties.
	 * @throws InvalidPropertyValue
	 */
	public void applyChanges() throws InvalidPropertyValue, VetoException {
		if(this.fProperties == null) {
			return;
		}

		// stop editing first...
		fTableCellEditor.stopCellEditing();

        this.fProperties.veto();
		// check for required missing values
		PropertyEntry pe = hasMissingValues();
		if(pe != null) {
		    throw new PropertyValueNotSet(
		            getTranslatedMessage(pe.getProperty()));
		}
	}

	/**
	 * Sets all the values to defaults.
	 */
	public void setValuesToDefaults() {
		if(this.fProperties == null) {
			return;
		}
		fTableCellEditor.cancelCellEditing();
		this.fProperties.setDefaults();
		((PropertyTableModel)this.fTableProps.getModel()).fireTableDataChanged();
	}

	/**
	 * Overriden to support read only mode.
	 * @see java.awt.Component#setEnabled(boolean)
	 */
	public void setEnabled(boolean e) {
		fEditable = e;
		TableModel model = fTableProps.getModel();
		if(model instanceof PropertyTableModel) {
			((PropertyTableModel)model).setEditable(e);
		}
	}

	/**
	 * Fires the component state changed event.
	 */
	protected void fireComponentStateChanged() {
		try {
			if(this.fListener != null){
				this.fListener.componentStateChanged();
			}
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * @param e
	 */
	private void handleTableSelectionChanged() {
		try {
			if(fTextAreaDesc == null) {
				return;
			}
			int idx = fTableProps.getSelectedRow();
			if(idx < 0) {
				return;
			}
			PropertyEntry entry = ((PropertyTableModel)fTableProps.getModel())
				.getEntryAt(idx);
			String desc = entry.getProperty() + ".desc";
			String descTranslated = getTranslatedMessage(desc);
			if(descTranslated.equals(desc)) {
				// no description found, so use the
				// translated name of the property
				descTranslated = getTranslatedMessage(entry.getProperty());
			}

			if(entry instanceof PropertyEntryNumber) {
			    PropertyEntryNumber pen = (PropertyEntryNumber)entry;
			    Comparable min = pen.getMin();
			    Comparable max = pen.getMax();
			    if(min != null || max != null) {
			        descTranslated += Utils.getNewLine();
			        descTranslated += MessageRepository.get(
			                Msg.COMMON_TYPEDPROPERTIES_VALUE_IN_RANGE,
			                new String[] {
			                        min != null ? min.toString() : "-",
			                        max != null ? max.toString() : "-"});
			    }
			}
			setDescriptionText(getTranslatedMessage(entry.getProperty()), descTranslated);
		} catch(Exception ex) {
			UIExceptionMgr.userException(ex);
		}
	}

	/**
	 * Sets the description text.
	 * @param desc
	 */
	private void setDescriptionText(String name, String desc) {
		fTextAreaDesc.setText(desc);
		fTextAreaDesc.setToolTipText(
				UIUtils.getMultilineHtmlText("<b>" + name + "</b><br>" + desc,
				UIConfiguration.getMaximumLineLengthForToolTipText()));
		fTextAreaDesc.setCaretPosition(0);
	}

	// package access
	/**
	 * @param msg
	 * @return the translated message
	 */
	String getTranslatedMessage(String msg) {
		return Utils.getTranslatedMessage(fComponent, msg);
	}

	/**
     * @return the properties.
     */
    TypedProperties getProperties() {
        return fProperties;
    }
}
