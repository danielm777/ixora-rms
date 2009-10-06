/**
 * 07-Feb-2006
 */
package com.ixora.rms.ui.dataviewboard.logs.definitions;

import com.ixora.rms.dataengine.definitions.QueryDef;
import com.ixora.rms.repository.DataView;
import com.ixora.rms.ui.dataviewboard.logs.LogBoard;

/**
 * @author Daniel Moraru
 */
public class LogDef extends DataView {

	/**
	 *
	 */
	public LogDef() {
		super();
	}

	/**
	 * @param viewname
	 * @param description
	 * @param query
	 * @param author
	 */
	public LogDef(String viewname, String description, QueryDef query,
			String author) {
		super(viewname, description, query, author);
	}

	/**
	 * @see com.ixora.rms.repository.DataView#getBoardClass()
	 */
	public String getBoardClass() {
		return LogBoard.class.getName();
	}
}
