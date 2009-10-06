/**
 * 22-Jul-2005
 */
package com.ixora.common.process;

import java.rmi.Remote;

import com.ixora.common.Startable;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.remote.Shutdownable;
import com.ixora.common.utils.Utils;

/**
 * @author Daniel Moraru
 */
public class RMIProcessWrapper implements Startable {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(RMIProcessWrapper.class);
    /**
     * Number of times it retries to get an RMI ref when the RMI process is started.
     */
    private static final int RETRY_CONNECT_TO_RMI_PROCESS = 10;
    /**
     * Number of times it waits before retrying to get an RMI object ref
     */
    private static final int RETRY_WAIT_TIME = 1000;
    /** The RMI process */
    private ProcessWrapper fProcess;
    /** RMI port */
    private int fPort;
    /** Host name */
    private String fHost;
    /** RMI object name */
    private String fObjectName;
    /** RMI object */
    private Remote fObject;

    /**
     * @param proc
     * @param host
     * @param port
     * @param objectName
     */
    public RMIProcessWrapper(LocalProcessWrapper proc, String host, int port, String objectName) {
		fProcess = proc;
		fHost = host;
		fPort = port;
		fObjectName = objectName;
    }

    /**
     * @param cmd
     * @param listener
     * @param host
     * @param port
     * @param objectName
     */
    public RMIProcessWrapper(String cmd, ProcessWrapper.Listener listener, String host, int port, String objectName) {
		fProcess = new LocalProcessWrapper(cmd, listener);
		fHost = host;
		fPort = port;
		fObjectName = objectName;
    }

	/**
	 * @see com.ixora.common.Startable#start()
	 */
	public void start() throws Throwable {
        fProcess.start();
		// try to get reference
		int ret = 0;
		String objName = "//" + fHost + ":" + fPort + "/" + fObjectName;
		Throwable lastException = null;
		while(ret < RETRY_CONNECT_TO_RMI_PROCESS) {
		    try {
		        fObject = java.rmi.Naming.lookup(objName);
		        if(fObject != null) {
		        	return;
		        }
		    } catch(Exception e) {
		        lastException = e;
		    }
	        Thread.sleep(RETRY_WAIT_TIME);
		    ++ret;
		}
		if(logger.isTraceEnabled()) {
			logger.trace("Failed to retrieve object " + objName
					+ " after " + ret + " retries. Error: " + Utils.getTrace(lastException));
		}
	    throw lastException;
	}

	/**
	 * If the remote object is an instance of com.ixora.common.remote.Shutdownable, the <code>shutdown()</code>
	 * method will be invoked before destroying the process.
	 * @see com.ixora.common.Startable#stop()
	 */
	public void stop() throws Throwable {
		if(fProcess == null) {
			return;
		}
		if(fObject instanceof Shutdownable) {
			try {
				((Shutdownable)fObject).shutdown();
			} catch(Exception e) {
				logger.error(e);
			}
		}
    	try {
	    	// and (this is a VERY IMPORTANT step)
	    	// unregister the name from the rmiregistry
	    	// since RMI is using transient object references
	    	java.rmi.Naming.unbind("//" + fHost + ":" + fPort + "/" + fObjectName);
    	} catch(Exception e) {
    		// this is normal as shutdown will kill the process hosting
    		// the RMI registry
    		if(logger.isTraceEnabled()) {
    			logger.error(e);
    		}
    	}
		fProcess.stop();
    	fProcess = null;
	}

	/**
	 * @return
	 */
	public Remote getRMIObject() {
		return fObject;
	}
}
