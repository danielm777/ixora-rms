package com.ixora.common;


/**
 * @author Daniel Moraru
 */
public interface Startable {
    /**
     * Starts the processing.
     * @throws Throwable
     */
    void start() throws Throwable;

    /**
     * Instances must stop processing.
     * @throws Throwable
     */
    void stop() throws Throwable;
}