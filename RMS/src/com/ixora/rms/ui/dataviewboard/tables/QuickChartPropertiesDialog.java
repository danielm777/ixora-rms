/*
 * Created on Nov 8, 2004
 */
package com.ixora.rms.ui.dataviewboard.tables;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.ixora.common.MessageRepository;
import com.ixora.common.ui.AppDialog;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.actions.ActionApply;
import com.ixora.common.ui.actions.ActionCancel;
import com.ixora.common.ui.actions.ActionDeselectAll;
import com.ixora.common.ui.forms.FormPanel;
import com.ixora.rms.ui.dataviewboard.charts.ChartStyle;
import com.ixora.rms.ui.dataviewboard.tables.messages.Msg;

/**
 * @author Daniel Moraru
 */
public final class QuickChartPropertiesDialog extends AppDialog {
	private FormPanel formPanel;
	private JComboBox jComboBoxStyles;
	private ItemsTableModel itemsTableModel;

	private ChartStyle selectedChartStyle;
	private String[] selectedItems;

	/**
	 * ItemsTableModel.
	 */
	private static final class ItemsTableModel extends DefaultTableModel {
		/**
		 * Constructor.
		 * @param data
		 */
		public ItemsTableModel(Object[][] data) {
			super(data, new Object[] {
				"",
				MessageRepository.get(TablesBoardComponent.NAME, Msg.TABLES_QUICK_CHART_PROPERTIES_COL2),
				MessageRepository.get(TablesBoardComponent.NAME, Msg.TABLES_QUICK_CHART_PROPERTIES_COL3)
			});
		}
		/**
		 * @see javax.swing.table.TableModel#getColumnClass(int)
		 */
		public Class getColumnClass(int columnIndex) {
			return columnIndex == 0 ? Boolean.class : String.class;
		}
		/**
		 * @see javax.swing.table.TableModel#isCellEditable(int, int)
		 */
		public boolean isCellEditable(int row, int column) {
			return column == 0 ? true : false;
		}

		/**
		 * @return
		 */
		public String[] getSelectedItems() {
			List ret = new LinkedList();
			Vector v;
			for(Iterator iter = this.dataVector.iterator(); iter.hasNext();) {
				v = (Vector)iter.next();
				if(((Boolean)v.get(0)).booleanValue()) {
					ret.add(v.get(2));
				}
			}
			return (String[])ret.toArray(new String[ret.size()]);
		}

		/**
		 * @param select
		 */
		public void setSelectedAll(boolean select) {
			for(Iterator iter = this.dataVector.iterator(); iter.hasNext();) {
				Vector v = (Vector)iter.next();
				v.set(0, Boolean.valueOf(select));
			}
			fireTableDataChanged();
		}
	}

	/**
	 * Constructor.
	 * @param parent the parent of this dialog
	 * @param styles chart styles
	 * @param items available items
	 * @param itemsDisplayNames the displayable names for items
	 */
	public QuickChartPropertiesDialog(Frame parent, ChartStyle[] styles, String[] items, String[]itemsDisplayNames) {
		super(parent, VERTICAL);
		if(items.length != itemsDisplayNames.length) {
			throw new IllegalArgumentException("mismatch length for paramters items and itemsDisplayNames");
		}
		setModal(true);
		setTitle(MessageRepository.get(TablesBoardComponent.NAME,
				Msg.TABLES_TITLE_QUICK_CHART_PROPERTIES));
		setPreferredSize(new Dimension(500, 300));
		formPanel = new FormPanel(FormPanel.VERTICAL2);
		jComboBoxStyles = UIFactoryMgr.createComboBox();

		// don't allow it to stretch on the vertical
		jComboBoxStyles.setMaximumSize(jComboBoxStyles.getPreferredSize());

		jComboBoxStyles.setModel(new DefaultComboBoxModel(styles));

		// items panel
		Object[][] data = new Object[items.length][3];
		for(int i = 0; i < data.length; i++) {
			data[i][0] = Boolean.TRUE;
			data[i][1] = itemsDisplayNames[i];
			data[i][2] = items[i];
		}
		itemsTableModel = new ItemsTableModel(data);
		JTable table = new JTable(itemsTableModel);
		table.getColumnModel().getColumn(0).setMaxWidth(25);
		table.getColumnModel().getColumn(0).setPreferredWidth(25);
		table.getColumnModel().getColumn(1).setMaxWidth(250);
		table.getColumnModel().getColumn(1).setPreferredWidth(180);
		JScrollPane sp = UIFactoryMgr.createScrollPane();
		sp.setViewportView(table);

		formPanel.addPairs(
				new String[] {
						MessageRepository.get(TablesBoardComponent.NAME, Msg.TABLES_QUICK_CHART_STYLES),
						MessageRepository.get(TablesBoardComponent.NAME, Msg.TABLES_QUICK_CHART_ITEMS)
					},
				new Component[] {
						jComboBoxStyles,
						sp
				}
		);

		buildContentPane();
	}

	/**
	 * @return
	 */
	public QuickChartProperties getQuickChartProperties() {
		if(selectedChartStyle == null || selectedItems == null || selectedItems.length == 0) {
			return null;
		}
		return new QuickChartProperties(selectedChartStyle, selectedItems);
	}

	/**
	 * @see com.ixora.common.ui.AppDialog#getDisplayPanels()
	 */
	protected Component[] getDisplayPanels() {
		return new Component[] {formPanel};
	}

	/**
	 * @see com.ixora.common.ui.AppDialog#getButtons()
	 */
	protected JButton[] getButtons() {
		return new JButton[] {
			new JButton(new ActionApply() {
				public void actionPerformed(ActionEvent e) {
					handleApply();
				}}),
			new JButton(new ActionDeselectAll() {
				public void actionPerformed(ActionEvent e) {
					handleDeselectAll();
				}}),
			new JButton(new ActionCancel() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			})};
	}

	/**
	 * Deselects all items.
	 */
	private void handleDeselectAll() {
		try {
			this.itemsTableModel.setSelectedAll(false);
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Collects return data and closes this dialog.
	 */
	private void handleApply() {
		try {
			this.selectedChartStyle = (ChartStyle)jComboBoxStyles.getSelectedItem();
			this.selectedItems = this.itemsTableModel.getSelectedItems();
			this.dispose();
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}
}
