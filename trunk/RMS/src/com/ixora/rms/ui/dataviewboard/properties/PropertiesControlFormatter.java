/**
 * 20-Feb-2006
 */
package com.ixora.rms.ui.dataviewboard.properties;

import java.awt.Color;

import com.ixora.rms.ui.dataviewboard.utils.TableBasedControlFormatter;

/**
 * @author Daniel Moraru
 */
public class PropertiesControlFormatter extends TableBasedControlFormatter {

	/**
	 * @param defaultNumberFormat
	 */
	public PropertiesControlFormatter(Color backgroundColor, Color stripe,
			int maxToolTipLength,
			String defaultNumberFormat) {
		super(backgroundColor, stripe, maxToolTipLength, defaultNumberFormat);
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.utils.TableBasedControlFormatter#format(java.lang.Object, int, int, java.awt.Color)
	 */
	public FormattingData format(Object val, int row, int col, Color bgColor) {
		FormattingData fd = super.format(val, row, col, bgColor);
		if(col == 2) {
			fd.fText = "";
			fd.fToolTip = "";
		}
		return fd;
	}
}
