/**
 * 11-Aug-2005
 */
package com.ixora.rms.ui.reactions;

import java.util.List;

import com.ixora.rms.reactions.ReactionDeliveryInfoJob;
import com.ixora.rms.reactions.ReactionDeliveryType;
import com.ixora.rms.reactions.ReactionLogRecord;

final class JobTableModel extends ReactionTableModel {

	public JobTableModel(List<ReactionLogRecord> lst) {
		super(lst);
		fColumns.add("Job Library ID");
	}


	/**
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		if(columnIndex < 5) {
			return super.getValueAt(rowIndex, columnIndex);
		}
		ReactionLogRecord rec = fData.get(rowIndex);
		ReactionDeliveryInfoJob del = (ReactionDeliveryInfoJob)rec.getReactionDeliveryInfo();
		switch(columnIndex) {
		case 5: // Job lib id
			return del.getJobLibrayId();
		}
		return null;
	}

	/**
	 * @see com.ixora.rms.ui.reactions.ReactionTableModel#acceptRecord(com.ixora.rms.reactions.ReactionLogRecord)
	 */
	protected boolean acceptRecord(ReactionLogRecord rec) {
		return rec.getReactionDeliveryType() == ReactionDeliveryType.JOB;
	}
}