/*
 * Created on 21-Apr-2005
 */
package com.ixora.rms.agents.sqlserver.events;

import com.ixora.rms.agents.impl.Counter;
import com.ixora.rms.data.CounterValue;
import com.ixora.rms.data.CounterValueDouble;

/**
 * SQLCounterAccum
 * Only holds one current value (rather than a set of samples), which
 * accumulates over time as events are received. Does not allow the
 * framework to delete the current value.
 */
public class SQLCounterAccum extends Counter {
	private static final long serialVersionUID = 8084534406382176379L;

	/**
     * Default constructor
     * @param name
     */
    public SQLCounterAccum(String name) {
        super(name, name + ".desc");
        fSamples.add(new CounterValueDouble(0));
    }

    /**
     * Accumulates the specified value to the current value
     * @param value
     */
    public void add(double value) {
        CounterValue cv = fSamples.get(0);
        fSamples.clear();
        fSamples.add(new CounterValueDouble(cv.getDouble() + value));
    }

    /**
     * Replaces the current value with the specified one
     * @param value
     */
    public void set(double value) {
        fSamples.clear();
        fSamples.add(new CounterValueDouble(value));
    }

    /**
     * Don't allow this function to be called
     * @see com.ixora.rms.agents.impl.Counter#dataReceived(com.ixora.rms.data.CounterValue)
     */
	public void dataReceived(CounterValue data)	{
		; // nothing
	}

	/**
	 * Don't allow this function to be called
	 */
	public void reset() {
		; // nothing
	}

}
