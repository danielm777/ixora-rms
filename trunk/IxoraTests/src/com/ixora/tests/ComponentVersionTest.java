/**
 * 01-Mar-2006
 */
package com.ixora.tests;

import junit.framework.TestCase;

import com.ixora.common.ComponentVersion;

/**
 * @author Daniel Moraru
 */
public class ComponentVersionTest extends TestCase {

	public void testParse() {
		ComponentVersion version = new ComponentVersion("1.3.2 FP4");
		assertEquals(version.toString(), "1.3.2 FP4");
		version = new ComponentVersion("1.3.2  FP4 ");
		assertEquals(version.toString(), "1.3.2 FP4");
		version = new ComponentVersion("1.3.2 ");
		assertEquals(version.toString(), "1.3.2");
	}
}
