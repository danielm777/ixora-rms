/*
 * Created on 30-Dec-2004
 */
package com.ixora.rms.agents.providers.parsers;

import java.util.List;
import java.util.Map;

import com.ixora.rms.CounterId;
import com.ixora.rms.EntityDescriptor;
import com.ixora.rms.EntityId;
import com.ixora.rms.data.CounterValue;
import com.ixora.rms.providers.ProviderId;
import com.ixora.rms.providers.parsers.Parser;

/**
 * @author Daniel Moraru
 */
public interface MonitoringDataParser extends Parser {

	/** Listener */
	public interface Listener {
		/** Entity counter values */
		void sampleData(ProviderId pid, EntityId eid, List<CounterId> counters, List<CounterValue> values);
		/** Invoked when all the data for this sample has been processed */
		void sampleEnded(ProviderId pid);
		/** Invoked when a new entities show up in the provider buffer */
		void newEntities(ProviderId pid, EntityDescriptor[] desc);
		/** Invoked when entities disappear from a provider buffer */
		void expiredEntities(ProviderId pid, EntityId[] eids);
	}

	/**
	 * @param desc the initial entity descriptors
	 */
	void setEntityDescriptors(Map<EntityId, EntityDescriptor> entities);
	/**
	 * @param eid
	 * @return true if the given entity represent a final entity; a parser is allowed to
	 * have special tokens in the entity id so an entity is final if it doesn't contain such
	 * tokens
	 */
	boolean isEntityFinal(EntityId eid);
	/**
	 * Sets the listener.
	 * @param l
	 */
	void setListener(Listener l);
	/**
	 * Sets the provider id of the provider that works with this parser.
	 * @param pid
	 */
	void setProviderId(ProviderId pid);
}
