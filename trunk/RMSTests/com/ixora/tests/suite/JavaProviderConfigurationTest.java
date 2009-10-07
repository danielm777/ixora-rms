/**
 * 03-Mar-2006
 */
package com.ixora.tests.suite;

import com.ixora.rms.providers.impl.java.Configuration;

import junit.framework.TestCase;

/**
 * @author Daniel Moraru
 */
public class JavaProviderConfigurationTest extends TestCase {
	public void testParameters() {
		Configuration conf = new Configuration();
		conf.setString(Configuration.PARAMETERS, "\"Param1 a \" \"Param2 a \" \"Param3 a \"");
		String[] params = conf.getParameters();
		int i = 1;
		for(String param : params) {
			assertEquals(param, "Param" + i + " a ");
			++i;
		}
	}
}
