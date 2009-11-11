/**
 * 19-Feb-2006
 */
package com.ixora.rms.ui.dataviewboard.utils;

import com.ixora.common.ui.filter.FilterNumber;

/**
 * @author Daniel Moraru
 */
public class FilterNumericValueDeltaHandler extends FilterNumber {
	private static final long serialVersionUID = 5966899031598921619L;

	/**
	 *
	 */
	public FilterNumericValueDeltaHandler() {
		super();
	}

	/**
	 *
	 */
	public FilterNumericValueDeltaHandler(Number low, Number high) {
		super(low, high);
	}

	/**
	 * @see com.ixora.common.ui.filter.Filter#accept(java.lang.Object)
	 */
	public boolean accept(Object obj) {
		if(obj == null) {
			return false;
		}
		if(!(obj instanceof NumericValueDeltaHandler)) {
			return true;
		}
		NumericValueDeltaHandler ndh = (NumericValueDeltaHandler)obj;
		return super.accept(ndh.getValue().getDouble());
	}
}
