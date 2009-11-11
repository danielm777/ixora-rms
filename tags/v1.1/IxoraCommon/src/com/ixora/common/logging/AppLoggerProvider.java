package com.ixora.common.logging;

/**
 * A logger provider. A provider is used to be able to use inheritance rather then delegation
 * for loggers as the delegation approach results in an extra object being created for each class
 * where as with inheritance the provider is used to retrieve a reference to the native logger.
 * @author Daniel Moraru
 */
public interface AppLoggerProvider {
	/**
	 * @return The logger for the given name.
	 * @param name
	 */
	AppLogger getLogger(String name);
}
