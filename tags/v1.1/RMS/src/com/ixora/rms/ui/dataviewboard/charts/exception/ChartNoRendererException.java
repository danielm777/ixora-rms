/*
 * Created on 13-May-2005
 */
package com.ixora.rms.ui.dataviewboard.charts.exception;

import com.ixora.rms.ui.dataviewboard.charts.messages.Msg;

/**
 * ChartNoRendererException
 */
public class ChartNoRendererException extends ChartException {
	private static final long serialVersionUID = -1243998668425785511L;

	/**
	 * Constructor.
	 */
	public ChartNoRendererException() {
		super(Msg.RMS_NO_RENDERER);
	}
}
