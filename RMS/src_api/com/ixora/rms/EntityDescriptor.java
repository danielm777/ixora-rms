/*
 * Created on 25-Jul-2004
 */
package com.ixora.rms;

import java.util.Collection;
import java.util.Set;

import org.w3c.dom.Node;

import com.ixora.common.xml.exception.XMLException;


/**
 * @author Daniel Moraru
 */
public interface EntityDescriptor extends MonitoringDescriptor {
	/**
	 * @return the counters
	 */
	Collection<CounterDescriptor> getCounterDescriptors();

	/**
	 * @param cid
	 * @return the counter descriptor for the counter
	 * with the given id
	 */
	CounterDescriptor getCounterDescriptor(CounterId cid);

	/**
	 * @param level
	 * @return the counters that fit the given monitoring level
	 */
	Collection<CounterDescriptor> getCounterDescriptorsForLevel(
			MonitoringLevel level);

	/**
	 * @return the id
	 */
	EntityId getId();

	/**
	 * @return the current configuration of this entity
	 */
	EntityConfiguration getConfiguration();

	/**
	 * @return the supported monitoring levels or null
	 * if the entity is not aware of monitoring levels
	 */
	MonitoringLevel[] getSupportedLevels();

	/**
	 * @return true if it supports an independent sampling period
	 */
	boolean supportsSamplingInterval();
	/**
	 * @return whether or not it has children.
	 */
	boolean hasChildren();
	/**
	 * @return true if this entity can refresh it's children
	 */
	boolean canRefreshChildren();
    /**
     * @return true if it's safe to refresh its children recursivelly
     */
    boolean safeToRefreshRecursivelly();
    /**
     * Writes this descriptor to xml writing descriptors only
     * for counters in the given set.
     * @param parent
     * @param monitoredCountersIds
     * @throws XMLException
     */
    void toXML(Node parent, Set monitoredCountersIds) throws XMLException;
}