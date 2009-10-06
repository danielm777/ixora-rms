/**
 * 09-Jan-2006
 */
package com.ixora.common.typedproperties;

import java.text.DecimalFormat;
import java.text.ParsePosition;

/**
 * Class to allow the number formatter to accept empty values.
 * @author Daniel Moraru
 */
public class AllowNullNumberFormat extends DecimalFormat {
	public static Number NULL_FLOAT = new Float(Double.NaN);
	public static Number NULL_INTEGER = new Integer(999999999);

	private Number fType;

	/**
	 * @param type
	 */
	public AllowNullNumberFormat(Number type) {
		super();
		fType = type;
	}

	/**
	 * @see java.text.NumberFormat#parse(java.lang.String, java.text.ParsePosition)
	 */
	public Number parse(String source, ParsePosition parsePosition) {
		if("".equals(source)) {
			parsePosition.setIndex(1);
			return fType == NULL_FLOAT ? NULL_FLOAT : NULL_INTEGER;
		}
		return super.parse(source, parsePosition);
	}

	/**
	 * @param value
	 * @return
	 */
	public static boolean isNull(Object value) {
		return NULL_FLOAT.equals(value) || NULL_INTEGER.equals(value);
	}
}
