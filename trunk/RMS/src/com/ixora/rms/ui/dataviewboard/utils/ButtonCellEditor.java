/**
 * 24-Sep-2005
 */
package com.ixora.rms.ui.dataviewboard.utils;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.UIUtils;

/**
 * @author Daniel Moraru
 */
public class ButtonCellEditor extends AbstractCellEditor implements TableCellEditor {
	private JButton fButton;
	private Frame fFrame;
	private TableBasedControlFormatter fFormatter;
	private TableModel fTableModel;
	private int fRow;
	private int fColumn;

	/**
	 * @param formatter
	 * @param frame
	 * @param tableModel
	 */
	public ButtonCellEditor(TableBasedControlFormatter formatter,
			Frame frame, TableModel tableModel) {
		super();
		fFormatter = formatter;
		fFrame = frame;
		fTableModel = tableModel;
		fButton = UIFactoryMgr.createButton();
		fButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				handleShowValueDialog();
			}});
		fButton.setPreferredSize(new Dimension(16, 16));
		fButton.setText("+");
	}

	/**
	 *
	 */
	private void handleShowValueDialog() {
		try {
			JDialog dlg = new LargeValueViewerDialog(fFrame, (String)getCellEditorValue(), "Value");
			UIUtils.centerDialogAndShow(fFrame, dlg);
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
	 */
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		fRow = row;
		fColumn = column;
		return fButton;
	}

	/**
	 * Subclasses can return here the column where to read the value from.
	 * @param column
	 * @return
	 */
	protected int mapColumnForValue(int column) {
		return column;
	}

	/**
	 * @see javax.swing.CellEditor#getCellEditorValue()
	 */
	public Object getCellEditorValue() {
		int realCol = mapColumnForValue(fColumn);
		return fFormatter.format(
				fTableModel.getValueAt(fRow, realCol),
				fRow, realCol, fButton.getBackground()).fText;
	}
}
