/*
 * Created on 04-Dec-2004
 */
package com.ixora.common.typedproperties.ui.list;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.ixora.common.typedproperties.TypedProperties;
import com.ixora.common.typedproperties.exception.VetoException;
import com.ixora.common.typedproperties.ui.PropertyTableCellEditor;
import com.ixora.common.typedproperties.ui.PropertyTableCellRenderer;
import com.ixora.common.ui.LayoutUtils;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.actions.ActionDelete;
import com.ixora.common.ui.actions.ActionDown;
import com.ixora.common.ui.actions.ActionNew;
import com.ixora.common.ui.actions.ActionUp;

/**
 * An editor for a list of TypedProperties.
 * @author Daniel Moraru
 */
public class TypedPropertiesListEditor extends JPanel {
	public static final int BUTTON_UP_DOWN 		= 1 << 1;
	public static final int BUTTON_NEW_DELETE	= 1 << 2;
	public static final int BUTTON_ALL 			= 1 << 3;
	public static final int BUTTON_NONE			= 1 << 4;

	/** Editor table */
	private JTable fTable;
	/** Model */
	private PropertyListTableModel fModel;
	/** Property value cell editor */
	private PropertyTableCellEditor fTableCellEditor;
	/** Property value cell renderer */
	private PropertyTableCellRenderer fTableCellRenderer;

	private JButton fButtonUp;
	private JButton fButtonDown;
	private JButton fButtonNew;
	private JButton fButtonDelete;

	private final class EventHandler implements ListSelectionListener {
		/**
		 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
		 */
		public void valueChanged(ListSelectionEvent e) {
			if(!e.getValueIsAdjusting() && e.getSource() == fTable.getSelectionModel()) {
				handleItemSelected(e);
			}
		}
	}

	/**
	 * Constructor.
	 * @param parent
	 * @param prototype object used to setup the columns of the editor table
	 * @param component the name of the component owning this editor
	 * @param properties initial values
	 * @param buttons
	 */
	public TypedPropertiesListEditor(TypedProperties prototype,
			String component,
			List<? extends TypedProperties> properties,
			int buttons) {
		this(new PropertyListTableModel(prototype, component, properties), buttons);
	}

	/**
	 * Constructor.
	 * @param model
	 * @param showButtons
	 */
	public TypedPropertiesListEditor(PropertyListTableModel model, int buttons) {
		setLayout(new BorderLayout());
		JScrollPane scrollPaneProps = UIFactoryMgr.createScrollPane();
		scrollPaneProps.setVerticalScrollBarPolicy(
				javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPaneProps.setHorizontalScrollBarPolicy(
				javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		fModel = model;
		fTable = new JTable(fModel);
		fTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fTable.setColumnSelectionAllowed(false);
		fTable.getTableHeader().setReorderingAllowed(false);
		scrollPaneProps.setViewportView(fTable);
		add(scrollPaneProps, BorderLayout.CENTER);

		this.fTableCellRenderer = new PropertyTableCellRenderer();
		this.fTableCellEditor = new PropertyTableCellEditor(this);

		int columns = fModel.getColumnCount();
		for(int i = 0; i < columns; i++) {
			fTable.getColumnModel().getColumn(i).setCellEditor(fTableCellEditor);
			fTable.getColumnModel().getColumn(i).setCellRenderer(fTableCellRenderer);
		}
		List<JButton> lst = new LinkedList<JButton>();
		if((BUTTON_NONE & buttons) == 0) {
			fTable.getSelectionModel().addListSelectionListener(new EventHandler());
			if((BUTTON_ALL & buttons) != 0 || (BUTTON_UP_DOWN & buttons) != 0) {
				fButtonUp = UIFactoryMgr.createButton(new ActionUp() {
					public void actionPerformed(ActionEvent e) {
						handleUp();
					}
				});
				fButtonDown = UIFactoryMgr.createButton(new ActionDown() {
					public void actionPerformed(ActionEvent e) {
						handleDown();
					}
				});
				fButtonUp.setEnabled(false);
				fButtonDown.setEnabled(false);
				lst.add(fButtonUp);
				lst.add(fButtonDown);
			}
			if((BUTTON_ALL & buttons) != 0 || (BUTTON_NEW_DELETE & buttons) != 0) {
				fButtonNew = UIFactoryMgr.createButton(new ActionNew() {
					public void actionPerformed(ActionEvent e) {
						handleNew();
					}
				});
				fButtonDelete = UIFactoryMgr.createButton(new ActionDelete() {
					public void actionPerformed(ActionEvent e) {
						handleDelete();
					}
				});
				fButtonDelete.setEnabled(false);
				lst.add(fButtonNew);
				lst.add(fButtonDelete);
			}
			LayoutUtils.alignVerticallyInPanelWithBorderLayout(
					this,
					lst.toArray(new Component[lst.size()]),
					BorderLayout.EAST);
		}
	}

	/**
	 * @see java.awt.Component#setEnabled(boolean)
	 */
	public void setEnabled(boolean e) {
		fTable.setEnabled(e);
		if(fButtonDelete != null) {
			fButtonDelete.setEnabled(e);
		}
		if(fButtonNew != null) {
			fButtonNew.setEnabled(e);
		}
		if(fButtonUp != null) {
			fButtonUp.setEnabled(e);
		}
		if(fButtonDown != null) {
			fButtonDown.setEnabled(e);
		}
	}

	/**
	 * Stop editing.
	 */
	public void stopEditing() {
		fTableCellEditor.stopCellEditing();
	}

	/**
	 * @return the model.
	 */
	public PropertyListTableModel getModel() {
		return fModel;
	}

	/**
	 * @return
	 */
	public PropertyTableCellEditor getCellEditor() {
		return fTableCellEditor;
	}

	/**
	 * @return the underlying table
	 */
	public JTable getTable() {
		return this.fTable;
	}

	/**
	 * Moves up a column.
	 */
	protected void handleUp() {
		try {
			int row = fTable.getEditingRow();
			int count = fTable.getRowCount();
			int col = -1;
			if(row >= 0 && row > 0) {
				col = fTable.getEditingColumn();
				fTable.getCellEditor().stopCellEditing();
				fModel.movePropertyUp(row);
				row--;
				if(col >= 0) {
					fTable.editCellAt(row, col);
				}
				fTable.setRowSelectionInterval(row, row);
			} else {
				row = fTable.getSelectedRow();
				if(row >= 0 && row > 0) {
					fModel.movePropertyUp(row);
					row--;
					fTable.setRowSelectionInterval(row, row);
				}
			}
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Moves downn a column.
	 */
	protected void handleDown() {
		try {
			int row = fTable.getEditingRow();
			int count = fTable.getRowCount();
			int col = -1;
			if(row >= 0 && row < count - 1) {
				col = fTable.getEditingColumn();
				fTable.getCellEditor().stopCellEditing();
				fModel.movePropertyDown(row);
				row++;
				if(col > 0) {
					fTable.editCellAt(row, col);
					fTable.setRowSelectionInterval(row, row);
				}
			} else {
				row = fTable.getSelectedRow();
				if(row >= 0 && row < count - 1) {
					fModel.movePropertyDown(row);
					row++;
					fTable.setRowSelectionInterval(row, row);
				}
			}
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Adds a column.
	 */
	protected void handleNew() {
		try {
			fModel.addNewProperty();
		} catch(VetoException e) {
			; // cancell silently
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Removes a column.
	 */
	protected void handleDelete() {
		try {
			int row = fTable.getEditingRow();
			if(row >= 0) {
				int count = fTable.getRowCount();
				int col = -1;
				if(count > 0) {
					col = fTable.getEditingColumn();
					fTable.getCellEditor().stopCellEditing();
				} else {
					fTable.getCellEditor().stopCellEditing();
				}
				fModel.removeProperty(row);
				if(col >= 0) {
					fTable.editCellAt(row == 0 ? 0 : --row, col);
				}
			}
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * @param ev
	 */
	private void handleItemSelected(ListSelectionEvent ev) {
		try {
			if(fButtonDelete != null) {
				fButtonDelete.setEnabled(true);
			}
			if(fButtonUp != null) {
				fButtonUp.setEnabled(true);
			}
			if(fButtonDown != null) {
				fButtonDown.setEnabled(true);
			}
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}
}
