/*
 * Created on 28-Dec-2004
 */
package com.ixora.rms.providers.impl.java;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import com.ixora.common.typedproperties.exception.VetoException;
import com.ixora.common.utils.Utils;
import com.ixora.rms.providers.ProviderCustomConfiguration;

/**
 * @author Daniel Moraru
 */
public class Configuration extends ProviderCustomConfiguration {
	public static final String CLASSPATH = "providers.java.classpath";
	public static final String CLASSLOADER_ID = "providers.java.classloader_id";
	public static final String IMPLEMENTATION_CLASS = "providers.java.impl_class";
	public static final String PARAMETERS = "providers.java.parameters";

	/**
	 * Constructor.
	 */
	public Configuration() {
		super();
		setProperty(CLASSPATH, TYPE_STRING, true, true);
		setProperty(CLASSLOADER_ID, TYPE_STRING, true, false);
		setProperty(IMPLEMENTATION_CLASS, TYPE_STRING, true, true);
		setProperty(PARAMETERS, TYPE_STRING, true, false);

		setString(CLASSLOADER_ID, "shared");
	}

	/**
	 * @return the list of parameters to be passed to the implementation class
	 */
	public String[] getParameters() {
		String s = getString(PARAMETERS);
		StringTokenizer tok = new StringTokenizer(s, "\"");
		List<String> ret = new LinkedList<String>();
		for(int i = 0; tok.hasMoreTokens(); ++i) {
			String p = tok.nextToken();
			if(Utils.isEmptyString(p)) {
				continue;
			}
			ret.add(p);
		}
		return ret.toArray(new String[ret.size()]);
	}

	/**
	 * @see com.ixora.rms.CustomConfiguration#getClasspath()
	 */
	public String getClasspath() {
		return getString(CLASSPATH);
	}

	/**
	 * @see com.ixora.rms.CustomConfiguration#getClassLoaderId()
	 */
	public String getClassLoaderId() {
		return getString(CLASSLOADER_ID);
	}

	/**
	 * @see com.ixora.common.typedproperties.TypedProperties#veto()
	 */
	public void veto() throws VetoException {
		try {
			getParameters();
		} catch(Exception e) {
			throw new VetoException(e.getLocalizedMessage());
		}
	}
}
