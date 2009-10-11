/*
 * Created on 15-Dec-2003
 */
package com.ixora.rms;

import java.io.Serializable;

/**
 * OperatingSystemInfo.
 * @author Daniel Moraru
 */
public final class OperatingSystemInfo implements Serializable {
	private static final long serialVersionUID = 7863828462515394874L;
	/** Name */
	private String name;
	/** Architecture */
	private String architecture;
	/** Version */
	private String version;

	/**
	 * Constructor.
	 * @param name
	 * @param architecture
	 * @param version
	 */
	public OperatingSystemInfo(String name, String architecture, String version) {
		super();
		this.name = name;
		this.architecture = architecture;
		this.version = version;
	}

	/**
	 * @return the OS architecture
	 */
	public String getArchitecture() {
		return architecture;
	}

	/**
	 * @return the OS name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the OS version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append(name);
		buff.append("/");
		buff.append(version);
		buff.append("/");
		buff.append(architecture);
		return buff.toString();
	}
}
