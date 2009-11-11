/**
 * 11-Aug-2005
 */
package com.ixora.rms.ui.reactions;

import java.util.List;

import com.ixora.rms.reactions.ReactionLogRecord;

/**
 * @author Daniel Moraru
 */
public final class JobPanel extends ReactionPanel {
	private static final long serialVersionUID = -6790424212968549657L;

	/**
	 *
	 */
	public JobPanel(List<ReactionLogRecord> lst) {
		super(new JobTableModel(lst));
	}
}
