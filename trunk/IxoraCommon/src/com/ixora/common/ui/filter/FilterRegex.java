/**
 * 15-Feb-2006
 */
package com.ixora.common.ui.filter;

import java.util.regex.Pattern;


/**
 * @author Daniel Moraru
 */
public class FilterRegex extends FilterString {
	private Pattern fFilterPattern;

	/**
	 * XML constructor.
	 */
	public FilterRegex() {
		super();
	}

	/**
	 * @param filter
	 * @param negative
	 */
	public FilterRegex(String filter, boolean negative) {
		super(filter, negative);
		if(filter == null) {
			throw new IllegalArgumentException("Null filter");
		}
		fFilterPattern = Pattern.compile(filter);
	}

	/**
	 * @see com.ixora.common.ui.filter.Filter#accept(java.lang.Object)
	 */
	public boolean accept(Object obj) {
		boolean ret = fFilterPattern.matcher(String.valueOf(obj)).find();
		if(!fNegative) {
			return ret;
		}
		return !ret;
	}
}
