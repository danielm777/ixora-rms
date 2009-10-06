/**
 * 20-Aug-2005
 */
package com.ixora.common.net.telnet;

import java.io.IOException;
import java.net.InetAddress;

/**
 * @author Daniel Moraru
 */
public final class Telnet {
	public static byte[] CRLF = "\r\n".getBytes();

	private Telnet() {
		super();
	}

	/**
	 * Creates a connection and logs in using the given credentials.
	 * @param host
	 * @param port
	 * @param shellPrompt
	 * @param username
	 * @param password
	 * @param cmdNotFound
	 * @return
	 * @throws IOException
	 */
	public static TelnetConnection createConnection(
			String host,
			String shellPrompt,
			String username,
			String password,
			String cmdNotFound) throws IOException {
		return createConnection(host, 23, "login:",
				"Password:", shellPrompt, username, password, cmdNotFound);
	}

	/**
	 * Creates a connection and logs in using the given credentials.
	 * @param addr
	 * @param shellPrompt
	 * @param username
	 * @param password
	 * @param cmdNotFound
	 * @return
	 * @throws IOException
	 */
	public static TelnetConnection createConnection(
			InetAddress addr,
			int port,
			String shellPrompt,
			String username,
			String password,
			String cmdNotFound) throws IOException {
		return createConnection(addr, port, "login:",
				"Password:", shellPrompt, username, password, cmdNotFound);
	}

	/**
	 * Creates a connection and logs in using the given credentials.
	 * @param host
	 * @param port
	 * @param shellPrompt
	 * @param username
	 * @param password
	 * @param cmdNotFound
	 * @return
	 * @throws IOException
	 */
	public static TelnetConnection createConnection(
			String host,
			int port,
			String shellPrompt,
			String username,
			String password,
			String cmdNotFound) throws IOException {
		return createConnection(InetAddress.getByName(host), port, "login:",
				"Password:", shellPrompt, username, password, cmdNotFound);
	}


	/**
	 * Creates a connection and logs in using the given credentials.
	 * @param host
	 * @param port
	 * @param loginPrompt
	 * @param passwordPrompt
	 * @param shellPrompt
	 * @param username
	 * @param password
	 * @param cmdNotFound
	 * @return
	 * @throws IOException
	 */
	public static TelnetConnection createConnection(
			String host,
			int port,
			String loginPrompt,
			String passwordPrompt,
			String shellPrompt,
			String username,
			String password,
			String cmdNotFound) throws IOException {
			return createConnection(InetAddress.getByName(host), port,
					loginPrompt, passwordPrompt, shellPrompt, username, password, cmdNotFound);
	}

	/**
	 * Creates a connection and logs in using the given credentials.
	 * @param addr
	 * @param port
	 * @param loginPrompt
	 * @param passwordPrompt
	 * @param shellPrompt
	 * @param username
	 * @param password
	 * @param cmdNotFound
	 * @return
	 * @throws IOException
	 */
	public static TelnetConnection createConnection(
			InetAddress addr,
			int port,
			String loginPrompt,
			String passwordPrompt,
			String shellPrompt,
			String username,
			String password,
			String cmdNotFound) throws IOException {
		TelnetConnection conn = new TelnetConnection(addr, port, loginPrompt,
				passwordPrompt, shellPrompt, username, password, cmdNotFound);
		conn.connect();
		return conn;
	}

	/**
	 * @param s
	 * @return
	 */
	public static byte[] getBytes(String s) {
		return s.getBytes();
	}
}
