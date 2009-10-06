/**
 * 13-Jul-2005
 */
package com.ixora.rms.logging.data;

import com.ixora.rms.data.CounterValue;

/**
 * @author Daniel Moraru
 */
public interface AggCounterValue {
	/**
	 * @param cv
	 */
	void addCounterValue(CounterValue cv);
	/**
	 * @return
	 */
	CounterValue getCounterValue();
}
