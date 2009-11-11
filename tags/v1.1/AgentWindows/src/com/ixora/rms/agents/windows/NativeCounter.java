package com.ixora.rms.agents.windows;

/**
 * NativeCounter
 * Native code constructs an instance of NativeCounter for each
 * counter retrieved from registry.
 */
public final class NativeCounter {
	String	name;
	String	description;
	int	type;

	/**
	 * Constructor, called from native code
	 * @param name
	 * @param description
	 * @param type
	 */
	public NativeCounter(String name, String description, int type) {
		this.name = name;
		this.description = description;
		this.type = type;
	}
}