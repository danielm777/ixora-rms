package com.ixora.common.net.icmp;

import java.math.BigInteger;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashSet;

/**
 * @author Cristian Costache
 * @author Daniel Moraru
 */
public class ICMP {
	private native int icmpSend(String host, int sequence, String data)
			throws java.net.SocketException, java.net.UnknownHostException;

	private native String icmpRecv(int timeout)
			throws java.net.SocketTimeoutException;

	static {
		System.loadLibrary("ping");
	}

	private int counter;
	private HashSet<Integer> received;

	public ICMP() {
		counter = 0;
		received = new HashSet<Integer>();
	}

	/**
	 * @param host
	 * @param bytes
	 * @return
	 * @throws SocketException
	 * @throws UnknownHostException
	 */
	public int send(String host, int bytes) throws SocketException,
			UnknownHostException {
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < bytes; i++)
			sb.append(i % 9);

		return send(host, sb.toString());
	}

	/**
	 * @param host
	 * @param data
	 * @return
	 * @throws SocketException
	 * @throws UnknownHostException
	 */
	public int send(String host, String data) throws SocketException,
			UnknownHostException {
		data = System.currentTimeMillis() + " " + data;
		int ret = icmpSend(host, counter, data);
		counter++;
		return ret;
	}

	/**
	 * @param timeout
	 * @return
	 * @throws java.net.SocketTimeoutException
	 */
	public long recv(int timeout) throws java.net.SocketTimeoutException {
		String res = icmpRecv(timeout);

		long endtime = System.currentTimeMillis();

		Integer I = new Integer(res.substring(0, res.indexOf(" ")));
		if (received.contains(I)) {
		} else {
			received.add(I);
		}

		String data = res.substring((res.indexOf(" ") + 1));
		data = data.substring(0, data.indexOf(" "));
		BigInteger bi = new BigInteger(data);
		long starttime = bi.longValue();

		return (endtime - starttime);
	}

	/**
	 * Pings <em>host</em> with a number of <em>bytes</em>, a number of
	 * <em>times</em> waiting for a maximum of <em>timeout</em> milliseconds
	 * each time. Returns the response time in milliseconds.
	 */
	public native static int ping(String host, int bytes, int timeout)
			throws java.net.SocketTimeoutException, java.net.SocketException,
			java.net.UnknownHostException;

}
