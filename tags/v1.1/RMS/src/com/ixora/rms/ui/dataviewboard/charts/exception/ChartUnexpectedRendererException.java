/*
 * Created on 31-Aug-2004
 *
 */
package com.ixora.rms.ui.dataviewboard.charts.exception;

import com.ixora.rms.ui.dataviewboard.charts.messages.Msg;

/**
 * ChartUnexpectedRendererException
 */
public class ChartUnexpectedRendererException extends ChartException {
	private static final long serialVersionUID = 3964763469472862816L;

	/**
	 * Constructor.
	 */
	public ChartUnexpectedRendererException(String type) {
		super(Msg.RMS_UNKNOWN_RENDERER, new String[]{type});
	}

	/**
	 * Constructor.
	 */
	public ChartUnexpectedRendererException(String received, String expected, String range) {
		super(Msg.RMS_INVALID_RENDERER, new String[]{received, expected, range});
	}

}
