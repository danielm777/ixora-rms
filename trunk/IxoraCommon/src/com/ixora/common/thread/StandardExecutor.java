package com.ixora.common.thread;

import com.ixora.common.Startable;
import com.ixora.common.StartableState;
import com.ixora.common.exception.AppException;
import com.ixora.common.exception.AppRuntimeException;
import com.ixora.common.exception.StartableIsAlreadyStarted;


/**
 * Standard implementation of an executor.
 * @author  Daniel Moraru
 */
public class StandardExecutor implements Executor {
    /** Startable instance whose execution is managed */
    protected Startable startable;
    /** State of the Startable instance managed */
    protected volatile StartableState state;
    /** Thread on which the Startables instance is run */
    protected Thread thread;
    /** Startable ID */
    protected String startableID;
    /** Called to stop the execution of the Startable instance */
    protected volatile boolean bStop = false;

    /** Listener */
    protected Executor.Listener listener;

    /** Creates new StandardExecutor */
    public StandardExecutor(Startable startable, String startableID, Listener listener) {
    	if(startable == null || startableID == null || listener == null) {
    		throw new IllegalArgumentException("null parameters");
    	}
        this.startable = startable;
        this.startableID = startableID;
        this.bStop = false;
        this.state = StartableState.NOT_STARTED;
        this.listener = listener;
    }

    /**
     * @see Executor#getStartable()
     */
    public Startable getStartable() {
        return this.startable;
    }

    /**
     * @see Executor#getStartableID()
     */
    public String getStartableID() {
        return this.startableID;
    }

    /**
     * @see Executor#getStartableState()
     */
    public StartableState getStartableState() {
		return this.state;
    }

    /**
     * @see Startable#start()
     * @throws StartableIsAlreadyStarted if the managed startable is already started
     */
    public void start() throws StartableIsAlreadyStarted {
    	this.bStop = false;
    	synchronized(this) {
	    	if(this.thread == null) {
				this.thread = new Thread() {
					 public void run() {
						StandardExecutor.this.run();
				 	}
			 	};
	    	}
    	}
        if(this.thread.isAlive()) {
            throw new StartableIsAlreadyStarted();
        }

        try {
            this.thread.start();
        } catch(Exception e) {
        	setStartableStateToError(e);
        	// this should not happen under normal circumstances
            throw new AppRuntimeException(e);
        }
    }

    /**
     * @see Startable#stop()
     */
    public void stop() {
    	synchronized(this) {
	    	try {
	        	if(this.thread == null || !this.thread.isAlive()) {
	                return;
	            }
	            stopStartable();
	            setStartableState(StartableState.STOPPED);
	            return;
	        } catch(InterruptedException e) {
	        	setStartableState(StartableState.STOPPED);
	        } catch(Throwable e) {
	        	setStartableStateToError(e);
	            return;
	        } finally {
	        	this.thread = null;
	        }
    	}
    }

    /**
     * The startable instance is run inside this method.
     */
    protected void run() {
        try {
        	setStartableState(StartableState.STARTED);
            try {
            	this.startable.start();
                if(!this.bStop) {
	                setStartableState(StartableState.FINISHED);
                }
            } catch(InterruptedException e) {
            	if(!this.bStop) {
            		setStartableStateToError(e);
            	}
            } catch(Throwable e) {
           		setStartableStateToError(e);
            }
        } catch(Exception e) {
        	setStartableStateToError(e);
        }
    }

    /**
     * Stops the startable.
     * @throws InterruptedException
     * @throws Throwable
     */
    protected void stopStartable() throws InterruptedException, Throwable {
        this.bStop = true;
        this.startable.stop();
        this.thread.interrupt();
        this.thread.join();
    }

    /**
     * Sets the state of the managed Startable instance.
     * @param StartableState newstate
     */
    protected void setStartableState(StartableState newstate) {
        this.state = newstate;
        this.listener.startableStateChanged(new ExecutorEvent(this.startableID, newstate));
    }

    /**
     * Sets the state of the managed Startable instance to error.
     * @param e the exception that caused the state transition to error
     */
    protected void setStartableStateToError(Throwable e) {
    	this.state = StartableState.ERROR;
    	this.listener.startableStateChanged(
    		new ExecutorEvent(
    			this.startableID,
				StartableState.ERROR,
				e instanceof Exception ? (Exception)e : new AppException(e)));
    }
}