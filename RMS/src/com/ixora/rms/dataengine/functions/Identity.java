package com.ixora.rms.dataengine.functions;

import java.util.List;

import com.ixora.rms.ResourceId;
import com.ixora.rms.CounterType;
import com.ixora.rms.data.CounterValue;
import com.ixora.rms.data.CounterValueDouble;
import com.ixora.rms.dataengine.Resource;
import com.ixora.rms.dataengine.definitions.FunctionDef;

/**
 * Identity
 * The identity function has only one argument and always returns it
 * unmodified
 * @author Cristian Costache
 * @author Daniel Moraru
 */
public class Identity extends Function {
	private static final long serialVersionUID = -5547345773850323180L;
	private CounterType fReturnType;

	/**
	 * Default constructor, builds this object from XML definition
	 */
	public Identity(FunctionDef fd, List<Resource> listQueryResources, ResourceId ridContext) {
	    super(fd, listQueryResources, ridContext, false);
	}

	/**
	 * Creates this function from the given resource
	 * @param r
	 */
	public Identity(Resource r) {
	    super(r);
	}

	/**
	 * @return Hardcoded name of this function's operation
	 */
	public static String getOp() {
		return "identity";
	}

	/**
	 * @return Simply returns the (only) argument unchanged
	 */
	public CounterValue execute(CounterValue[] args, CounterType[] types) {
	    // If no arguments then function was defined incorrectly
	    // so return 0 so the user can still see something.
	    if (args.length < 1) {
	        return new CounterValueDouble(0);
	    }

	    if(fReturnType == null) {
	    	fReturnType = types[0];
	    }
		return args[0];
	}

	/**
	 * @see com.ixora.rms.dataengine.functions.Function#getReturnedType()
	 */
	public CounterType getReturnedType() {
		return fReturnType;
	}
}
