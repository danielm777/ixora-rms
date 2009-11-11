/*
 * Created on 14-Nov-2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.ixora.common.logging.adapters.log4j;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import com.ixora.common.logging.AppLogger;
import com.ixora.common.security.license.exception.LicenseException;

/**
 * Adapter to a Log4j logger.
 * @author Daniel Moraru
 */
public final class AppLoggerLog4j extends Logger implements AppLogger {

	/**
	 * Constructor for AppLoggerLog4j.
	 * @param arg0
	 */
	public AppLoggerLog4j(String arg0) {
		super(arg0);
	}

	/**
	 * @see com.ixora.common.logging.AppLogger#isWarnEnabled()
	 */
	public boolean isWarnEnabled() {
		return super.isEnabledFor(Priority.WARN);
	}

	/**
	 * @see com.ixora.common.logging.AppLogger#isTraceEnabled()
	 */
	public boolean isTraceEnabled() {
		return super.isEnabledFor(Priority.DEBUG);
	}

	/**
	 * @see com.ixora.common.logging.AppLogger#info(String)
	 */
	public void info(String msg) {
		super.info(msg);
	}

	/**
	 * @see com.ixora.common.logging.AppLogger#warn(String)
	 */
	public void warn(String msg) {
		super.warn(msg);
	}

	/**
	 * @see com.ixora.common.logging.AppLogger#trace(String)
	 */
	public void trace(String msg) {
		super.debug(msg);
	}

	/**
	 * @see com.ixora.common.logging.AppLogger#error(String)
	 */
	public void error(String msg) {
		super.error(msg);
	}

	/**
	 * @see com.ixora.common.logging.AppLogger#error(String, Throwable)
	 */
	public void error(String msg, Throwable ex) {
        if(ex instanceof LicenseException) {
            return;
        }
		super.error(msg, ex);
	}

	/**
	 * @see com.ixora.common.logging.AppLogger#error(Throwable)
	 */
	public void error(Throwable ex) {
        if(ex instanceof LicenseException) {
            return;
        }
		super.error("", ex);
	}
}

