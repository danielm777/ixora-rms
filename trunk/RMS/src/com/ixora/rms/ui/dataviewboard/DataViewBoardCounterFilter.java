/*
 * Created on 19-Feb-2005
 */
package com.ixora.rms.ui.dataviewboard;

import com.ixora.rms.CounterType;

/**
 * @author Daniel Moraru
 */
public interface DataViewBoardCounterFilter {
	/**
	 * @param cd
	 * @return true if the board accepts counters of the given type
	 */
	boolean accept(CounterType ct);
}
