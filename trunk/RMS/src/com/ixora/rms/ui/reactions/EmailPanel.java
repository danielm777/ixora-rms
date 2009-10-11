/**
 * 11-Aug-2005
 */
package com.ixora.rms.ui.reactions;

import java.util.List;

import com.ixora.rms.reactions.ReactionLogRecord;

/**
 * @author Daniel Moraru
 */
public final class EmailPanel extends ReactionPanel {
	private static final long serialVersionUID = 7688348206843900058L;

	/**
	 *
	 */
	public EmailPanel(List<ReactionLogRecord> lst) {
		super(new EmailTableModel(lst));
	}
}
