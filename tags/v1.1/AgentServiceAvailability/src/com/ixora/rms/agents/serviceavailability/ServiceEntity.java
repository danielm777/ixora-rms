/*
 * Created on 12-Jan-2005
 */
package com.ixora.rms.agents.serviceavailability;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketTimeoutException;

import com.ixora.common.utils.Utils;
import com.ixora.rms.CounterId;
import com.ixora.rms.CounterType;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.AgentExecutionContext;
import com.ixora.rms.agents.impl.Counter;
import com.ixora.rms.agents.impl.Entity;
import com.ixora.rms.agents.serviceavailability.messages.Msg;
import com.ixora.rms.data.CounterValueDouble;
import com.ixora.rms.data.CounterValueString;

/**
 * @author Daniel Moraru
 */
public final class ServiceEntity extends Entity {
	private int misses;
	/**
	 * Cache here the inet address as it's very expensive; There is also a bug in JDK that
	 * makes creating a Socket take around 5 seconds.
	 */
	private InetSocketAddress address;

	/**
	 * Constructor.
	 * @param id
	 * @param c
	 */
	public ServiceEntity(EntityId parent, AgentExecutionContext c) {
		super(new EntityId(parent, Msg.SERVICE), getDescriptionFor(Msg.SERVICE), c);
		fHasChildren = false;
		addCounter(new Counter(
				Msg.COUNTER_SERVICE_RESPONSE_TIME,
				getDescriptionFor(Msg.COUNTER_SERVICE_RESPONSE_TIME),
				CounterType.LONG));
		addCounter(new Counter(
				Msg.COUNTER_SERVICE_TIME_TO_CONNECT,
				getDescriptionFor(Msg.COUNTER_SERVICE_TIME_TO_CONNECT),
				CounterType.LONG));
		addCounter(new Counter(
				Msg.COUNTER_SERVICE_BYTES_IN_REPLY,
				getDescriptionFor(Msg.COUNTER_SERVICE_BYTES_IN_REPLY),
				CounterType.LONG));
		addCounter(new Counter(
				Msg.COUNTER_SERVICE_TIME_TO_WRITE,
				getDescriptionFor(Msg.COUNTER_SERVICE_TIME_TO_WRITE),
				CounterType.LONG));
		addCounter(new Counter(
				Msg.COUNTER_SERVICE_MISSES,
				getDescriptionFor(Msg.COUNTER_SERVICE_MISSES),
				CounterType.LONG));
        addCounter(new Counter(
                Msg.COUNTER_SERVICE_REPLY,
                getDescriptionFor(Msg.COUNTER_SERVICE_REPLY),
                CounterType.STRING));
	}

	/**
	 * @see com.ixora.rms.agents.impl.Entity#retrieveCounterValues()
	 */
	protected void retrieveCounterValues() throws Throwable {
		Configuration conf = (Configuration)fContext.getAgentConfiguration().getAgentCustomConfiguration();
		long rt = 0;
		long ttc = 0;
		long ttw = 0;
		long bytes = 0;
        String reply = "";
		Socket conn = null;
		try {
			// See bug 5092063 at bugs.sun.com
			// The socket impl has changed in Java 1.5 and the creation of a
			// socket it's hugely expensive even when that bug will be fixed;
			// the following is a good workaround...
			conn = new Socket(Proxy.NO_PROXY);
			if(address == null) {
				address = new InetSocketAddress(
						fContext.getAgentConfiguration().getMonitoredHost(),
						conf.getInt(Configuration.PORT));
			}
			conn.setTcpNoDelay(true);
			// measure from here on
			// connect
			long tmp = System.currentTimeMillis();
			conn.connect(address);

			ttc = System.currentTimeMillis() - tmp;
			OutputStream os = conn.getOutputStream();
			byte[] data = conf.getString(Configuration.DATA).getBytes("ASCII7");

			// write
			tmp = System.currentTimeMillis();
			os.write(data);
			os.flush();
			ttw = System.currentTimeMillis() - tmp;

			// read
			int timeOut = conf.getInt(Configuration.READ_TIMEOUT);
			if(timeOut > 0) {
				conn.setSoTimeout(timeOut);
				try {
					InputStream is = conn.getInputStream();
                    ByteArrayOutputStream tos = new ByteArrayOutputStream();
					Utils.transferContent(is, tos, null);
                    bytes = tos.size();
                    reply = tos.toString("ASCII7");
				} catch(SocketTimeoutException e) {
					; // ignore
				}
				rt = System.currentTimeMillis() - tmp;
			} else {
				rt = ttw;
			}
		} catch(Exception e) {
			misses++;
		} finally {
			if(conn != null) {
				conn.close();
			}
		}
		getCounter(new CounterId(Msg.COUNTER_SERVICE_RESPONSE_TIME)).dataReceived(
				new CounterValueDouble(rt));
		getCounter(new CounterId(Msg.COUNTER_SERVICE_TIME_TO_CONNECT)).dataReceived(
				new CounterValueDouble(ttc));
		getCounter(new CounterId(Msg.COUNTER_SERVICE_TIME_TO_WRITE)).dataReceived(
				new CounterValueDouble(ttw));
		getCounter(new CounterId(Msg.COUNTER_SERVICE_BYTES_IN_REPLY)).dataReceived(
				new CounterValueDouble(bytes));
		getCounter(new CounterId(Msg.COUNTER_SERVICE_MISSES)).dataReceived(
				new CounterValueDouble(misses));
        getCounter(new CounterId(Msg.COUNTER_SERVICE_REPLY)).dataReceived(
                new CounterValueString(reply));
	}

	/**
	 * @param msg
	 * @return
	 */
	private static String getDescriptionFor(String msg) {
		return msg + ".description";
	}
}
