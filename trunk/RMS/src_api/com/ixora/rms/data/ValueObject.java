/**
 * 18-Mar-2006
 */
package com.ixora.rms.data;

import com.ixora.common.xml.XMLExternalizable;

/**
 * This interface must be implemented by all objects that are values
 * for CounterValueObject counters.
 * @author Daniel Moraru
 */
public interface ValueObject extends XMLExternalizable {
	/**
	 * When this method is invoked this object must aggregate it's data
	 * with data from the given <code>obj</code>.
	 * @param obj
	 */
	void aggregate(ValueObject obj);
}
