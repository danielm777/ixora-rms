/**
 * 24-Sep-2005
 */
package com.ixora.rms.ui.dataviewboard.utils;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.ixora.common.ui.TextComponentHandler;
import com.ixora.common.ui.UIFactory;
import com.ixora.common.ui.UIFactoryMgr;

/**
 * @author Daniel Moraru
 */
public class TableBasedControlCellEditor extends DefaultCellEditor {
	private TableBasedControlFormatter fFormatter;

	/**
	 *
	 */
	public TableBasedControlCellEditor(TableBasedControlFormatter formatter) {
		super(UIFactoryMgr.createTextField());
		fFormatter = formatter;
		// disable editing menu items from the context popup menu
		TextComponentHandler handler = (TextComponentHandler)editorComponent.getClientProperty(UIFactory.TEXTC_COMPONENT_HANDLER);
		if(handler != null) {
			JPopupMenu menu = handler.getPopupMenu();
			menu.remove(handler.getMenuItemCut());
			menu.remove(handler.getMenuItemPaste());
			menu.remove(handler.getMenuItemRedo());
			menu.remove(handler.getMenuItemUndo());
		}
		((JTextField)editorComponent).setEditable(false);
	}

	/**
	 * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
	 */
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		return super.getTableCellEditorComponent(table,
				fFormatter.format(value, row, column, editorComponent.getBackground()).fText,
				isSelected, row, column);
	}


}
