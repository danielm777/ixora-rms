/**
 * 21-Mar-2006
 */
package com.ixora.rms.batch.messages;

/**
 * @author Daniel Moraru
 */
public interface Msg {
	public static final String PORT_NUMBER_MISSING =
		"batch.port_number_missing";
	public static final String SESSION_PROCESS_NOT_STARTED =
		"batch.session_process_not_started";
	public static final String STARTING_SESSION =
		"batch.starting_session";
	public static final String SESSION_STARTED =
		"batch.session_started";
	public static final String FAILED_TO_START_SESSION =
		"batch.failed_to_start_session";
	public static final String PRESS_ENTER_TO_EXIT_CONSOLE =
		"batch.press_enter_to_exit_console";
	public static final String SESSION_PROCESS_WAS_STOPPED =
		"batch.session_process_was_stopped";
	public static final String SESSION_STARTED_ON_PORT =
		"batch.session_started_on_port";
	public static final String SESSION_STOPPING =
		"batch.session_stopping";
}
