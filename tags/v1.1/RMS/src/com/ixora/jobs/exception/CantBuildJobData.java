/*
 * Created on 28-Sep-2004
 */
package com.ixora.jobs.exception;

import com.ixora.common.exception.AppException;

/**
 * @author Daniel Moraru
 */
public final class CantBuildJobData extends AppException {
	private static final long serialVersionUID = -4582935278421313877L;

	/**
     * Constructor.
     * @param cause
     */
    public CantBuildJobData(Throwable cause) {
        super(cause);
    }
}
