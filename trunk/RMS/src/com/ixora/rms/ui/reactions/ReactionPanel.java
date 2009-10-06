/**
 * 11-Aug-2005
 */
package com.ixora.rms.ui.reactions;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

import javax.swing.Box;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.ixora.common.ui.TableSorter;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.rms.reactions.ReactionLogRecord;

/**
 * @author Daniel Moraru
 */
public abstract class ReactionPanel extends JPanel {
	protected JTable fTable;
	protected TableSorter fTableModelSorter;
	protected ReactionTableModel fTableModel;
	protected ReactionTableCellRenderer fCellRenderer;
	protected JEditorPane fMessagePane;

	private final class EventHandler implements ListSelectionListener {
		/**
		 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
		 */
		public void valueChanged(ListSelectionEvent e) {
			if(e.getValueIsAdjusting()) {
				return;
			}
			handleRecordSelected(e);
		}
	}

	/**
	 *
	 */
	protected ReactionPanel(ReactionTableModel model) {
		super(new BorderLayout());
		fTableModel = model;
		fTableModelSorter = new TableSorter(fTableModel);
		fTable = UIFactoryMgr.createTable();
		fTable.setModel(fTableModelSorter);
		fTableModelSorter.setTableHeader(fTable.getTableHeader());
        fTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fCellRenderer = new ReactionTableCellRenderer();
        TableColumnModel columnModel = this.fTable.getColumnModel();
        int cols = columnModel.getColumnCount();
        TableColumn col;
        for(int i = 0; i < cols; i++) {
            col = columnModel.getColumn(i);
            col.setCellRenderer(fCellRenderer);
            int w = getColumnWidth(i);
            if(w > 0) {
            	col.setPreferredWidth(w);
            }
        }
		fTable.setShowGrid(false);
		fTable.setIntercellSpacing(new Dimension(0, 0));
		JScrollPane sp = UIFactoryMgr.createScrollPane();
		sp.setViewportView(fTable);
		add(sp, BorderLayout.CENTER);

		fMessagePane = UIFactoryMgr.createHtmlPane();
		sp = UIFactoryMgr.createScrollPane();
		sp.setViewportView(fMessagePane);
		sp.setPreferredSize(new Dimension(50, 100));
		Box box = Box.createVerticalBox();
		box.add(Box.createVerticalStrut(UIConfiguration.getPanelPadding()));
		box.add(sp);
		add(box, BorderLayout.SOUTH);

		fTable.getSelectionModel().addListSelectionListener(new EventHandler());
		fTableModelSorter.setSortingStatus(1, TableSorter.DESCENDING);
	}

	/**
	 * @param lst
	 */
	public void update(List<ReactionLogRecord> lst) {
		int sel = fTable.getSelectedRow();
		fTableModel.updateData(lst);
		if(sel >= 0 && fTableModel.getRowCount() > sel) {
			((DefaultListSelectionModel)fTable.getSelectionModel()).setSelectionInterval(sel, sel);
		}
	}

	/**
	 * @param idx
	 * @return
	 */
	protected int getColumnWidth(int idx) {
        switch(idx) {
        case 0:
        	return 70;
        case 1:
        	return 170;
        case 2:
        	return 70;
        case 3:
        	return 200;
        }
        return -1;
	}

	/**
	 * @param ev
	 */
	protected void handleRecordSelected(ListSelectionEvent ev) {
		try {
			int idx = ev.getFirstIndex();
			if(idx < 0) {
				// try last index
				idx = ev.getLastIndex();
			}
			if(idx >= 0) {
				int midx = fTableModelSorter.modelIndex(idx);
				ReactionLogRecord rec = fTableModel.getRecordAtRow(midx);
				if(rec != null) {
					// TODO localize
					StringBuffer buff = new StringBuffer();
					buff.append("<b>Message:</b> ");
					buff.append(rec.getReactionEvent().getMessage());
					Throwable error = rec.getReactionDeliveryInfo().getError();
					if(error != null) {
						buff.append("<br><b>Delivery error:</b> ");
						buff.append(error.getMessage() != null ? error.getMessage() : error.toString());
					}
					fMessagePane.setText(buff.toString());
				}
			}
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}

	}

}
