/*
 * Created on 19-Feb-2005
 */
package com.ixora.rms.ui.dataviewboard.tables;

import com.ixora.rms.CounterType;
import com.ixora.rms.ui.dataviewboard.DataViewBoardCounterFilter;

/**
 * @author Daniel Moraru
 */
public final class TablesBoardCounterFilter implements DataViewBoardCounterFilter {

	/**
	 * Constructor.
	 */
	public TablesBoardCounterFilter() {
		super();
	}

    /**
     * @see com.ixora.rms.ui.dataviewboard.DataViewBoardCounterFilter#accept(com.ixora.rms.CounterType)
     */
    public boolean accept(CounterType ct) {
		// can't plot simple counters, only data views
        return false;
    }
}
