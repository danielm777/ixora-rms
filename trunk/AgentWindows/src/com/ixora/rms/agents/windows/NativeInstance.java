package com.ixora.rms.agents.windows;

/**
 * NativeInstance
 * Native code constructs an instance of NativeInstance for
 * each performance instance retrieved from Windows registry.
 */
public final class NativeInstance {
	int	id;
	String	name;

	/**
	 * Constructor, called from native code
	 * @param id
	 * @param name
	 */
	public NativeInstance(int id, String name) {
		this.id = id;
		this.name = name;
		if (id != -1)
		    this.name += "#" + id;
	}
}