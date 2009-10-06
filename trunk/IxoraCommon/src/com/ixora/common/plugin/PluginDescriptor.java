/**
 * 20-Aug-2005
 */
package com.ixora.common.plugin;

/**
 * @author Daniel Moraru
 */
public interface PluginDescriptor {
	/**
	 * @return the classpath used by this plugin. It must be a comma separated list of the form
	 * "C:/lib/,C:/lib/(.*\.jar),C:/otherlib/other.jar" where the expression
	 * in the paranthesis will be treated as a regular expression
	 */
	String getClasspath();

	/**
	 * @return the id of the class loader to use for code specified
	 * in the classpath returned by <code>getClasspath()</code>.<br>
	 * This allows the classloaders to be used between entities, thus
	 * allowing them to share resources.<br>If null is returned, a new
	 * class loader will be created; the same if the id returned hasn't been
	 * used before.
	 */
	String getClassLoaderId();

	/**
	 * @return true if 'parent last' classloader is needed for this plugin
	 */
	boolean useParentLastClassloader();
}
