package com.ixora.rms.dataengine.functions;

import java.util.List;

import com.ixora.rms.ResourceId;
import com.ixora.rms.CounterType;
import com.ixora.rms.data.CounterValue;
import com.ixora.rms.data.CounterValueDouble;
import com.ixora.rms.dataengine.Resource;
import com.ixora.rms.dataengine.definitions.FunctionDef;

/**
 * TimeDifferential
 * Returns the differential (distance between consecutive elements)
 * over a time interval:
 *
 *        y(t1) - y(t0)
 *    v = -------------
 *           t1 - t0
 *
 * Takes two counters: the value and the timestamp.
 * Needs at least two samples before it starts returning anything.
 */
public class TimeDifferential extends Function {
	private static final long serialVersionUID = -4376753474979961748L;
	protected double 	previousValue = 0;
	protected double 	previousTimestamp = 0;
	protected boolean isFirst = true;

	/**
	 * Default constructor, builds this object from XML definition
	 */
	public TimeDifferential(FunctionDef fd, List<Resource> listQueryResources, ResourceId ridContext) {
	    super(fd, listQueryResources, ridContext, true);
	}

	/**
	 * @return Hardcoded name of this function's operation
	 */
	public static String getOp() {
		return "timediff";
	}

	/**
	 * Always two arguments. First time returns zero, afterwards returns
	 * the difference between consecutive values, over the time interval.
	 */
	public CounterValue execute(CounterValue[] args, CounterType[] types) {
	    // If no arguments then function was defined incorrectly
	    // so return 0 so the user can still see something.
	    if (args.length < 2) {
	        return new CounterValueDouble(0);
	    }

		double currentValue = args[0].getDouble();
		double currentTimestamp = args[1].getDouble();

		// For the first iteration remember the values and return 0
		if (isFirst) {
			isFirst = false;
			previousValue = currentValue;
			previousTimestamp = currentTimestamp;
			return new CounterValueDouble(0);
		}

		// For subsequent iterations calculate the difference
		double timestampDiff = (currentTimestamp - previousTimestamp);
		CounterValue retVal;
		if (timestampDiff != 0) {
			retVal = new CounterValueDouble((currentValue - previousValue) / timestampDiff);
		} else { // no time variation
			retVal = new CounterValueDouble(0);
		}

		previousValue = currentValue;
		previousTimestamp = currentTimestamp;

		return retVal;
	}

	/**
	 * @see com.ixora.rms.dataengine.functions.Function#getReturnedType()
	 */
	public CounterType getReturnedType() {
		return CounterType.DOUBLE;
	}
}
