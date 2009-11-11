/*
 * Created on 31-Dec-2003
 */
package com.ixora.rms.ui.dataviewboard.charts.exception;

import com.ixora.rms.exception.RMSException;
import com.ixora.rms.ui.dataviewboard.charts.ChartsBoardComponent;

/**
 * FailedToPlotQuery.
 * @author Daniel Moraru
 */
public abstract class ChartException extends RMSException {
	private static final long serialVersionUID = -6135848372991583278L;

	/**
	 * @param s
	 */
	public ChartException(String s) {
		super(ChartsBoardComponent.NAME, s, true);
	}

	/**
	 * @param msgKey
	 * @param msgTokens
	 */
	public ChartException(String msgKey, String[] msgTokens) {
		super(ChartsBoardComponent.NAME, msgKey, msgTokens);
	}
}
