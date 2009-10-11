/*
 * Created on 13-May-2005
 */
package com.ixora.rms.ui.dataviewboard.charts.exception;

import com.ixora.rms.ui.dataviewboard.charts.messages.Msg;

/**
 * ChartCannotMixRenderersException
 * If multiple renderers are used on the same plot, they all have to
 * be of the same class (Category or XY), mixing is not allowed
 */
public class ChartCannotMixRenderersException extends ChartException {
	private static final long serialVersionUID = 2615702126694899404L;

	/**
	 * Constructor.
	 */
	public ChartCannotMixRenderersException() {
		super(Msg.RMS_MULTIPLE_RENDERERS_CANNOT_MIX);
	}
}
