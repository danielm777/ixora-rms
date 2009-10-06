/**
 * 13-Jul-2005
 */
package com.ixora.rms.logging.data;

import com.ixora.rms.data.CounterValue;
import com.ixora.rms.data.CounterValueDouble;

/**
 * @author Daniel Moraru
 */
public class AggCounterValueDouble implements AggCounterValue {
	/** Sum of values */
	private double fSum;
	/** Number of samples */
	private int fSamples;


	public AggCounterValueDouble() {
		super();
	}

	/**
	 * @see com.ixora.rms.logging.data.AggCounterValue#addCounterValue(com.ixora.rms.data.CounterValue)
	 */
	public void addCounterValue(CounterValue cv) {
		fSum += cv.getDouble();
		fSamples++;
	}

	/**
	 * @see com.ixora.rms.logging.data.AggCounterValue#getCounterValue()
	 */
	public CounterValue getCounterValue() {
		if(fSamples == 0) {
			return new CounterValueDouble(0);
		}
		return new CounterValueDouble(fSum/fSamples);
	}
}
