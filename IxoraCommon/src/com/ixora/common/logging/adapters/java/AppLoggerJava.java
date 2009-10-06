/*
 * Created on 24-Nov-2004
 */
package com.ixora.common.logging.adapters.java;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.ixora.common.logging.AppLogger;
import com.ixora.common.security.license.exception.LicenseException;
import com.ixora.common.utils.Utils;

/**
 * AppLoggerJava
 */
public final class AppLoggerJava implements AppLogger {

    /**
     * The real logger object wrapped by this class. Same instance of
     * logger may be shared by more AppLoggerJava objects.
     */
    private Logger logger;

	/**
	 * Constructor for AppLoggerJava.
	 * @param name
	 */
	public AppLoggerJava(String name) {
        logger = Logger.getLogger(name);
	}

	/**
	 * @see com.ixora.common.logging.AppLogger#isWarnEnabled()
	 */
	public boolean isWarnEnabled() {
		return logger.isLoggable(Level.WARNING);
	}

	/**
	 * @see com.ixora.common.logging.AppLogger#isTraceEnabled()
	 */
	public boolean isTraceEnabled() {
	    return logger.isLoggable(Level.FINE);
	}

    /**
     * @see com.ixora.common.logging.AppLogger#isInfoEnabled()
     */
    public boolean isInfoEnabled() {
        return logger.isLoggable(Level.INFO);
    }

	/**
	 * @see com.ixora.common.logging.AppLogger#info(String)
	 */
	public void info(String msg) {
	    logger.info(msg);
	}

	/**
	 * @see com.ixora.common.logging.AppLogger#warn(String)
	 */
	public void warn(String msg) {
	    logger.warning(msg);
	}

	/**
	 * @see com.ixora.common.logging.AppLogger#trace(String)
	 */
	public void trace(String msg) {
	    logger.fine(msg);
	}

	/**
	 * @see com.ixora.common.logging.AppLogger#error(String)
	 */
	public void error(String msg) {
	    logger.severe(msg);
	}

	/**
	 * @see com.ixora.common.logging.AppLogger#error(String, Throwable)
	 */
	public void error(String msg, Throwable ex) {
        if(ex instanceof LicenseException) {
            return;
        }
        logger.severe(msg + Utils.getNewLine() + Utils.getTrace(ex).toString());
	}

	/**
	 * @see com.ixora.common.logging.AppLogger#error(Throwable)
	 */
	public void error(Throwable ex) {
        if(ex instanceof LicenseException) {
            return;
        }
	    logger.severe(Utils.getTrace(ex).toString());
	}
}

