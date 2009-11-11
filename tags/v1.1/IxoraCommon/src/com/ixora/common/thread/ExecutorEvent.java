/*
 * Created on 24-Sep-2004
 */
package com.ixora.common.thread;

import com.ixora.common.StartableState;


/**
 * Event.
 */
public final class ExecutorEvent {
    /** Startable state */
    private StartableState startableState;
    /** Startable ID */
    private String startableID;
    /** Exception associated with the error state */
    private Exception exception;

    /** Creates new Event */
    public ExecutorEvent(String startableID, StartableState state) {
        this.startableState = state;
        this.startableID = startableID;
    }

    /** Creates new Event */
    public ExecutorEvent(String startableID, StartableState state, Exception e) {
    	this.startableState = state;
    	this.startableID = startableID;
    	this.exception = e;
    }

    /**
     * @return the state of the managed startable
     */
    public StartableState getStartableState() {
        return this.startableState;
    }

    /**
     * @return the ID of the managed startable
     */
    public String getStartableID() {
        return this.startableID;
    }
	/**
	 * @return the exception associated with the error state
	 */
	public Exception getException() {
		return exception;
	}

}