/*
 * Created on 31-Aug-2004
 *
 */
package com.ixora.rms.ui.dataviewboard.charts.exception;

import com.ixora.rms.ui.dataviewboard.charts.messages.Msg;

/**
 * ChartMultipleRenderersException
 */
public class ChartMultipleRenderersException extends ChartException {
	private static final long serialVersionUID = -2557533180463782392L;

	/**
	 * Constructor.
	 */
	public ChartMultipleRenderersException() {
		super(Msg.RMS_MULTIPLE_RENDERERS_FOR_CATEGORY_AND_XY_ONLY);
	}
}
