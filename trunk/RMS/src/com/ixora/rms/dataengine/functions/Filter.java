/*
 * Created on 01-Feb-2005
 */
package com.ixora.rms.dataengine.functions;

import java.util.LinkedList;
import java.util.List;

import com.ixora.rms.ResourceId;
import com.ixora.rms.CounterType;
import com.ixora.rms.data.CounterValue;
import com.ixora.rms.data.CounterValueDouble;
import com.ixora.rms.dataengine.Resource;
import com.ixora.rms.dataengine.definitions.ValueFilterDef;
import com.ixora.rms.dataengine.definitions.ValueFilterRuleDef;

/**
 * Filter
 * Constructed based on a ValueFilterDef, allows only specified
 * values for a given resource, effectively filtering the output of
 * a query.
 * @author Cristian Costache
 * @author Daniel Moraru
 */
public class Filter extends Function {
	private List<String> fListValues;
	private CounterType fReturnType;

	/**
	 * Constructs a filter function, allowing only specified values
	 * for the given resource.
	 * @param r1 resource to watch
	 */
	public Filter(ValueFilterDef vfd,
			List<Resource> listQueryResources, ResourceId ridContext) {
		super(vfd, listQueryResources, ridContext, false);
		fReturnType = CounterType.DOUBLE;
		fListValues = new LinkedList<String>();
		// Remember the list of accepted values
		for (ValueFilterRuleDef rule : vfd.getRules()) {
			this.fListValues.add(rule.getValue());
		}
	}

	/**
	 * @return Hardcoded name of this function's operation
	 */
	public static String getOp() {
		return "filter";
	}

	/**
	 * This function only accepts one argument (and ignores the rest if any)
	 * Returns the value of its argument, if within accepted values,
	 * or null otherwise. Returning null has the effect of rejecting the
	 * current set of resource values produced by the query.
	 * @see com.ixora.rms.dataengine.functions.Function#execute(com.ixora.rms.data.CounterValue[])
	 */
	public CounterValue execute(CounterValue[] args, CounterType[] types) {
	    // If no arguments then function was defined incorrectly
	    // so return 0 so the user can still see something.
	    if (args.length < 1) {
	        return new CounterValueDouble(0);
	    }

		// Check if this value matches any rule
		if (args[0] instanceof CounterValueDouble) {
			CounterValueDouble cvd = (CounterValueDouble)args[0];
			for (String rule : fListValues) {
				if (Double.parseDouble(rule) == cvd.getDouble()) {
					fReturnType = CounterType.DOUBLE;
					return cvd;
				}
			}
		} else { // CounterValueString or anything else
			CounterValue cv = args[0];
			for (String rule : fListValues) {
				if (cv.toString().equals(rule)) {
					fReturnType = CounterType.STRING;
					return cv;
				}
			}
		}

		// No rule was matched, reject this value
		return null;
	}

	/**
	 * @see com.ixora.rms.dataengine.functions.Function#getReturnedType()
	 */
	public CounterType getReturnedType() {
		return fReturnType;
	}
}
