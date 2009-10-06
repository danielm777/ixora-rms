/**
 * 15-Feb-2006
 */
package com.ixora.rms.ui.dataviewboard.logs;

import com.ixora.rms.CounterType;
import com.ixora.rms.ui.dataviewboard.DataViewBoardCounterFilter;

/**
 * @author Daniel Moraru
 */
public class LogBoardCounterFilter implements DataViewBoardCounterFilter {

	/**
	 *
	 */
	public LogBoardCounterFilter() {
		super();
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.DataViewBoardCounterFilter#accept(com.ixora.rms.CounterType)
	 */
	public boolean accept(CounterType ct) {
		if(ct == CounterType.OBJECT) {
			return true;
		}
		return false;
	}
}
