/**
 * 20-Feb-2006
 */
package com.ixora.rms.ui.dataviewboard.logs;

import java.awt.Color;
import java.text.SimpleDateFormat;

import com.ixora.rms.ui.dataviewboard.utils.TableBasedControlFormatter;

/**
 * @author Daniel Moraru
 */
public class LogControlFormatter extends TableBasedControlFormatter {

	/**
	 * @param defaultNumberFormat
	 */
	public LogControlFormatter(Color backgroundColor, Color stripe,
			int maxToolTipLength,
			String defaultNumberFormat) {
		super(backgroundColor, stripe, maxToolTipLength, defaultNumberFormat);
		fDefaultDateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss.SSS");
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.utils.TableBasedControlFormatter#format(java.lang.Object, int, int, java.awt.Color)
	 */
	public FormattingData format(Object val, int row, int col, Color bgColor) {
		FormattingData fd = super.format(val, row, col, bgColor);
		if(col == 9) {
			fd.fText = "";
			fd.fToolTip = "";
		}
		return fd;
	}
}
