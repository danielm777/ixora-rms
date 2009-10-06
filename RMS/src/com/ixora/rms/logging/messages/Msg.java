/*
 * Created on 09-Jun-2004
 */
package com.ixora.rms.logging.messages;

/**
 * @author Daniel Moraru
 */
public interface Msg {
	public static final String LOGGING_CANT_OPEN_FILE_FOR_READING =
		"logging.cant_open_file_for_reading";
    public static final String INVALID_LOG =
        "logging.invalid_log";
	public static final String LOGGING_CANT_OPEN_FILE_FOR_WRITING =
		"logging.cant_open_file_for_writing";
	public static final String LOGGING_UNRECOGNIZED_LOG_TYPE =
	    "logging.unrecognized_log_type";
    public static final String LOGGING_NO_LOG_WAS_LOADED =
        "logging.no_log_was_loaded";
}
