/**
 * 12-Dec-2005
 */
package com.ixora.common.net.telnet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.regex.Pattern;

import org.apache.commons.net.telnet.TelnetClient;

import com.ixora.common.exception.AppRuntimeException;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.utils.Utils;

/**
 * @author Daniel Moraru
 */
public class TelnetConnection {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(TelnetConnection.class);
	public interface CommandListener {
		/** Invoked if the command <code>cmd</code> was not found on the server */
		void commandNotFound(String cmd);
		/** Invoked when the prompt is found;i.e the current command finished executing */
		void prompt();
		/** Invoked when a new line is received while executing the current command */
		void commandOutput(String line);
	}

	private String fHost;
	private InetAddress fInetAddress;

	private int fPort;

	private String fLoginPrompt;
	private String fPasswordPrompt;
	private String fShellPrompt;
	private String fUsername;
	private String fPassword;
	private String fCommand;
	private TelnetClient fConn;
	private InputStream fInputStream;
	private OutputStream fOutputStream;

	private Thread fStreamConsumer;
	private StringBuffer fCurrentLine;
	private char[] fLastChars;
	private int fLastCharsIdx;
	private Pattern fCommandNotFoundPattern;
	/** The number of times the check for command not found was done */
	private int fCommandNotFoundCheckCount;
	private CommandListener fListener;

	/**
	 * @param host
	 * @param port
	 * @param loginPrompt
	 * @param passwordPrompt
	 * @param shellPrompt
	 * @param username
	 * @param password
	 * @param cmdNotFoundPattern the regex pattern used to detect that the command was not
	 * found on the remote system
	 */
	public TelnetConnection(
			String host,
			int port,
			String loginPrompt,
			String passwordPrompt,
			String shellPrompt,
			String username,
			String password,
			String cmdNotFoundPattern) {
		fHost = host;
		fPort = port;
		init(loginPrompt, passwordPrompt, shellPrompt, username, password, cmdNotFoundPattern);
	}

	/**
	 * @param addr
	 * @param port
	 * @param loginPrompt
	 * @param passwordPrompt
	 * @param shellPrompt
	 * @param username
	 * @param password
	 * @param cmdNotFoundPattern the regex pattern used to detect that the command was not
	 * found on the remote system
	 */
	public TelnetConnection(
			InetAddress addr,
			int port,
			String loginPrompt,
			String passwordPrompt,
			String shellPrompt,
			String username,
			String password,
			String cmdNotFoundPattern) {
		fInetAddress = addr;
		fPort = port;
		init(loginPrompt, passwordPrompt, shellPrompt, username, password, cmdNotFoundPattern);
	}

	/**
	 * @param loginPrompt
	 * @param passwordPrompt
	 * @param shellPrompt
	 * @param username
	 * @param password
	 * @param cmdNotFoundPattern
	 */
	private void init(String loginPrompt, String passwordPrompt, String shellPrompt, String username, String password, String cmdNotFoundPattern) {
		fLoginPrompt = processPrompt(loginPrompt);
		fPasswordPrompt = processPrompt(passwordPrompt);
		fShellPrompt = processPrompt(shellPrompt);
		fUsername = username;
		fPassword = password;

		fLastChars = new char[fShellPrompt.length()];
		fCommandNotFoundPattern = Pattern.compile(cmdNotFoundPattern);
	}

	/**
	 * @param host
	 * @param shellPrompt
	 * @param username
	 * @param password
	 * @param cmdNotFoundPattern
	 */
	public TelnetConnection(
			String host,
			String shellPrompt,
			String username,
			String password,
			String cmdNotFoundPattern) {
		this(host, 23, "login:",
				"Password:", shellPrompt, username, password, cmdNotFoundPattern);
	}

	/**
	 * @param host
	 * @param shellPrompt
	 * @param username
	 * @param password
	 * @param cmdNotFoundPattern
	 */
	public TelnetConnection(
			InetAddress add,
			String shellPrompt,
			String username,
			String password,
			String cmdNotFoundPattern) {
		this(add, 23, "login:",
				"Password:", shellPrompt, username, password, cmdNotFoundPattern);
	}

	/**
	 * @param host
	 * @param port
	 * @param shellPrompt
	 * @param username
	 * @param password
	 * @param cmdNotFoundPattern
	 */
	public TelnetConnection(String host,
			int port,
			String shellPrompt,
			String username,
			String password,
			String cmdNotFoundPattern) {
		this(host, port, "login:",
				"Password:", shellPrompt, username, password, cmdNotFoundPattern);
	}

	/**
	 * @param prompt
	 * @return
	 */
	private static String processPrompt(String prompt) {
		return prompt.trim() + " ";
	}

	/**
	 * @throws IOException
	 */
	public void connect() throws IOException {
		synchronized(this) {
			if(fConn != null) {
				if(fConn.isConnected()) {
					throw new IOException("Already connected");
				}
			}
			fConn = new TelnetClient();
			if(fInetAddress != null) {
				fConn.connect(fInetAddress, fPort);
			} else {
				fConn.connect(fHost, fPort);
			}
			fInputStream = fConn.getInputStream();
			fOutputStream = fConn.getOutputStream();
		}
		readUntil(fLoginPrompt);
		writeLine(fUsername);
		readUntil(fPasswordPrompt);
		writeLine(fPassword);
		String recv = readUntil(
					fShellPrompt,
					new String[]{"ogin incorrect", "invalid login"});
		if(recv == null) {
			throw new IOException("Incorrect credentials for telnet session");
		}

        fStreamConsumer = new Thread(new Runnable(){
			public void run() {
				try {
					int read;
					while((read = fInputStream.read()) > 0) {
						if(fCurrentLine == null) {
							fCurrentLine = new StringBuffer();
						}
						char ch = (char)read;
						if(ch != '\r' && ch != '\n') {
							fCurrentLine.append(ch);
							checkForCommandEnd(ch);
						} else {
							handleNewLine();
						}
					}
				} catch(Exception e) {
					// it happens when it's stopped but also when a network error occcurs
					finished();
				}
			}
        }, "TelnetConnectionStreamReader");
        fStreamConsumer.start();
	}

	/**
	 * Executes the given command.
	 * @param cmd
	 * @param listener
	 * @throws IOException
	 */
	public void execute(String cmd, CommandListener listener) throws IOException {
		synchronized(this) {
			if(fCommand != null) {
				throw new AppRuntimeException("Command '" + cmd + "' is still executing on this connection");
			}
			fCommand = cmd;
			fListener = listener;
		}
		writeLine(cmd);
	}


	/**
	 * @param pattern
	 * @return
	 * @throws IOException
	 */
	private String readUntil(String pattern) throws IOException {
		// read without error checking
		return readUntil(pattern, null);
	}

	/**
	 * @param pattern
	 * @param errorStrings if null there is no check for the input stream
	 * @return null if errorString was not null and was found before the pattern otherwise
	 * it returns the data read until the pattern was found
	 * @throws IOException
	 */
	private String readUntil(String pattern, String[] errorStrings) throws IOException {
		checkStreams();
		char lastChar = pattern.charAt(pattern.length() - 1);
		StringBuilder sb = new StringBuilder();
		do {
			int read = fInputStream.read();
			if(read < 0) {
				throw new IOException("Unexpected end of stream");
			}
			char ch = (char)read;
			sb.append(ch);
			if(!Utils.isEmptyArray(errorStrings)) {
				// do error checking
				for(String err : errorStrings) {
					if(sb.indexOf(err) >= 0) {
						return null;
					}
				}
			}
			if(ch == lastChar) {
				String str = sb.toString();
				if(str.endsWith(pattern)) {
					return str;
				}
			}
		} while(true);
	}

	/**
	 * @throws IOException
	 */
	private void checkStreams() throws IOException {
		if(fInputStream == null || fOutputStream == null) {
			throw new IOException("Not connected");
		}
	}

	/**
	 * @param cmd
	 * @throws IOException
	 */
	private void writeLine(String cmd) throws IOException {
		checkStreams();
		fOutputStream.write(Telnet.getBytes(cmd));
		fOutputStream.write(Telnet.CRLF);
		fOutputStream.flush();
	}

	/**
	 * @throws IOException
	 */
	public void disconnect() throws IOException {
		synchronized(this) {
			if(fConn != null) {
				if(fConn.isConnected()) {
					fConn.disconnect();
				}
			}
			fCommand = null;
			fListener = null;
		}
	}

	private void handleNewLine() {
		if(fCurrentLine != null) {
			if(fCommandNotFoundPattern != null) {
				if(fCommandNotFoundPattern.matcher(fCurrentLine).find()) {
					fListener.commandNotFound(fCommand);
				}
				if(fCommandNotFoundCheckCount >= 2) { // check the first two lines for command not found
					fCommandNotFoundPattern = null;
					fCommandNotFoundCheckCount = 0;
				}
				fCommandNotFoundCheckCount++;
			}
			synchronized(this) {
				fListener.commandOutput(fCurrentLine.toString());
			}
			fCurrentLine = null;
		}
	}

	private void checkForCommandEnd(char ch) {
		if(fLastCharsIdx < fLastChars.length) {
			if(fShellPrompt.charAt(fLastCharsIdx) == ch) {
				fLastChars[fLastCharsIdx++] = ch;
				if(fLastCharsIdx == fLastChars.length) {
					finished();
				}
			} else {
				fLastCharsIdx = 0;
			}
		} else {
			finished();
		}
	}

	/**
	 *
	 */
	private void finished() {
		synchronized(this) {
			if(fListener != null) {
				try {
					fListener.prompt();
				} catch(Throwable e) {
					logger.error(e);
				}
			}
			fCommand = null;
			fListener = null;
		}
	}

	/**
	 * @return
	 */
	public OutputStream getOutputStream() {
		return fConn.getOutputStream();
	}

	/**
	 * @return
	 */
	public boolean isConnected() {
		return fConn.isConnected();
	}
}
