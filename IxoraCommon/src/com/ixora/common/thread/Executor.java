package com.ixora.common.thread;

import com.ixora.common.Startable;
import com.ixora.common.StartableState;



/**
 * All classes that manage the execution of a
 * Startable instance should implement this interface.
 * @author  Daniel Moraru
 */
public interface Executor extends Startable {
	/**
	 * Executor Listener
	 */
	public interface Listener  {
	    /**
	     * Invoked when the state of the Executor's managed Startable instance
	     * has changed.
	     * @param Event
	     */
	    void startableStateChanged(ExecutorEvent ev);
	}

    /**
     * Returns the Startable instance managed.
     * @return Startable
     */
    Startable getStartable();

    /**
     * Returns the ID assigned to the managed Startable instance.
     * @return String
     */
    String getStartableID();

    /**
     * Returns the state of the managed Startable instance.
     * @return StartableState
     */
    StartableState getStartableState();
}