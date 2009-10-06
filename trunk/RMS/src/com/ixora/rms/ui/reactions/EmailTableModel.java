/**
 * 11-Aug-2005
 */
package com.ixora.rms.ui.reactions;

import java.util.List;

import com.ixora.rms.reactions.ReactionDeliveryInfoEmail;
import com.ixora.rms.reactions.ReactionDeliveryType;
import com.ixora.rms.reactions.ReactionLogRecord;

final class EmailTableModel extends ReactionTableModel {

	public EmailTableModel(List<ReactionLogRecord> lst) {
		super(lst);
		fColumns.add("To");
		fColumns.add("Server");
	}


	/**
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		if(columnIndex < 5) {
			return super.getValueAt(rowIndex, columnIndex);
		}
		ReactionLogRecord rec = fData.get(rowIndex);
		ReactionDeliveryInfoEmail del = (ReactionDeliveryInfoEmail)rec.getReactionDeliveryInfo();
		switch(columnIndex) {
		case 5: // To
			return del.getTo();
		case 6: // Server
			return del.getServer() + "(" + del.getPort() + ")";
		}
		return null;
	}

	/**
	 * @see com.ixora.rms.ui.reactions.ReactionTableModel#acceptRecord(com.ixora.rms.reactions.ReactionLogRecord)
	 */
	protected boolean acceptRecord(ReactionLogRecord rec) {
		return rec.getReactionDeliveryType() == ReactionDeliveryType.EMAIL;
	}
}