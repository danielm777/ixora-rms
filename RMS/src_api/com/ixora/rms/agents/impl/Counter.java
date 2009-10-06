
package com.ixora.rms.agents.impl;

import java.util.LinkedList;
import java.util.List;

import com.ixora.common.Reusable;
import com.ixora.rms.CounterDescriptor;
import com.ixora.rms.CounterDescriptorImpl;
import com.ixora.rms.CounterId;
import com.ixora.rms.CounterType;
import com.ixora.rms.MonitoringLevel;
import com.ixora.rms.data.CounterValue;

/**
 * @author Cristian Costache
 * @author Daniel Moraru
 */
public class Counter extends CounterDescriptorImpl implements Reusable {
	/** Counter samples */
	// samples of CounterValues
	protected List<CounterValue> fSamples;

	/**
	 * Constructor with default type of INT and non-discreet
	 * @param id
	 * @param description
	 */
	public Counter(String id, String description) {
		this(id, description,
				CounterType.DOUBLE, // default counter type
				false);	// non-discreet by default
	}

	/**
	 * Constructor for a non-discreet counter.
	 * @param id
	 * @param description
	 * @param type
	 */
	public Counter(
			String id,
			String description,
			CounterType type) {
		this(id, description, type, false);
	}

	/**
	 * Constructor.
	 * @param id
	 * @param description
	 * @param type
	 * @param discreet
	 */
	public Counter(
			String id,
			String description,
			CounterType type,
			boolean discreet) {
		this(id, null, description, type, discreet, null, null);
	}

	/**
	 * Constructor.
	 * @param id
	 * @param description
	 * @param type
	 * @param level
	 */
	public Counter(
			String id,
			String description,
			CounterType type,
			MonitoringLevel level) {
		this(id, null, description, type, false, level, null);
	}

	/**
	 * Constructor.
	 * @param id
	 * @param description
	 * @param type
	 * @param viewboardClass
	 */
	public Counter(
			String id,
			String description,
			CounterType type,
			String viewboardClass) {
		this(id, null, description, type, false, null, viewboardClass);
	}

	/**
	 * @param id
	 * @param alternateName
	 * @param description
	 * @param type
	 * @param discreet
	 * @param level
	 * @param viewBoardClass
	 */
	public Counter(String id,
			String alternateName,
			String description,
			CounterType type,
			boolean discreet,
			MonitoringLevel level,
			String viewBoardClass) {
		super(new CounterId(id), alternateName, description, type, discreet, level, viewBoardClass);
		fSamples = new LinkedList<CounterValue>();
	}

	/**
	 * Constructor.
	 * @param cd
	 */
	public Counter(CounterDescriptor cd) {
		super(cd);
		fSamples = new LinkedList<CounterValue>();
	}

	/**
	 * Adds the incoming data to the history
	 * @param data
	 */
	public void dataReceived(CounterValue data)	{
		fSamples.add(data);
	}

	/**
	 * Clears the counter history
	 */
	public void reset() {
		fSamples.clear();
	}

	/**
	 * @return a list of all the values in the history of the counter
	 */
	public List<CounterValue> getSamples() {
		return fSamples;
	}

	/**
	 * @return the counter descriptor
	 */
	public CounterDescriptor extractDescriptor() {
		return (CounterDescriptor) clone();
	}

	/**
	 * Sets the enabled flag.
	 * @param enabled
	 */
	public void setEnabled(boolean enabled) {
		this.fEnabled = enabled;
	}

	/**
	 * Debug only.
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append("Id=");
		buff.append(this.fCounterId);
		buff.append(":Enabled=");
		buff.append(this.fEnabled);
		buff.append(":Level=");
		buff.append(this.fLevel);
		buff.append(":Samples=");
		buff.append(this.fSamples.size());
		return buff.toString();
	}
}
