/**
 * 04-Aug-2005
 */
package com.ixora.rms.dataengine;

import java.io.Serializable;
import java.util.List;

import com.ixora.rms.ResourceId;
import com.ixora.rms.data.CounterValue;

/**
 * Interface implemented by classes that analyze monitoring data.
 * @author Daniel Moraru
 */
public interface Analyzer extends Serializable {
	/**
	 * @return the identifier for this analyzer
	 */
	String getID();
	/**
	 * Invoked after the analyzer has been cloned; cloning is required
	 * as one analyzer will be created for each resource id regex match.
	 * @param matchedRids the matched resource ids which will be the same
	 * for the lifetime of each analyzer
	 */
	void initializeInstance(List<ResourceId> matchedRids);
	/**
	 * @return the regex resources which have to be analyzed
	 */
	List<ResourceId> getAnalyzedResources();
	/**
	 * Sets the values for the resources that have to be analyzed; the values
	 * are in the same order as in the resource list returned
	 * by <code>getAnalyzedResources()</code>.
	 * @param values
	 * @throws Throwable
	 */
	void analyze(List<CounterValue> values) throws Throwable;
}
