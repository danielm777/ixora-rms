/*
 * Created on 21-Apr-2004
 */
package com.ixora.rms.agents.apache;

import com.ixora.rms.agents.AgentExecutionContext;

/**
 * Execution context for apache agent.
 * @author Daniel Moraru
 */
public interface ApacheAgentExecutionContext extends AgentExecutionContext {
	/**
	 * @return the content of the status page
	 */
	String getStatusPageContent();
	/**
	 * @return the Apache version
	 */
	int getApacheVersion();
	/**
	 * @return true if the extended status is on
	 */
	boolean isExtendedStatusOn();
}
