/*
 * Created on 15-Apr-2005
 */
package com.ixora.rms.agents.impl.jmx;

import com.ixora.rms.CounterType;
import com.ixora.rms.agents.impl.Counter;

/**
 * @author Daniel Moraru
 */
public class JMXCounter extends Counter {
	protected String fJMXName;

	/**
	 * Constructor.
	 * @param id
	 * @param description
	 * @param type
	 */
	public JMXCounter(String id, String description, CounterType type) {
		super(id, description, type);
	}

	/**
	 * Constructor.
	 * @param jmxName
	 * @param id
	 * @param description
	 * @param type
	 */
	public JMXCounter(String jmxName, String id, String description, CounterType type) {
		super(id, description, type);
		this.fJMXName = jmxName;
	}

	/**
	 * Constructor.
	 * @param jmxName
	 * @param id
	 * @param description
	 * @param type
	 * @param discreet
	 */
	public JMXCounter(String jmxName, String id, String description, CounterType type,
			boolean discreet) {
		super(id, description, type, discreet);
		this.fJMXName = jmxName;
	}
	/**
	 * @return the JMX name of this counter.
	 */
	public String getJMXName() {
		return fJMXName;
	}
}
