/**
 * 27-Dec-2005
 */
package com.ixora.rms.agents.mysql;

import java.util.Map;

import com.ixora.rms.CounterId;
import com.ixora.rms.CounterType;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.AgentExecutionContext;
import com.ixora.rms.agents.impl.Counter;
import com.ixora.rms.agents.impl.Entity;
import com.ixora.rms.data.CounterValueDouble;
import com.ixora.rms.data.CounterValueString;
import com.ixora.rms.exception.RMSException;

/**
 * @author Daniel Moraru
 */
public class MySQLEntity extends Entity {
	private static final long serialVersionUID = -2053753340827764618L;

	/**
	 * @param id
	 * @param c
	 */
	public MySQLEntity(EntityId id, AgentExecutionContext c, String[] counterNames) {
		super(id, c);
		for(String counterName : counterNames) {
			addCounter(new Counter(counterName, counterName, getCounterType(counterName)));
		}
	}

	/**
	 * @param counterName
	 * @return
	 */
	private CounterType getCounterType(String counterName) {
		if(Msg.RPL_STATUS.equals(counterName)
				|| Msg.SLAVE_RUNNING.equals(counterName)) {
			return CounterType.STRING;
		} else {
			return CounterType.DOUBLE;
		}
	}

	/**
	 * @see com.ixora.rms.agents.impl.Entity#retrieveCounterValues()
	 */
	protected void retrieveCounterValues() throws Throwable {
		try {
			Map<CounterId, Object> values = ((MySQLAgentContext)fContext).getValues();
			if(values != null) {
				for(Counter counter : fCounters.values()) {
					Object val = values.get(counter.getId());
					if(val != null) {
						String sval = val.toString();
						try {
							Double dval = Double.parseDouble(sval);
							counter.dataReceived(new CounterValueDouble(dval.doubleValue()));
						} catch(NumberFormatException e) {
							if(counter.getType() == CounterType.STRING) {
								counter.dataReceived(new CounterValueString(sval));
							}
						}
					} else {
						fContext.error(new RMSException(counter.getId() + " not found for entity " + this.getId()));
					}
				}
			}} catch(Exception e) {
				e.printStackTrace();
			}
	}

	/**
	 * Overriden to be public.
	 * @see com.ixora.rms.agents.impl.Entity#addChildEntity(com.ixora.rms.agents.impl.Entity)
	 */
	public void addChildEntity(Entity entity) throws Throwable {
		super.addChildEntity(entity);
	}
}
