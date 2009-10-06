package com.ixora.rms.dataengine.functions;

import java.util.List;

import com.ixora.rms.ResourceId;
import com.ixora.rms.CounterType;
import com.ixora.rms.data.CounterValue;
import com.ixora.rms.data.CounterValueDouble;
import com.ixora.rms.dataengine.Resource;
import com.ixora.rms.dataengine.definitions.FunctionDef;

/**
 * Sum
 * Sums (accumulates) the value of a counter over time.
 */
public class Sum extends Function {
	private double	fAccumulator = 0;

	/**
	 * Default constructor, builds this object from XML definition
	 */
	public Sum(FunctionDef fd, List<Resource> listQueryResources, ResourceId ridContext) {
	    super(fd, listQueryResources, ridContext, true);
	}

	/**
	 * @return Hardcoded name of this function's operation
	 */
	public static String getOp() {
		return "sum";
	}

	/**
	 * Accumulates the value of the only parameter to an internal sum.
	 * @param args list of input params
	 * @return the current value of the accumulator
	 */
	public CounterValue execute(CounterValue[] args, CounterType[] types) {
	    // If no arguments then function was defined incorrectly
	    // so return 0 so the user can still see something.
	    if (args.length < 1) {
	        return new CounterValueDouble(0);
	    }

	    // Just add the param to the internal value and return
	    fAccumulator += args[0].getDouble();
		return new CounterValueDouble(fAccumulator);
	}

	/**
	 * @see com.ixora.rms.dataengine.functions.Function#getReturnedType()
	 */
	public CounterType getReturnedType() {
		return CounterType.DOUBLE;
	}
}
