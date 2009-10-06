/**
 * 20-Aug-2005
 */
package com.ixora.common.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.regex.Pattern;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;

import com.ixora.common.exception.AppRuntimeException;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;

/**
 * @author Daniel Moraru
 */
public class SSH2ProcessWrapper implements RemoteProcessWrapper {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(SSH2ProcessWrapper.class);
	private RemoteProcessWrapper.Listener fListener;
	private String fCommand;
	private Connection fConn;
	private Thread fStreamConsumerOut;
	private Thread fStreamConsumerErr;
	private Session fSession;
	private Pattern fCommandNotFoundPattern;
	private volatile boolean fFinished;

	/**
	 * @param conn SSH2 connection, it will be unaffected by this class
	 * @param cmd the command to execute
	 * @param cmdNotFoundPattern the regex pattern used to detect that the command was not found
	 * on the remote system
	 * @param listener
	 */
	public SSH2ProcessWrapper(Connection conn, String cmd, String cmdNotFoundPattern, RemoteProcessWrapper.Listener listener) {
		super();
		fCommand = cmd;
		fConn = conn;
		fCommandNotFoundPattern = Pattern.compile(cmdNotFoundPattern);
		fListener = listener;
	}

	/**
	 * @see com.ixora.common.process.ProcessWrapper#getProcessInput()
	 */
	public OutputStream getProcessInput() {
		return fSession == null ? null : fSession.getStdin();
	}

	/**
	 * @see com.ixora.common.Startable#start()
	 */
	public void start() throws Throwable {
		if(fSession != null) {
			throw new AppRuntimeException("The process has already been started");
		}
		fSession = fConn.openSession();
		final BufferedReader brOut = new BufferedReader(new InputStreamReader(fSession.getStdout()));
        fStreamConsumerOut = new Thread(new Runnable(){
			public void run() {
				try {
					int checks = 0;
					String line;
					while((line = brOut.readLine()) != null) {
						if(checks < 2) { // check the first two lines for command not found
							checkForCommandNotFound(line);
							checks++;
						}
						fListener.output(line);
					}
				} catch(Exception e) {
					; // it happens when it's stopped and when a network error occurs
					if(logger.isTraceEnabled()) {
						logger.error(e);
					}
				} finally {
					finished();
				}
			}
        }, "SSH2ProcessStreamReaderOut");
        fStreamConsumerOut.start();
		final BufferedReader brErr = new BufferedReader(new InputStreamReader(fSession.getStderr()));
        fStreamConsumerErr = new Thread(new Runnable(){
			public void run() {
				try {
					int checks = 0;
					String line;
					while((line = brErr.readLine()) != null) {
						if(checks < 2) { // check the first two lines for command not found
							checkForCommandNotFound(line);
							checks++;
						}
						fListener.error(line);
					}
				} catch(Exception e) {
					; // ignore, the out stream will do the work
					if(logger.isTraceEnabled()) {
						logger.error(e);
					}
				}
			}
        }, "SSH2ProcessStreamReaderError");
        fStreamConsumerErr.start();
        fSession.execCommand(fCommand);
	}

	/**
	 * @see com.ixora.common.Startable#stop()
	 */
	public void stop() throws Throwable {
		if(fSession != null) {
			fSession.close();
		}
		finished();
	}

	/**
	 * @throws InterruptedException
	 * @throws IOException
	 * @see com.ixora.common.process.ProcessWrapper#waitFor()
	 */
	public void waitFor() throws InterruptedException, IOException {
		if(fFinished) {
			return;
		}
		synchronized(this) {
			wait();
		}
	}

	/**
	 *
	 */
	private void finished() {
		fFinished = true;
		synchronized(this) {
			notifyAll();
		}
	}

	/**
	 * This method will only be invoked once per output stream to detect if the command
	 * was not found on the remote system
	 * @param line
	 */
	private void checkForCommandNotFound(String line) {
		if(fCommandNotFoundPattern.matcher(line).find()) {
			fListener.remoteError(new IOException("Remote command " + fCommand + " was not found."));
		}
	}
}
