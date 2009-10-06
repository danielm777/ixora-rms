package com.ixora.common.pooling;

/**
 * InactivityControllerFactory
 * @author Daniel Moraru
 */
public final class InactivityControllerFactory {
	/**
	 * Constructor for InactivityControllerFactory.
	 */
	private InactivityControllerFactory() {
		super();
	}

	/**
	 * @return An inactivity controller that clears objects that have been inactive
	 * for more then <code>timeOut</code> millisecond. A cleaner thread will be used
	 * which will run every <code>cleanupInterval</code> seconds.
	 */
	public static InactivityController createTimingOutController(long timeOut, int cleanupInterval) throws Exception {
		return new TimedInactivityController(timeOut, cleanupInterval);
	}
}
