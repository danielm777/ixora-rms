/**
 * 20-Aug-2005
 */
package com.ixora.common.process;

import java.io.IOException;
import java.io.OutputStream;

import com.ixora.common.net.telnet.TelnetConnection;

/**
 * @author Daniel Moraru
 */
public class TelnetProcessWrapper implements RemoteProcessWrapper {
	private RemoteProcessWrapper.Listener fListener;
	private String fCommand;
	private TelnetConnection fConn;
	private volatile boolean fFinished;

	/**
	 * @param conn the telnet connection will be closed when <code>stop()</code> will be called but it's passed
	 * here as a parameter and it is recommended to be created outside this class as future versions of this class might
	 * be able to reuse a telnet connection...
	 * @param cmd the command to execute
	 * @param listener
	 */
	public TelnetProcessWrapper(TelnetConnection conn, String cmd, RemoteProcessWrapper.Listener listener) {
		super();
		fCommand = cmd;
		fConn = conn;
		fListener = listener;
	}

	/**
	 * @see com.ixora.common.process.ProcessWrapper#getProcessInput()
	 */
	public OutputStream getProcessInput() {
		return fConn.getOutputStream();
	}

	/**
	 * @see com.ixora.common.Startable#start()
	 */
	public void start() throws Throwable {
		fConn.execute(
				fCommand,
				new TelnetConnection.CommandListener() {
					public void commandNotFound(String cmd) {
						fListener.remoteError(new IOException("Remote command " + fCommand + " was not found."));
					}
					public void prompt() {
						finished();
					}
					public void commandOutput(String line) {
						fListener.output(line);
					}
				});
	}

	/**
	 * @see com.ixora.common.Startable#stop()
	 */
	public void stop() throws Throwable {
		disconnect();
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
		disconnect();
	}

	private void disconnect() throws IOException {
		// do it by stopping conn for now...
		fConn.disconnect();
	}


	private void finished() {
		fFinished = true;
		synchronized(this) {
			notifyAll();
		}
	}
}
