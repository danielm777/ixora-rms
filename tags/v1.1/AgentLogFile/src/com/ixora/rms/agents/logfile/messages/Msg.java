/*
 * Created on 25-Dec-2005
 */
package com.ixora.rms.agents.logfile.messages;

/**
 * @author Daniel Moraru
 */
public interface Msg {
	public static final String LOG_FILE_NAME = "agents.logfile";

	// config
	public static final String LOG_FILE_PATH = "log_file_path";
	public static final String LOG_RECORD_REGEX = "log_record_regex";
	public static final String FILE_ENCODING = "file_encoding";
	public static final String LOG_RECORD_BEGIN_REGEX = "log_record_begin_regex";
	public static final String LOG_RECORD_END_REGEX = "log_record_end_regex";
	public static final String AVAILABLE_LOG_PARSERS = "available_log_parsers";
	public static final String LOG_PARSER_CODE = "log_parser_code";
	public static final String CURRENT_LOG_PARSER = "current_log_parser";

	// errors
	public static final String ERROR_INVALID_PATH = "error.invalid_path";

	// counter
	public static final String COUNTER_LOG_RECORDS = "log_records";
	public static final String COUNTER_SIZE_BYTES = "file_size";
	public static final String COUNTER_LAST_MODIFIED = "last_changed";
}
