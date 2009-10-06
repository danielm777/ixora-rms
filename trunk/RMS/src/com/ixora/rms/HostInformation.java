package com.ixora.rms;

import java.io.Serializable;

import com.ixora.common.ComponentVersion;

/**
 * HostInformation.
 * @author Daniel Moraru
 */
public final class HostInformation implements Serializable {
	/** Host */
	private String host;
	/** Operating system on host */
	private OperatingSystemInfo operatingSystem;
	/** The version of the host manager installed on the host */
	private ComponentVersion hostManagerVersion;
	/** Time on the remote host */
	private long timeRemote;
	/** The time delta between the remote and control host */
	private long timeDelta;

	/**
	 * Constructor for HostInformation.
	 * @param os
	 * @param hostManagerVersion
	 */
	public HostInformation(OperatingSystemInfo os, ComponentVersion hostManagerVersion) {
		super();
		if(os == null) {
			throw new IllegalArgumentException("null operating system name");
		}
		if(hostManagerVersion == null) {
			throw new IllegalArgumentException("null host manager version");
		}
		this.operatingSystem = os;
		this.hostManagerVersion = hostManagerVersion;
		this.timeRemote = System.currentTimeMillis();
	}

	/**
	 * Returns the operatingSystem.
	 * @return String
	 */
	public OperatingSystemInfo getOperatingSystem() {
		return operatingSystem;
	}

	/**
	 * Returns the host manager version.
	 * @return ComponentVersion
	 */
	public ComponentVersion getHostManagerVersion() {
		return hostManagerVersion;
	}

	/**
	 * @return the host name
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host the host name
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return this.host + " [" + this.operatingSystem.toString() + "]";
	}

	/**
	 * @return the time on the host.
	 */
	public long getRemoteTime() {
		return timeRemote;
	}

	/**
	 * Sets the time on the remote host.
	 */
	public void setRemoteTime() {
		this.timeRemote = System.currentTimeMillis();
	}

	/**
	 * @return the time on the host.
	 */
	public long getDeltaTime() {
		return timeDelta;
	}

	/**
	 * @param host the host name
	 */
	public void calculateDeltaTime() {
		this.timeDelta = this.timeRemote - System.currentTimeMillis();
	}
}
