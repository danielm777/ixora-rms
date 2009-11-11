/**
 * 10-May-2007
 */
package com.ixora.common.utils;

/**
 * @author Daniel Moraru
 */
public class JavaVersion {
	public enum Version {
		V1_4, V1_5, V1_6
	}

	private JavaVersion() {
		super();
	}

	public static Version getJavaVersion() {
		String version = System.getProperty("java.version");
		if(version == null) {
			return Version.V1_4;
		}
		if(version.startsWith("1.6") || version.startsWith("6")) {
			return Version.V1_6;
		}
		if(version.startsWith("1.5") || version.startsWith("5")) {
			return Version.V1_5;
		}
		return Version.V1_4;
	}

	public static boolean isAtLeast(Version version) {
		Version vers = getJavaVersion();
		if(vers.compareTo(version) >= 0) {
			return true;
		}
		return false;
	}
}
