/*
 * Created on 28-Dec-2004
 */
package com.ixora.rms.providers.impl.process;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.process.LocalProcessWrapper;
import com.ixora.common.process.ProcessWrapper;
import com.ixora.common.process.RemoteProcessWrapper;
import com.ixora.common.utils.Utils;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.providers.ProviderConfiguration;
import com.ixora.rms.providers.ProviderCustomConfiguration;
import com.ixora.rms.providers.exception.InvalidProviderConfiguration;
import com.ixora.rms.providers.impl.AbstractProvider;

/**
 * A provider based on an external process. The process might be executed accross a
 * remote connection to the host.
 * @author Daniel Moraru
 */
public class ProviderProcess extends AbstractProvider {
    /** Logger */
    private static final AppLogger logger = AppLoggerFactory.getLogger(ProviderProcess.class);
	/** Process wrapper */
	private ProcessWrapper fProcess;
	/** True if the provider was started */
	private boolean fRunning;
	/** Current buffer */
	private List<String> fCurrentBuffer;
    /**
     * OS command that launches the process. It is used often so
     * it is cached here rather than read from the configuration.
     */
    private String fCommand;
	/**
     * Whether or not the process dies after each burst of
     * data and needs to be restarted. It is cheked often so
     * it is cached here rather than read from the configuration.
     */
    private boolean fIsVolatile;
    /**
     * Line marking the end of a data buffer; If not null all lines received from
     * the process will be buffered untile a line matching this marker is
     * received.
     */
    private Pattern fEndOfBufferMarker;
    /**
     * If the process is volatile this timer is used to check the state
     * of the process; if the process has stopped it will be restarted.
     */
    private Timer fVolatileProcessMonitor;
    /** Event handler */
    private EventHandler fEventHandler;
    /** RemoteConnection */
    private RemoteConnection fConn;
    /** True whe the provider is stopping */
    private boolean fStopping;
	/**
	 * Event handler.
	 */
	private class EventHandler implements RemoteProcessWrapper.Listener {
		/**
		 * @see com.ixora.common.process.ProcessWrapper.Listener#error(java.lang.String)
		 */
		public void error(String line) {
			fireError(line, new RMSException(line));
		}
		/**
		 * @see com.ixora.common.process.ProcessWrapper.Listener#output(java.lang.String)
		 */
		public void output(String line) {
			handleOutput(line);
		}
		/**
		 * @see com.ixora.common.process.RemoteProcessWrapper.Listener#remoteError(java.lang.Exception)
		 */
		public void remoteError(Exception ex) {
			fireError(ex.getLocalizedMessage(), ex);
		}
	}

	/**
	 * Constructor.
	 */
	public ProviderProcess() {
		super();
        fEventHandler = new EventHandler();
	}

	/**
	 * @throws InvalidProviderConfiguration
	 * @throws Throwable
	 * @throws RMSException
	 * @see com.ixora.rms.providers.Provider#configure(com.ixora.rms.internal.providers.ProviderConfiguration)
	 */
	public void configure(ProviderConfiguration conf) throws InvalidProviderConfiguration, RMSException, Throwable {
        super.configure(conf);
        // nullify cached data
        if(fConn != null) {
        	try {
        		fConn.disconnect();
        	} catch(Exception e) {
				fireError("Failed to disconnect the existing remote connection", e);
			}
            fConn = null;
        }
        ProviderCustomConfiguration custom = conf.getProviderCustomConfiguration();
        if(custom == null) {
            return;
        }
        fCommand = custom.getString(Configuration.COMMAND);
        if(fCommand == null) {
            throw new InvalidProviderConfiguration("Command cannot be null");
        }

        // stop monitor first
        if(this.fVolatileProcessMonitor != null) {
            this.fVolatileProcessMonitor.purge();
        }

		boolean mustRestart = fRunning;
		try {
			stop();
		} catch (Throwable e) {
			fireError("Failed to stop old process", e);
		}

        Object obj = custom.getObject(Configuration.PROCESS_IS_VOLATILE);
        this.fIsVolatile = (obj != null && ((Boolean)obj).booleanValue());
        obj = custom.getObject(Configuration.PROCESS_END_OF_BUFFER_MARKER);
        if(obj != null) {
            String pat = (String)obj;
            if(Utils.isEmptyString(pat)) {
                fEndOfBufferMarker = null;
            } else {
                this.fEndOfBufferMarker = Pattern.compile(pat);
            }
        } else {
            this.fEndOfBufferMarker = null;
        }

        // at this moment both the process and the monitor are stopped so ot should
        // be safe thread-wise to rebuild the current buffer and the process
        if(fIsVolatile || fEndOfBufferMarker != null) {
			fCurrentBuffer = new LinkedList<String>();
		}

		if(mustRestart) {
			try {
				start();
			} catch (Throwable e1) {
				fireError("Failed to restart process", e1);
			}
		}

        if(this.fIsVolatile) {
        	this.fVolatileProcessMonitor = new Timer(true);
            this.fVolatileProcessMonitor.schedule(new TimerTask(){
				public void run() {
					handleVolatileProcessMonitorAction();
				}

            }, 0, 1);
        }
	}

	/**
	 * @throws RMSException
	 */
	private void createRemoteConnectionIfNeeded() throws RMSException {
		if(fConn != null) {
			return;
		}
		if(!RemoteConnection.requiresRemoteConnection(fConfiguration)) {
			return;
		}
		fConn = new RemoteConnection(fConfiguration);
	}


	/**
	 * @throws Throwable
	 *
	 */
	private void createProcess() throws Throwable {
		// at this stage the connection must have been created if required
        if(fConn == null) {
        	fProcess = new LocalProcessWrapper(fCommand, fEventHandler);
        } else {
        	if(fProcess != null) {
        		fProcess.stop();
        	}
			fProcess = fConn.createProcess(fConfiguration, fCommand, fEventHandler);
        }
	}

	/**
	 * @see com.ixora.common.Startable#start()
	 */
	public void start() throws Throwable {
        synchronized (this) {
   			fStopping = false;
        	createRemoteConnectionIfNeeded();
        	createProcess();
   			fProcess.start();
   			fRunning = true;
        }
	}

	/**
	 * @see com.ixora.common.Startable#stop()
	 */
	public void stop() throws Throwable {
        synchronized (this) {
        	try {
        		fStopping = true;
	    		if(fProcess != null) {
	    			fProcess.stop();
	    			fRunning = false;
	    		}
	    		if(fConn != null) {
	   				fConn.disconnect();
	    			fConn = null;
	    		}
				fProcess = null;
        	} finally {
        		fStopping = false;
        	}
        }
	}

	/**
	 * @see com.ixora.rms.providers.Provider#cleanup()
	 */
	public void cleanup() {
        try {
            stop();
        } catch (Throwable e) {
            logger.error(e);
        }
	}

	/**
	 * This provider doesn't need a collector to run on so this method returns false.
	 * @see com.ixora.rms.providers.Provider#requiresExternalCollector()
	 */
	public boolean requiresExternalCollector() {
		return false;
	}

	/**
	 * @param line
	 */
	private void handleOutput(String line) {
        synchronized (this) {
        	if(fStopping || !fRunning) { // don't send data when stopping as it might be incomplete
        		fCurrentBuffer.clear();
        		return;
        	}
    		if(!fIsVolatile && fEndOfBufferMarker == null) {
    			// no buffer defined
    			fireData(new String[] {line});
    		} else {
                if(fEndOfBufferMarker != null && fEndOfBufferMarker.matcher(line).find()) {
                   flushCurrentBuffer();
                   return;
                }
    			// add data to buffer
    			fCurrentBuffer.add(line);
    		}
        }
	}

    /**
     * Flushes the current buffer for volatile process.
     */
    private void flushCurrentBuffer() {
        synchronized (this) {
        	if(fStopping || !fRunning) { // don't send data when stopping as it might be incomplete
        		fCurrentBuffer.clear();
        		return;
        	}
            if(fCurrentBuffer.size() > 0) {
                String[] buff = fCurrentBuffer.toArray(new String[fCurrentBuffer.size()]);
                fireData(buff);
                // clean up buffer
                fCurrentBuffer.clear();
            }
        }
    }

    /**
     *
     */
    private void handleVolatileProcessMonitorAction() {
       try {
           if(!fRunning) {
               return;
           }
           // if it's supposed to be running
           // restart it if stopped
           if(this.fProcess != null) {
               // wait for the process to die
        	   long timeToWait = 1000 * this.fConfiguration.getSamplingInterval().intValue();
               long timeIn = System.currentTimeMillis();
               this.fProcess.waitFor();
               long timeOut = System.currentTimeMillis();
               timeToWait = timeToWait - (timeOut - timeIn);

               // flush buffer
               flushCurrentBuffer();

               // respect the sampling interval
               if(timeToWait > 0) {
                   Thread.sleep(timeToWait);
               }

               synchronized(this) {
                   // restart if still needed
                   if(fRunning) {
                	   // create process using the same telnet connection
                       createProcess();
                       fProcess.start();
                   }
               }
           }
       } catch(InterruptedException s) {
    	   ; // ignore, it's normal when the provider is stopped
       } catch(Throwable e) {
           this.fireError("Unexpected error", e);
       }
    }
}
