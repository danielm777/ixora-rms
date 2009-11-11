/*
 * Created on 25-Dec-2005
 */
package com.ixora.rms.agents.file.messages;

/**
 * @author Daniel Moraru
 */
public interface Msg {
	public static final String FILE_NAME = "agents.file";

	// config
	public static final String ROOT_FOLDER = "config.root_folder";
	public static final String FILE_NAME_PATTERN = "config.file_name_pattern";
	public static final String IGNORE_FOLDERS = "config.ignore_folders";

	// counters
	public static final String COUNTER_SIZE_BYTES = "size_bytes";
	public static final String COUNTER_LAST_MODIFIED = "last_modified";

	// errors
	public static final String ERROR_INVALID_PATH = "error.invalid_path";
}
