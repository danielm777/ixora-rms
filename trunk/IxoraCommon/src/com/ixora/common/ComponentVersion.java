package com.ixora.common;

import java.io.Serializable;

import com.ixora.common.utils.Utils;

/**
 * @author Daniel Moraru
 */
public final class ComponentVersion implements Comparable, Serializable {
	private int major;
	private int minor;
	private int build;
	/** Extra version info found in the product file */
	private String fExtraVersionInfo;

	/**
	 */
	public ComponentVersion(int major, int minor, int build) {
		this(major, minor, build, null);
	}

	/**
	 */
	public ComponentVersion(int major, int minor, int build, String extra) {
		super();
		this.major = major;
		this.minor = minor;
		this.build = build;
		fExtraVersionInfo = extra;
	}

	/**
	 * @param version String of the form <i>major.minor.build extra</i>.
	 */
	public ComponentVersion(String version) {
		super();
		if(version == null) {
			throw new IllegalArgumentException("null version string");
		}
		int idx = version.indexOf(".");
		if(idx < 0) {
			throw new IllegalArgumentException("invalid version string " + version);
		}
		this.major = Integer.parseInt(version.substring(0, idx));
		version = version.substring(idx + 1);
		idx = version.indexOf(".");
		if(idx < 0) {
			throw new IllegalArgumentException("invalid version string " + version);
		}
		this.minor = Integer.parseInt(version.substring(0, idx));

		int idxSpace = version.indexOf(' ');
		if(idxSpace < 0) {
			version = version.substring(idx + 1);
			this.build = Integer.parseInt(version);
		} else {
			String builds = version.substring(idx + 1, idxSpace);
			this.build = Integer.parseInt(builds);
			this.fExtraVersionInfo = version.substring(idxSpace + 1).trim();
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if(Utils.isEmptyString(fExtraVersionInfo)) {
			return "" + major + "." + minor + "." + build;
		} else {
			return "" + major + "." + minor + "." + build + " " + fExtraVersionInfo;
		}
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		if(obj == this) {
			return true;
		}
		if(!(obj instanceof ComponentVersion)) {
			return false;
		}
		ComponentVersion that = (ComponentVersion)obj;
		if(this.major == that.major
			&& this.minor == that.minor
			&& this.build == that.build) {
			return true;
		}
		return false;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return toString().hashCode();
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		if(!(o instanceof ComponentVersion)){
			return -1;
		}
		ComponentVersion other = (ComponentVersion)o;
		if(this.major > other.major) {
			return 1;
		} else if(this.major < other.major) {
			return -1;
		}
		if(this.minor > other.minor) {
			return 1;
		} else if(this.minor < other.minor) {
			return -1;
		}
		if(this.build > other.build) {
			return 1;
		} else if(this.build < other.build) {
			return -1;
		}
		return 0;
	}
}