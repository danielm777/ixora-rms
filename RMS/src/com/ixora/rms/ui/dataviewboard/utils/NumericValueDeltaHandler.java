/*
 * Created on Nov 6, 2004
 */
package com.ixora.rms.ui.dataviewboard.utils;

import com.ixora.rms.data.CounterValue;

/**
 * Class that holds a numerical counter value and a history of
 * variations. It is comparable after the the value of the counter.
 * @author Daniel Moraru
 */
public final class NumericValueDeltaHandler implements Comparable<NumericValueDeltaHandler> {
    /** The value of the delta history */
    private int deltaHistory;
    /** Current counter value */
    private CounterValue value;
    /** Difference between the old and the new value*/
    private double delta;
    /** Context for this instance */
    private NumericValueDeltaHandlerContext context;

    /**
     * Constructor.
     * @param context
     * @param val
     */
    public NumericValueDeltaHandler(
    		NumericValueDeltaHandlerContext context,
			CounterValue val) {
    	this.context = context;
    	this.value = val;
    }

    /**
     * @return the current delta
     */
    public double getDelta() {
        return this.delta;
    }
    /**
     * @return the counter value
     */
    public CounterValue getValue() {
        return this.value;
    }
    /**
     * @return the deltaHistory.
     */
    public int getDeltaHistory() {
        return deltaHistory;
    }

    /**
     * Sets the new value.
     * @param val
     */
    public void setValue(CounterValue val) {
    	int deltaHistorySize = context.getDeltaHistorySize();
        delta = this.value.getDouble() - val.getDouble();
        if(delta > 0 && deltaHistory < deltaHistorySize) {
        	// reset delta if direction has changed
        	if(deltaHistory < 0) {
        		deltaHistory = 0;
        	}
            deltaHistory++;
        } else if(delta < 0 && deltaHistory > -deltaHistorySize) {
        	// reset delta if direction has changed
        	if(deltaHistory > 0) {
        		deltaHistory = 0;
        	}
            deltaHistory--;
        } else if(delta == 0) {
        	deltaHistory = 0;
        }
        this.value = val;
    }

	/**
	 * @see java.lang.Comparable#compareTo(com.ixora.rms.ui.dataviewboard.utils.NumericValueDeltaHandler)
	 */
	public int compareTo(NumericValueDeltaHandler arg0) {
		double thatValue = arg0.value.getDouble();
		double thisValue = value.getDouble();
		if(thisValue < thatValue) {
			return -1;
		} else if(thisValue > thatValue) {
			return 1;
		}
		return 0;
	}
}
