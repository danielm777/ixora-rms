/*
 * Created on 17-Apr-2004
 */
package com.ixora.rms.agents.apache;

import com.ixora.rms.CounterId;
import com.ixora.rms.CounterType;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.AgentExecutionContext;
import com.ixora.rms.agents.apache.exception.InvalidDataPattern;
import com.ixora.rms.agents.apache.messages.Msg;
import com.ixora.rms.agents.impl.Counter;
import com.ixora.rms.agents.impl.Entity;
import com.ixora.rms.data.CounterValueDouble;

/**
 * Entity representing the state of a worker server.
 * Counters:
 * 		Counter name: Child Server number(generation)(OS process ID)
 *
 * 		Number of accesses this connection
 * 		Number of accesses this child
 * 		Number of accessses this slot
 * 		Mode of operation
 *		Seconds since beginning of most recent request
 *		Milliseconds required to process most recent request
 *		Kilobytes transferred this connection
 *		Megabytes transferred this child
 *		Total megabytes transferred this slot
 * @author Daniel Moraru
 */
public final class ApacheEntityServers extends Entity
	implements ApacheConstants {
	private static final long serialVersionUID = 6187024818327912268L;
	/** Last records obtained after parsing the status page content */
	private String[][] records;
	/**
	 * Child entity for ApacheEntityServer.
	 * Counters:
	 * 		Counter name: Child Server number(generation)(OS process ID)
	 *
	 * 		Number of accesses this connection
	 * 		Number of accesses this child
	 * 		Number of accesses this slot
	 * 		Mode of operation
	 *		Seconds since beginning of most recent request
	 *		Milliseconds required to process most recent request
	 *		Kilobytes transferred this connection
	 *		Megabytes transferred this child
	 *		Total megabytes transferred this slot
	 */
	private final class ApacheEntityServer extends Entity {
		private static final long serialVersionUID = -2756982410492433684L;
		private String[] tokens;
		/**
		 * @param id
		 * @param c
		 */
		public ApacheEntityServer(
			EntityId id,
			AgentExecutionContext c) {
			super(id, c);
			addCounter(new Counter(
					Msg.APACHE_ENTITY_SERVER_COUNTER_ACCESSES_THIS_CHILD,
					Msg.APACHE_ENTITY_SERVER_COUNTER_ACCESSES_THIS_CHILD + ".description",
					CounterType.LONG));
			addCounter(new Counter(
					Msg.APACHE_ENTITY_SERVER_COUNTER_ACCESSES_THIS_CONNECTION,
					Msg.APACHE_ENTITY_SERVER_COUNTER_ACCESSES_THIS_CONNECTION + ".description"));
			addCounter(new Counter(
					Msg.APACHE_ENTITY_SERVER_COUNTER_ACCESSES_THIS_SLOT,
					Msg.APACHE_ENTITY_SERVER_COUNTER_ACCESSES_THIS_SLOT + ".description"));
			addCounter(new Counter(
					Msg.APACHE_ENTITY_SERVER_COUNTER_KILOBYTES_TRANSFERRED_THIS_CONNECTION,
					Msg.APACHE_ENTITY_SERVER_COUNTER_KILOBYTES_TRANSFERRED_THIS_CONNECTION + ".description",
					CounterType.DOUBLE));
			addCounter(new Counter(
					Msg.APACHE_ENTITY_SERVER_COUNTER_MEGABYTES_TRANSFERRED_THIS_CHILD,
					Msg.APACHE_ENTITY_SERVER_COUNTER_MEGABYTES_TRANSFERRED_THIS_CHILD + ".description",
					CounterType.DOUBLE));
			addCounter(new Counter(
					Msg.APACHE_ENTITY_SERVER_COUNTER_TOTAL_MEGABYTES_TRANSFERRED_THIS_SLOT,
					Msg.APACHE_ENTITY_SERVER_COUNTER_TOTAL_MEGABYTES_TRANSFERRED_THIS_SLOT + ".description",
					CounterType.DOUBLE));
			addCounter(new Counter(
					Msg.APACHE_ENTITY_SERVER_COUNTER_SECONDS_SINCE_MOST_RECENT_REQUEST,
					Msg.APACHE_ENTITY_SERVER_COUNTER_SECONDS_SINCE_MOST_RECENT_REQUEST + ".description",
					CounterType.DOUBLE));
		}

		/**
		 * @see com.ixora.rms.agents.impl.Entity#retrieveCounterValues()
		 */
		protected void retrieveCounterValues() throws Throwable {
			if(this.tokens == null) {
				return;
			}
			int n;
			float f;
			Counter c = getCounter(
				new CounterId(Msg.APACHE_ENTITY_SERVER_COUNTER_ACCESSES_THIS_CHILD));
			if(c.isEnabled()) {
				n = Integer.parseInt(tokens[ApacheUtils.IDX_ACCESSES_THIS_CHILD]);
				c.dataReceived(new CounterValueDouble(n));
			}
			c = getCounter(
				new CounterId(Msg.APACHE_ENTITY_SERVER_COUNTER_ACCESSES_THIS_CONNECTION));
			if(c.isEnabled()) {
				n = Integer.parseInt(tokens[ApacheUtils.IDX_ACCESSES_THIS_CONNECTION]);
				c.dataReceived(new CounterValueDouble(n));
			}
			c = getCounter(
				new CounterId(Msg.APACHE_ENTITY_SERVER_COUNTER_ACCESSES_THIS_SLOT));
			if(c.isEnabled()) {
				n = Integer.parseInt(tokens[ApacheUtils.IDX_ACCESSES_THIS_SLOT]);
				c.dataReceived(new CounterValueDouble(n));
			}
			c = getCounter(
				new CounterId(Msg.APACHE_ENTITY_SERVER_COUNTER_KILOBYTES_TRANSFERRED_THIS_CONNECTION));
			if(c.isEnabled()) {
				f = Float.parseFloat(tokens[ApacheUtils.IDX_KILOBYTES_TRANSFERRED_THIS_CONNECTION]);
				c.dataReceived(new CounterValueDouble(f));
			}
			c = getCounter(
				new CounterId(Msg.APACHE_ENTITY_SERVER_COUNTER_MEGABYTES_TRANSFERRED_THIS_CHILD));
			if(c.isEnabled()) {
				f = Float.parseFloat(tokens[ApacheUtils.IDX_MEGABYTES_TRANSFERRED_THIS_CHILD]);
				c.dataReceived(new CounterValueDouble(f));
			}
			c = getCounter(
				new CounterId(Msg.APACHE_ENTITY_SERVER_COUNTER_SECONDS_SINCE_MOST_RECENT_REQUEST));
			if(c.isEnabled()) {
				f = Float.parseFloat(tokens[ApacheUtils.IDX_SECONDS_SINCE_BEGINING_MOST_RECENT_REQUEST]);
				c.dataReceived(new CounterValueDouble(f));
			}
			c = getCounter(
				new CounterId(Msg.APACHE_ENTITY_SERVER_COUNTER_TOTAL_MEGABYTES_TRANSFERRED_THIS_SLOT));
			if(c.isEnabled()) {
				f = Float.parseFloat(tokens[ApacheUtils.IDX_TOTAL_MEGABYTES_TRANSFERRED_THIS_SLOT]);
				c.dataReceived(new CounterValueDouble(f));
			}
			tokens = null;
		}
		/**
		 * @param tokens The tokens to set.
		 */
		public void setTokens(String[] tokens) {
			this.tokens = tokens;
		}
	}
	/**
	 * @param parent
	 * @param c
	 */
	public ApacheEntityServers(EntityId parent, AgentExecutionContext c) {
		super(new EntityId(parent, Msg.APACHE_ENTITY_SERVERS),
				Msg.APACHE_ENTITY_SERVERS_DESCRIPTION, c);
		fHasChildren = true;
	}

	/**
	 * @see com.ixora.rms.agents.impl.Entity#retrieveCounterValues()
	 */
	protected void retrieveCounterValues() throws Throwable {
		; // nothing, the entity will have queries in control center
	}

	/**
	 * @see com.ixora.rms.agents.impl.EntityTree#updateChildrenEntities(boolean)
	 */
	public void updateChildrenEntities(boolean recursive) throws Throwable {
		if(records == null) {
			parseForRecords();
		}
		if(records != null) {
			String childServerNumber;
			for(int i = 0; i < records.length; i++) {
				childServerNumber = records[i][ApacheUtils.IDX_CHILD_SERVER_NUMBER];
				EntityId eid = new EntityId(getId(), "Server"	+ childServerNumber);
				if(fChildrenEntities.get(eid) == null) {
					addChildEntity(new ApacheEntityServer(eid, fContext));
				}
			}
		}
	}

	/**
	 * @throws Throwable
	 * @see com.ixora.rms.agents.impl.EntityTree#onBeginCycle()
	 */
	protected void onBeginCycle() throws Throwable {
		// set the counters for all children entities
		parseForRecords();
		if(records != null) {
			String childServerNumber;
			boolean newChildren = false;
			String[] r;
			for(int i = 0; i < records.length; i++) {
				r = records[i];
				childServerNumber = r[ApacheUtils.IDX_CHILD_SERVER_NUMBER];
				EntityId eid = new EntityId(getId(), "Server" + childServerNumber);
				ApacheEntityServer e = (ApacheEntityServer)fChildrenEntities.get(eid);
				if(e == null) {
					e = new ApacheEntityServer(eid, fContext);
					newChildren = true;
					addChildEntity(e);
				}
				e.setTokens(r);
			}
			if(newChildren) {
				fireChildrenEntitiesChanged();
			}
		}
	}

	/**
	 * Parses the extended status information for records
	 */
	private void parseForRecords() {
		ApacheAgentExecutionContext c = (ApacheAgentExecutionContext)this.fContext;
		String text = c.getStatusPageContent();
		if(text == null) {
			return;
		}
		switch(c.getApacheVersion()) {
			case VERSION_IBM_6_0_X:
				records = ApacheUtils.parseExtendedStatus_ibm_6_0(text);
			break;
			case VERSION_ORACLE_1_3_X:
			case VERSION_IBM_1_3_X:
				records = ApacheUtils.parseExtendedStatus_1_3_x(text);
			break;
			case VERSION_ORACLE_2_0_X:
			case VERSION_IBM_2_0_X:
				records = ApacheUtils.parseExtendedStatus_1_3_x(text);
			break;
		}
		if(records == null) {
			fContext.error(new InvalidDataPattern());
		}
	}
}
