package com.ixora.common;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;

/**
 * Class used to consume the streams of launched processes.
 * @author: Daniel Moraru
 */
final public class StreamConsumer extends Thread {
	/**
	 * Listener.
	 */
	public interface Listener {
		/**
		 * Invoked when a line becomes available on the managed stream.
		 * @param line
		 */
		void out(String line);
	}

	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(StreamConsumer.class);
	/** Reader on the input stream */
    private BufferedReader is;
	/** Listener */
	private Listener listener;

    /**
     * StreamDataEater constructor comment.
     * @param processInputStream the process input stream.
     * @param listener it can be null
     */
    public StreamConsumer(InputStream processInputStream, Listener listener) {
    	super("ProcessStreamConsumer");
    	if(processInputStream == null) {
    		throw new IllegalArgumentException("null stream");
    	}
        this.is = new BufferedReader(new InputStreamReader(processInputStream));
        this.listener = listener;
    }

    /**
     * @see java.lang.Thread#run()
     */
    public void run() {
        try {
            String line;
            while((line = is.readLine()) != null) {
                if(this.listener != null) {
                    this.listener.out(line);
                }
            }
        } catch(IOException e) {
            logger.error(e);
        }
    }
}