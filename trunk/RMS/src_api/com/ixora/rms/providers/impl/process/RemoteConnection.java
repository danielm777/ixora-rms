/**
 * 11-Mar-2006
 */
package com.ixora.rms.providers.impl.process;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import ch.ethz.ssh2.Connection;

import com.ixora.common.exception.AppRuntimeException;
import com.ixora.common.net.telnet.Telnet;
import com.ixora.common.net.telnet.TelnetConnection;
import com.ixora.common.process.ProcessWrapper;
import com.ixora.common.process.RemoteProcessWrapper;
import com.ixora.common.process.SSH2ProcessWrapper;
import com.ixora.common.process.TelnetProcessWrapper;
import com.ixora.rms.CustomConfiguration;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.providers.ProviderConfiguration;

/**
 * @author Daniel Moraru
 */
final class RemoteConnection {
	private static final String COMMAND_NOT_FOUND_PATTERN = "not found";
	/** Cached inet address for the remote telnet host */
    private InetAddress fInetAddress;
    /** Telnet connection */
	private TelnetConnection fTelnetConnection;
	/** SSH2 connection */
	private Connection fSSH2Connection;

	/**
	 * @throws RMSException
	 */
	public RemoteConnection(ProviderConfiguration config) throws RMSException {
		super();
        createConnection(config);
	}

	/**
	 * @param config
	 * @throws RMSException
	 */
	private void createConnection(ProviderConfiguration config) throws RMSException {
		Configuration custom = (Configuration)config.getCustom();
        String sem = custom.getString(Configuration.EXECUTION_MODE);
		ProcessExecutionMode execMode = ProcessExecutionMode.resolve(sem);
        if(execMode == null || execMode == ProcessExecutionMode.NORMAL) {
        	throw new AppRuntimeException("The execution mode is not remote: " + execMode.toString());
        }
        if(execMode == ProcessExecutionMode.TELNET) {
        	try {
        		if(fInetAddress == null) {
        			fInetAddress = InetAddress.getByName(config.getHost());
        		}
        		fTelnetConnection = Telnet.createConnection(
	        			fInetAddress,
	        			custom.getPort(),
	        			custom.getString(Configuration.USERNAME_PROMPT).trim(),
	        			custom.getString(Configuration.PASSWORD_PROMPT).trim(),
	        			custom.getString(Configuration.SHELL_PROMPT).trim(),
	        			custom.getString(Configuration.USERNAME).trim(),
	        			custom.getString(Configuration.PASSWORD).trim(),
	        			COMMAND_NOT_FOUND_PATTERN);
        	} catch(IOException e) {
        		// TODO localize
        		throw new RMSException("Failed to establish telnet connection. Error: " + e.getMessage());
        	}
        } else if(execMode == ProcessExecutionMode.SSH2) {
        	try {
        		if(fInetAddress == null) {
        			fInetAddress = InetAddress.getByName(config.getHost());
        		}
        		fSSH2Connection = new Connection(new InetSocketAddress(fInetAddress, custom.getPort()));
        		fSSH2Connection.connect();
        		fSSH2Connection.authenticateWithPassword(
	        			custom.getString(Configuration.USERNAME).trim(),
	        			custom.getString(Configuration.PASSWORD).trim());
        	} catch(IOException e) {
        		// TODO localize
        		throw new RMSException("Failed to establish ssh2 connection. Error: " + e.getMessage());
        	}
        }

	}

	/**
	 * @param config
	 * @return
	 */
	public static boolean requiresRemoteConnection(ProviderConfiguration config) {
		CustomConfiguration custom = config.getCustom();
        String sem = custom.getString(Configuration.EXECUTION_MODE);
        ProcessExecutionMode execMode = ProcessExecutionMode.resolve(sem);
        if(execMode == ProcessExecutionMode.TELNET || execMode == ProcessExecutionMode.SSH2) {
        	return true;
        }
        return false;
	}

	/**
	 * This method will be invoked multiple times during the life of this object and the connection
	 * to the remote host is reused as much as possible.
	 * @param conf
	 * @param command
	 * @param procListener
	 * @return
	 * @throws RMSException
	 */
	public ProcessWrapper createProcess(ProviderConfiguration conf, String command, RemoteProcessWrapper.Listener procListener) throws RMSException {
		if(fTelnetConnection != null) {
			// the way the telnet process works is that it closes the telnet
			// connection when it's stopped so check here if we need to create a new one
			if(!fTelnetConnection.isConnected()) {
				createConnection(conf);
			}
			return new TelnetProcessWrapper(fTelnetConnection, command,	procListener);
		} else if(fSSH2Connection != null) {
			// unlike the telnet connection, the ssh2 connection remains open
			return new SSH2ProcessWrapper(fSSH2Connection, command,
					COMMAND_NOT_FOUND_PATTERN,
					procListener);
		}
		throw new AppRuntimeException("Unknown process type");
	}

	/**
	 * @throws IOException
	 */
    public void disconnect() throws IOException {
    	if(fTelnetConnection != null) {
    		if(fTelnetConnection.isConnected()) {
    			fTelnetConnection.disconnect();
    		}
    	}
    	if(fSSH2Connection != null) {
    		fSSH2Connection.close();
    	}
    }
}
