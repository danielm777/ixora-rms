package com.ixora.common.logging.adapters.log4j;

import org.apache.log4j.LogManager;
import org.apache.log4j.spi.LoggerFactory;

import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerProvider;

/**
 * Logger provider for Log4j.
 * @author Daniel Moraru
 */
public final class AppLoggerProviderLog4j implements AppLoggerProvider {
	/**
	 * Log4j logger factory.
	 */
	private LoggerFactory factory = new AppLoggerLog4jFactory();

	/**
	 * Constructor for AppLoggerProviderLog4j.
	 */
	public AppLoggerProviderLog4j() {
		super();
	}

	/**
	 * @see com.ixora.common.logging.AppLoggerProvider#getLogger(String)
	 */
	public AppLogger getLogger(String name) {
		return (AppLogger)LogManager.getLogger(name, factory);
	}

}

