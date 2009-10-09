package com.ixora.common.exception;

/**
 * Thrown when the application/component configuration
 * cannot be loaded.
 * @author Daniel Moraru
 */
public final class FailedToLoadConfiguration extends AppException {
	private static final long serialVersionUID = 16038281807506155L;

	/**
	 * Constructor for FailedToLoadConfiguration.
	 * @param e
	 */
	public FailedToLoadConfiguration(Exception e) {
		super(e);
	}
}
