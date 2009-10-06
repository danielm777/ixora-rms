package com.ixora.common.exception;


/**
 * Thrown when the application/component configuration
 * cannot be saved.
 * @author Daniel Moraru
 */
public final class FailedToSaveConfiguration extends AppException {

	/**
	 * Constructor for FailedToSaveConfiguration.
	 * @param e
	 */
	public FailedToSaveConfiguration(Throwable e) {
		super(e);
	}
}
