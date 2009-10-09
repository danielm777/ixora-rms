/**
 * 16-Jan-2006
 */
package com.ixora.common.plugin;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author Daniel Moraru
 */
public final class ParentLastClassLoader extends URLClassLoader {

	/**
	 * @param urls
	 * @param parent
	 */
	public ParentLastClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}

	/**
	 * @see java.lang.ClassLoader#loadClass(java.lang.String, boolean)
	 */
	protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		Class<?> c = findLoadedClass(name);
		if (c == null) {
		    try {
		    	c = findClass(name);
		    } catch (ClassNotFoundException e) {
		        c = super.loadClass(name, resolve);
		    }
		}
		if (resolve) {
		    resolveClass(c);
		}
		return c;
	}
}
