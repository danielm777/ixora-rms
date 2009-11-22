/*
 * Created on 21-Apr-2005
 */
package com.ixora.rms.agents.sqlserver.events;

import com.ixora.rms.CounterType;
import com.ixora.rms.agents.impl.Counter;
import com.ixora.rms.data.CounterValue;
import com.ixora.rms.data.CounterValueString;

/**
 * SQLCounterString
 * Only holds one current value (rather than a set of samples), which can
 * be replaced with a new value when a new event is received. Does not allow
 * the framework to delete the current value.
 */
public class SQLCounterString extends Counter {
	private static final long serialVersionUID = -4094865912086308589L;

	/**
     * Default constructor
     * @param name
     */
    public SQLCounterString(String name) {
        super(name, name + ".desc", CounterType.STRING);
        fSamples.add(new CounterValueString(""));
    }

    /**
     * Replaces the current value with the specified one
     * @param value
     */
    public void set(String value) {
        fSamples.clear();
        fSamples.add(new CounterValueString(value));
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
