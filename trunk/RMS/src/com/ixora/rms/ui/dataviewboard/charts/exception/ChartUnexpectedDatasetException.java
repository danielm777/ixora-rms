/*
 * Created on 31-Aug-2004
 *
 */
package com.ixora.rms.ui.dataviewboard.charts.exception;

import com.ixora.rms.ui.dataviewboard.charts.messages.Msg;

/**
 * ChartUnexpectedDatasetException
 */
public class ChartUnexpectedDatasetException extends ChartException {
// invalid dataset {ds}, {expected_ds} expected
	/**
	 * Constructor.
	 */
	public ChartUnexpectedDatasetException(String received, String expected) {
		super(Msg.RMS_INVALID_DATASET, new String[]{received, expected});
	}
}
