package com.ixora.common.exception;


/**
 * Thrown when the application/component tries to modify a read only
 * configuration.
 * @author Daniel Moraru
 */
public final class ReadOnlyConfiguration extends AppException {
	private static final long serialVersionUID = 3994745709705763062L;

	/**
	 * Constructor for ReadOnlyConfiguration.
	 * @param confFile
	 * @param e
	 */
	public ReadOnlyConfiguration() {
		super();
	}
}
