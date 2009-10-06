/*
 * Created on 16-Jun-2004
 */
package com.ixora.common.os;

/**
 * Operating system utilities.
 * @author Daniel Moraru
 */
public class OSUtils {
	public static final int WINDOWS = 0;
	public static final int WINDOWS_NT = 1;
	public static final int WINDOWS_2000 = 2;
	public static final int WINDOWS_XP = 3;
	public static final int UNIX = 4;
	public static final int AIX = 5;
	public static final int LINUX = 6;
	public static final int SOLARIS = 7;

	/**
	 * Constructor.
	 */
	private OSUtils() {
		super();
	}

	/**
	 * @return true if the operating system is the one specified
	 */
	public static boolean isOs(int os) {
		switch(os) {
			case WINDOWS:
				if(System.getProperty("os.name").indexOf("indows") > 0) {
					return true;
				}
			break;
			case UNIX:
				if(System.getProperty("os.name").indexOf("indows") < 0) {
					return true;
				}
			break;
		}
		return false;
	}

	/**
	 * The same as <code>System.getProperty("os.name")</code>.
	 * @return
	 */
	public static String getOsName() {
		return System.getProperty("os.name");
	}
}
