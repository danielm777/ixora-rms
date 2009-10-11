/**
 * 12-Aug-2005
 */
package com.ixora.rms.ui.reactions;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.ixora.common.utils.Utils;
import com.ixora.rms.reactions.ReactionDeliveryInfo;
import com.ixora.rms.reactions.ReactionLogRecord;

/**
 * @author Daniel Moraru
 */
abstract class ReactionTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -7306297573138744829L;
	private static final int MAX_RECORDS = 1000;
	protected List<String> fColumns;
	protected List<ReactionLogRecord> fData;

	public ReactionTableModel(List<ReactionLogRecord> lst) {
		super();
		fColumns = new LinkedList<String>();
		fColumns.add("Severity");
		fColumns.add("Date");
		fColumns.add("State");
		fColumns.add("Message");
		fColumns.add("Error");
		updateData(lst);
	}

	/**
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	public String getColumnName(int column) {
		return fColumns.get(column);
	}

	/**
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return fData.size();
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return fColumns.size();
	}

	/**
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		ReactionLogRecord rec = fData.get(rowIndex);
		ReactionDeliveryInfo del = rec.getReactionDeliveryInfo();
		switch(columnIndex) {
		case 0: // Severity
			return rec.getReactionEvent().getSeverity();
		case 1: // Date
			return new Date(rec.getReactionEvent().getTimestamp());
		case 2: // State
			return rec.getStates().get(rec.getStates().size() - 1).getState();
		case 3: // Message
			return rec.getReactionEvent().getMessage();
		case 4: // Error
			return del.getError() == null ? "" : del.getError().getMessage();
		}
		return null;
	}

	/**
	 * @param type
	 * @return
	 */
	protected abstract boolean acceptRecord(ReactionLogRecord rec);

	/**
	 * @param lst
	 */
	protected void updateData(List<ReactionLogRecord> lst) {
		fData = new LinkedList<ReactionLogRecord>();
		if(!Utils.isEmptyCollection(lst)) {
			for(ReactionLogRecord rec : lst) {
				if(acceptRecord(rec)) {
					if(fData.size() >= MAX_RECORDS) {
						fData.remove(0);
					}
					fData.add(rec);
				}
			}
		}
		fireTableDataChanged();
	}

	/**
	 * @param midx
	 */
	public ReactionLogRecord getRecordAtRow(int midx) {
		return fData.get(midx);
	}

}
