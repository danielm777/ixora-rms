/*
 * Created on 20-Jan-2005
 */
package com.ixora.rms.agents.providers.parsers;

import java.util.Set;

import com.ixora.rms.CounterId;
import com.ixora.rms.EntityId;
import com.ixora.rms.providers.parsers.ParsingRulesDefinition;

/**
 * @author Daniel Moraru
 */
public interface MonitoringDataParsingRulesDefinition extends ParsingRulesDefinition {
	/**
	 * @return all entities defined by the parser
	 */
	Set<EntityId> getEntities();
	/**
	 * @param e
	 * @return all counters belonging to the given entity
	 */
	Set<CounterId> getCounters(EntityId e);
	/**
	 * @return whether or not to accumulate volatile entities, i.e. entities
	 * will not be removed if they do not show up in the latest data sample
	 */
	boolean accumulateVolatileEntities();
}
