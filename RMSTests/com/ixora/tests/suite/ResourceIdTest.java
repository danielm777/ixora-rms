/**
 * 08-Jan-2006
 */
package com.ixora.tests.suite;

import junit.framework.TestCase;

import com.ixora.rms.ResourceId;

/**
 * @author Daniel Moraru
 */
public class ResourceIdTest extends TestCase {

	public void testHost() {
		ResourceId rid = new ResourceId("(.*)");
		System.out.println("ResourceIdTest.testHost() - " + rid.toString());
		assertEquals("(.*)", rid.toString());

		rid = new ResourceId("(.*)/(windows.*)");
		System.out.println("ResourceIdTest.testHost() - " + rid.toString());
		assertEquals("(.*)/(windows.*)", rid.toString());

		rid = new ResourceId("(h.*)/(windows.*)/(e1.*)/(e2.*)/[(c.*)]");
		System.out.println("ResourceIdTest.testHost() - " + rid.toString());
		assertEquals("(h.*)/(windows.*)/(e1.*)/(e2.*)/[(c.*)]", rid.toString());

	}
}
