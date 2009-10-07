/**
 * 27-Mar-2006
 */
package com.ixora.rms.providers.impl.java;

/**
 * Context for classes used for Java providers.
 * @author Daniel Moraru
 */
public interface JavaProviderImplementationContext {
	/**
	 * The <code>error</code> message will be reported to the console. Use this method
	 * only for non-fatal errors, otherwise propagate all exceptions.
	 * @param error
	 * @param t
	 */
	void error(String error, Throwable t);
}
