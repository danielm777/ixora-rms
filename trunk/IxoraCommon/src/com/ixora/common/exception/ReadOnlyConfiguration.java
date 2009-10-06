package com.ixora.common.exception;


/**
 * Thrown when the application/component tries to modify a read only
 * configuration.
 * @author Daniel Moraru
 */
public final class ReadOnlyConfiguration extends AppException {

	/**
	 * Constructor for ReadOnlyConfiguration.
	 * @param confFile
	 * @param e
	 */
	public ReadOnlyConfiguration() {
		super();
	}
}
