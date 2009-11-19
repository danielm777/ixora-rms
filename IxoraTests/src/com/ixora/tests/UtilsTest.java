/**
 * 11-Mar-2006
 */
package com.ixora.tests;

import junit.framework.TestCase;

import com.ixora.common.os.OSUtils;
import com.ixora.common.utils.Utils;

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
	
	public void testStitchPathFragments() {
		StringBuffer buff = new StringBuffer();
		String path1 = "C:\\Root\\";
		String path2 = "aaa\\bbb";
		Utils.stitchPathFragments(buff, path1, path2);
		assertEquals(buff.toString(), path1 + path2);
		
		buff.delete(0, buff.length());
		path1 = "C:\\Root";
		path2 = "aaa\\bbb";
		Utils.stitchPathFragments(buff, path1, path2);
		System.err.println(buff.toString());
		assertEquals(buff.toString(), path1 + "\\" + path2);

		buff.delete(0, buff.length());
		path1 = "C:\\Root\\";
		path2 = "\\aaa\\bbb";
		Utils.stitchPathFragments(buff, path1, path2);
		System.err.println(buff.toString());
		assertEquals(buff.toString(), "C:\\Root" + path2);
		
	}
}
