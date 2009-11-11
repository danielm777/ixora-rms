/**
 * 11-Mar-2006
 */
package com.ixora.tests.suite;

import junit.framework.TestCase;

import com.ixora.common.os.OSUtils;

/**
 * @author Daniel Moraru
 */
public class UtilsTest extends TestCase {
	public void testIsOS() {
		String os = System.getProperty("os.name");
		try {
			System.setProperty("os.name", "Windows");
			assertTrue(OSUtils.isOs(OSUtils.WINDOWS));
			assertFalse(OSUtils.isOs(OSUtils.UNIX));
			System.setProperty("os.name", "Linux");
			assertFalse(OSUtils.isOs(OSUtils.WINDOWS));
			assertTrue(OSUtils.isOs(OSUtils.UNIX));
		} finally {
			System.setProperty("os.name", os);
		}
	}
}
