package com.ixora.common.pooling.exception;

import com.ixora.common.exception.AppException;

/**
 * Thrown when a pool fails to create a new object.
 * @author Daniel Moraru
 */
public final class FailedToCreateObject extends AppException {
	private static final long serialVersionUID = -4640849746825868976L;

	/**
	 * Constructor for FailedToCreateObject.
	 */
	public FailedToCreateObject() {
		super();
	}

	/**
	 * Constructor for FailedToCreateObject.
	 * @param e
	 */
	public FailedToCreateObject(Exception e) {
		super(e);
	}

}
