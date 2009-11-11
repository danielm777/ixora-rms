package com.ixora.common.logging.adapters.log4j;


import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;

/**
 * Log4j logger factory.
 * @author Daniel Moraru
 */
public final class AppLoggerLog4jFactory implements LoggerFactory {

	/**
	 * Constructor
	 */
	public AppLoggerLog4jFactory() {
		super();
	}

	/**
	 * @see org.apache.log4j.spi.LoggerFactory#makeNewLoggerInstance(String)
	 */
	public Logger makeNewLoggerInstance(String arg0) {
		return new AppLoggerLog4j(arg0);
	}

}
