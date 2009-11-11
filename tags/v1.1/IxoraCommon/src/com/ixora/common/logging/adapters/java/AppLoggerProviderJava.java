/*
 * Created on 24-Nov-2004
 */
package com.ixora.common.logging.adapters.java;

import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerProvider;

/**
 * AppLoggerProviderJava
 */
public final class AppLoggerProviderJava implements AppLoggerProvider {

	/**
	 * Constructor for AppLoggerProviderJava.
	 */
	public AppLoggerProviderJava() {
		super();
	}

	/**
	 * @see com.ixora.common.logging.AppLoggerProvider#getLogger(String)
	 */
	public AppLogger getLogger(String name) {
	    return new AppLoggerJava(name);
	}
}

