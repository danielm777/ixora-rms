package com.ixora.common.logging;

/**
 * Logger interface. Implemented by adapters to various logging libraries.
 * @author Daniel Moraru
 */
public interface AppLogger {
	/**
	 * @return True if info level is enabled.
	 */
	boolean isInfoEnabled();
	/**
	 * @return True if warn level is enabled.
	 */
	boolean isWarnEnabled();
	/**
	 * @return True if trace level is enabled.
	 */
	boolean isTraceEnabled();
	/**
	 * Info message.
	 * @param msg
	 */
	void info(String msg);
	/**
	 * Warn message.
	 * @param msg
	 */
	void warn(String msg);
	/**
	 * Trace message.
	 * @param msg
	 */
	void trace(String msg);
	/**
	 * Error message.
	 * @param msg
	 */
	void error(String msg);
	/**
	 * Error message.
	 * @param msg
	 * @param ex
	 */
	void error(String msg, Throwable ex);
	/**
	 * Error message.
	 * @param ex
	 */
	void error(Throwable ex);
}
