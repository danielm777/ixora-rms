/*
 * Created on 05-Jun-2005
 */
package com.ixora.common.ui;

/**
 * NonFatalErrorHandler
 */
public interface NonFatalErrorHandler {
    /**
     * Invoked when a non-fatal error occurred.
     * @param error
     * @param t exception acompanying the error; it can be null
     */
    void nonFatalError(String error, Throwable t);
}
