/**
 * 09-Jan-2006
 */
package com.ixora.common.typedproperties.ui;

import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.text.NumberFormatter;

import com.ixora.common.typedproperties.AllowNullNumberFormat;

/**
 * @author Daniel Moraru
 */
public class AllowNullNumberFormatter extends NumberFormatter {
	private static final long serialVersionUID = -8518804248817558020L;

	/**
	 *
	 */
	public AllowNullNumberFormatter() {
		super();
	}

	/**
	 * @param format
	 */
	public AllowNullNumberFormatter(NumberFormat format) {
		super(format);
	}

	/**
	 * @see javax.swing.JFormattedTextField$AbstractFormatter#stringToValue(java.lang.String)
	 */
	public Object stringToValue(String text) throws ParseException {
		if("".equals(text)) {
			if(getValueClass().equals(Float.class)) {
				return AllowNullNumberFormat.NULL_FLOAT;
			} else if(getValueClass().equals(Integer.class)) {
				return AllowNullNumberFormat.NULL_INTEGER;
			}
		}
		return super.stringToValue(text);
	}

	/**
	 * @see javax.swing.JFormattedTextField$AbstractFormatter#valueToString(java.lang.Object)
	 */
	public String valueToString(Object value) throws ParseException {
		if(AllowNullNumberFormat.isNull(value)) {
			return "";
		}
		return super.valueToString(value);
	}


}
