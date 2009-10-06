/*
 * Created on 19-Feb-2005
 */
package com.ixora.rms.ui.dataviewboard.charts;

import com.ixora.rms.CounterType;
import com.ixora.rms.ui.dataviewboard.DataViewBoardCounterFilter;

/**
 * @author Daniel Moraru
 */
public final class ChartsBoardCounterFilter implements DataViewBoardCounterFilter {

	/**
	 * Constructor.
	 */
	public ChartsBoardCounterFilter() {
		super();
	}

    /**
     * @see com.ixora.rms.ui.dataviewboard.DataViewBoardCounterFilter#accept(com.ixora.rms.CounterType)
     */
    public boolean accept(CounterType ct) {
    	if(ct == CounterType.STRING || ct == CounterType.OBJECT || ct == CounterType.DATE) {
			return false;
		}
		return true;
    }
}
