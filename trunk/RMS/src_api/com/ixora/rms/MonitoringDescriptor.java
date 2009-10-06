/*
 * Created on 25-Jul-2004
 */
package com.ixora.rms;

import com.ixora.common.xml.XMLExternalizable;

/**
 * MonitoringDescriptor.
 * @author Daniel Moraru
 */
public interface MonitoringDescriptor extends XMLExternalizable {
	/**
	 * @return the counter name
	 */
	String getName();

	/**
	 * @return true if the counter is enabled
	 */
	boolean isEnabled();

	/**
	 * @return the description of the counter
	 */
	String getDescription();

	/**
	 * @return the monitoring level this counter belongs to
	 */
	MonitoringLevel getLevel();

	/**
	 * @param level
	 * @return true if this entity applies to the given level
	 */
	boolean appliesToLevel(MonitoringLevel level);

	/**
	 * This is used as the last alternative for a translated
	 * name if no translated value is found for <code>name</code>
	 * in the message repository.
	 * Used for agents that provide both a name key as well as
	 * a translated value, which means we don't necesarilly provide
	 * a message file. In this case by using the name key in the
	 * queries and the alternate name in UI we can used the queries
	 * for any languages provided by the agent.
	 * @return Returns the alternate name.
	 */
	String getAlternateName();
}