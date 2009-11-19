/*
 * Created on 17-Jan-2004
 */
package com.ixora.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Daniel Moraru
 */
public class AllTests {
	public static void main(String[] args) {
		junit.swingui.TestRunner.run(AllTests.class);
	}
	public static Test suite() {
		TestSuite suite = new TestSuite("Test for com.ixora.tests");
		//$JUnit-BEGIN$
		suite.addTest(new TestSuite(CircullarLinkedListTest.class));
		suite.addTest(new TestSuite(ComponentVersionTest.class));
		suite.addTest(new TestSuite(UtilsTest.class));
		//$JUnit-END$
		return suite;
	}
}
