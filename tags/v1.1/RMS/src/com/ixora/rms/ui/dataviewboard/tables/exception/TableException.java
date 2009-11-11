/*
 * Created on 31-Dec-2003
 */
package com.ixora.rms.ui.dataviewboard.tables.exception;

import com.ixora.rms.exception.RMSException;
import com.ixora.rms.ui.dataviewboard.tables.TablesBoardComponent;

/**
 * FailedToPlotQuery.
 * @author Daniel Moraru
 */
public abstract class TableException extends RMSException {
	private static final long serialVersionUID = 3008231265182478771L;

	/**
	 * @param s
	 */
	public TableException(String s) {
		super(TablesBoardComponent.NAME, s, true);
	}

	/**
	 * @param msgKey
	 * @param msgTokens
	 */
	public TableException(String msgKey, String[] msgTokens) {
		super(TablesBoardComponent.NAME, msgKey, msgTokens);
	}
}
