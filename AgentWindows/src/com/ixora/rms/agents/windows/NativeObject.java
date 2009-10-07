package com.ixora.rms.agents.windows;

/**
 * NativeObject
 * Native code builds an instance of NativeObject for each performance
 * object retrieved from registry.
 */
public final class NativeObject {
	String	name;
	String	description;
	String	totalInstanceName;
	NativeInstance[] instances;
	NativeCounter[] counters;

	/**
	 * Constructor, called from native code
	 * @param name
	 * @param description
	 * @param totalInstanceName
	 * @param instances
	 * @param counters
	 */
	public NativeObject(String name, String description, String totalInstanceName,
		NativeInstance[] instances, NativeCounter[] counters) {
		this.name = name;
		this.description = description;
		this.totalInstanceName = totalInstanceName;
		this.instances = instances;
		this.counters = counters;
	}
}