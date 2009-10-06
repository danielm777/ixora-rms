/*
 * Created on 13-May-2005
 */
package com.ixora.rms.ui.dataviewboard.charts.exception;

import com.ixora.rms.ui.dataviewboard.charts.messages.Msg;

/**
 * ChartNoRendererException
 */
public class ChartNoRendererException extends ChartException {
	/**
	 * Constructor.
	 */
	public ChartNoRendererException() {
		super(Msg.RMS_NO_RENDERER);
	}
}
