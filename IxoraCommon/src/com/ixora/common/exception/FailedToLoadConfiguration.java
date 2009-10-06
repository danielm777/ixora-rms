package com.ixora.common.exception;

/**
 * Thrown when the application/component configuration
 * cannot be loaded.
 * @author Daniel Moraru
 */
public final class FailedToLoadConfiguration extends AppException {

	/**
	 * Constructor for FailedToLoadConfiguration.
	 * @param e
	 */
	public FailedToLoadConfiguration(Exception e) {
		super(e);
	}
}
