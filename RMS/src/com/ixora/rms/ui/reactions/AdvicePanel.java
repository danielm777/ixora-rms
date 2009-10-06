/**
 * 11-Aug-2005
 */
package com.ixora.rms.ui.reactions;

import java.util.List;

import com.ixora.common.ui.TableSorter;
import com.ixora.rms.reactions.ReactionLogRecord;

/**
 * @author Daniel Moraru
 */
public final class AdvicePanel extends ReactionPanel {

	/**
	 *
	 */
	public AdvicePanel(List<ReactionLogRecord> lst) {
		super(new AdviceTableModel(lst));
		fTableModelSorter.setSortingStatus(1, TableSorter.NOT_SORTED);
		fTableModelSorter.setSortingStatus(0, TableSorter.DESCENDING);

	}

	/**
	 * @see com.ixora.rms.ui.reactions.ReactionPanel#getColumnWidth(int)
	 */
	protected int getColumnWidth(int idx) {
        switch(idx) {
        case 0:
        	return 170;
        case 1:
        	return 70;
        case 2:
        	return 400;
        }
        return -1;
	}
}
