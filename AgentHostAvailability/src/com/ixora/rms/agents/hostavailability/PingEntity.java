/*
 * Created on 12-Jan-2005
 */
package com.ixora.rms.agents.hostavailability;

import java.net.InetAddress;

import com.ixora.common.net.icmp.ICMP;
import com.ixora.rms.CounterId;
import com.ixora.rms.CounterType;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.AgentExecutionContext;
import com.ixora.rms.agents.hostavailability.messages.Msg;
import com.ixora.rms.agents.impl.Counter;
import com.ixora.rms.agents.impl.Entity;
import com.ixora.rms.data.CounterValueDouble;
import com.ixora.rms.exception.RMSException;

/**
 * @author Daniel Moraru
 */
public final class PingEntity extends Entity {
	private static final long serialVersionUID = 2507562654982915317L;
	private int fMisses;
	private boolean fICMPLibraryNotLoadedErrorSent;

	/**
	 * Constructor.
	 * @param id
	 * @param c
	 */
	public PingEntity(EntityId parent, AgentExecutionContext c) {
		super(new EntityId(parent, Msg.ENTITY_PING), getDescriptionFor(Msg.ENTITY_PING), c);
		fHasChildren = false;
		addCounter(new Counter(
				Msg.COUNTER_PING_RESPONSE_TIME,
				getDescriptionFor(Msg.COUNTER_PING_RESPONSE_TIME),
				CounterType.LONG));
		addCounter(new Counter(
				Msg.COUNTER_PING_MISSES,
				getDescriptionFor(Msg.COUNTER_PING_MISSES),
				CounterType.LONG));
	}

	/**
	 * @see com.ixora.rms.agents.impl.Entity#retrieveCounterValues()
	 */
	protected void retrieveCounterValues() throws Throwable {
		Configuration conf = (Configuration)fContext.getAgentConfiguration().getAgentCustomConfiguration();
		int ms = -1;
		try {
			try {
				ms = ICMP.ping(fContext.getAgentConfiguration().getMonitoredHost(),
						conf.getInt(Msg.PACKET_SIZE),
						conf.getInt(Msg.PING_TIMEOUT));
			} catch(NoClassDefFoundError e) { // that means the ping library was not found
				if(!fICMPLibraryNotLoadedErrorSent) {
					fContext.error(new RMSException("The native ping library could not be loaded; " +
							"the agent will use the default ping implementation. The host response " +
							"time will be less accurate and the specified packet size will be ignored."));
					fICMPLibraryNotLoadedErrorSent = true;
				}
				InetAddress addr = InetAddress.getByName(fContext.getAgentConfiguration().getMonitoredHost());
				long time = System.currentTimeMillis();
				if(!addr.isReachable(conf.getInt(Msg.PING_TIMEOUT))) {
					fMisses++;
				} else {
					ms = (int)(System.currentTimeMillis() - time);
				}
			}
		} catch(Exception e) {
			fMisses++;
		}
		getCounter(new CounterId(Msg.COUNTER_PING_RESPONSE_TIME)).dataReceived(
				new CounterValueDouble(ms));
		getCounter(new CounterId(Msg.COUNTER_PING_MISSES)).dataReceived(
				new CounterValueDouble(fMisses));
	}

	/**
	 * @param msg
	 * @return
	 */
	private static String getDescriptionFor(String msg) {
		return msg + ".description";
	}
}
