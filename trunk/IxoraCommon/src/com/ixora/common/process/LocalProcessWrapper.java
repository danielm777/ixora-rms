package com.ixora.common.process;

import java.io.IOException;
import java.io.OutputStream;

import com.ixora.common.StreamConsumer;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;

/**
 * Process wrapper.
 * @author Daniel Moraru
 */
public class LocalProcessWrapper implements ProcessWrapper {

	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(LocalProcessWrapper.class);
    /** Command used to launch the process */
    private String fCommand;
    /** Process */
    private Process fProcess;
    /** Output stream eater */
    private StreamConsumer fOutputStreamEater;
    /** Error stream eater */
    private StreamConsumer fErrorStreamEater;
    /** Listener */
    private Listener fListener;


	/**
	 * Constructor.
	 * @param proc
	 * @param listener it can be null
	 */
    public LocalProcessWrapper(Process proc, Listener listener) {
        if(proc == null) {
            throw new IllegalArgumentException("process is null");
        }
        this.fProcess = proc;
        this.fListener = listener;
    }

	/**
	 * Constructor.
	 * @param cmd the command used to launch the process
	 * @param listener it can be null
	 */
    public LocalProcessWrapper(String cmd, Listener listener) {
        if(cmd == null || cmd.length() == 0) {
            throw new IllegalArgumentException("invalid command");
        }
        this.fCommand = cmd;
        this.fListener = listener;
    }

    /**
	 * @see com.ixora.common.process.ProcessWrapper#start()
	 */
    public void start() throws Throwable {
        try {
    		if(logger.isTraceEnabled()) {
    			logger.trace("Starting process with command line: " + this.fCommand);
    		}
        	if(this.fProcess == null) {
        		this.fProcess = Runtime.getRuntime().exec(this.fCommand);
        	}
            this.fOutputStreamEater = new StreamConsumer(
                                             this.fProcess.getInputStream(),
                                             this.fListener == null ? null :
                                             new StreamConsumer.Listener() {
                                             	public void out(String line) {
                                             		fListener.output(line);
                                             	}
                                             });
            this.fErrorStreamEater = new StreamConsumer(
                                            this.fProcess.getErrorStream(),
											this.fListener == null ? null :
											new StreamConsumer.Listener() {
											   public void out(String line) {
												   fListener.error(line);
											   }
											});
            this.fOutputStreamEater.start();
            this.fErrorStreamEater.start();
            return;
        } catch(IOException e) {
            throw e;
        }
    }

	/**
	 * @see com.ixora.common.process.ProcessWrapper#getProcessInput()
	 */
	public OutputStream getProcessInput() {
		return this.fProcess.getOutputStream();
	}

    /**
	 * @see com.ixora.common.process.ProcessWrapper#stop()
	 */
    public void stop() {
        try {
            if(this.fProcess != null) {
                this.fProcess.destroy();
                this.fProcess.waitFor();
            }
        } catch(InterruptedException e) {
            logger.error(e);
        }
    }

    /**
	 * @see com.ixora.common.process.ProcessWrapper#waitFor()
	 */
    public void waitFor() {
        try {
            if(this.fProcess != null) {
                this.fProcess.waitFor();
            }
        } catch(InterruptedException e) {
            logger.error(e);
        }
    }
}