package com.ixora.common.exception;


/**
 * Thrown when the application/component configuration
 * cannot be saved.
 * @author Daniel Moraru
 */
public final class FailedToSaveConfiguration extends AppException {
	private static final long serialVersionUID = -7833638774761212831L;

	/**
	 * Constructor for FailedToSaveConfiguration.
	 * @param e
	 */
	public FailedToSaveConfiguration(Throwable e) {
		super(e);
	}
}
