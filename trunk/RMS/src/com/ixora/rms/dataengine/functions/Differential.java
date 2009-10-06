package com.ixora.rms.dataengine.functions;

import java.util.List;

import com.ixora.rms.ResourceId;
import com.ixora.rms.CounterType;
import com.ixora.rms.data.CounterValue;
import com.ixora.rms.data.CounterValueDouble;
import com.ixora.rms.dataengine.Resource;
import com.ixora.rms.dataengine.definitions.FunctionDef;

/**
 * Differential
 * For one counter only, returns the difference between two consecutive
 * values. Only starts returning results after at least two samples.
 *
 *    v = y(t1) - y(t0)
 *
 */
public class Differential extends Function {
	protected double fPreviousValue = 0;
	protected boolean fIsFirst = true;

	/**
	 * Default constructor, builds this object from XML definition
	 */
	public Differential(FunctionDef fd, List<Resource> listQueryResources, ResourceId ridContext) {
	    super(fd, listQueryResources, ridContext, true);
	}

	/**
	 * @return Hardcoded name of this function's operation
	 */
	public static String getOp() {
		return "diff";
	}

	/**
	 * Just one argument. First time returns zero, afterwards returns the
	 * difference between consecutive values
	 */
	public CounterValue execute(CounterValue[] args, CounterType[] types) {
	    // If no arguments then function was defined incorrectly
	    // so return 0 so the user can still see something.
	    if (args.length < 1) {
	        return new CounterValueDouble(0);
	    }

		double currentValue = args[0].getDouble();

		// For the first iteration remember the value and return 0
		if (fIsFirst) {
			fIsFirst = false;
			fPreviousValue = currentValue;
			return new CounterValueDouble(0);
		}

		// For subsequent iterations calculate the difference
		CounterValue retVal = new CounterValueDouble(currentValue - fPreviousValue);
		fPreviousValue = currentValue;

		return retVal;
	}

	/**
	 * @see com.ixora.rms.dataengine.functions.Function#getReturnedType()
	 */
	public CounterType getReturnedType() {
		return CounterType.DOUBLE;
	}
}
