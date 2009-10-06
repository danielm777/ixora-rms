package com.ixora.rms.dataengine.functions;

import java.util.List;

import com.ixora.rms.ResourceId;
import com.ixora.rms.CounterType;
import com.ixora.rms.data.CounterValue;
import com.ixora.rms.data.CounterValueDouble;
import com.ixora.rms.dataengine.Resource;
import com.ixora.rms.dataengine.definitions.FunctionDef;

/**
 * Average
 * Holds a running average of a value over time.
 */
public class Average extends Function {
	/** Number of elements in the average */
	protected double fElements = 0;
	/** Calculated average */
	protected double fRunningAvg = 0;

	/**
	 * Default constructor, builds this object from XML definition
	 */
	public Average(FunctionDef fd, List<Resource> listQueryResources, ResourceId ridContext) {
	    super(fd, listQueryResources, ridContext, true);
	}

	/**
	 * @return Hardcoded name of this function's operation
	 */
	public static String getOp() {
		return "average";
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

		// We need to know how many elements are there so we can
		// add to an existing average.
		fElements++;
		if (fElements == 1) {
			return new CounterValueDouble(currentValue);
		}

		// Unpack the average and repack it with n+1 elements
		fRunningAvg = (fRunningAvg*(fElements-1) + currentValue)/fElements;
		return new CounterValueDouble(fRunningAvg);
	}

	/**
	 * @see com.ixora.rms.dataengine.functions.Function#getReturnedType()
	 */
	public CounterType getReturnedType() {
		return CounterType.DOUBLE;
	}
}
