/**
 * 11-Aug-2005
 */
package com.ixora.rms.ui.reactions;

import java.util.Date;
import java.util.List;

import com.ixora.rms.reactions.ReactionDeliveryInfo;
import com.ixora.rms.reactions.ReactionDeliveryType;
import com.ixora.rms.reactions.ReactionLogRecord;

final class AdviceTableModel extends ReactionTableModel {

	public AdviceTableModel(List<ReactionLogRecord> lst) {
		super(lst);
		fColumns.remove("Severity");
		fColumns.remove("Error");
	}

	/**
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		ReactionLogRecord rec = fData.get(rowIndex);
		ReactionDeliveryInfo del = rec.getReactionDeliveryInfo();
		switch(columnIndex) {
		case 0: // Date
			return new Date(rec.getReactionEvent().getTimestamp());
		case 1: // State
			return rec.getStates().get(rec.getStates().size() - 1).getState();
		case 2: // Message
			return rec.getReactionEvent().getMessage();
		}
		return null;
	}

	/**
	 * @see com.ixora.rms.ui.reactions.ReactionTableModel#acceptRecord(com.ixora.rms.reactions.ReactionLogRecord)
	 */
	protected boolean acceptRecord(ReactionLogRecord rec) {
		return rec.getReactionDeliveryType() == ReactionDeliveryType.ADVICE;
	}
}