/**
 * 20-Aug-2005
 */
package com.ixora.common.process;

import java.io.OutputStream;

import com.ixora.common.Startable;

/**
 * @author Daniel Moraru
 */
public interface ProcessWrapper extends Startable {
	/**
	 * Listener.
	 */
	public interface Listener {
		/**
		 * Invoked when a line is available from the process
		 * error stream.
		 * @param line
		 */
		void error(String line);
		/**
		 * Invoked when a line is available from the process
		 * output stream.
		 * @param line
		 */
		void output(String line);
	}

	/**
	 * @return The process input
	 */
	OutputStream getProcessInput();

    /**
	 * Waits for the process to finish.
     * @throws Exception
	 */
    void waitFor() throws Exception;
}