/*
 * Created on 21-Jul-2004
 */
package com.ixora.rms.client.model;

import java.util.Collection;

import com.ixora.rms.CounterId;
import com.ixora.rms.EntityConfiguration;
import com.ixora.rms.EntityId;
import com.ixora.rms.MonitoringDescriptor;
import com.ixora.rms.MonitoringLevel;

/**
 * Entity model info.
 * @author Daniel Moraru
 */
public interface EntityInfo
		extends MonitoringDescriptor, ArtefactInfoContainer {
	/**
	 * @return the entity id
	 */
	EntityId getId();
	/**
	 * @return the entity configuration
	 */
	EntityConfiguration getConfiguration() ;

	/**
	 * @return the supported levels of this entity
	 */
	MonitoringLevel[] getSupportedLevels();

	/**
	 * @return true if this entity supports sampling interval
	 */
	boolean supportsSamplingInterval();

	/**
	 * @return true if this entity is able to refresh its children
	 */
	boolean canRefreshChildren();
	/**
	 * @return true if this entity can refresh the subtree for which it is root
	 */
	boolean safeToRefreshRecursivelly();
	/**
	 * @return the translated translated description
	 */
	String getTranslatedDescription();
	/**
	 * @return the translated entity name
	 */
	String getTranslatedName();
	/**
	 * @return the translated entity path
	 */
	String getTranslatedPath();
	/**
	 * @return the translatedPathFragments.
	 */
	String[] getTranslatedPathFragments();
	/**
	 * @param cid
	 * @return counter info for the given counter
	 */
	CounterInfo getCounterInfo(CounterId cid);
	/**
	 * @return all counters
	 */
	Collection<CounterInfo> getCounterInfo();
	/**
	 * @return the info for the counters for the
	 * given level
	 */
	Collection<CounterInfo> getCounterInfoForLevel(MonitoringLevel level);
	/**
	 * @return true if there are uncommitted counters that belong
	 * to the current monitoring level
	 */
	boolean uncommittedVisibleCounters();
	/**
	 * @return true if this entity has children
	 */
	boolean hasChildren();
	/**
	 * @return true if the user modified this entity
	 */
	boolean isDirty();
}