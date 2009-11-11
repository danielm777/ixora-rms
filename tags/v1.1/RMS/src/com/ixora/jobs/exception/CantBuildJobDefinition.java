/*
 * Created on 28-Sep-2004
 */
package com.ixora.jobs.exception;

import com.ixora.common.exception.AppException;

/**
 * @author Daniel Moraru
 */
public final class CantBuildJobDefinition extends AppException {
	private static final long serialVersionUID = -8827639120049116380L;

	/**
     * Constructor.
     * @param cause
     */
    public CantBuildJobDefinition(Throwable cause) {
        super(cause);
    }
}
