/**
 * 27-Dec-2005
 */
package com.ixora.rms.agents.mysql;

import java.util.Map;

import com.ixora.rms.CounterId;
import com.ixora.rms.agents.AgentExecutionContext;

/**
 * @author Daniel Moraru
 */
public interface MySQLAgentContext extends AgentExecutionContext {
	/**
	 * @return the values extracted by the agent
	 */
	Map<CounterId, Object> getValues();
}
