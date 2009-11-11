/**
 * 13-Jul-2005
 */
package com.ixora.rms.logging.data;

import com.ixora.rms.data.CounterValue;
import com.ixora.rms.data.CounterValueString;

/**
 * @author Daniel Moraru
 */
public class AggCounterValueString implements AggCounterValue {
	/** Value */
	private String fValue;

	/**
	 * @see com.ixora.rms.logging.data.AggCounterValue#addCounterValue(com.ixora.rms.data.CounterValue)
	 */
	public void addCounterValue(CounterValue cv) {
		fValue = cv.toString();
	}

	/**
	 * @see com.ixora.rms.logging.data.AggCounterValue#getCounterValue()
	 */
	public CounterValue getCounterValue() {
		return new CounterValueString(fValue);
	}
}
