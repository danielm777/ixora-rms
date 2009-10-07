/*
 * Created on 17-Apr-2004
 */
package com.ixora.rms.agents.apache;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.ixora.rms.CounterId;
import com.ixora.rms.CounterType;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.AgentExecutionContext;
import com.ixora.rms.agents.apache.exception.InvalidDataPattern;
import com.ixora.rms.agents.apache.messages.Msg;
import com.ixora.rms.agents.impl.Counter;
import com.ixora.rms.agents.impl.Entity;
import com.ixora.rms.data.CounterValueDouble;
import com.ixora.rms.data.CounterValueString;

/**
 * 'Requests' entity.
 * Counters:
 *		Milliseconds required to process request
 *		Client IP address - pivot
 *		Virtual host - pivot
 * @author Daniel Moraru
 */
public final class ApacheEntityRequests extends Entity
		implements ApacheConstants {
	/** Last records obtained after parsing the status page content */
	private String[][] records;
	/**
	 * 'Request' entity.
	 * Counters:
	 *		Milliseconds required to process request
	 *		Client IP address - possible pivot
	 *		Virtual host - possible pivot
	 * @author Daniel Moraru
	 */
	private final class ApacheEntityRequest extends Entity {
		/**
		 * List of records
		 */
		private List<String[]> tokens;
		/**
		 * @param id
		 * @param c
		 */
		public ApacheEntityRequest(
				EntityId id, AgentExecutionContext c) {
			super(id, c);
			addCounter(new Counter(
				Msg.APACHE_ENTITY_REQUEST_COUNTER_MILLS_REQUIRED_TO_PROCESS_REQUEST,
				Msg.APACHE_ENTITY_REQUEST_COUNTER_MILLS_REQUIRED_TO_PROCESS_REQUEST + ".description"));
			addCounter(new Counter(
					Msg.APACHE_ENTITY_REQUEST_COUNTER_CLIENT,
					Msg.APACHE_ENTITY_REQUEST_COUNTER_CLIENT + ".description",
					CounterType.STRING));
			addCounter(new Counter(
					Msg.APACHE_ENTITY_REQUEST_COUNTER_VHOST,
					Msg.APACHE_ENTITY_REQUEST_COUNTER_VHOST + ".description",
					CounterType.STRING));
		}

		/**
		 * @see com.ixora.rms.agents.impl.Entity#retrieveCounterValues()
		 */
		protected void retrieveCounterValues() throws Throwable {
			if(this.tokens == null) {
				return;
			}
			int n;
			String[] record;
			Counter c = getCounter(
				new CounterId(Msg.APACHE_ENTITY_REQUEST_COUNTER_MILLS_REQUIRED_TO_PROCESS_REQUEST));
			if(c.isEnabled()) {
				for(Iterator iter = tokens.iterator(); iter.hasNext();) {
					record = (String[])iter.next();
					n = Integer.parseInt(record[ApacheUtils.IDX_MILLISECONDS_REQUIRED_TO_PROCESS_MOST_RECENT_REQUEST]);
					c.dataReceived(new CounterValueDouble(n));
				}
			}
			c = getCounter(
					new CounterId(Msg.APACHE_ENTITY_REQUEST_COUNTER_CLIENT));
			if(c.isEnabled()) {
				for(Iterator iter = tokens.iterator(); iter.hasNext();) {
					record = (String[])iter.next();
					c.dataReceived(new CounterValueString(
							record[ApacheUtils.IDX_CLIENT]));
				}
			}
			c = getCounter(
					new CounterId(Msg.APACHE_ENTITY_REQUEST_COUNTER_VHOST));
			if(c.isEnabled()) {
				for(Iterator iter = tokens.iterator(); iter.hasNext();) {
					record = (String[])iter.next();
					c.dataReceived(new CounterValueString(
							record[ApacheUtils.IDX_VHOST]));
				}
			}
			tokens = null;
		}
		/**
		 * @param tokens The tokens to set.
		 */
		public void setTokens(List<String[]> tokens) {
			this.tokens = tokens;
		}
	}

	/**
	 * @param parent
	 * @param c
	 */
	public ApacheEntityRequests(EntityId parent, AgentExecutionContext c) {
		super(new EntityId(parent, Msg.APACHE_ENTITY_REQUESTS),
				Msg.APACHE_ENTITY_REQUESTS_DESCRIPTION, c);
		fHasChildren = true;
	}

	/**
	 * @see com.ixora.rms.agents.impl.Entity#retrieveCounterValues()
	 */
	protected void retrieveCounterValues() throws Throwable {
		; // nothing, the entity will have queries
	}

	/**
	 * @see com.ixora.rms.agents.impl.Entity#updateChildrenEntities(boolean)
	 */
	public void updateChildrenEntities(boolean recursive) throws Throwable {
		if(records == null) {
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
		}
		if(records != null) {
			for(int i = 0; i < records.length; i++) {
				EntityId eid = new EntityId(getId(), records[i][ApacheUtils.IDX_REQUEST]);
				if(fChildrenEntities.get(eid) == null) {
					addChildEntity(new ApacheEntityRequest(eid, fContext));
				}
			}
		} else {
			fContext.error(new InvalidDataPattern());
		}
	}

	/**
	 * @throws Throwable
	 * @see com.ixora.rms.agents.impl.EntityTree#onBeginCycle()
	 */
	protected void onBeginCycle() throws Throwable {
		parseForRecords();
		if(records != null) {
			String request;
			boolean newChildren = false;
			String[] record;
			HashMap<String, List<String[]>> m = new HashMap<String, List<String[]>>();
			for(int i = 0; i < records.length; i++) {
				record = records[i];
				request = record[ApacheUtils.IDX_REQUEST];
				EntityId eid = new EntityId(getId(), request);
				ApacheEntityRequest e = (ApacheEntityRequest)fChildrenEntities.get(eid);
				if(e == null) {
					newChildren = true;
					e = new ApacheEntityRequest(eid, fContext);
					addChildEntity(e);
				}
				// get tokens for this request
				List<String[]> lst = m.get(request);
				if(lst == null) {
					lst = new LinkedList<String[]>();
					m.put(request, lst);
				}
				lst.add(record);
			}
			// now set the tokens for each request
			ApacheEntityRequest e;
			List lst;
			for(Iterator iter = fChildrenEntities.values().iterator(); iter.hasNext();) {
				e = (ApacheEntityRequest)iter.next();
				lst = m.get(e.getName());
				if(lst != null) {
					e.setTokens(lst);
				}
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
